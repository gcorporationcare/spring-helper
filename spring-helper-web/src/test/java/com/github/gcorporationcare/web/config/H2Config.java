package com.github.gcorporationcare.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.github.gcorporationcare.web.config.ApiConfig;

@Configuration
@EnableTransactionManagement
public class H2Config extends ApiConfig {

}
