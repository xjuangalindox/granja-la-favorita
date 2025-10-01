package com.example.demo.models;

import java.time.LocalDate;

import com.example.demo.models.enums.EstatusMonta;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "montas")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MontaModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String nota;

	@Column(name = "fecha_monta")
	private LocalDate fechaMonta;
	@Column(name = "cantidad_montas")
	private Integer cantidadMontas;
	@Enumerated(EnumType.STRING)
	private EstatusMonta estatus;

	// RELATIONS

	@ManyToOne(targetEntity = ConejoModel.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "hembra_id")
	private ConejoModel hembra;

	@ManyToOne(targetEntity = ConejoModel.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "macho_id")
	private ConejoModel macho;

	// Opcional al momento de persistir
	@OneToOne(mappedBy = "monta", cascade = CascadeType.ALL, orphanRemoval = true)
	NacimientoModel nacimiento;
}
