package com.example.demo.controllers.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    
    // @JsonIgnore // Necesario para evitar recursividad circular
    @JsonBackReference
    private ConejoDTO conejo;

    // VALIDATION
    @AssertTrue(message = "Inicio recreo debe ser menor que fin recreo")
    private boolean isInicioAntesDeFin() {
        // finRecreo NULL
        if(finRecreo == null) return true;
        // inicioRecreo < finRecreo
        return inicioRecreo.isBefore(finRecreo);
    }
}
