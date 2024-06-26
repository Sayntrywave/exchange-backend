package com.korotkov.exchange.service;


import com.korotkov.exchange.dto.request.ModeratorDecision;
import com.korotkov.exchange.dto.request.ReportDetails;
import com.korotkov.exchange.model.*;
import com.korotkov.exchange.repository.HouseModerationRepository;
import com.korotkov.exchange.repository.ReportedUserRepository;
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
    ReportedUserRepository reportedUserRepository;
    HouseService houseService;
    UserService userService;

    @Transactional
    public void moderate(ModeratorDecision decision) {
        HouseModeration houseModeration = houseModerationRepository.getReferenceById(decision.getId());

        houseModeration.setDecision(decision.getDecision());
        houseModeration.setIsApproved(decision.getIsApproved());

        houseModerationRepository.save(houseModeration);
        if (decision.getIsApproved()) {
            saveHouse(houseModeration);
        }

    }

    @Transactional
    public void saveHouse(HouseModeration houseModeration) {
        House house = houseModeration.getHouse();

        String city = houseModeration.getCity();
        if (city != null) {
            house.setCity(city);
        }
        String address = houseModeration.getAddress();
        if (address != null) {
            house.setAddress(address);
        }
        String description = houseModeration.getDescription();
        if (description != null) {
            house.setDescription(description);
        }
        house.setStatus(HouseStatus.UP_FOR_SALE);

        houseService.save(house);
    }

    public List<HouseModeration> findAllHouses() {
        return houseModerationRepository.findAllByIsApprovedIsNull();
    }

    public List<ReportedUser> findAllReportedUsers() {
        return reportedUserRepository.findAllByIsRejectedNull();
    }

    @Transactional
    public void banUser(ReportDetails reportDetails) {
        User user = userService.getById(reportDetails.getUserId());
        user.setIsInBan(true);
        userService.save(user);

        ReportedUser reportedUser = reportedUserRepository.getReferenceById(reportDetails.getId());
        reportedUser.setIsRejected(false);
        reportedUserRepository.save(reportedUser);
    }

    public void rejectRequest(ReportDetails reportDetails) {
        ReportedUser reportedUser = reportedUserRepository.getReferenceById(reportDetails.getId());
        reportedUser.setIsRejected(true);
        reportedUserRepository.save(reportedUser);
    }
}
