package com.korotkov.exchange.service;


import com.korotkov.exchange.dto.request.HouseReviewEditRequest;
import com.korotkov.exchange.dto.request.HouseReviewRequest;
import com.korotkov.exchange.model.*;
import com.korotkov.exchange.repository.HouseModerationRepository;
import com.korotkov.exchange.repository.HouseRepository;
import com.korotkov.exchange.repository.HouseReviewRepository;
import com.korotkov.exchange.repository.TradeRepository;
import com.korotkov.exchange.util.BadRequestException;
import com.korotkov.exchange.util.ImageMetaData;
import com.korotkov.exchange.util.UserHasNoRightsException;
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


    public List<House> findAllMyHouses() {
        return houseRepository.getAllByUserId(userService.getCurrentUser().getId());
    }

    public House findHouseById(int id) {
        return houseRepository.getReferenceById(id);
    }

    public List<String> findAllAvailableCities() {
        return houseRepository.findAllCities();
    }


    @Transactional
    public HouseModeration create(House house) {
        house.setUser(userService.getCurrentUser());
        return sendToModerator(house, house);
    }

    @Transactional
    public void save(House house) {
        houseRepository.save(house);
    }


    @Transactional
    public void edit(House temp, int id) {
        House house = houseRepository.getReferenceById(id);

        String city = temp.getCity();
        if (city != null) {
            house.setCity(city);
        }
        String address = temp.getAddress();
        if (address != null) {
            house.setAddress(address);
        }
        String description = temp.getCity();
        if (description != null) {
            house.setCity(description);
        }

        sendToModerator(temp, house);
    }


    @Transactional
    public HouseModeration sendToModerator(House temp, House house) {
        house.setStatus(HouseStatus.MODERATED);
        save(house);

        HouseModeration houseModeration = modelMapper.map(temp, HouseModeration.class);
        houseModeration.setHouse(house);
        return houseModerationRepository.save(houseModeration);
    }

    @Transactional
    public void delete(Integer id) {
        House house = houseRepository.getReferenceById(id);

        if (house.getUser().getId() == userService.getCurrentUser().getId()) {
            houseRepository.delete(house);
        } else {
            throw new BadRequestException("you can't delete rother users' houses");
        }
    }


    public List<House> findAllHouses(String city) {
        return houseRepository.getAllByCityIgnoreCaseStartingWith(city, userService.getCurrentUser().getId());
    }

    public List<House> findAllHouses(String city, Date sDate, Date eDate) {
        User currentUser = userService.getCurrentUser();
        return houseRepository.getAllCitiesWithCityAndDate(city, sDate, eDate, currentUser == null ? null : currentUser.getId());
    }

    public void addImages(MultipartFile[] files, int id) {
        for (MultipartFile file : files) {
            fileService.uploadFile(file, "house_images/" + id + "/" + file.getOriginalFilename());
        }
    }

    public List<ImageMetaData> findAllHouseImages(Integer houseId) {
        return fileService.getAllImages("house_images/" + houseId + "/");
    }

    @Transactional
    public void addReview(HouseReviewRequest request) {
        User currentUser = userService.getCurrentUser();
        HouseReview review = modelMapper.map(request, HouseReview.class);
        review.setHouse(houseRepository.getReferenceById(request.getHouseId()));
        if (currentUser.getId() != review.getHouse().getUser().getId()) {
            if (tradeRepository.findAllByUserAndHouse(review.getHouse().getId(), currentUser.getId()) != 0) {
                review.setAuthor(currentUser);
                reviewRepository.save(review);
                review.getHouse().getUser().addRating(request.getRating());
                review.getHouse().addRating(request.getRating());
            } else {
                throw new BadRequestException("you can't add review of house if you don't have a trade");
            }
        }
    }

    @Transactional
    public void editReview(HouseReviewEditRequest request) {
        User currentUser = userService.getCurrentUser();
        HouseReview review = reviewRepository.getReferenceById(request.getId());

        if (currentUser.getId() == review.getAuthor().getId()) {
            Integer rating = request.getRating();
            if (rating != null) {
                review.getHouse().getUser().editRating(rating - review.getRating());
                review.getHouse().editRating(rating - review.getRating());
            }
            String description = request.getDescription();
            if (description != null) {
                review.setDescription(description);
            }
            reviewRepository.save(review);
        } else {
            throw new BadRequestException("you can't edit other users' reviews");
        }
    }

    @Transactional
    public void deleteReview(int id) {
        User currentUser = userService.getCurrentUser();
        HouseReview review = reviewRepository.getReferenceById(id);
        if (currentUser.getRole().equals(UserRole.MODERATOR) || currentUser.getId() == review.getAuthor().getId()) {
            reviewRepository.delete(review);
        } else {
            throw new UserHasNoRightsException("you can't delete other users' reviews");
        }
    }

    public HouseReview getReview(int id) {
        return reviewRepository.getReferenceById(id);
    }

    public List<HouseReview> getAllUsersReviews(int userId) {
        return reviewRepository.findAllByAuthor_Id(userId);
    }

    public List<HouseReview> getAllReviewsAboutUser(int userId) {
        return reviewRepository.findAllUsersReviews(userId);
    }

    public List<HouseReview> findAllReviews(int id) {
        return reviewRepository.findAllByHouse_Id(id);
    }

    public InputStreamResource getImage(Integer id, String path) {
        return fileService.getFile(path);
    }

    public List<House> getUserHousesByUserId(int id) {
        return houseRepository.getHousesByUserId(id);
    }
}
