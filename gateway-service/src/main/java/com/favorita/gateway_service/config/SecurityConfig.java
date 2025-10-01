package com.favorita.gateway_service.config;

import java.net.URI;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchanges -> exchanges
                    .pathMatchers(publicEndpoints()).permitAll()
                    .anyExchange().authenticated()
                )
                .formLogin(form -> form
                        .authenticationSuccessHandler((webFilterExchange, authentication) -> {
                            ServerWebExchange exchange = webFilterExchange.getExchange();
                            exchange.getResponse().setStatusCode(HttpStatus.FOUND); // 302 Redirección
                            exchange.getResponse().getHeaders().setLocation(URI.create("/")); // Redirigir a "/"
                            // exchange.getResponse().getHeaders().setLocation(URI.create("/home")); // Redirigir a "/home"
                            return exchange.getResponse().setComplete();
                        })
                )
                .logout(logout -> logout
                    .logoutSuccessHandler((webFilterExchange, authentication) -> {
                        ServerWebExchange exchange = webFilterExchange.getExchange();
                        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                        exchange.getResponse().getHeaders().setLocation(URI.create("/"));
                        // exchange.getResponse().getHeaders().setLocation(URI.create("/home"));
                        return exchange.getResponse().setComplete();
                    })
                )
                // .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public GlobalFilter addAuthHeaderFilter() {
        return (exchange, chain) -> 
            ReactiveSecurityContextHolder.getContext()
            .map(securityContext -> securityContext.getAuthentication())
            .flatMap(authentication -> {
                boolean isAuthenticated = authentication != null && authentication.isAuthenticated()
                    && !(authentication instanceof AnonymousAuthenticationToken);

                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("User-Authenticated", String.valueOf(isAuthenticated))
                    .build();
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            })
            .switchIfEmpty(
                // En caso que no haya contexto de seguridad (no autenticado)
                chain.filter(exchange.mutate()
                    .request(exchange.getRequest().mutate()
                        .header("User-Authenticated", "false")
                        .build())
                    .build())
            );
    }

    // Endpoints publicos
    private String[] publicEndpoints(){
        return new String[] {
            "/actuator/health", // Solo para comprobar salud de gateway-service
            "/actuator/info",   // Solo para comprobar salud de gateway-service

            "/",            // Pagina principal
            "/index.css",   // Necesario para pagina principal
            "/js/index.js", // Necesario para pagina principal
            
            "/login",
            "/disponible/ejemplares",
            "/disponible/articulos",
        };
    }
}
