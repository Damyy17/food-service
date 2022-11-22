package com.example.foodservice.controller;

import com.example.foodservice.entity.Order;
import com.example.foodservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping(path = "/aggregator1", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Order> receiveOrderFromDinningHall(@RequestBody Order order) throws InterruptedException {
        System.out.println(order.toString());
        //add order to data structure
        orderService.addOrderToDH(order);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "aggregator2", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Order> receiveOrderFromKitchen(@RequestBody Order order) throws InterruptedException {
        System.out.println(order.toString());
        //add order to data structure
        orderService.addOrderToKitchen(order);

        return new ResponseEntity<>(HttpStatus.OK);
    }


}
