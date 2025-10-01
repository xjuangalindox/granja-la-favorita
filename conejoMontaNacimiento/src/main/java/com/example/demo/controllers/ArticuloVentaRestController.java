package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.services.IArticuloVentaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/articulos/venta")
public class ArticuloVentaRestController {
    
    @Autowired
    private IArticuloVentaService articuloVentaService;

    @GetMapping("/existe-por-articulo/{id}")
    public ResponseEntity<Boolean> existsByArticuloId(@PathVariable("id") Long id) {
        boolean existe = articuloVentaService.existsByArticuloId(id);
        return ResponseEntity.ok(existe);
    }
}
