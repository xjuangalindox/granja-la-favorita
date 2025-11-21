package com.example.demo.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.RecreoModel;

@Repository
public interface IRecreoRepository extends CrudRepository<RecreoModel, Long>{
    Page<RecreoModel> findAll(Pageable pageable);

    // Obtener recreos de un conejo
    Page<RecreoModel> findByConejoId(Long idConejo, Pageable pageable);
}
