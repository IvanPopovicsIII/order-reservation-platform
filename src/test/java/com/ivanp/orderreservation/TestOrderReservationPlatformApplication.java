package com.ivanp.orderreservation;

import org.springframework.boot.SpringApplication;

public class TestOrderReservationPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.from(OrderReservationPlatformApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
