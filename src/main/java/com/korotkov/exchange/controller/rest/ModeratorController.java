package com.korotkov.exchange.controller.rest;


import com.korotkov.exchange.dto.request.ModeratorDecision;
import com.korotkov.exchange.dto.response.HouseModerationResponse;
import com.korotkov.exchange.dto.response.HouseResponse;
import com.korotkov.exchange.dto.response.UserDtoResponse;
import com.korotkov.exchange.model.HouseModeration;
import com.korotkov.exchange.service.ModeratorService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class ModeratorController {

    ModeratorService moderatorService;
    ModelMapper mapper;

    @GetMapping("/moderator/houses")
    public ResponseEntity<List<HouseModerationResponse>> getHouses(){
        List<HouseModerationResponse> listOfHouses = moderatorService.getListOfHouses().stream()
                .map(houseModeration -> {
                    HouseModerationResponse map = mapper.map(houseModeration, HouseModerationResponse.class);
                    map.setHouse(mapper.map(houseModeration.getHouse(), HouseResponse.class));
//                    map.setUser(mapper.map(houseModeration.getUser(), UserDtoResponse.class));
                    return map;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(listOfHouses);
    }

    @PutMapping("/moderator/house")
    public void setDecision(@RequestBody ModeratorDecision decision){
        moderatorService.moderate(decision);
    }
}
