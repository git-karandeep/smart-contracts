package com.generalcontracts.startup.controller;

import com.generalcontracts.startup.model.Orders;
import com.generalcontracts.startup.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping
    public Orders create(@RequestBody Orders order) {
        return service.save(order);
    }

    @GetMapping
    public List<Orders> getAll() {
        return service.getAll();
    }
}
