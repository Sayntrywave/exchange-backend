package com.korotkov.exchange.controller.rest;


import com.korotkov.exchange.dto.request.HouseRequest;
import com.korotkov.exchange.dto.request.TradeRequest;
import com.korotkov.exchange.dto.request.TradeStatusRequest;
import com.korotkov.exchange.dto.response.HouseResponse;
import com.korotkov.exchange.dto.response.TradeResponse;
import com.korotkov.exchange.dto.response.UserDtoResponse;
import com.korotkov.exchange.model.House;
import com.korotkov.exchange.service.HouseService;
import com.korotkov.exchange.service.TradeService;
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
public class HouseController {

    ModelMapper modelMapper;
    HouseService houseService;
    TradeService tradeService;



    @GetMapping("/houses/find")
    public ResponseEntity<List<HouseResponse>> getAllHouses(@RequestParam(name = "c") String city){
        return ResponseEntity.ok(getDtoListOfHouseResponses(houseService.findAllHousesByCity(city)));
    }

    @GetMapping("/houses")
    public ResponseEntity<List<HouseResponse>> getAllMyHouses(){
        return ResponseEntity.ok(getDtoListOfHouseResponses(houseService.findAllMyHouses()));
    }

    @GetMapping("/cities")
    public ResponseEntity<List<String>> getAllAvailableCities(){
        return ResponseEntity.ok(houseService.findAllAvailableCities());
    }

    @PostMapping("/houses")
    public void saveHouse(@RequestBody HouseRequest request) {
        houseService.save(modelMapper.map(request, House.class));
    }


    @GetMapping("/houses/{id}")
    public ResponseEntity<HouseResponse> getHouseById(@PathVariable Integer id){
        return ResponseEntity.ok(getHouseResponse(houseService.findHouseById(id)));
    }

    @PutMapping("/houses/{id}")
    public void editHouse(@PathVariable Integer id, @RequestBody HouseRequest request) {
        houseService.edit(modelMapper.map(request, House.class));
    }

    @DeleteMapping("/houses/{id}")
    public void deleteHouse(@PathVariable Integer id) {
        houseService.delete(id);
    }



    @PostMapping("/houses/trade")
    public void makeTrade(@RequestBody TradeRequest request){
        tradeService.save(request);
    }

    @PutMapping("/houses/trade")
    public void changeTradeStatus(@RequestBody TradeStatusRequest statusRequest){
        tradeService.changeStatus(statusRequest.getStatus(), statusRequest.getId());
    }

    @GetMapping("/houses/trades")
    public List<TradeResponse> getAllMyTrades(){
        return tradeService.findAllMyTrades().stream()
                .map(trade -> {
                    TradeResponse map = modelMapper.map(trade, TradeResponse.class);
                    map.setGivenHouse(getHouseResponse(trade.getGivenHouse()));
                    map.setReceivedHouse(getHouseResponse(trade.getReceivedHouse()));
                    return map;
                }).collect(Collectors.toList());
    }

    private HouseResponse getHouseResponse(House house){
        HouseResponse dto = modelMapper.map(house, HouseResponse.class);
        dto.setUser(modelMapper.map(house.getUser(),UserDtoResponse.class));
        return dto;
    }


    private List<HouseResponse> getDtoListOfHouseResponses(List<House> list){
        return list.stream()
                .map(this::getHouseResponse)
                .collect(Collectors.toList());
    }

}
