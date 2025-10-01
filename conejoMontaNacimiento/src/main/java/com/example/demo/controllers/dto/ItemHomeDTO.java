package com.example.demo.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemHomeDTO {
    
    private String tipo;
    private String secureUrl;

    // ejemplar
    private String sexo;

    // articulo
    private String nombre;
    // private String descripcion;
}
