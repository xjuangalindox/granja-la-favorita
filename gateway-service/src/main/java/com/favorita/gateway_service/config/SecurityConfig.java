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
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    // @Autowired
    // private final CustomAuthEntryPoint customAuthEntryPoint;

    // public SecurityConfig(CustomAuthEntryPoint customAuthEntryPoint){
        // this.customAuthEntryPoint = customAuthEntryPoint;
    // }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())

                // .exceptionHandling(ex -> ex.authenticationEntryPoint(customAuthEntryPoint))

                .authorizeExchange(exchanges -> exchanges
                    .pathMatchers(publicEndpoints()).permitAll() // Continuar sin autenticación
                    .anyExchange().authenticated() // Redirigir al login (.formLogin)
                )

                .formLogin(form -> form
                        .authenticationSuccessHandler((webFilterExchange, authentication) -> {
                            ServerWebExchange exchange = webFilterExchange.getExchange();

                            return exchange.getSession().flatMap(session -> {
                                String requestedUrl = session.getAttribute("requested-url");

                                if(requestedUrl == null){
                                    requestedUrl = "/conejos"; // Por defecto
                                }

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
                // .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public WebFilter saveRequestUrlFilter(){
        return (exchange, chain) -> {
            
            return exchange.getPrincipal()
                .flatMap(p -> chain.filter(exchange)) // usuario ya autenticado -> nada que hacer
                .switchIfEmpty(
                    // usuario no autenticado -> guardar la URL si es privada
                    exchange.getSession().flatMap(session -> {
                        String path = exchange.getRequest().getURI().getPath();

                        // MOSTRAR EN LOG
                        logger.info("🟡🟡🟡 Path: {}", path);

                        boolean isPublic = path.equals("/logout")
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
}
