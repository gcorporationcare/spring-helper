package com.github.gcorporationcare.notest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.github.gcorporationcare.data.config.DataConfig;

@Configuration
@EnableTransactionManagement
public class H2Config extends DataConfig {

}
