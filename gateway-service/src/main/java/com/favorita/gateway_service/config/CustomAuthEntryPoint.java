package com.favorita.gateway_service.config;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class CustomAuthEntryPoint implements ServerAuthenticationEntryPoint{

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthEntryPoint.class);

    // Entry point por defecto de Spring Security (form login)
    private final RedirectServerAuthenticationEntryPoint defaultEntryPoint =
            new RedirectServerAuthenticationEntryPoint("/index");

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        String originalUrl = exchange.getRequest().getURI().toString();
        logger.info("🔴 ACCESO BLOQUEADO. URL ORIGINAL: {}", originalUrl);

        // Redirigir al login
        // exchange.getResponse().setStatusCode(HttpStatus.FOUND);
        // exchange.getResponse().getHeaders().setLocation(URI.create("/montas"));
        // return exchange.getResponse().setComplete();

        // 2️⃣ Delegar al entry point por defecto para que haga redirect al login
        return defaultEntryPoint.commence(exchange, ex);
    }

        // ⚡ Evitar bucle infinito
        // if (originalUrl.startsWith("/login") || originalUrl.startsWith("/.well-known") || originalUrl.contains("/favicon")) {
        //     exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        //     return exchange.getResponse().setComplete();
        // }

        // Redirigir al login
        // exchange.getResponse().setStatusCode(HttpStatus.FOUND);
        // exchange.getResponse().getHeaders().setLocation(URI.create("/login"));
        // return exchange.getResponse().setComplete();
        
    //     exchange.getResponse().setStatusCode(HttpStatus.FOUND); // 302
    //     exchange.getResponse().getHeaders().setLocation(URI.create("/login"));
    //     return exchange.getResponse().setComplete();
    // }
}
    
// ResponseCookie cookie = ResponseCookie.from("originalUrl", originalUrl)
//         .path("/")
//         .maxAge(60)
//         .httpOnly(false) // ponlo en false para poder verlo por debug
//         .build();
// exchange.getResponse().addCookie(cookie);

        // Guardar la URL si quieres usarla después
        // exchange.getAttributes().put("originalUrl", originalUrl);


// String originalUrl = exchange.getRequest().getCookies()
//         .getFirst("originalUrl") != null
//             ? exchange.getRequest().getCookies().getFirst("originalUrl").getValue()
//             : null;

//             if (originalUrl != null) {
//                 exchange.getResponse().getHeaders().setLocation(URI.create(originalUrl));
//             } else {
//                 exchange.getResponse().getHeaders().setLocation(URI.create("/"));
//             }
