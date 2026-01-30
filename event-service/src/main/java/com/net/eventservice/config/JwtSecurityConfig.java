package com.net.eventservice.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration personnalisée pour la validation JWT avec Keycloak.
 * Cela permet de gérer les cas où l'issuer-uri n'est pas immédiatement
 * disponible au démarrage.
 */
@Configuration
public class JwtSecurityConfig {

    /**
     * Crée un JwtDecoder personnalisé pour accepter les tokens JWT de Keycloak
     * Utilise le JWK Set URI pour valider les signatures
     */
    @Bean
    public JwtDecoder jwtDecoder(RestTemplate restTemplate) {
        String jwkSetUri = System.getenv("KEYCLOAK_JWK_SET_URI");
        if (jwkSetUri == null || jwkSetUri.isEmpty()) {
            jwkSetUri = "http://localhost:8080/realms/event-platform-realm/protocol/openid-connect/certs";
        }

        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        // No need to set validator - NimbusJwtDecoder validates by default
        // including signature validation using the JWK Set

        return jwtDecoder;
    }

    /**
     * RestTemplate pour les appels aux endpoints Keycloak
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
