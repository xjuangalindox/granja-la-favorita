package com.example.demo.models;

import jakarta.persistence.Column;
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
@Table(name = "articulos_venta")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ArticuloVentaModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer cantidad;
    private Double subtotal;

    // RELATIONS
    // @ManyToOne
    // @JoinColumn(name = "articulo_id")
    // private ArticuloModel articulo;

    @ManyToOne
    @JoinColumn(name = "venta_id")
    //@JoinColumn(name = "venta_id", nullable = false)
    private VentaModel venta;

    @Column(name = "articulo_id")
    private Long articuloId;
}
