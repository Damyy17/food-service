package com.example.foodservice.repository;

import com.example.foodservice.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

@Repository
public class DataRepository {

    private static final BlockingQueue<Order> dataFromDH = new LinkedBlockingDeque<>();
    private static final BlockingQueue<Order> dataFromKitchen = new LinkedBlockingDeque<>();

    public void addOrderFromDinningHall(Order order) throws InterruptedException {
        dataFromDH.put(order);
    }

    public void addOrderFromKitchen(Order order) throws InterruptedException {
        dataFromKitchen.put(order);
    }

    public static Order takeOrderFromDH() throws InterruptedException {
        return dataFromDH.take();
    }

    public static Order takeDataFromKitchen() throws InterruptedException {
        return dataFromKitchen.take();
    }

}
