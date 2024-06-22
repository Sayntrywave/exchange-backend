package com.korotkov.exchange.service;


import com.korotkov.exchange.dto.request.TradeRequest;
import com.korotkov.exchange.model.House;
import com.korotkov.exchange.model.Trade;
import com.korotkov.exchange.model.TradeStatus;
import com.korotkov.exchange.repository.TradeRepository;
import com.korotkov.exchange.util.BadRequestException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.boot.Banner;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class TradeService {

    TradeRepository tradeRepository;
    ModelMapper modelMapper;
    HouseService houseService;
    UserService userService;


    @Transactional
    public void save(TradeRequest request){
        Trade trade = modelMapper.map(request, Trade.class);


        trade.setGivenHouse(houseService.findHouseById(request.getGivenHouseId()));

        if(trade.getGivenHouse().getUser().getId() != userService.getCurrentUser().getId()){
            throw new BadRequestException("you can't trade not your houses");
        }


        trade.setReceivedHouse(houseService.findHouseById(request.getReceivedHouseId()));

        if (tradeRepository.isTradePossible(request.getGivenHouseId(), request.getReceivedHouseId(), request.getStartDate(), request.getEndDate()) == 0) {
            trade.setStatus(TradeStatus.PENDING);
            tradeRepository.save(trade);
        }
        else {
            throw new BadRequestException(trade + "невозможен, этот дом уже сдается в эти сроки");
        }
    }

    @Transactional
    public void changeStatus(TradeStatus status, int tradeId){
        Trade trade = tradeRepository.getReferenceById(tradeId);
        if(trade.getReceivedHouse().getUser().getId() == userService.getCurrentUser().getId()){
            if(status.equals(TradeStatus.REJECTED)){
                if(trade.getStatus().equals(TradeStatus.COMPLETED)){
                    throw new BadRequestException("Нельзя отменить совершенную сделку");
                }
            }
            trade.setStatus(status);
        }
        else {
            throw new BadRequestException("Нельзя изменить сделку других пользователей");
        }
    }

    public boolean haveTrade(int userId, House house){
        return tradeRepository.findAllByUserAndHouse(userId, house.getId()) != 0;
    }


    public List<Trade> findAllMyTrades() {
        return tradeRepository.findAllMyTrades(userService.getCurrentUser().getId());
    }
}
