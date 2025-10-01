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
@Table(name = "fotos_ejemplar")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FotoEjemplarModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id")
    private String publicId;
    @Column(name = "secure_url")
    private String secureUrl;

    @ManyToOne
    // @JoinColumn(name = "ejemplar_id")
    @JoinColumn(name = "ejemplar_id", nullable = false)
    private EjemplarModel ejemplar;
}
