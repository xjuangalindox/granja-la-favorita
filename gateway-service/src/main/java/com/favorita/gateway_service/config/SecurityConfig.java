package com.favorita.gateway_service.config;

import java.net.URI;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    // ALMACENAR PATH (PRIVADA) ANTES DE AUTENTICATION
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE) // Ejecutar antes de seguridad
    public WebFilter saveRequestUrlFilter(){
        return (exchange, chain) -> {
            
            return exchange.getPrincipal()
                .flatMap(p -> chain.filter(exchange)) // usuario ya autenticado -> nada que hacer
                .switchIfEmpty( // usuario no autenticado -> guardar la URL si es privada
                    exchange.getSession().flatMap(session -> {
                        String path = exchange.getRequest().getURI().getPath();

                        // MOSTRAR EN LOG
                        logger.info("🟡🟡🟡 Path: {}", path);

                        boolean isPublic = path.equals("/logout")
                                            || path.startsWith("/api/")
                                            || path.startsWith("/.well-known")
                                            || path.endsWith(".ico") 
                                            || path.endsWith(".css") 
                                            || path.endsWith(".js") 
                                            || Arrays.stream(publicEndpoints())
                                            .anyMatch(publicUrl -> path.equals(publicUrl));                    
                                            
                        if(!isPublic){
                            logger.info("🟢🟢🟢 Guardando URL privada: {}", path);
                            session.getAttributes().put("requested-url", path);
                        }

                        return chain.filter(exchange);
                    })
                );
        };
    }

    // SECUTIRY PARA APIS
    @Bean
    @Order(1)
    public SecurityWebFilterChain apiSecurity(ServerHttpSecurity http) {
        return http
            .securityMatcher(ServerWebExchangeMatchers.pathMatchers("/api/**"))
            .csrf(csrf -> csrf.disable()) // Desactivar para APIs
            .authorizeExchange(ex -> ex
                .anyExchange().authenticated() // No existen endpoint publicos con /api/**
            )
            .httpBasic(basic -> {})
            .build();
    }

    // SECUTIRY PARA FORMULARIOS
    @Bean
    @Order(2)
    public SecurityWebFilterChain webSecurity(ServerHttpSecurity http) {
        return http
                // CSRF activo (correcto para formularios)
                .authorizeExchange(exchanges -> exchanges
                    .pathMatchers(publicEndpoints()).permitAll()
                    .anyExchange().authenticated()
                )

                .formLogin(form -> form
                        .authenticationSuccessHandler((webFilterExchange, authentication) -> {
                            ServerWebExchange exchange = webFilterExchange.getExchange();

                            return exchange.getSession().flatMap(session -> {
                                String requestedUrl = session.getAttribute("requested-url");
                                if(requestedUrl == null) requestedUrl = "/"; // Redirect toindex.html

                                // Limpiar la URL guardada
                                session.getAttributes().remove("requested-url");

                                exchange.getResponse().setStatusCode(HttpStatus.FOUND); // 302 Redirección
                                exchange.getResponse().getHeaders().setLocation(URI.create(requestedUrl));

                                return exchange.getResponse().setComplete();
                            });

                        })
                )
                
                .logout(logout -> logout
                    .logoutSuccessHandler((webFilterExchange, authentication) -> {
                        ServerWebExchange exchange = webFilterExchange.getExchange();

                        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                        exchange.getResponse().getHeaders().setLocation(URI.create("/")); // Redirigir al index
                        return exchange.getResponse().setComplete();
                    })
                )
                .httpBasic(basic -> basic.disable()) // 🚫 DESACTIVAR httpBasic
                .build();
    }

    // ENDPOINTS PUBLICOS
    private String[] publicEndpoints(){
        return new String[] {
            "/actuator/health", // Usado por Eureka para comprobar salud
            "/actuator/info",   // Solo para comprobar salud de gateway-service

            "/",            // Pagina principal
            "/index.css",   // Necesario para pagina principal
            "/js/index.js", // Necesario para pagina principal
            
            "/login",           // login de spring
            // "/default-ui.css",  // css del login de spring

            // "/.well-known/appspecific/com.chrome.devtools.json", // endpoint del navegador

            "/disponible/ejemplares",
            "/disponible/articulos"
        };
    }

    // VALIDAR AUTENTICACION
    @Bean
    public GlobalFilter addAuthHeaderFilter() {
        return (exchange, chain) -> 
            ReactiveSecurityContextHolder.getContext()
            .map(securityContext -> securityContext.getAuthentication())
            .flatMap(authentication -> {
                boolean isAuthenticated = authentication != null && authentication.isAuthenticated()
                    && !(authentication instanceof AnonymousAuthenticationToken);
                    
                // Agregar header User-Authenticated : true
                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("User-Authenticated", String.valueOf(isAuthenticated))
                    .build();
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            })
            .switchIfEmpty(
                // En caso que no haya contexto de seguridad (no autenticado)
                // Agregar header User-Authenticated : false
                chain.filter(exchange.mutate()
                    .request(exchange.getRequest().mutate()
                        .header("User-Authenticated", "false")
                        .build())
                    .build())
            );
    }

    // EXTRAER HEADERS
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
