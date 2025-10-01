package com.example.demo.controllers.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ArticuloDTO {
    
    private Long id;

    private String nombre;
    private String descripcion;
    private String presentacion;    //1 kg, Bolsa (200 gramos)
    private double precio;

    private MultipartFile imagen; // Unicamente para recibir la imagen del formulario
    private String publicId;
    private String secureUrl;
}
