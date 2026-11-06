package com.ivanp.orderreservation.order.controller;

import com.ivanp.orderreservation.order.entity.OrderStatus;
import com.ivanp.orderreservation.order.request.CreateOrderItemRequest;
import com.ivanp.orderreservation.order.request.CreateOrderRequest;
import com.ivanp.orderreservation.order.response.OrderItemResponse;
import com.ivanp.orderreservation.order.response.OrderResponse;
import com.ivanp.orderreservation.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import javax.print.attribute.standard.Media;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void getAllOrders_whenOrdersExist_returns200() throws Exception {

        // Arrange
        OrderResponse order = new OrderResponse(
                1L,
                OrderStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now(),
                List.of()
        );

        when(orderService.getAllOrders())
                .thenReturn(List.of(order));

        // Act & Assert
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void getOrderById_whenOrderDoesNotExist_returns404() throws Exception {
        // Arrange

        when(orderService.getOrderById(999L))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Order not found: 999"
                ));

        // Act & Assert
        mockMvc.perform(get("/orders/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("@.status").value(404))
                .andExpect(jsonPath("@.message").value(containsString("Order not found: 999") ));

        verify(orderService).getOrderById(999L);
    }

    @Test
    void createOrder_whenValidRequest_returns201() throws Exception {

        // Arrange
        String requestJson = """
                {
                  "items": [
                    {"productId": 101, "quantity": 2}
                  ]
                }
                """;

        OrderResponse response = new OrderResponse(
                42L,
                OrderStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now(),
                List.of(new OrderItemResponse(101L, 2))
        );

        when(orderService.createOrder(any(CreateOrderRequest.class)))
                .thenReturn(response);

        // Act & Assert

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.items[0].productId").value(101))
                .andExpect(jsonPath("$.items[0].quantity").value(2));

        verify(orderService).createOrder(
                new CreateOrderRequest(
                        List.of(new CreateOrderItemRequest(101L, 2))
                )
        );
    }

    @Test
    void createOrder_whenQuantityIsNegative_returns400() throws Exception{

        // Arrange
        String requestJson = """
                {
                  "items": [
                    {"productId": 101, "quantity": -2}
                  ]
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(orderService);

    }

    @Test
    void createOrder_whenMalformedJson_returns400() throws Exception {

        // Arrange
        String requestJson = """
                {
                  "items": [
                    {"productId": 101, "quantity": 2,}
                  ]
                }
                """;

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(orderService);

    }

}
