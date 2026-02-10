package com.example.demo.controllers.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EjemplarVendidoDTO {
    private String sexo;
    private LocalDate fechaNacimiento;
    
    private List<ImagenDTO> imagenes;
    private ProgenitorDTO padre;
    private ProgenitorDTO madre;
}
