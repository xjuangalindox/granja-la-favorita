package com.example.demo.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.EjemplarModel;
import java.util.List;

@Repository
public interface EjemplarRepository extends CrudRepository<EjemplarModel, Long>{
    // EjemplarController
    List<EjemplarModel> findByVendido(boolean vendido);
}
