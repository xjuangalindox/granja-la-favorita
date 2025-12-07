package com.favorita.gateway_service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;

@Configuration
public class AppConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    @Bean
    public GlobalFilter forwardedHeaderFilter() {
        return (exchange, chain) -> {
            ServerHttpRequest originalRequest = exchange.getRequest();

            // EXTRAER LOS DATOS
            String host = originalRequest.getHeaders().getHost().getHostName();
            int port = originalRequest.getHeaders().getHost().getPort();
            String proto = originalRequest.getURI().getScheme();

            // MOSTRAR EN LOG
            // logger.info("🔵🔵🔵 FORWARDED INFO -> Host: {}, Port: {}, Proto: {}", host, port, proto);

            ServerHttpRequest mutatedRequest = originalRequest.mutate()
                .header("X-Forwarded-Host", host)
                .header("X-Forwarded-Port", String.valueOf(port))
                .header("X-Forwarded-Proto", proto)
                .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        };
    }

}
