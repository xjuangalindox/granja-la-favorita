package com.example.demo.controllers.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RecreoDTO {

    private Long id;

    @NotNull(message = "Inicio recreo es obligatorio")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime inicioRecreo;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime finRecreo;
    
    @JsonIgnore // Necesario para evitar recursividad circular
    private ConejoDTO conejo;

    // VALIDATION
    @AssertTrue(message = "Inicio recreo debe ser menor que Fin recreo")
    private boolean isInicioAntesDeFin() {
        // finRecreo NULL
        if(finRecreo == null) return true;
        // inicioRecreo < finRecreo
        return inicioRecreo.isBefore(finRecreo);
    }
}
