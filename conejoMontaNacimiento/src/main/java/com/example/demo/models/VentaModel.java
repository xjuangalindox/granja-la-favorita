package com.example.demo.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.models.enums.EstatusVenta;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ventas")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VentaModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_cliente")
    private String nombreCliente;
    @Column(name = "vinculo_contacto")
    private String vinculoContacto; //"FACEBOOK", "WHATSAPP", "FACEBOOK Y WHATSAPP"
    private String telefono;
    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;
    @Column(name = "lugar_entrega")
    private String lugarEntrega;
    @Column(name = "total_venta")
    private Double totalVenta;
    private Double adelanto;
    private Double extra;
    private String nota;

    @Enumerated(EnumType.STRING)
    private EstatusVenta estatus;   //PENDIENTE, APARTADO, ENTREGADO, REGISTRADO

    //@OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(mappedBy = "venta") // Gestion manualmente
    private List<ArticuloVentaModel> articulosVenta = new ArrayList<>();

    //@OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(mappedBy = "venta") // Gestion manualmente
    private List<EjemplarVentaModel> ejemplaresVenta = new ArrayList<>();

    //@OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    //private List<NacimientoModel> nacimientos = new ArrayList<>();
}
