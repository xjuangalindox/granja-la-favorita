package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.example.demo.controllers.dto.MontaDTO;
import com.example.demo.models.enums.EstatusMonta;

public interface IMontaService {
    
    // public List<MontaDTO> obtenerMontas(); // Obtener todas las montas
    public Page<MontaDTO> findAll(int pagina, int cantidad); // Obtener montas paginadas
    public Page<MontaDTO> findByEstatus(int pagina, int cantidad, EstatusMonta estatus); // Obtener montas paginadas por estatus

    // Obtener monta por id
    public Optional<MontaDTO> obtenerMontaById(Long id);

    // Guardar monta
    public MontaDTO guardarMonta(MontaDTO montaDTO);

    // Editar monta
    public MontaDTO editarMonta(Long id, MontaDTO montaDTO);

    // Eliminar monta por id
    public boolean eliminarMontaById(Long id);

    // Obtener los nacimiento cuyo nacimiento == null
    public List<MontaDTO> findByNacimientoIsNull();

    //public boolean existsById(Long id);
}
