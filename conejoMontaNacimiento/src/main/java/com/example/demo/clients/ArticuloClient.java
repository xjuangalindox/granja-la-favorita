package com.example.demo.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.controllers.dto.ArticuloDTO;

// @FeignClient(name = "microservicio-articulos", url="http://microservicio-articulos:8082")
@FeignClient(name = "microservicio-articulos")
public interface ArticuloClient {

    // Obtener acticulos del RestControllerArticulo, donde stock = true
    @GetMapping("/api/articulos")
    List<ArticuloDTO> obtenerArticulos();

    @GetMapping("/api/articulos/{id}")
    ArticuloDTO obtenerArticuloPorId(@PathVariable Long id);
}
