package com.example.demo.services;

import org.springframework.data.domain.Page;

import com.example.demo.controllers.dto.RecreoDTO;

public interface IRecreoService {

    public Page<RecreoDTO> findAll(int pageNumber, int pageSize);
    public RecreoDTO findById(Long id);
    public RecreoDTO saveRecreo(RecreoDTO recreoDTO);
    public RecreoDTO updateRecreo(Long id, RecreoDTO recreoDTO);
    public void deleteById(Long id);

    // Obtener recreos de un conejo
    public Page<RecreoDTO> findByConejoId(Long conejoId, int pageNumber, int pageSize);
}
