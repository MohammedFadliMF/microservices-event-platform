package com.net.eventservice;

import com.net.eventservice.entities.Event;
import com.net.eventservice.repository.EventRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.net.eventservice")
@EnableFeignClients
public class EventServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventServiceApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner commandLineRunner(EventRepository eventRepository) {
//		{
//			return args -> {
//				eventRepository.save(
//						Event.builder()
//								.title("Spring Boot Workshop")
//								.description("Learn the basics of Spring Boot")
//								.eventDate(java.time.LocalDate.of(2024, 7, 15))
//								.location("New York")
//								.capacity(100)
//								.organizerId(1L)
//								.build()
//				);
//				eventRepository.save(
//						Event.builder()
//								.title("Java Conference")
//								.description("Annual Java developers conference")
//								.eventDate(java.time.LocalDate.of(2024, 9, 10))
//								.location("San Francisco")
//								.capacity(500)
//								.organizerId(2L)
//								.build()
//				);
//			};
//		}
//	}

}
