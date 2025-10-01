package com.example.demo.controllers.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EjemplarVentaDTO {

    private Long id;

    private Double precio;

    // RELATIONS
    EjemplarDTO ejemplar;
    
    @JsonIgnore
    VentaDTO venta;
}
