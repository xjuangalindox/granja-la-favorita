package com.favorita.gateway_service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.session.WebSessionManager;

@Configuration
public class AppConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

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

            // EXTRAER LOS DATOS
            String host = originalRequest.getHeaders().getHost().getHostName();
            int port = originalRequest.getHeaders().getHost().getPort();
            String proto = originalRequest.getURI().getScheme();

            // MOSTRAR EN LOG
            // logger.info("FORWARDED INFO -> Host: {}, Port: {}, Proto: {}", host, port, proto);

            ServerHttpRequest mutatedRequest = originalRequest.mutate()
                .header("X-Forwarded-Host", host)
                .header("X-Forwarded-Port", String.valueOf(port))
                .header("X-Forwarded-Proto", proto)
                .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        };
    }

    @Bean
    public WebSessionManager webSessionManager(){
        return exchange -> exchange.getSession()
            .doOnNext(webSession -> webSession.setMaxIdleTime(java.time.Duration.ofDays(1)));
    }
}
