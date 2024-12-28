package com.example.bank_application;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Locale;

@SpringBootApplication

@OpenAPIDefinition(
		info = @Info(
				title = "The JAVA Api",
				description = "Back end API for ABC Bank",
				version = "v1.0",
				contact = @Contact(
						name = "abc",
						email = "anc@gmail.com",
						url = "www.gmail.com"
				),
				license = @License(
						name = "Name",
						url = "www.gmail.com"
				)
		),
		externalDocs = @ExternalDocumentation(
				description = "ABC",
				url = "www.gmail.com"
		)
)
public class BankApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankApplication.class, args);
	}

}
