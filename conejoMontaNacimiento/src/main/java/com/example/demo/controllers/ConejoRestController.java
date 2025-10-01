package com.example.demo.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloudinary.http5.api.Response;
import com.example.demo.services.IConejoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/conejos")
public class ConejoRestController {
    
    @Autowired
    private IConejoService conejoService;

    // Usado "razas/eliminar/{id}"
    @GetMapping("/existe-por-raza/{id}")
    public ResponseEntity<Boolean> existsByRazaId(@PathVariable("id") Long id) {
        boolean existe = conejoService.existsByRazaId(id);
        return ResponseEntity.ok(existe); // true si existe, false si no existe
    }
    
}
