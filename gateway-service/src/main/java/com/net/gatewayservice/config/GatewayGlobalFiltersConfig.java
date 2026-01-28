package com.net.gatewayservice.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.TokenRelayGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayGlobalFiltersConfig {

    @Bean
    public GlobalFilter tokenRelayGlobalFilter(TokenRelayGatewayFilterFactory factory) {
        GatewayFilter gatewayFilter = factory.apply();

        return (exchange, chain) ->
                gatewayFilter.filter(exchange, chain);
    }
}
