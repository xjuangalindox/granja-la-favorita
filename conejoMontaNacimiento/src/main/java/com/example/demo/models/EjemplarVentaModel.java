package com.example.demo.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ejemplares_venta")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EjemplarVentaModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double precio;

    // RELATIONS
    @ManyToOne
    @JoinColumn(name = "ejemplar_id")
    //@JoinColumn(name = "ejemplar_id", nullable = false)
    private EjemplarModel ejemplar;
    
    @ManyToOne
    @JoinColumn(name = "venta_id", nullable = false)
    private VentaModel venta;
}
