package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import com.example.demo.controllers.dto.EjemplarDTO;

public interface IEjemplarService {

    public Optional<EjemplarDTO> obtenerEjemplarPorId(Long id);

    public List<EjemplarDTO> obtenerEjemplares();

    public EjemplarDTO guardarEjemplar(EjemplarDTO ejemplarDTO);

    public boolean eliminarEjemplarPorId(Long id);

    public EjemplarDTO editarEjemplar(EjemplarDTO ejemplarDTO);

    public EjemplarDTO agregarEjemplar(EjemplarDTO ejemplarDTO, Long id);

    // EjemplarController
    public List<EjemplarDTO> ejemplaresDisponibles(boolean vendido);
}
