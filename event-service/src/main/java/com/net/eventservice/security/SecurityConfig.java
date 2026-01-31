package com.net.eventservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
        private JwtAuthConverter jwtAuthConverter;

        public SecurityConfig(JwtAuthConverter jwtAuthConverter) {
                this.jwtAuthConverter = jwtAuthConverter;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
                return httpSecurity
                                .csrf(csrf -> csrf.disable())
                                .cors(Customizer.withDefaults())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(ar -> ar
<<<<<<< HEAD
=======
                                                .requestMatchers("/api/events/auth").permitAll()
>>>>>>> dev
                                                .requestMatchers("/actuator/**").permitAll()
                                                .requestMatchers("/actuator/health/**").permitAll()
                                                .requestMatchers(
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/v3/api-docs/**",
                                                                "/v3/api-docs")
                                                .permitAll()
                                                // Allow GET requests to /api/events without authentication
                                                .requestMatchers("GET", "/api/events", "/api/events/**").permitAll()
                                                // Allow GET requests to /api/v1/events without authentication
                                                .requestMatchers("GET", "/api/v1/events", "/api/v1/events/**")
                                                .permitAll()
                                                // All other requests require authentication
                                                .anyRequest().authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)))
                                .build();
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration corsConfig = new CorsConfiguration();
                corsConfig.setAllowedOrigins(Arrays.asList("*"));
                corsConfig.setAllowedMethods(Arrays.asList("*"));
                corsConfig.setAllowedHeaders(Arrays.asList("*"));
                corsConfig.setExposedHeaders(Arrays.asList("*"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", corsConfig);
                return source;
        }
}
