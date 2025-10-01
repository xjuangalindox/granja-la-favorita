package com.example.demo.controllers.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EjemplarDTO {

    private Long id;

    @JsonIgnore
    private MultipartFile imagen;

    @JsonIgnore
    private List<MultipartFile> imagenes;
    
    //private String nombreImagen;

    // private String publicId;
    // private String secureUrl;

    private String sexo;
    private boolean vendido;
    private Double precio;
    private Double precioOferta;

    // RELATIONS

    @JsonIgnore
    private NacimientoDTO nacimiento;

    //@ToString.Exclude
    //@EqualsAndHashCode.Exclude

    private List<FotoEjemplarDTO> fotos;
}
