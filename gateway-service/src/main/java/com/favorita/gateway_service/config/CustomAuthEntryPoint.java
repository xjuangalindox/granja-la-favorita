package com.favorita.gateway_service.config;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class CustomAuthEntryPoint implements ServerAuthenticationEntryPoint{

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthEntryPoint.class);

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        String originalUrl = exchange.getRequest().getURI().toString();
        // logger.info("🔴🔴🔴 ACCESO BLOQUEADO. URL ORIGINAL: {}", originalUrl);

        // Redirigir al login personalizado
        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
        exchange.getResponse().getHeaders().setLocation(URI.create("/login"));
        return exchange.getResponse().setComplete();
    }
}
