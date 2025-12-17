package com.favorita.articulos.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.favorita.articulos.model.ArticuloModel;

@Repository
public interface IArticuloRepository extends CrudRepository<ArticuloModel, Long>{
    // Evidar articulos duplicados
    boolean existsByNombre(String nombre);
    List<ArticuloModel> findByStockTrue();
}
