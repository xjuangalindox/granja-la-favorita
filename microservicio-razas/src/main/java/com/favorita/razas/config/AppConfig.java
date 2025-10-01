package com.favorita.razas.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;

@Configuration
public class AppConfig {
    
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
    
    // @Bean
    // public ForwardedHeaderFilter forwardedHeaderFilter(){
    //     return new ForwardedHeaderFilter();
    // }

    // @Bean
    // public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
    //     return factory -> factory.setUseRelativeRedirects(false);
    // }
}
