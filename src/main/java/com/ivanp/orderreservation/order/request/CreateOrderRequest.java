package com.ivanp.orderreservation.order.request;

import com.ivanp.orderreservation.order.entity.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.List;

public record CreateOrderRequest(
        @NotEmpty
        @Valid
        List<CreateOrderItemRequest> items
) {

}


