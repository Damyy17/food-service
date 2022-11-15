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

    @Autowired
    private DataRepository dataRepository;

    final static int NR_OF_THREADS = 5;
    static ReentrantLock mutex = new ReentrantLock();


    public static void sendOrderToDH(Order order) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "http://127.0.0.1:7001/distribution";
        URI uri = new URI(baseUrl);

        try {
            restTemplate.postForEntity(uri, order, Order.class);
        } catch (RestClientException e){
            e.printStackTrace();
        }
    }

    public static void sendOrderToKitchen(Order order) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "http://127.0.0.1:7002/order";
        URI uri = new URI(baseUrl);

        try {
            restTemplate.postForEntity(uri, order, Order.class);
        } catch (RestClientException e){
            e.printStackTrace();
        }
    }

    public void addOrderToDH(Order order){
        dataRepository.addOrderFromDinningHall(order);
    }

    public void addOrderToKitchen(Order order){
        dataRepository.addOrderFromKitchen(order);
    }

    public static Order takeOrderFromDH() throws InterruptedException {
        return DataRepository.takeOrderFromDH();
    }

    public static Order takeOrderFromKitchen() throws InterruptedException {
        return DataRepository.takeDataFromKitchen();
    }

    public static boolean checkIfEmptyDH(){
        return DataRepository.checkIfEmptyDH();
    }

    public static boolean checkIfEmptyKitchen(){
        return DataRepository.checkIfEmptyKitchen();
    }

    public static void sendToDinningHall(){
        while(true){
            mutex.lock();
            if (!checkIfEmptyKitchen()) {
                Order order;
                try {
                    order = takeOrderFromKitchen();
                    sendOrderToDH(order);
                } catch (URISyntaxException | InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mutex.unlock();
        }
    }

    public static void sendToKitchen(){
        while(true){
            mutex.lock();
            if (!checkIfEmptyDH()) {
                Order order;
                try {
                    order = takeOrderFromDH();
                    sendOrderToKitchen(order);
                } catch (URISyntaxException | InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mutex.unlock();
        }
    }

    public static void runServer(){
        for (int i = 1; i <= NR_OF_THREADS; i++) {
            new Thread(OrderService::sendToKitchen).start();
        }
        for (int i = 1; i <= NR_OF_THREADS; i++) {
            new Thread(OrderService::sendToDinningHall).start();
        }
    }

}
