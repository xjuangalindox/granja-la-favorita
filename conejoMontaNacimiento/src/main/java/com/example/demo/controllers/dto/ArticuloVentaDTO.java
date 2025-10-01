package com.example.demo.controllers.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ArticuloVentaDTO {

    private Long id;

    private Integer cantidad;
    private Double subtotal;

    // RELATIONS
    private ArticuloDTO articulo;

    @JsonIgnore
    private VentaDTO venta;
}
