package com.example.demo.controllers.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NacimientoDTO {

	private Long id;

	@PastOrPresent
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate fechaNacimiento;

	@PositiveOrZero
	@Max(value = 15)
	private Integer gazaposVivos;

	@PositiveOrZero
	@Max(value = 15)
	private Integer gazaposMuertos;

	@Size(min = 0, max = 50)
	private String nota;
	
	// RELACIONES
	@Valid
	private MontaDTO monta;
	
	@Valid
	private List<EjemplarDTO> ejemplares;

	//@JsonIgnore
	//private VentaDTO venta;
}
