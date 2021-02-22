package com.gcorp;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import lombok.NoArgsConstructor;

@EnableJpaAuditing
@SpringBootApplication
@NoArgsConstructor
public class ApiStarter {
	public static void main(String[] args) {
		new SpringApplicationBuilder().sources(ApiStarter.class).build().run(args);
	}
}
