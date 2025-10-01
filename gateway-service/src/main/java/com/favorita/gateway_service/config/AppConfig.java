package com.favorita.gateway_service.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;

@Configuration
public class AppConfig {
    

    // @Bean
    // public GlobalFilter forwardedHeaderFilter() {
    //     return (exchange, chain) -> {
    //         ServerHttpRequest request = exchange.getRequest().mutate()
    //             .header("X-Forwarded-Host", request.getHeaders().getHost().getHostName())
    //             .header("X-Forwarded-Port", String.valueOf(request.getHeaders().getHost().getPort()))
    //             .header("X-Forwarded-Proto", request.getURI().getScheme())
    //             .build();
    //         return chain.filter(exchange.mutate().request(request).build());
    //     };
    // }

    @Bean
    public GlobalFilter forwardedHeaderFilter() {
        return (exchange, chain) -> {
            ServerHttpRequest originalRequest = exchange.getRequest();

            ServerHttpRequest mutatedRequest = originalRequest.mutate()
                .header("X-Forwarded-Host", originalRequest.getHeaders().getHost().getHostName())
                // .header("X-Forwarded-Port", "8080") // <- forzamos el puerto del Gateway
                .header("X-Forwarded-Port", String.valueOf(originalRequest.getHeaders().getHost().getPort()))
                .header("X-Forwarded-Proto", originalRequest.getURI().getScheme())
                .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        };
    }
}
