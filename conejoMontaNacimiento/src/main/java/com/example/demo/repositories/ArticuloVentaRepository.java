package com.example.demo.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.ArticuloVentaModel;

@Repository
public interface ArticuloVentaRepository extends CrudRepository<ArticuloVentaModel, Long>{
    // Usado: "/articulos/eliminar/{id}"
    boolean existsByArticuloId(Long id);
}
