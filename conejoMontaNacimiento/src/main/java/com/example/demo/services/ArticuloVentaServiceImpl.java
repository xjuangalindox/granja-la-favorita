package com.example.demo.services;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.clients.ArticuloClient;
import com.example.demo.controllers.dto.ArticuloDTO;
import com.example.demo.controllers.dto.ArticuloVentaDTO;
import com.example.demo.models.ArticuloVentaModel;
import com.example.demo.repositories.ArticuloVentaRepository;

@Service
public class ArticuloVentaServiceImpl implements IArticuloVentaService{

    @Autowired
    private ArticuloClient articuloClient;

    @Autowired
    private ArticuloVentaRepository articuloVentaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public boolean eliminarArticuloVentaPorId(Long id) {
        if(articuloVentaRepository.existsById(id)){
            articuloVentaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public ArticuloVentaDTO editarArticuloVenta(Long id, ArticuloVentaDTO articuloVentaDTO) {

        // Obtener el articulo venta original
        Optional<ArticuloVentaDTO> articuloVentaOpt = obtenerArticuloVentaPorId(id);
        if(articuloVentaOpt.isEmpty()){
            throw new RuntimeException("ArticuloVenta no encontrado.");
        }

        // Obtener el articulo asignado en el formulario
        ArticuloDTO articuloDTO = articuloClient.obtenerArticuloPorId(articuloVentaDTO.getArticulo().getId());
        // Optional<ArticuloDTO> articuloOpt = articuloService.obtenerPorId(articuloVentaDTO.getArticulo().getId());
        // if(articuloOpt.isEmpty()){
        //     throw new RuntimeException("Articulo no encontrado.");
        // }

        // Setear nueva informacion y persistir
        ArticuloVentaDTO articuloVenta = articuloVentaOpt.get();
        articuloVenta.setCantidad(articuloVentaDTO.getCantidad());
        articuloVenta.setSubtotal(articuloVentaDTO.getSubtotal());
        articuloVenta.setArticulo(articuloDTO);
        // articuloVenta.setArticulo(articuloOpt.get());

        ArticuloVentaModel articuloVentaModel = modelMapper.map(articuloVenta, ArticuloVentaModel.class);
        articuloVentaModel = articuloVentaRepository.save(articuloVentaModel);

        return modelMapper.map(articuloVentaModel, ArticuloVentaDTO.class);
    }

    @Override
    public ArticuloVentaDTO guardarArticuloVenta(ArticuloVentaDTO articuloVentaDTO) {
        
        ArticuloDTO articuloDTO = articuloClient.obtenerArticuloPorId(articuloVentaDTO.getArticulo().getId());
        // Optional<ArticuloDTO> articuloOpt = articuloService.obtenerPorId(articuloVentaDTO.getArticulo().getId());
        // if(articuloOpt.isEmpty()){
        //     throw new RuntimeException("Articulo no encontrado");
        // }

        // Asigar articulo a articulo venta y persistir
        articuloVentaDTO.setArticulo(articuloDTO);
        // articuloVentaDTO.setArticulo(articuloOpt.get());

        ArticuloVentaModel articuloVentaModel = modelMapper.map(articuloVentaDTO, ArticuloVentaModel.class);
        articuloVentaModel = articuloVentaRepository.save(articuloVentaModel);
        
        return modelMapper.map(articuloVentaModel, ArticuloVentaDTO.class);
    }

    @Override
    public Optional<ArticuloVentaDTO> obtenerArticuloVentaPorId(Long id) {
        return articuloVentaRepository.findById(id)
            .map(model -> modelMapper.map(model, ArticuloVentaDTO.class));
    }

    // Usado: "/articulos/eliminar/{id}"
    @Override
    public boolean existsByArticuloId(Long id) {
        return articuloVentaRepository.existsByArticuloId(id);
    }
    
}
