package com.favorita.articulos.services;

import java.util.List;
import java.util.Optional;

import com.favorita.articulos.controller.dto.ArticuloDTO;

public interface IArticuloService {
    public List<ArticuloDTO> obtenerArticulos();
    public List<ArticuloDTO> obtenerArticulosStockTrue();
    public Optional<ArticuloDTO> obtenerArticuloPorId(Long id);
    public ArticuloDTO guardarArticulo(ArticuloDTO articuloDTO);
    public ArticuloDTO editarArticulo(Long id, ArticuloDTO articuloDTO);
    public boolean eliminarArticuloPorId(Long id);
}
