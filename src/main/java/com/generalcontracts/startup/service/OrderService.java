package com.generalcontracts.startup.service;

import com.generalcontracts.startup.model.Orders;
import com.generalcontracts.startup.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrdersRepository repository;

    public Orders save(Orders order) {
        return repository.save(order);
    }

    public List<Orders> getAll() {
        return repository.findAll();
    }
}