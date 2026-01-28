package com.net.gatewayservice.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.RemoveRequestHeaderGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewaySecurityFiltersConfig {

    @Bean
    public GlobalFilter removeCookieHeader(RemoveRequestHeaderGatewayFilterFactory factory) {
        GatewayFilter gatewayFilter =
                factory.apply(config -> config.setName("Cookie"));

        return (exchange, chain) ->
                gatewayFilter.filter(exchange, chain);
    }
}
