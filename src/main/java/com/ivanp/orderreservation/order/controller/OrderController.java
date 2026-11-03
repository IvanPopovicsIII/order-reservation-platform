package com.ivanp.orderreservation.order.controller;


import com.ivanp.orderreservation.order.entity.Order;
import com.ivanp.orderreservation.order.request.CreateOrderRequest;
import com.ivanp.orderreservation.order.response.OrderResponse;
import com.ivanp.orderreservation.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {

        this.orderService = orderService;

    }

    @GetMapping
    public List<OrderResponse> getAllOrders() {

        return orderService.getAllOrders();

    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest createOrderRequest) {

        OrderResponse response = orderService.createOrder( createOrderRequest );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@Valid @PathVariable Long id){

        return orderService.getOrderById(id);
    }

}
