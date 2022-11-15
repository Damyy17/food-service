package com.example.foodservice.entity;

import com.example.foodservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;

public class SenderDH extends Thread{
    String name;

    @Autowired
    private OrderService orderService;


    public SenderDH(String name){
        this.name = name;
    }

    @Override
    public void run() {
        System.out.println("Started thread " + name);
        OrderService.sendToDinningHall();
    }
}
