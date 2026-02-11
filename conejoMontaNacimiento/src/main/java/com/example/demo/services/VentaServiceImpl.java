package com.example.demo.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.example.demo.clients.ArticuloClient;
import com.example.demo.clients.RazaClient;
import com.example.demo.controllers.VentaController;
import com.example.demo.controllers.dto.ArticuloDTO;
import com.example.demo.controllers.dto.ArticuloVendidoDTO;
import com.example.demo.controllers.dto.ArticuloVentaDTO;
import com.example.demo.controllers.dto.EjemplarDTO;
import com.example.demo.controllers.dto.EjemplarVendidoDTO;
import com.example.demo.controllers.dto.EjemplarVentaDTO;
import com.example.demo.controllers.dto.ImagenDTO;
import com.example.demo.controllers.dto.ProgenitorDTO;
import com.example.demo.controllers.dto.RazaDTO;
import com.example.demo.controllers.dto.VentaDTO;
import com.example.demo.controllers.dto.VentaDetalleDTO;
import com.example.demo.models.ArticuloVentaModel;
import com.example.demo.models.ConejoModel;
import com.example.demo.models.EjemplarModel;
import com.example.demo.models.EjemplarVentaModel;
import com.example.demo.models.VentaModel;
import com.example.demo.models.enums.EstatusVenta;
import com.example.demo.repositories.ArticuloVentaRepository;
import com.example.demo.repositories.EjemplarRepository;
import com.example.demo.repositories.EjemplarVentaRepository;
import com.example.demo.repositories.VentaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class VentaServiceImpl implements IVentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IArticuloVentaService articuloVentaService;

    @Autowired
    private IEjemplarVentaService ejemplarVentaService;

    @Autowired
    private IEjemplarService ejemplarService;

    @Override
    public Page<VentaDTO> findByEstatusIsNull(int pagina, int cantidad) {
        Pageable pageable = PageRequest.of(pagina, cantidad, Sort.by("fechaEntrega").descending());
        Page<VentaModel> pageVentas = ventaRepository.findByEstatusIsNull(pageable);

        return pageVentas.map(ventaModel -> modelMapper.map(ventaModel, VentaDTO.class));
    }

    @Override
    public Page<VentaDTO> findAll(int pagina, int cantidad) {
        Pageable pageable = PageRequest.of(pagina, cantidad, Sort.by("fechaEntrega").descending());
        Page<VentaModel> pageVentas = ventaRepository.findAll(pageable);

        return pageVentas.map(ventaModel -> modelMapper.map(ventaModel, VentaDTO.class));
    }

    @Override
    public Page<VentaDTO> findByEstatus(int pagina, int cantidad, EstatusVenta estatus) {
        Pageable pageable = PageRequest.of(pagina, cantidad, Sort.by("FechaEntrega").descending());
        Page<VentaModel> pageVentas = ventaRepository.findByEstatus(pageable, estatus);

        return pageVentas.map(ventaModel -> modelMapper.map(ventaModel, VentaDTO.class));
    }

    @Override
    public List<VentaDTO> obtenerVentas() {
        List<VentaModel> listaVentas = (List<VentaModel>) ventaRepository.findAll();
        listaVentas.sort(Comparator.comparing(VentaModel::getFechaEntrega).reversed());
        //listaVentas.sort(Comparator.comparing(venta -> venta.getFechaEntrega(), Comparator.reverseOrder()));

        return listaVentas.stream()
            .map(item -> modelMapper.map(item, VentaDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<VentaDTO> obtenerVentaPorId(Long id) {
        return ventaRepository.findById(id)
            .map(model -> modelMapper.map(model, VentaDTO.class));
    }

    @Override
    @Transactional
    public VentaDTO guardarVenta(VentaDTO ventaDTO) {
        // Settear informacion del formulario al modelo
        VentaModel ventaModel = new VentaModel();
        ventaModel.setNombreCliente(ventaDTO.getNombreCliente());
        ventaModel.setVinculoContacto(ventaDTO.getVinculoContacto());
        ventaModel.setTelefono(ventaDTO.getTelefono());
        ventaModel.setFechaEntrega(ventaDTO.getFechaEntrega());
        ventaModel.setLugarEntrega(ventaDTO.getLugarEntrega());
        ventaModel.setTotalVenta(ventaDTO.getTotalVenta());
        ventaModel.setAdelanto(ventaDTO.getAdelanto());
        ventaModel.setNota(ventaDTO.getNota());
        // ventaModel.setEstatus(ventaDTO.getEstatus());

        // Persistir informacion general de la venta
        ventaRepository.save(ventaModel);
        return modelMapper.map(ventaModel, VentaDTO.class);
    }

    @Override // No funcionando, eliminar de la inetrfaz despues de terminar el modulo ventas
    public VentaDTO actualizarDatosPrincipales(Long id, VentaDTO ventaDTO) {
        
        return null;
    }

    @Autowired
    private ArticuloVentaRepository articuloVentaRepository;

    @Autowired
    private EjemplarRepository ejemplarRepository;

    @Autowired
    private EjemplarVentaRepository ejemplarVentaRepository;

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// FILTROS ARTICULOS VENTA Y EJEMPLARES VENTA (NUEVOS Y EXISTENTES)
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<ArticuloVentaDTO> filtrarArticulosNuevos(List<ArticuloVentaDTO> articulosVenta){
        return articulosVenta.stream()
            .filter(item -> 
                item.getId() == null &&
                item.getCantidad() != null &&
                item.getSubtotal() != null &&
                item.getArticulo() != null &&
                item.getArticulo().getId() != null)
            .collect(Collectors.toList());
    }

    private List<ArticuloVentaDTO> filtrarArticulosExistentes(List<ArticuloVentaDTO> articulosVenta) {
        return articulosVenta.stream()
            .filter(item -> item.getId() != null &&
                item.getCantidad() != null &&
                item.getSubtotal() != null &&
                item.getArticulo() != null &&
                item.getArticulo().getId() != null)
            .collect(Collectors.toList());
    }

    private List<EjemplarVentaDTO> filtrarEjemplaresNuevos(List<EjemplarVentaDTO> ejemplaresVenta) {
        return ejemplaresVenta.stream()
            .filter(ejeVenta -> 
                ejeVenta.getId() == null &&
                ejeVenta.getPrecio() != null &&
                ejeVenta.getEjemplar() != null &&
                ejeVenta.getEjemplar().getId() != null &&
                ejeVenta.getEjemplar().isVendido())
            .collect(Collectors.toList());
    }

    private List<EjemplarVentaDTO> filtrarEjemplaresExistentes(List<EjemplarVentaDTO> ejemplaresVenta) {
        return ejemplaresVenta.stream()
            .filter(item -> 
                item.getId() != null &&
                //item.getPrecio() != null &&
                item.getEjemplar() != null &&
                item.getEjemplar().getId() != null)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VentaDTO editarVenta(Long id, VentaDTO ventaDTO, List<Long> idsArticulosVentaEliminados, List<Long> idsNacimientosEliminados) {

        // Obtener venta original
        VentaModel ventaOriginal = ventaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("La venta con id "+id+" no fue encontrada"));

        // 1. Eliminar articulos venta eliminados en el formulario
        if(idsArticulosVentaEliminados != null && !idsArticulosVentaEliminados.isEmpty()){
            idsArticulosVentaEliminados.forEach(idArtVenta -> {
                // Obtener model de articulo venta
                ArticuloVentaModel articuloVentaModel = articuloVentaRepository.findById(idArtVenta)
                    .orElseThrow(() -> new RuntimeException("Articulo venta con id "+idArtVenta+" no encontrado"));
                
                // Desvincular articulo venta de la venta
                articuloVentaModel.setVenta(null);
                articuloVentaRepository.save(articuloVentaModel);

                // Eliminar articulo venta
                articuloVentaRepository.deleteById(articuloVentaModel.getId());
            });
        }

        // 2. Liberar ejemplares y eliminar ejemplares venta eliminados en el formulario
        if(idsNacimientosEliminados != null && !idsNacimientosEliminados.isEmpty()){
            for(EjemplarVentaModel ejemplarVentaModel : ventaOriginal.getEjemplaresVenta()){
                EjemplarModel ejemplarModel = ejemplarVentaModel.getEjemplar();

                // Liberar ejemplares y eliminar ejemplares venta
                if(idsNacimientosEliminados.contains(ejemplarModel.getNacimiento().getId())){
                    // ¿El ejemplar pertenecia al nacimiento eliminado en el formulario?
                    ejemplarModel.setVendido(false);
                    ejemplarRepository.save(ejemplarModel);

                    // Eliminar ejemplar venta
                    ejemplarVentaModel.setVenta(null);
                    ejemplarVentaRepository.save(ejemplarVentaModel);
                    ejemplarVentaRepository.deleteById(ejemplarVentaModel.getId());
                }
            }
        }

        // Obtener venta original limpia
        ventaOriginal = ventaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("La venta con id "+id+" no fue encontrada"));

        // 3. Actualizar informacion general de la venta original
        ventaOriginal.setNombreCliente(ventaDTO.getNombreCliente());
        ventaOriginal.setVinculoContacto(ventaDTO.getVinculoContacto());
        ventaOriginal.setTelefono(ventaDTO.getTelefono());
        ventaOriginal.setFechaEntrega(ventaDTO.getFechaEntrega());
        ventaOriginal.setLugarEntrega(ventaDTO.getLugarEntrega());
        ventaOriginal.setTotalVenta(ventaDTO.getTotalVenta());
        ventaOriginal.setAdelanto(ventaDTO.getAdelanto());
        ventaOriginal.setNota(ventaDTO.getNota());
        // ventaOriginal.setEstatus(ventaDTO.getEstatus());

        ventaOriginal = ventaRepository.save(ventaOriginal);
        VentaDTO venta = modelMapper.map(ventaOriginal, VentaDTO.class);

        // Listas para articulosVenta y ejemplaresVenta filtrados (nuevos y existentes)
        List<ArticuloVentaDTO> articulosNuevos = new ArrayList<>();
        List<ArticuloVentaDTO> articulosExistentes = new ArrayList<>();
        List<EjemplarVentaDTO> ejemplaresNuevos = new ArrayList<>();
        List<EjemplarVentaDTO> ejemplaresExistentes = new ArrayList<>();

        // 4. Filtrar articulosVenta y ejemplaresVenta (nuevos y existentes)
        if(ventaDTO.getArticulosVenta() != null && !ventaDTO.getArticulosVenta().isEmpty()){
            articulosNuevos = filtrarArticulosNuevos(ventaDTO.getArticulosVenta());
            articulosExistentes = filtrarArticulosExistentes(ventaDTO.getArticulosVenta());
        }
        if(ventaDTO.getEjemplaresVenta() != null && !ventaDTO.getEjemplaresVenta().isEmpty()){
            ejemplaresNuevos = filtrarEjemplaresNuevos(ventaDTO.getEjemplaresVenta());
            ejemplaresExistentes = filtrarEjemplaresExistentes(ventaDTO.getEjemplaresVenta());
        }

        // 5. Persistir articulos venta y ejemplares venta (NUEVOS)
        if(articulosNuevos != null && !articulosNuevos.isEmpty()){
            articulosNuevos.forEach(articuloVenta -> {
                articuloVenta.setVenta(venta);
                articuloVentaService.guardarArticuloVenta(articuloVenta);
            });
        }

        if(ejemplaresNuevos != null && !ejemplaresNuevos.isEmpty()){
            ejemplaresNuevos.forEach(ejemplarVenta -> {
                ejemplarVenta.setVenta(venta);
                ejemplarVentaService.guardarEjemplarVenta(ejemplarVenta);
            });
        }

        // 5. Persistir articulos venta y ejemplares venta (MODIFICADOS)
        if(articulosExistentes != null && !articulosExistentes.isEmpty()){
            articulosExistentes.forEach(articuloVenta -> articuloVentaService.editarArticuloVenta(articuloVenta.getId(), articuloVenta));
        }

        if(ejemplaresExistentes != null && !ejemplaresExistentes.isEmpty()){
            for(EjemplarVentaDTO ejemplarVentaDTO : ejemplaresExistentes){
                // ¿El ejemplar sigue vendido?
                if(ejemplarVentaDTO.getEjemplar().isVendido()){
                    ejemplarVentaService.editarEjemplarVenta(ejemplarVentaDTO.getId(), ejemplarVentaDTO);

                // Ejemplar paso a disponible
                }else{
                    // Liberar ejemplar
                    EjemplarModel ejemplarModel = ejemplarRepository.findById(ejemplarVentaDTO.getEjemplar().getId())
                        .orElseThrow(() -> new RuntimeException("El ejemplar con id "+id+" no fue encontrado"));
                    ejemplarModel.setVendido(false);
                    ejemplarRepository.save(ejemplarModel);

                    // Eliminar ejemplar venta
                    EjemplarVentaModel ejemplarVentaModel = ejemplarVentaRepository.findById(ejemplarVentaDTO.getId())
                        .orElseThrow(() -> new RuntimeException("El ejemplar venta con id "+ejemplarVentaDTO.getId()+" no fue encontrado"));
                    ejemplarVentaModel.setVenta(null);
                    ejemplarVentaRepository.save(ejemplarVentaModel);
                    ejemplarVentaRepository.deleteById(ejemplarVentaModel.getId());
                }
            }
        }

        return modelMapper.map(ventaOriginal, VentaDTO.class);
    }

    @Override
    @Transactional
    public boolean eliminarVenta(Long id) {
    // public boolean eliminarVenta(VentaDTO ventaDTO) {

        // ¿Existe la venta?
        Optional<VentaModel> ventaOpt = ventaRepository.findById(id);
        if(ventaOpt.isEmpty()){
            String msg = "La venta con id "+id+" no fue encontrada.";
            // Falta implementar logger
            throw new RuntimeException(msg);
        }

        
        VentaDTO ventaDTO = modelMapper.map(ventaOpt.get(), VentaDTO.class);

        try {
            // Eliminar articulos venta (si existen)
            if(ventaDTO.getArticulosVenta() != null && !ventaDTO.getArticulosVenta().isEmpty()){
                ventaDTO.getArticulosVenta().forEach(articuloVenta -> articuloVentaService.eliminarArticuloVentaPorId(articuloVenta.getId()));
            }
            
            // Eliminar ejemplares venta (si existen)
            if(ventaDTO.getEjemplaresVenta() != null && !ventaDTO.getEjemplaresVenta().isEmpty()){
                ventaDTO.getEjemplaresVenta().forEach(ejemplarVenta -> {
                    
                    // Liberar ejemplar
                    EjemplarDTO ejemplarDTO = ejemplarVenta.getEjemplar();
                    ejemplarDTO.setVendido(false);
                    ejemplarService.editarEjemplar(ejemplarDTO);

                    // Eliminar ejemplar venta
                    ejemplarVentaService.eliminarEjemplarVentaPorId(ejemplarVenta.getId());
                });
            }

            // Eliminar venta
            ventaRepository.deleteById(ventaDTO.getId());
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Autowired
    private ArticuloClient articuloClient;

    @Autowired
    private RazaClient razaClient;

    @Override
    @Transactional(readOnly = true)
    public VentaDetalleDTO obtenerVentaDetalle(Long ventaId) {
        VentaModel venta = ventaRepository.findById(ventaId)
            .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada"));
        
        VentaDetalleDTO ventaDetalleDTO = new VentaDetalleDTO();

        // Articulos
        if(!CollectionUtils.isEmpty(venta.getArticulosVenta())){            
            List<ArticuloVendidoDTO> articulos = venta.getArticulosVenta()
                .stream()
                .map(av -> {
                    ArticuloVendidoDTO dto = new ArticuloVendidoDTO();
                    dto.setCantidad(av.getCantidad());

                    ArticuloDTO art = articuloClient.obtenerArticuloPorId(av.getArticuloId());
                    dto.setNombre(art.getNombre());
                    dto.setSecureUrl(art.getSecureUrl());
                    dto.setPresentacion(art.getPresentacion());

                    return dto;
                })
                .toList();

            ventaDetalleDTO.setArticulos(articulos);
        }

        // Ejemplares
        if(!CollectionUtils.isEmpty(venta.getEjemplaresVenta())){
            List<EjemplarVendidoDTO> ejemplares = venta.getEjemplaresVenta()
                .stream()
                .map(ev -> {
                    EjemplarVendidoDTO ejemplar = new EjemplarVendidoDTO();
                    ejemplar.setSexo(ev.getEjemplar().getSexo());
                    ejemplar.setFechaNacimiento(ev.getEjemplar().getNacimiento().getFechaNacimiento());

                    if(!CollectionUtils.isEmpty(ev.getEjemplar().getFotos())){
                        List<ImagenDTO> imagenes = ev.getEjemplar().getFotos()
                            .stream()
                            .map(f -> new ImagenDTO(f.getSecureUrl()))
                            .toList();
                        
                        ejemplar.setImagenes(imagenes);
                    }
                    
                    ejemplar.setPadre(crearProgenitorDTO(ev.getEjemplar().getNacimiento().getMonta().getMacho()));
                    ejemplar.setMadre(crearProgenitorDTO(ev.getEjemplar().getNacimiento().getMonta().getHembra()));

                    return ejemplar;
                })
                .toList();

            ventaDetalleDTO.setEjemplares(ejemplares);
        }

        return ventaDetalleDTO;
    }

    private ProgenitorDTO crearProgenitorDTO(ConejoModel conejoModel){
        ProgenitorDTO progenitorDTO = new ProgenitorDTO();

        progenitorDTO.setNombre(conejoModel.getNombre());
        progenitorDTO.setSecureUrl(conejoModel.getSecureUrl());
        RazaDTO raza = razaClient.obtenerRazaPorId(conejoModel.getRazaId());
        progenitorDTO.setRaza(raza.getNombre());

        return progenitorDTO;
    }

/* 

    @Override
    public VentaDTO guardarVenta(VentaDTO ventaDTO) {

        // Guardar imgs en proyecto y settear imagen a nombreImagen en DTO
        for (EjemplarDTO item : ventaDTO.getEjemplares()) {
            if(item.getImagen() != null && !item.getImagen().isEmpty()){
                String nombreImagen = item.getImagen().getOriginalFilename();
                Path ruta = Paths.get("src/main/resources/static/img/ejemplares", nombreImagen);

                // Nombre sin extension de img
                String nombreSinExtension = FilenameUtils.getBaseName(item.getImagen().getOriginalFilename()); // Ejemplo: "conejoNegro"
                // Extension de img
                String extension = "." + FilenameUtils.getExtension(item.getImagen().getOriginalFilename()); // Ejemplo: ".jpg"

                int contador = 1;
                while (Files.exists(ruta)) {
                    nombreImagen = nombreSinExtension + "(" + contador + ")" + extension; // ejemplo: "conejo(1).jpg"
                    ruta = Paths.get("src/main/resources/static/img/ejemplares", nombreImagen);
                    contador++;
                }

                try {
                    Files.write(ruta, item.getImagen().getBytes());
                    item.setNombreImagen(nombreImagen);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Tranformar de VentaDTO a VentaModel
        VentaModel ventaModel = modelMapper.map(ventaDTO, VentaModel.class);

        // Asignar VentaModel a cada ArticuloVentaModel
        for (ArticuloVentaModel item : ventaModel.getArticulos()) {
            item.setVenta(ventaModel);
        }

        // Asignar VentaModel a cada EjemplarModel
        for (EjemplarModel item : ventaModel.getEjemplares()) {
            //item.setVenta(ventaModel);
        }

        // Persistir VentaModel
        VentaModel guardado = ventaRepository.save(ventaModel);

        // Mapear VentaModel a VentaDTO y retornar
        return modelMapper.map(guardado, VentaDTO.class);
    }

    @Override
    public VentaDTO actualizarDatosPrincipales(Long id, VentaDTO ventaDTO) {
        // Obtener VentaModel con ID
        VentaModel ventaModel = ventaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada"));

        // Setear informacion de VentaDTO a VentaModel
        ventaModel.setNombreCliente(ventaDTO.getNombreCliente());
        ventaModel.setVinculoContacto(ventaDTO.getVinculoContacto());
        ventaModel.setTelefono(ventaDTO.getTelefono());
        ventaModel.setFechaEntrega(ventaDTO.getFechaEntrega());
        ventaModel.setLugarEntrega(ventaDTO.getLugarEntrega());
        ventaModel.setTotalVenta(ventaDTO.getTotalVenta());
        ventaModel.setNota(ventaDTO.getNota());
        ventaModel.setEstatus(ventaDTO.getEstatus());

        // Persistir VentaModel con nueva informacion principal
        VentaModel guardado = ventaRepository.save(ventaModel);

        // Mapear VentaModel a VentaDTO y retornar
        return modelMapper.map(guardado, VentaDTO.class);
    }

    @Override
    public VentaDTO editarVenta(Long id, VentaDTO ventaDTO) {
        return null;
    }

    @Override
    public boolean eliminarVenta(Long id) {
        Optional<VentaModel> optionalVenta = ventaRepository.findById(id);

        // Si no existe, retorna false
        if(optionalVenta.isEmpty()){
            return false;
        }

        // Obtener VentaModel de Optional
        VentaModel ventaModel = optionalVenta.get();

        for(EjemplarModel item : ventaModel.getEjemplares()){
            Path ruta = Paths.get("src/main/resources/static/img/ejemplares", item.getNombreImagen());
            try {
                Files.deleteIfExists(ruta);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Eliminar la venta y todo lo asociado
        ventaRepository.deleteById(id);
        return true;
    }*/

}
