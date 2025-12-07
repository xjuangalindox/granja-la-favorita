package com.favorita.gateway_service.config;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.web.server.authentication.HttpBasicServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    // @Autowired
    private final CustomAuthEntryPoint customAuthEntryPoint;

    public SecurityConfig(CustomAuthEntryPoint customAuthEntryPoint){
        this.customAuthEntryPoint = customAuthEntryPoint;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())

                .exceptionHandling(ex -> ex.authenticationEntryPoint(customAuthEntryPoint))

                .authorizeExchange(exchanges -> exchanges
                    .pathMatchers(publicEndpoints()).permitAll() // Continuar sin autenticación
                    .anyExchange().authenticated() // Redirigir al login (.formLogin)
                )

                .formLogin(form -> form
                        .authenticationSuccessHandler((webFilterExchange, authentication) -> {
                            ServerWebExchange exchange = webFilterExchange.getExchange();

                            // 🟡 RECUPERAR URL ORIGINAL
                            String originalUrl = (String) exchange.getAttribute("originalUrl");
                            logger.info("🟡🟡🟡 Redirigiendo a URL ORIGINAL: {}", originalUrl);

                            exchange.getResponse().setStatusCode(HttpStatus.FOUND); // 302 Redirección
                            exchange.getResponse().getHeaders().setLocation(URI.create("/")); // Redirigir al index
                            // exchange.getResponse().getHeaders().setLocation(URI.create(originalUrl)); // Redirigir a "/home"
                            return exchange.getResponse().setComplete();
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
                // .httpBasic(Customizer.withDefaults())
                .build();
    }

    // @Bean
    // @Order(Ordered.HIGHEST_PRECEDENCE) // ✅ Se ejecuta PRIMERO (antes que Security)
    // public GlobalFilter addAuthHeaderFilter(){
    //     return (exchange, chain) -> {
    //         // 🔵 CAPTURAR URL ORIGINAL
    //         String originalUrl = exchange.getRequest().getURI().toString();
    //         logger.info("🔵🔵🔵 URL CAPTURADA ANTES DE SECURITY: {}", originalUrl);
    //         exchange.getAttributes().put("originalUrl", originalUrl);

    //         // ✅ VALIDAR AUTENTICACION
    //         return ReactiveSecurityContextHolder.getContext()
    //             .map(securityContext -> securityContext.getAuthentication())
    //             .flatMap(authentication -> {
    //                 boolean isAuthenticated = authentication != null && authentication.isAuthenticated()
    //                     && !(authentication instanceof AnonymousAuthenticationToken);
                        
    //                 // Agregar header User-Authenticated : true
    //                 ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
    //                     .header("User-Authenticated", String.valueOf(isAuthenticated))
    //                     .build();
    //                 return chain.filter(exchange.mutate().request(mutatedRequest).build());
    //             })
    //             .switchIfEmpty(
    //                 // En caso que no haya contexto de seguridad (no autenticado)
    //                 // Agregar header User-Authenticated : false
    //                 chain.filter(exchange.mutate()
    //                     .request(exchange.getRequest().mutate()
    //                         .header("User-Authenticated", "false")
    //                         .build())
    //                     .build())
    //             );
    //     };
    // }

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

    // Endpoints publicos
    private String[] publicEndpoints(){
        return new String[] {
            "/actuator/health", // Solo para comprobar salud de gateway-service
            "/actuator/info",   // Solo para comprobar salud de gateway-service

            "/",            // Pagina principal
            "/index.css",   // Necesario para pagina principal
            "/js/index.js", // Necesario para pagina principal
            
            "/login",       // login de spring
            "/disponible/ejemplares",
            "/disponible/articulos"
        };

        // 👇 AGREGAR ESTO
        // "/.well-known/**"
    }
}
