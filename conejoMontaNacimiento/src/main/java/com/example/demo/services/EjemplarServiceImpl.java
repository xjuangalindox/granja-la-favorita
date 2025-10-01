package com.example.demo.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.controllers.dto.EjemplarDTO;
import com.example.demo.models.EjemplarModel;
import com.example.demo.models.NacimientoModel;
import com.example.demo.models.VentaModel;
import com.example.demo.repositories.EjemplarRepository;
import com.example.demo.repositories.NacimientoRepository;
import com.example.demo.repositories.VentaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class EjemplarServiceImpl implements IEjemplarService{

    @Autowired
    private EjemplarRepository ejemplarRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private NacimientoRepository nacimientoRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Override
    public EjemplarDTO guardarEjemplar(EjemplarDTO ejemplarDTO) {
        // Mapear de EjemplarDTO a EjemplarModel
        EjemplarModel ejemplarModel = modelMapper.map(ejemplarDTO, EjemplarModel.class);
        // Persistir EjemplarModel
        EjemplarModel guardado = ejemplarRepository.save(ejemplarModel);
        // Mapear y retornar
        return modelMapper.map(guardado, EjemplarDTO.class);
    }

    @Override
    public boolean eliminarEjemplarPorId(Long id) {
        if(ejemplarRepository.existsById(id)){
            ejemplarRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public EjemplarDTO editarEjemplar(EjemplarDTO ejemplarDTO) {
        // Obtener EjemplarModel con ID
        EjemplarModel ejemplarModel = ejemplarRepository.findById(ejemplarDTO.getId())
            .orElseThrow(() -> new EntityNotFoundException("Ejemplar no encontrado"));
        
        // Obtener NacimientoModel de ejemplarDTO
        NacimientoModel nacimientoModel = nacimientoRepository.findById(ejemplarDTO.getNacimiento().getId())
            .orElseThrow(() -> new EntityNotFoundException("Nacimiento no encontrado"));

        // Setear informacion de EjemplarDTO a EjemplarModel
        //ejemplarModel.setNombreImagen(ejemplarDTO.getNombreImagen());
        ejemplarModel.setSexo(ejemplarDTO.getSexo());
        ejemplarModel.setVendido(ejemplarDTO.isVendido());
        ejemplarModel.setNacimiento(nacimientoModel);

        // Persistir EjemplarModel con la nueva informacion
        EjemplarModel guardado = ejemplarRepository.save(ejemplarModel);

        // Maperar EjemplarModel a EjemplarDTO y retornar
        return modelMapper.map(guardado, EjemplarDTO.class);        
    }

    @Override
    public EjemplarDTO agregarEjemplar(EjemplarDTO ejemplarDTO, Long id) {
        // Setear informacion del EjemplarDTO al EjemplarModel
        EjemplarModel ejemplarModel = new EjemplarModel();
        //ejemplarModel.setNombreImagen(ejemplarDTO.getNombreImagen());
        ejemplarModel.setSexo(ejemplarDTO.getSexo());
        //ejemplarModel.setPrecio(ejemplarDTO.getPrecio());

        // Validar y asignar NacimientoModel a EjemplarModel
        NacimientoModel nacimientoModel = nacimientoRepository.findById(ejemplarDTO.getNacimiento().getId())
            .orElseThrow(() -> new EntityNotFoundException("Nacimiento no encontrado"));
        ejemplarModel.setNacimiento(nacimientoModel);

        // Validar y asignar VentaModel a EjemplarModel
        VentaModel ventaModel = ventaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada"));
        //ejemplarModel.setVenta(ventaModel);

        // Persistir VentaModel
        EjemplarModel guardado = ejemplarRepository.save(ejemplarModel);

        // Mapear VentaModel a VentaDTO y retornar
        return modelMapper.map(guardado, EjemplarDTO.class);
    }

    @Override
    public Optional<EjemplarDTO> obtenerEjemplarPorId(Long id) {
        return ejemplarRepository.findById(id)
            .map(model -> modelMapper.map(model, EjemplarDTO.class));
    }

    @Override
    public List<EjemplarDTO> obtenerEjemplares() {
        List<EjemplarModel> listaEjemplares = (List<EjemplarModel>) ejemplarRepository.findAll();

        return listaEjemplares.stream()
            .map(ejemplar -> modelMapper.map(ejemplar, EjemplarDTO.class))
            .collect(Collectors.toList());
    }

    // EjemplarController
    @Override
    public List<EjemplarDTO> ejemplaresDisponibles(boolean vendido) {
        return ejemplarRepository.findByVendido(vendido).stream()
            .map(model -> modelMapper.map(model, EjemplarDTO.class))
            .collect(Collectors.toList());
    }    

}
