package com.favorita.gateway_service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
public class SaveOriginalUrlFilter implements WebFilter{

    private static final Logger logger = LoggerFactory.getLogger(SaveOriginalUrlFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().toString();

        // logger.info("🔵 FILTRO EJECUTADO: {}", path);

        return chain.filter(exchange);
    }
        
        // return exchange.getPrincipal()
        //     .doOnNext(principal -> {
        //         // Autenticado → no hacemos nada
        //     })
        //     .switchIfEmpty(
        //         Mono.fromRunnable(() -> {
        //             String url = exchange.getRequest().getURI().toString();
        //             logger.info("🔵 URL ACCEDIDA SIN AUTENTICAR: {}", url);
        //         })
        //     )
        //     .then(chain.filter(exchange));        
        
        // // Solo si no está autenticado
        // return exchange.getPrincipal()
        //     .flatMap(principal -> chain.filter(exchange)) // Si está autenticado, continuar
        //     .switchIfEmpty(Mono.defer(() -> {

        //         // Guardamos la URL original si no está autenticado
        //         String originalUrl = exchange.getRequest().getURI().toString();
        //         logger.info("URL ORIGINAL NO AUTENTICADO: {}", originalUrl);

        //         return exchange.getSession()
        //             .doOnNext(session -> session.getAttributes().put("originalUrl", originalUrl))
        //             .then(chain.filter(exchange));

        //         // Almacenar atributo (originalUrl) en exchange
        //         // exchange.getAttributes().put("originalUrl", originalUrl);
        //         // return chain.filter(exchange);
        //     })
        // );
    
}
