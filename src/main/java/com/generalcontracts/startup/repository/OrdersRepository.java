package com.generalcontracts.startup.repository;


import com.generalcontracts.startup.model.Orders;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrdersRepository extends MongoRepository<Orders, String> {
}