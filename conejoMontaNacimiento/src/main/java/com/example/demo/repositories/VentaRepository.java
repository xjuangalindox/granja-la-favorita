package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.VentaModel;
import com.example.demo.models.enums.EstatusVenta;

@Repository
public interface VentaRepository extends CrudRepository<VentaModel, Long>{
    List<VentaModel> findAllByOrderByFechaEntregaDesc();

    Page<VentaModel> findByEstatusIsNull(Pageable pageable);
    Page<VentaModel> findAll(Pageable Pageable);
    Page<VentaModel> findByEstatus(Pageable pageable, EstatusVenta estatus);
}
