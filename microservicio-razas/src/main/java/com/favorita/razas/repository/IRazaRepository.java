package com.favorita.razas.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.favorita.razas.model.RazaModel;

@Repository
public interface IRazaRepository extends CrudRepository<RazaModel, Long> {
    
}
