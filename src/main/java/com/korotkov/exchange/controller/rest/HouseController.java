package com.korotkov.exchange.controller.rest;


import com.korotkov.exchange.dto.request.*;
import com.korotkov.exchange.dto.response.HouseResponse;
import com.korotkov.exchange.dto.response.HouseReviewResponse;
import com.korotkov.exchange.dto.response.TradeResponse;
import com.korotkov.exchange.dto.response.UserDtoResponse;
import com.korotkov.exchange.model.House;
import com.korotkov.exchange.model.HouseReview;
import com.korotkov.exchange.service.FileService;
import com.korotkov.exchange.service.HouseService;
import com.korotkov.exchange.service.TradeService;
import com.korotkov.exchange.util.ImageMetaData;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
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
    public ResponseEntity<List<HouseResponse>> getAllHouses(@RequestParam(name = "c") String city,
                                                            @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern="yyyy-MM-dd")Date sDate,
                                                            @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern="yyyy-MM-dd")Date eDate){
        if(eDate == null || sDate == null){
            return ResponseEntity.ok(getDtoListOfHouseResponses(houseService.findAllHouses(city)));
        }
        return ResponseEntity.ok(getDtoListOfHouseResponses(houseService.findAllHouses(city, sDate, eDate)));
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
        houseService.create(modelMapper.map(request, House.class));
    }


    @GetMapping("/houses/{id}")
    public ResponseEntity<HouseResponse> getHouseById(@PathVariable Integer id){
        return ResponseEntity.ok(getHouseResponse(houseService.findHouseById(id)));
    }

    @PutMapping("/houses/{id}")
    public void editHouse(@PathVariable Integer id, @RequestBody HouseRequest request) {
        houseService.edit(modelMapper.map(request, House.class), id);
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

    @GetMapping(value = "/houses/{id}/images")
    public List<ImageMetaData> getAllHouseImages(@PathVariable("id") Integer id){

        return houseService.findAllHouseImages(id);
    }

    @GetMapping(value = "/houses/{id}/image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<InputStreamResource> getHouseImage(@PathVariable("id") Integer id, @RequestParam("path") String path){

        return ResponseEntity.ok(houseService.getImage(id, path));
    }



    @PostMapping(value = "/houses/{id}/images")
    public void addHouseImages(@PathVariable("id") Integer id, @RequestPart("files") MultipartFile[] files){
        houseService.addImages(files, id);
    }

    @GetMapping("/user/trades")
    public List<TradeResponse> getAllMyTrades(){
        return tradeService.findAllMyTrades().stream()
                .map(trade -> {
                    TradeResponse map = modelMapper.map(trade, TradeResponse.class);
                    map.setGivenHouse(getHouseResponse(trade.getGivenHouse()));
                    map.setReceivedHouse(getHouseResponse(trade.getReceivedHouse()));
                    return map;
                }).collect(Collectors.toList());
    }

    @PostMapping("/houses/review")
    public void addReview(@RequestBody HouseReviewRequest houseReviewRequest){
        houseService.addReview(houseReviewRequest);
    }

    @GetMapping("/houses/{id}/reviews")
    public ResponseEntity<List<HouseReviewResponse>> getReviews(@PathVariable int id){
        return ResponseEntity.ok(houseService.findAllReviews(id).stream()
                .map(houseReview -> {
                    HouseReviewResponse map = modelMapper.map(houseReview, HouseReviewResponse.class);
                    map.setUserDtoResponse(modelMapper.map(houseReview.getAuthor(), UserDtoResponse.class));
                    return map;
                })
                .collect(Collectors.toList()));
    }
    @PutMapping("/houses/review")
    public ResponseEntity<HttpStatus> editReview(@RequestBody HouseReviewEditRequest editRequest){

        houseService.editReview(editRequest);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/houses/reviews/{id}")
    public ResponseEntity<HttpStatus> deleteReview(@PathVariable int id){

        houseService.deleteReview(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/houses/reviews/{id}")
    public ResponseEntity<HouseReviewResponse> getReview(@PathVariable int id){
        HouseReview review = houseService.getReview(id);
        HouseReviewResponse map = modelMapper.map(review, HouseReviewResponse.class);
        map.setUserDtoResponse(modelMapper.map(review.getAuthor(), UserDtoResponse.class));
        return ResponseEntity.ok(map);
    }

    @GetMapping("/users/{id}/reviews")
    public ResponseEntity<List<HouseReviewResponse>> getUserReviews(@PathVariable int id){
        return ResponseEntity.ok(houseService.getAllUsersReviews(id).stream()
                .map(houseReview -> {
                    HouseReviewResponse map = modelMapper.map(houseReview, HouseReviewResponse.class);
                    map.setUserDtoResponse(modelMapper.map(houseReview.getAuthor(), UserDtoResponse.class));
                    return map;
                }
        ).collect(Collectors.toList()));
    }

    @GetMapping("/users/{id}/houses")
    public ResponseEntity<List<HouseResponse>> getUserHouses(@PathVariable int id){
        return ResponseEntity.ok(getDtoListOfHouseResponses(houseService.getUserHousesByUserId(id)));
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
