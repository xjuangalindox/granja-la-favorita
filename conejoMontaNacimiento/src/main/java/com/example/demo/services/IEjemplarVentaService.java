package com.example.demo.services;

import java.util.Optional;

import com.example.demo.controllers.dto.EjemplarVentaDTO;

public interface IEjemplarVentaService {
    
    public Optional<EjemplarVentaDTO> obtenerEjemplarVentaPorId(Long id);

    public EjemplarVentaDTO guardarEjemplarVenta(EjemplarVentaDTO ejemplarVentaDTO);

    public boolean eliminarEjemplarVentaPorId(Long id);

    public EjemplarVentaDTO editarEjemplarVenta(Long id, EjemplarVentaDTO ejemplarVentaDTO);
}
