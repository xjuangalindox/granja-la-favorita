package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.controllers.dto.VentaDetalleDTO;
import com.example.demo.services.VentaServiceImpl;

@RestController
@RequestMapping("/api/ventas")
public class VentaRestController {

    @Autowired
    private VentaServiceImpl ventaServiceImpl;

    @GetMapping("/{id}")
    public ResponseEntity<VentaDetalleDTO> obtenerDetallVenta(@PathVariable Long id){
        VentaDetalleDTO detalle = ventaServiceImpl.obtenerVentaDetalle(id);

        return ResponseEntity.ok(detalle);
    }
}
