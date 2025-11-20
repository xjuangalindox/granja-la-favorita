package com.example.demo.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "recreos")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RecreoModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "inicio_recreo")
    private LocalDateTime inicioRecreo;

    @Column(name = "fin_recreo")
    private LocalDateTime finRecreo;

    @ManyToOne(targetEntity = ConejoModel.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "conejo_id", nullable = false)
    private ConejoModel conejo;
}
