package com.example.demo.services;

import java.util.Optional;

import com.example.demo.controllers.dto.ArticuloVentaDTO;

public interface IArticuloVentaService {

    public Optional<ArticuloVentaDTO> obtenerArticuloVentaPorId(Long id);

    public ArticuloVentaDTO guardarArticuloVenta(ArticuloVentaDTO articuloVentaDTO);

    public boolean eliminarArticuloVentaPorId(Long id);

    public ArticuloVentaDTO editarArticuloVenta(Long id, ArticuloVentaDTO articuloVentaDTO);

    // Usado: /articulos/eliminar/{id}
    public boolean existsByArticuloId(Long id);

}
