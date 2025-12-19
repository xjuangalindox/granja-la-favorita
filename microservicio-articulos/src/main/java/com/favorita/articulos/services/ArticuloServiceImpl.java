package com.favorita.articulos.services;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.favorita.articulos.clients.ArticuloVentaClient;
import com.favorita.articulos.controller.dto.ArticuloDTO;
import com.favorita.articulos.model.ArticuloModel;
import com.favorita.articulos.repository.IArticuloRepository;
import com.favorita.articulos.util.ArchivoUtil;

@Service
public class ArticuloServiceImpl implements IArticuloService{

    @Autowired
    private IArticuloRepository articuloRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ArchivoUtil archivoUtil;

    @Override
    public List<ArticuloDTO> obtenerArticulosStockTrue() {
        List<ArticuloModel> articulos = (List<ArticuloModel>) articuloRepository.findByStockTrue();
        articulos.sort(Comparator.comparing(ArticuloModel::getNombre));

        return articulos.stream()
            .map(model -> modelMapper.map(model, ArticuloDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public List<ArticuloDTO> obtenerArticulos() {
        // List<ArticuloModel> articulos = (List<ArticuloModel>) articuloRepository.findByStockTrue();
        List<ArticuloModel> articulos = (List<ArticuloModel>) articuloRepository.findAll();
        articulos.sort(Comparator.comparing(ArticuloModel::getNombre));

        return articulos.stream()
            .map(model -> modelMapper.map(model, ArticuloDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<ArticuloDTO> obtenerArticuloPorId(Long id) {
        return articuloRepository.findById(id)
            .map(model -> modelMapper.map(model, ArticuloDTO.class));
    }

    @Override
    @Transactional
    public ArticuloDTO guardarArticulo(ArticuloDTO articuloDTO) {
        String nombreDTO = articuloDTO.getNombre();

        // ¿El articulo ya existe?
        if(articuloRepository.existsByNombre(nombreDTO)) throw new RuntimeException("El artículo "+nombreDTO+" ya se encuentra registrado");

        // ¿Se guardo la imagen en CLOUDINARY?
        try {
            Map<String, Object> resultUpload = archivoUtil.subirImagenCloudinary(articuloDTO.getImagen(), "articulos", Optional.empty());
            String publicId = resultUpload.get("public_id").toString();
            articuloDTO.setPublicId(publicId);
            articuloDTO.setSecureUrl(archivoUtil.getUrlWithPagina(publicId));

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        // Mapear, persistir y retornar
        ArticuloModel articuloModel = modelMapper.map(articuloDTO, ArticuloModel.class);
        articuloModel = articuloRepository.save(articuloModel);
        return modelMapper.map(articuloModel, ArticuloDTO.class);
    }

    @Override
    @Transactional
    public ArticuloDTO editarArticulo(Long id, ArticuloDTO articuloDTO) {
        ArticuloModel articuloModel = articuloRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("El articulo con id "+id+" no fue encontrado"));

        String nombreDTO = articuloDTO.getNombre();

        // Se cambio el nombre
        if(!nombreDTO.equalsIgnoreCase(articuloModel.getNombre())){
            // Articulo existente
            if(articuloRepository.existsByNombre(nombreDTO)) throw new RuntimeException("El articulo "+nombreDTO+" ya se encuentra registrado");

            // Setear nombre al model
            articuloModel.setNombre(nombreDTO);
        }

        // Se cambio la imagen
        if(articuloDTO.getImagen() != null && !articuloDTO.getImagen().isEmpty()){
            try {
                // Eliminar imagen
                archivoUtil.eliminarImagenCloudinary(articuloModel.getPublicId());
                // Subir nueva imagen
                Map<String, Object> resultUpload = archivoUtil.subirImagenCloudinary(articuloDTO.getImagen(), "articulos", Optional.empty());
                String publicId = resultUpload.get("public_id").toString();

                // Setear publicId al model
                articuloModel.setPublicId(publicId);
                articuloModel.setSecureUrl(archivoUtil.getUrlWithPagina(publicId));

            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        // Setear informacion al model
        articuloModel.setPresentacion(articuloDTO.getPresentacion());
        articuloModel.setDescripcion(articuloDTO.getDescripcion());
        articuloModel.setPrecio(articuloDTO.getPrecio());
        articuloModel.setStock(articuloDTO.getStock());

        articuloModel = articuloRepository.save(articuloModel);
        return modelMapper.map(articuloModel, ArticuloDTO.class);
    }

    //     // -------------------------------------------------------------------------------

    //     // ¿Existe el articulo?
    //     Optional<ArticuloModel> articuloOpt = articuloRepository.findById(id);
    //     if(articuloOpt.isEmpty()){
    //         String msg = "El articulo con id "+id+" no fue encontrado.";
    //         throw new RuntimeException(msg);
    //     }

    //     // Informacion original del articulo
    //     ArticuloModel articuloModel = articuloOpt.get();

    //     // SE CAMBIO EL NOMBRE DEL ARTICULO EN EL FORMULARIO
    //     if(!articuloDTO.getNombre().equalsIgnoreCase(articuloModel.getNombre())){

    //         // ¿EL ARTICULO YA ESTA REGISTRADO?
    //         if(articuloRepository.existsByNombre(articuloDTO.getNombre())){
    //             String msg = "El articulo "+articuloDTO.getNombre()+" ya se encuentra registrado.";
    //             throw new RuntimeException(msg);
    //         }

    //         // SE CAMBIO LA IMAGEN DEL ARTICULO
    //         if(articuloDTO.getImagen() != null && !articuloDTO.getImagen().isEmpty()){
    //             // SUBIR NUEVA IMAGEN Y ELIMINAR ANTIGUA (CLOUDINARY)
    //             try {
    //                 archivoUtil.eliminarImagenCloudinary(articuloModel.getPublicId());

    //                 Map<String, Object> resultUpload = archivoUtil.subirImagenCloudinary(articuloDTO.getImagen(), "articulos", Optional.of(articuloDTO.getNombre()));
    //                 String publicId = resultUpload.get("public_id").toString();
    //                 articuloModel.setPublicId(publicId);
    //                 articuloModel.setSecureUrl(archivoUtil.getUrlWithPagina(publicId));
                    
    //                 // articuloModel.setPublicId(resultUpload.get("public_id").toString());
    //                 // articuloModel.setSecureUrl(resultUpload.get("secure_url").toString());

    //             } catch (Exception e) {
    //                 throw new RuntimeException(e.getMessage());
    //             }
            
    //         // NO SE CAMBIO LA IMAGEN DEL ARTICULO
    //         }else{
    //             // RENOMBRAR IMAGEN DEL ARTICULO
    //             try {
    //                 Map<String, Object> resultRename = archivoUtil.renombrarImagenCloudinary(articuloModel.getPublicId(), "articulos", articuloDTO.getNombre());
    //                 String publicId = resultRename.get("public_id").toString();
    //                 articuloModel.setPublicId(publicId);
    //                 articuloModel.setSecureUrl(archivoUtil.getUrlWithPagina(publicId));
                    
    //                 // articuloModel.setPublicId(resultRename.get("public_id").toString());
    //                 // articuloModel.setSecureUrl(resultRename.get("secure_url").toString());    

    //             } catch (Exception e) {
    //                 throw new RuntimeException(e.getMessage());
    //             }
    //         }
    //     }

    //     // NO SE CAMBIO EL NOMBRE DEL ARTICULO EN EL FORMULARIO
    //     if(articuloDTO.getNombre().equalsIgnoreCase(articuloModel.getNombre())){

    //         // SE CAMBIO LA IMAGEN DEL ARTICULO
    //         if(articuloDTO.getImagen() != null && !articuloDTO.getImagen().isEmpty()){
    //             // ELIMINAR IMAGEN ANTIGUA Y SUBIR NUEVA IMAGEN (CLOUDINARY)
    //             try {
    //                 archivoUtil.eliminarImagenCloudinary(articuloModel.getPublicId());
                    
    //                 Map<String, Object> resultUpload = archivoUtil.subirImagenCloudinary(articuloDTO.getImagen(), "articulos", Optional.of(articuloDTO.getNombre()));
    //                 String publicId = resultUpload.get("public_id").toString();
    //                 articuloModel.setPublicId(publicId);
    //                 articuloModel.setSecureUrl(archivoUtil.getUrlWithPagina(publicId));
                    
    //                 // articuloModel.setPublicId(resultUpload.get("public_id").toString());
    //                 // articuloModel.setSecureUrl(resultUpload.get("secure_url").toString());                    

    //             } catch (Exception e) {
    //                 throw new RuntimeException(e.getMessage());
    //             }
    //         }
    //     }
        
    //     // Settear nueva informacion al articulo original
    //     articuloModel.setNombre(articuloDTO.getNombre());
    //     articuloModel.setDescripcion(articuloDTO.getDescripcion());
    //     articuloModel.setPresentacion(articuloDTO.getPresentacion());
    //     articuloModel.setPrecio(articuloDTO.getPrecio());
    //     articuloModel.setStock(articuloDTO.getStock());

    //     // Persistir y retornar
    //     articuloModel = articuloRepository.save(articuloModel);
    //     return modelMapper.map(articuloModel, ArticuloDTO.class);
    // }

    @Autowired
    private ArticuloVentaClient articuloVentaClient;

    @Override
    @Transactional
    public boolean eliminarArticuloPorId(Long id) {
        ArticuloModel articuloModel = articuloRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("El articulo con id "+id+" no fue encontrado"));

        // Si existe algún articuloventa con este articulo, no permitir eliminar
        if(articuloVentaClient.existsByArticuloId(id)){
            throw new RuntimeException("El articulo "+articuloModel.getNombre()+" esta siendo utilizado actualmente, primero debe desvincular para eliminar.");
        }

        archivoUtil.eliminarImagenCloudinary(articuloModel.getPublicId());    
        articuloRepository.deleteById(id);
        return true;
    }
    
}
