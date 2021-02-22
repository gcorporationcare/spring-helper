package com.gcorp.notest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.gcorp.config.ApiConfig;

@Configuration
@EnableTransactionManagement
public class H2Config extends ApiConfig {

}
