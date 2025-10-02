package com.example.demo.controllers.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConejoDTO {

	private Long id;

	private LocalDateTime inicioRecreo;
	private LocalDateTime finRecreo;

	@JsonIgnore // Ignorar al crear el JSON, solo para recibir desde el frontend
	private MultipartFile imagen;

	private String nombre;
	private String sexo;
	private Double peso;
	private boolean activo;
	private String nota;

    private String publicId;
    private String secureUrl;

	@DateTimeFormat(pattern = "yyyy-MM-dd") // Necesario para manejar un solo formaro en el frontend
	private LocalDate fechaNacimiento;
	private Integer totalNacimientos;
	private Integer totalGazapos;

	private RazaDTO raza;
}
