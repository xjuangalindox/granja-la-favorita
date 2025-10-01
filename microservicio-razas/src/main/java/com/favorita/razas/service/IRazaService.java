package com.favorita.razas.service;

import java.util.List;
import java.util.Optional;

import com.favorita.razas.controller.dto.RazaDTO;

public interface IRazaService {

    List<RazaDTO> obtenerRazas();
    
    Optional<RazaDTO> obtenerRazaPorId(Long id);
    
    RazaDTO guardarRaza(RazaDTO razaDTO);
    
    RazaDTO editarRaza(Long id, RazaDTO razaDTO);
    
    boolean eliminarRazaPorId(Long id);
}
