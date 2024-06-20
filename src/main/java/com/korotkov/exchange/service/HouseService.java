package com.korotkov.exchange.service;


import com.korotkov.exchange.dto.request.HouseReviewRequest;
import com.korotkov.exchange.model.*;
import com.korotkov.exchange.repository.HouseModerationRepository;
import com.korotkov.exchange.repository.HouseRepository;
import com.korotkov.exchange.repository.HouseReviewRepository;
import com.korotkov.exchange.repository.TradeRepository;
import com.korotkov.exchange.util.ImageMetaData;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class HouseService {

    HouseRepository houseRepository;
    HouseModerationRepository houseModerationRepository;
    UserService userService;
    FileService fileService;
    ModelMapper modelMapper;
    TradeRepository tradeRepository;
    HouseReviewRepository reviewRepository;



    public List<House> findAllMyHouses(){
        return houseRepository.getAllByUserId(userService.getCurrentUser().getId());
    }

    public House findHouseById(int id){
        return houseRepository.getReferenceById(id);
    }

    public List<String> findAllAvailableCities(){
        return houseRepository.findAllCities();
    }



    @Transactional
    public void create(House house){
        house.setUser(userService.getCurrentUser());
        sendToModerator(house, house);
    }

    @Transactional
    public void save(House house){
        houseRepository.save(house);
    }


    @Transactional
    public void edit(House temp, int id){
        //todo rename
        House house = houseRepository.getReferenceById(id);

        String city = temp.getCity();
        if(city != null){
            house.setCity(city);
        }
        String address = temp.getAddress();
        if(address != null){
            house.setAddress(address);
        }
        String description = temp.getCity();
        if(description != null){
            house.setCity(description);
        }

        sendToModerator(temp, house);
    }


    @Transactional
    public void sendToModerator(House temp, House house){
        house.setStatus(HouseStatus.MODERATED);
        save(house);

        HouseModeration houseModeration = modelMapper.map(temp, HouseModeration.class);
        houseModeration.setHouse(house);
        houseModerationRepository.save(houseModeration);
    }

    @Transactional
    public void delete(Integer id) {
        House house = houseRepository.getReferenceById(id);

        if (house.getUser().getId() == userService.getCurrentUser().getId()) {
            houseRepository.delete(house);
        }
        else {
            //todo
        }
    }


    public List<House> findAllHouses(String city) {
        return houseRepository.getAllByCityIgnoreCaseStartingWith(city);
    }

    public List<House> findAllHouses(String city, Date sDate, Date eDate){
        return houseRepository.getAllCitiesWithCityAndDate(city, sDate, eDate);
    }

    public void addImages(MultipartFile[] files, int id) {
        //todo make status to be moderated
        for (MultipartFile file : files) {
            fileService.uploadFile(file,"house_images/" + id + "/" + file.getOriginalFilename());
        }
    }

    public List<ImageMetaData> findAllHouseImages(Integer houseId){
        return fileService.getAllImages("house_images/" + houseId + "/");
    }

    @Transactional
    public void addReview(HouseReviewRequest request) {
        User currentUser = userService.getCurrentUser();
        HouseReview review = modelMapper.map(request, HouseReview.class);
        review.setHouse(houseRepository.getReferenceById(request.getHouseId()));
         if(currentUser.getId() != review.getHouse().getUser().getId()){
            //todo
            if(tradeRepository.findAllByUserAndHouse(review.getHouse().getId(),currentUser.getId())!= 0){
                review.setAuthor(currentUser);
                reviewRepository.save(review);
                review.getHouse().getUser().addRating(request.getRating());
                //todo проверить чтобы можно было только один отзыв
            }
        }
    }

    public List<HouseReview> findAllReviews(int id) {
        return reviewRepository.findAllByHouse_Id(id);
    }

    public InputStreamResource getImage(Integer id, String path) {
        return fileService.getFile(path);
    }
}
