package com.example.demo.config;

import java.util.concurrent.Executor;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration
public class AppConfig {
    
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(2);     // Hilos base (2 vCPU en el VPS)
        executor.setMaxPoolSize(3);      // Máx. hilos permitidos
        executor.setQueueCapacity(30);   // Tareas en espera
        executor.setKeepAliveSeconds(60); // Tiempo vivo de hilos extra
        executor.setThreadNamePrefix("IMG-ASYNC-"); // Prefijo en logs

        executor.setWaitForTasksToCompleteOnShutdown(true); // Espera al apagar
        executor.setAwaitTerminationSeconds(30); // Tiempo máx. de espera
        
        executor.initialize();
        return executor;
    }

    @Value("${cloudinary.cloud-name}")
    private String cloudName;
    
    @Value("${cloudinary.api-key}")
    private String apiKey;
    
    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Bean
    Cloudinary cloudinary(){
        return new Cloudinary(
            ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
            )
        );
    }
}
