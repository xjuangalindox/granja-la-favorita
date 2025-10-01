package com.favorita.razas.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.favorita.razas.clients.ConejoClient;
import com.favorita.razas.controller.dto.RazaDTO;
import com.favorita.razas.model.RazaModel;
import com.favorita.razas.repository.IRazaRepository;

@Service
public class RazaServiceImpl implements IRazaService{

    @Autowired
    private IRazaRepository razaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ConejoClient conejoClient;

    @Override
    public List<RazaDTO> obtenerRazas() {
        List<RazaModel> razas = (List<RazaModel>) razaRepository.findAll();
        razas.sort(Comparator.comparing(RazaModel::getNombre));

        return razas.stream()
            .map(model -> modelMapper.map(model, RazaDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<RazaDTO> obtenerRazaPorId(Long id) {
        return razaRepository.findById(id)
            .map(model -> modelMapper.map(model, RazaDTO.class));
    }

    @Override
    public RazaDTO guardarRaza(RazaDTO razaDTO) {
        RazaModel razaModel = modelMapper.map(razaDTO, RazaModel.class);
        razaModel = razaRepository.save(razaModel);

        return modelMapper.map(razaModel, RazaDTO.class);
    }

    @Override
    public RazaDTO editarRaza(Long id, RazaDTO razaDTO) {
        RazaModel razaModel = razaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("La raza con id "+id+" no fue encontrada"));
        
        razaModel.setNombre(razaDTO.getNombre());
        razaModel = razaRepository.save(razaModel);

        return modelMapper.map(razaModel, RazaDTO.class);
    }

    @Override
    @Transactional
    public boolean eliminarRazaPorId(Long id) {
        RazaModel razaModel = razaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("La raza con id "+id+" no fue encontrada."));
        
        // Si existe algún conejo con esta raza, no permitir eliminar
        if(conejoClient.existsByRazaId(id)){
            throw new RuntimeException("La raza "+razaModel.getNombre()+" esta siendo utilizada actualmente, primero debe desvincular para eliminar.");
        }

        razaRepository.deleteById(id);
        return true;
    }
    
}
