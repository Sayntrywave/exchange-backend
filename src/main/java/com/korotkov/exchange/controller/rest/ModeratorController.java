package com.korotkov.exchange.controller.rest;


import com.korotkov.exchange.dto.request.ModeratorDecision;
import com.korotkov.exchange.dto.request.ReportDetails;
import com.korotkov.exchange.dto.request.ReportRequest;
import com.korotkov.exchange.dto.response.HouseModerationResponse;
import com.korotkov.exchange.dto.response.HouseResponse;
import com.korotkov.exchange.dto.response.ReportedUserResponse;
import com.korotkov.exchange.dto.response.UserDtoResponse;
import com.korotkov.exchange.service.ModeratorService;
import com.korotkov.exchange.service.UserService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@RequestMapping("/moderator")
public class ModeratorController {

    ModeratorService moderatorService;
    ModelMapper mapper;

    @GetMapping("/houses")
    public ResponseEntity<List<HouseModerationResponse>> getHouses(){
        List<HouseModerationResponse> listOfHouses = moderatorService.findAllHouses().stream()
                .map(houseModeration -> {
                    HouseModerationResponse map = mapper.map(houseModeration, HouseModerationResponse.class);
                    map.setHouse(mapper.map(houseModeration.getHouse(), HouseResponse.class));
//                    map.setUser(mapper.map(houseModeration.getUser(), UserDtoResponse.class));
                    return map;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(listOfHouses);
    }

    @PutMapping("/house")
    public void setDecision(@RequestBody ModeratorDecision decision){
        moderatorService.moderate(decision);
    }

    @PutMapping("/ban")
    public ResponseEntity<String> banUser(@RequestBody ReportDetails reportDetails){
        moderatorService.banUser(reportDetails);
        return ResponseEntity.ok("banned!");
    }

    @PutMapping("/reject")
    public ResponseEntity<String> rejectReport(@RequestBody ReportDetails reportDetails){
        moderatorService.rejectRequest(reportDetails);
        return ResponseEntity.ok("Rejected!");
    }


    @GetMapping("/reported-users")
    public ResponseEntity<List<ReportedUserResponse>> getReportedUsers(){
        return ResponseEntity.ok(moderatorService.findAllReportedUsers().stream()
                .map(reportedUser -> {
                    ReportedUserResponse map = mapper.map(reportedUser, ReportedUserResponse.class);
                    map.setReporter(mapper.map(reportedUser.getReporter(), UserDtoResponse.class));
                    map.setReportedUser(mapper.map(reportedUser.getReportedUser(), UserDtoResponse.class));
                    return map;
                }).collect(Collectors.toList()));
    }
}
