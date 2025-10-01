package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.example.demo.controllers.dto.ConejoDTO;

public interface IConejoService {

    public Page<ConejoDTO> findAll(int pagina, int cantidad);
    public Page<ConejoDTO> findBySexo(int pagina, int cantidad, String sexo);

    // Obtener conejo por id
    public Optional<ConejoDTO> obtenerConejoById(Long id);

    // Obtener conejos
    public List<ConejoDTO> obtenerConejos();

    // Guardar conejo
    public ConejoDTO guardarConejo(ConejoDTO conejoDTO);

    // Editar conejo
    public ConejoDTO editarConejo(Long id, ConejoDTO conejoDTO);

    // Eliminar conejo por id
    public boolean eliminarConejoById(Long id);

    // EXTRAS
    public List<ConejoDTO> obtenerConejosPorSexo(String sexo);
    public boolean existsByNombre(String nombre);
    public boolean existsById(Long id);

    // Usado "razas/eliminar/{id}"
    public boolean existsByRazaId(Long id);

    // Usado: formulario montas
    List<ConejoDTO> obtenerConejosActivosPorSexo(String sexo);
}
