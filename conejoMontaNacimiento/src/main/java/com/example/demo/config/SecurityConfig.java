package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.core.session.SessionRegistry;
// import org.springframework.security.core.session.SessionRegistryImpl;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
// @EnableWebSecurity
public class SecurityConfig {
    
    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
    //     return httpSecurity
    //         .csrf().disable() // Cross-Site Request Forgery. Util para trabajar con formularios
    //         .authorizeHttpRequests(auth -> { // Acceso a endpoints
    //             auth.requestMatchers(publicEndpoints()).permitAll(); // List<String> endpoints
    //             auth.anyRequest().authenticated();
    //         })
    //         .formLogin(form -> {
    //             //form.loginPage("/login"); // Opcional: si tienes un login personalizado
    //             form.successHandler(successHandler()); // endpoint despues de iniciar sesion
    //             form.permitAll();
    //         })
    //         .sessionManagement()
    //             .sessionCreationPolicy(SessionCreationPolicy.ALWAYS) // ALWAYS - IF_REQUIRED - NEVER - STATELESS
    //             .invalidSessionUrl("/login")
    //             .maximumSessions(1)
    //                 .expiredUrl("/login")
    //                 .sessionRegistry(sessionRegistry()) // Registrar informacion de la session
    //         .and()
    //         .sessionFixation() // Evita ataques de fijacion de la sesion
    //             .migrateSession() // migrateSession() - newSession() - none()
    //         .and()
    //         .httpBasic() // Permitir enviar credenciales en el header del request
    //         .and()
    //         .build();
    // }

    // // Endpoints publicos
    // private String[] publicEndpoints(){
    //     return new String[] {
    //         "/granja-favorita",
    //         "/api/razas/**"
    //     };
    // }

    // // Endpoint despues de autenticarse
    // private AuthenticationSuccessHandler successHandler(){
    //     return ((request, response, authentication) -> {
    //         response.sendRedirect("/granja-favorita");
    //     });
    // }

    // @Bean
    // public SessionRegistry sessionRegistry(){
    //     return new SessionRegistryImpl();
    // }

}
