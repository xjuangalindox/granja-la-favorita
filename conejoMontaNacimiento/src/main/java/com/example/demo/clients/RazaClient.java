package com.example.demo.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
// import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.controllers.dto.RazaDTO;

// @FeignClient(name = "microservicio-razas", url = "http://microservicio-razas:8081")
@FeignClient(name = "microservicio-razas")
public interface RazaClient {
    
    @GetMapping("/api/razas")
    List<RazaDTO> obtenerRazas();

    @GetMapping("/api/razas/{id}")
    RazaDTO obtenerRazaPorId(@PathVariable Long id);

    // @PostMapping("/api/razas")
    // RazaDTO guardarRaza(@RequestBody RazaDTO razaDTO);

    // @PutMapping("/api/razas/{id}")
    // RazaDTO editarRaza(@PathVariable Long id, @RequestBody RazaDTO razaDTO);

    // @DeleteMapping("/api/razas/{id}")
    // void eliminarRazaPorId(@PathVariable Long id);
}
