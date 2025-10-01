package com.example.demo.controllers.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.demo.models.NacimientoModel;
import com.example.demo.models.enums.EstatusVenta;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VentaDTO {
    private Long id;

    private String nombreCliente;
    private String vinculoContacto; //"FACEBOOK", "WHATSAPP", "FACEBOOK Y WHATSAPP"
    private String telefono;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaEntrega;
    private String lugarEntrega;
    private Double totalVenta;
    private String nota;
    
    @Enumerated(EnumType.STRING)
    private EstatusVenta estatus;   //PENDIENTE, APARTADO, ENTREGADO

    // RELATIONS
    private List<ArticuloVentaDTO> articulosVenta = new ArrayList<>();
    private List<EjemplarVentaDTO> ejemplaresVenta = new ArrayList<>();
    //private List<NacimientoDTO> nacimientos = new ArrayList<>();
}
