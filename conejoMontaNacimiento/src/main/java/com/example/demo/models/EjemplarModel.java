package com.example.demo.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ejemplares")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EjemplarModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@Column(name = "nombre_imagen")
    //private String nombreImagen;

    // @Column(name = "public_id")
    // private String publicId;
    // @Column(name = "secure_url")
    // private String secureUrl;

    private String sexo;
    private boolean vendido;
    private Double precio;
    private Double precioOferta;

    // RELATIONS
    @ManyToOne
    @JoinColumn(name = "nacimiento_id")
    private NacimientoModel nacimiento;

    //@ManyToOne
    //@JoinColumn(name = "venta_id") // Nullable, porque puede no estar vendido aún
    //private VentaModel venta;

    //@OneToOne(mappedBy = "ejemplar") // Agregado
    //private EjemplarVentaModel ejemplarVenta; // Agregado

    // @OneToMany(mappedBy = "ejemplar")
    @OneToMany(mappedBy = "ejemplar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FotoEjemplarModel> fotos = new ArrayList<>();
}
