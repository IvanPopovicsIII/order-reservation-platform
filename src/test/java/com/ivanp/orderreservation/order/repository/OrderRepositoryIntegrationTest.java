package com.ivanp.orderreservation.order.repository;

import com.ivanp.orderreservation.order.entity.Order;
import com.ivanp.orderreservation.order.entity.OrderItem;
import com.ivanp.orderreservation.order.entity.OrderStatus;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
public class OrderRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:18");

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EntityManager entityManager;


    @Test
    void saveOrder_withItems_persistsEverything() {

        // Arrange
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        order.addItem(new OrderItem(101L, 2));

        // Act
        Order saved = orderRepository.saveAndFlush(order);
        Long savedId = saved.getOrderId();

        entityManager.clear();

        Order loaded = orderRepository.findById(savedId)
                .orElseThrow();

        // Assert
        assertEquals(OrderStatus.PENDING, loaded.getStatus());
        assertEquals(1, loaded.getItems().size());
        assertEquals(101L, loaded.getItems().get(0).getProductId());
        assertEquals(2, loaded.getItems().get(0).getQuantity());
        assertSame(loaded, loaded.getItems().get(0).getOrder());
    }
}
