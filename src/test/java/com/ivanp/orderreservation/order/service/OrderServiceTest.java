package com.ivanp.orderreservation.order.service;

import com.github.dockerjava.api.exception.NotFoundException;
import com.ivanp.orderreservation.order.entity.Order;
import com.ivanp.orderreservation.order.entity.OrderStatus;
import com.ivanp.orderreservation.order.repository.OrderRepository;
import com.ivanp.orderreservation.order.request.CreateOrderItemRequest;
import com.ivanp.orderreservation.order.request.CreateOrderRequest;
import com.ivanp.orderreservation.order.response.OrderResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    void getAllOrders_whenNoOrders_returnsEmptyList() {

        // Arrange
        when(orderRepository.findAll())
                .thenReturn(Collections.emptyList());

        // Act
        List<OrderResponse> response = orderService.getAllOrders();

        // Assert
        assertTrue(response.isEmpty());
        verify(orderRepository).findAll();
    }

    @Test
    void createOrder_whenValidRequest_savesOrderWithItems() {

        // Arrange
        CreateOrderRequest request = new CreateOrderRequest(
                List.of(
                        new CreateOrderItemRequest(101L, 2),
                        new CreateOrderItemRequest(205L, 1)
                )
        );

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setOrderId(42L);
                    return order;
                });

        // Act

        OrderResponse response = orderService.createOrder(request);

        ArgumentCaptor<Order> captor =
        ArgumentCaptor.forClass(Order.class);

        verify(orderRepository).save(captor.capture());

        Order savedOrder = captor.getValue();

        // Assert

        assertEquals(OrderStatus.PENDING, savedOrder.getStatus());
        assertNotNull(savedOrder.getCreatedAt());
        assertNotNull(savedOrder.getUpdatedAt());

        assertEquals(2, savedOrder.getItems().size());

        assertEquals(101L, savedOrder.getItems().get(0).getProductId());
        assertEquals(2, savedOrder.getItems().get(0).getQuantity());

        assertSame(savedOrder, savedOrder.getItems().get(0).getOrder());

        assertEquals(42L, response.id());
        assertEquals(2, response.items().size());

    }



}
