package com.ivanp.orderreservation.order.service;

import com.github.dockerjava.api.exception.NotFoundException;
import com.ivanp.orderreservation.order.entity.Order;
import com.ivanp.orderreservation.order.entity.OrderStatus;
import com.ivanp.orderreservation.order.repository.OrderRepository;
import com.ivanp.orderreservation.order.response.OrderResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void getOrderById_whenOrderExists_returnsOrderResponse() {

        // Arrange
        Long orderId = 1L;

        Order order = new Order();
        order.setOrderId(orderId);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        // Act
        OrderResponse response = orderService.getOrderById(orderId);

        // Assert
        assertEquals(orderId, response.id());
        assertEquals(OrderStatus.PENDING, response.status());

        verify(orderRepository).findById(orderId);

    }

    @Test
    void getOrderById_whenOrderDoesNotExist_throwsNotFound() {

        // Arrange
        Long orderId = 1L;

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        // Act
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> orderService.getOrderById(orderId)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(orderRepository).findById(orderId);

    }

    @Test
    void getAllOrders_whenOrdersExist_returnsResponses() {
        // Arrange

        Order order1 = new Order();
        Order order2 = new Order();
        order1.setOrderId(1L);
        order1.setStatus(OrderStatus.PENDING);
        order2.setOrderId(2L);
        order2.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findAll())
                .thenReturn(List.of(order1, order2));

        // Act
        List<OrderResponse> response = orderService.getAllOrders();

        // Assert
        assertEquals(2, response.size());

        assertEquals(1L, response.get(0).id());
        assertEquals(OrderStatus.PENDING, response.get(0).status());

        assertEquals(2L, response.get(1).id());
        assertEquals(OrderStatus.CONFIRMED, response.get(1).status());

        verify(orderRepository).findAll();


    }

}
