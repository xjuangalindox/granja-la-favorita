package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.example.demo.controllers.dto.VentaDTO;
import com.example.demo.models.enums.EstatusVenta;

public interface IVentaService {
    
    public Page<VentaDTO> findByEstatusIsNull(int pagina, int cantidad);
    public Page<VentaDTO> findAll(int pagina, int cantidad);
    public Page<VentaDTO> findByEstatus(int pagina, int cantidad, EstatusVenta estatus);

    public List<VentaDTO> obtenerVentas();

    public Optional<VentaDTO> obtenerVentaPorId(Long id);

    public VentaDTO guardarVenta(VentaDTO ventaDTO);

    public VentaDTO actualizarDatosPrincipales(Long id, VentaDTO ventaDTO);

    public VentaDTO editarVenta(Long id, VentaDTO ventaDTO, List<Long> idsArticulosVentaEliminados, List<Long> idsNacimientosEliminados);

    public boolean eliminarVenta(Long id);
    // public boolean eliminarVenta(VentaDTO ventaDTO);
}
