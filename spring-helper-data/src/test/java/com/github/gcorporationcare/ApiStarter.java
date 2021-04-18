package com.github.gcorporationcare;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import lombok.NoArgsConstructor;


@SpringBootApplication
@NoArgsConstructor
public class ApiStarter {
	public static final String HANDLER = "handler";
	public static final String HIBERNATE_LAZY_INITIALIZER = "hibernateLazyInitializer";

	public static void main(String[] args) {
		new SpringApplicationBuilder().sources(ApiStarter.class).build().run(args);
	}
}
