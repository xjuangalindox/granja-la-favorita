package com.example.demo.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.controllers.dto.RecreoDTO;
import com.example.demo.models.ConejoModel;
import com.example.demo.models.RecreoModel;
import com.example.demo.repositories.ConejoRepository;
import com.example.demo.repositories.IRecreoRepository;

@Service
public class RecreoServiceImpl implements IRecreoService{

    @Autowired
    private IRecreoRepository recreoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<RecreoDTO> findAll(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("inicioRecreo").descending());
        Page<RecreoModel> pageRecreos = recreoRepository.findAll(pageable);

        return pageRecreos.map(
            model -> modelMapper.map(model, RecreoDTO.class)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RecreoDTO findById(Long id) {
        RecreoModel recreoModel = recreoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recreo con id "+id+" no encontrado"));
        
        return modelMapper.map(recreoModel, RecreoDTO.class);
    }

    @Override
    @Transactional
    public RecreoDTO saveRecreo(RecreoDTO recreoDTO) {
        RecreoModel recreoModel = modelMapper.map(recreoDTO, RecreoModel.class);
        recreoModel = recreoRepository.save(recreoModel);
        return modelMapper.map(recreoModel, RecreoDTO.class);
    }

    @Autowired
    private ConejoRepository conejoRepository;

    @Override
    @Transactional
    public RecreoDTO updateRecreo(Long id, RecreoDTO recreoDTO) {
        // 1. Validar existencia
        RecreoModel recreoModel = recreoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recreo con id "+id+" no encontrado"));

        ConejoModel conejoModel = conejoRepository.findById(recreoDTO.getConejo().getId())
            .orElseThrow(() -> new RuntimeException("Conejo con id "+recreoDTO.getConejo().getId()+" no encontrado"));

        recreoModel.setInicioRecreo(recreoDTO.getInicioRecreo());
        recreoModel.setFinRecreo(recreoDTO.getFinRecreo());
        recreoModel.setConejo(conejoModel);

        // 2. Mapear solo los campos cambiados
        // modelMapper.map(recreoDTO, recreoModel);

        // 3. Guardar el mismo objeto modificado
        recreoModel = recreoRepository.save(recreoModel);

        return modelMapper.map(recreoModel, RecreoDTO.class);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if(!recreoRepository.existsById(id)){
            throw new RuntimeException("Recreo con id "+id+" no encontrado");
        }

        recreoRepository.deleteById(id);
    }

    // Obtener recreos de un conejo
    @Override
    @Transactional(readOnly = true)
    public Page<RecreoDTO> findByConejoId(Long conejoId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("inicioRecreo").descending());
        Page<RecreoModel> pageRecreos = recreoRepository.findByConejoId(conejoId, pageable);

        return pageRecreos.map(
            model -> modelMapper.map(model, RecreoDTO.class)
        );
    }
}
