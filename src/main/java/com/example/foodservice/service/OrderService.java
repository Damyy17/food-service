package com.example.foodservice.service;

import com.example.foodservice.entity.Order;
import com.example.foodservice.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class OrderService {

    final static int NR_OF_THREADS = 5;
    static ReentrantLock mutex = new ReentrantLock();

    @Autowired
    private DataRepository dataRepository;

    public static void sendOrderToDH(Order order) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "http://127.0.0.1:7001/distribution";
        URI uri = new URI(baseUrl);

        try {
            restTemplate.postForEntity(uri, order, Order.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        }
    }

    public static void sendOrderToKitchen(Order order) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "http://127.0.0.1:7002/order";
        URI uri = new URI(baseUrl);

        try {
            restTemplate.postForEntity(uri, order, Order.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        }
    }

    public void addOrderToDH(Order order) throws InterruptedException {
        dataRepository.addOrderFromDinningHall(order);
    }

    public void addOrderToKitchen(Order order) throws InterruptedException {
        dataRepository.addOrderFromKitchen(order);
    }

    public static Order takeOrderFromDH() throws InterruptedException {
        return DataRepository.takeOrderFromDH();
    }

    public static Order takeOrderFromKitchen() throws InterruptedException {
        return DataRepository.takeDataFromKitchen();
    }

    public static void sendOrders(){
        while (true){
            mutex.lock();
            Order dhOrder, kitchenOrder;
            try {
                kitchenOrder = takeOrderFromDH();
                sendOrderToKitchen(kitchenOrder);
                dhOrder = takeOrderFromKitchen();
                sendOrderToDH(dhOrder);
            } catch (InterruptedException | URISyntaxException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mutex.unlock();
        }
    }

    public static void runServer(){
        for (int i = 0; i < NR_OF_THREADS; i++) {
            Thread orderSender = new Thread(OrderService::sendOrders);
            orderSender.start();
        }
    }

}
