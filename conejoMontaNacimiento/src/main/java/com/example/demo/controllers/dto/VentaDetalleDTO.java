package com.example.demo.controllers.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VentaDetalleDTO {
    private List<ArticuloVendidoDTO> articulos;
    private List<EjemplarVendidoDTO> ejemplares;
}
