package com.example.demo.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CollectionId;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "conejos")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ConejoModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "inicio_recreo")
	private LocalDateTime inicioRecreo;
	@Column(name = "fin_recreo")
	private LocalDateTime finRecreo;

	private String nombre;
	private String sexo;
	private Double peso;
	private boolean activo;
	private String nota;

	@Column(name = "public_id")
    private String publicId;
    @Column(name = "secure_url")
    private String secureUrl;

	@Column(name = "fecha_nacimiento")
	private LocalDate fechaNacimiento;
	@Column(name = "total_nacimientos")
	private Integer totalNacimientos;
	@Column(name = "total_gazapos")
	private Integer totalGazapos;

	@Column(name = "raza_id")
	private Long razaId;

	// Relacion con RecreoModel
	@OneToMany(targetEntity = RecreoModel.class, mappedBy = "conejo", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<RecreoModel> recreos;

	// RELATIONS
	// @ManyToOne(targetEntity = RazaModel.class, fetch = FetchType.LAZY)
	// @JoinColumn(name = "raza_id")
	// private RazaModel raza;
}