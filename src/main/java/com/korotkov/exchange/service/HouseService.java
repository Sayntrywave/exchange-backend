package com.korotkov.exchange.service;


import com.korotkov.exchange.model.House;
import com.korotkov.exchange.repository.HouseRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class HouseService {

    HouseRepository houseRepository;
    UserService userService;



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
    public void save(House house){
        house.setUser(userService.getCurrentUser());
        houseRepository.save(house);
    }

    @Transactional
    public void edit(House house){
        save(house);
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

    public List<House> findAllHousesByCity(String city) {
        return houseRepository.getAllByCityIgnoreCaseStartingWith(city);
    }
}
