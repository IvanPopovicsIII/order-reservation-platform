package com.ivanp.orderreservation.order.service;

import com.ivanp.orderreservation.order.entity.Order;
import com.ivanp.orderreservation.order.entity.OrderItem;
import com.ivanp.orderreservation.order.entity.OrderStatus;
import com.ivanp.orderreservation.order.repository.OrderRepository;
import com.ivanp.orderreservation.order.request.CreateOrderItemRequest;
import com.ivanp.orderreservation.order.request.CreateOrderRequest;
import com.ivanp.orderreservation.order.response.OrderItemResponse;
import com.ivanp.orderreservation.order.response.OrderResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public OrderResponse getOrderById(Long id) {

        return this.toResponse(orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Order not found : " + id
                )));
    }

    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        for (CreateOrderItemRequest item : request.items()) {
            order.addItem(new OrderItem(item.productId(), item.quantity()));
        }

        Order saved = orderRepository.save(order);
        return toResponse(saved);

    }

    private OrderResponse toResponse(Order order) {

        OrderResponse response = new OrderResponse(
                order.getOrderId(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                toItemResponses(order.getItems())
        );

        return response;
    }

    private List<OrderItemResponse> toItemResponses(List<OrderItem> items) {
        return items.stream()
                .map(item -> new OrderItemResponse(
                        item.getProductId(),
                        item.getQuantity()
                ))
                .toList();
    }

}
