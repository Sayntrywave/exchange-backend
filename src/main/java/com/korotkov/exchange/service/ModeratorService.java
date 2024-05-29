package com.korotkov.exchange.service;


import com.korotkov.exchange.dto.request.ModeratorDecision;
import com.korotkov.exchange.model.House;
import com.korotkov.exchange.model.HouseModeration;
import com.korotkov.exchange.model.HouseStatus;
import com.korotkov.exchange.repository.HouseModerationRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class ModeratorService {

    HouseModerationRepository houseModerationRepository;
    HouseService houseService;

    @Transactional
    public void moderate(ModeratorDecision decision){
        HouseModeration houseModeration = houseModerationRepository.getReferenceById(decision.getId());

        houseModeration.setDecision(decision.getDecision());
        houseModeration.setIsApproved(decision.getIsApproved());

        houseModerationRepository.save(houseModeration);
        if(decision.getIsApproved()){
            saveHouse(houseModeration);
        }

    }

    @Transactional
    public void saveHouse(HouseModeration houseModeration){
        House house = houseModeration.getHouse();

        String city = houseModeration.getCity();
        if(city != null){
            house.setCity(city);
        }
        String address = houseModeration.getAddress();
        if(address != null){
            house.setAddress(address);
        }
        String description = houseModeration.getDescription();
        if(description != null){
            house.setDescription(description);
        }
        house.setStatus(HouseStatus.UP_FOR_SALE);

        houseService.save(house);
    }
    public List<HouseModeration> getListOfHouses(){
        return houseModerationRepository.findAllByIsApprovedIsNull();
    }

}
