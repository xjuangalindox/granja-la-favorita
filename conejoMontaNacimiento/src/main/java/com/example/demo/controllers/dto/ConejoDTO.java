package com.example.demo.controllers.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConejoDTO {

	private Long id;

	@PastOrPresent(message = "La fecha de inicio del recreo no puede ser futura")
	private LocalDateTime inicioRecreo;
	private LocalDateTime finRecreo;

	// Validacion personalizada: fin > inicio
	@AssertTrue(message = "La fecha de fin del recreo debe ser porterior a la de inicio")
	public boolean isFinRecreoValido(){
		if(inicioRecreo == null || finRecreo == null){
			return true; // Si alguno es nulo, no hay error
		}
		return finRecreo.isAfter(inicioRecreo); // esctrictamente mayor
	}

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
