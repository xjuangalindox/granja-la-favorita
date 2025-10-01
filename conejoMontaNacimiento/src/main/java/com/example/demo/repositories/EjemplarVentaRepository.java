package com.example.demo.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.EjemplarVentaModel;

@Repository
public interface EjemplarVentaRepository extends CrudRepository<EjemplarVentaModel, Long>{
    
}
