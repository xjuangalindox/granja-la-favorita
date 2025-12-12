package com.example.demo.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import org.bouncycastle.crypto.RuntimeCryptoException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.controllers.dto.EjemplarDTO;
import com.example.demo.controllers.dto.FotoEjemplarDTO;
import com.example.demo.controllers.dto.MontaDTO;
import com.example.demo.controllers.dto.NacimientoDTO;
import com.example.demo.models.EjemplarModel;
import com.example.demo.models.FotoEjemplarModel;
import com.example.demo.models.MontaModel;
import com.example.demo.models.NacimientoModel;
import com.example.demo.models.enums.EstatusMonta;
import com.example.demo.repositories.EjemplarRepository;
import com.example.demo.repositories.FotoEjemplarRepository;
import com.example.demo.repositories.MontaRepository;
import com.example.demo.repositories.NacimientoRepository;
import com.example.demo.util.ArchivoUtil;

@Service
public class NacimientoServiceImpl implements INacimientoService{

	// Logger para capturar errores
    private static final Logger logger = LoggerFactory.getLogger(NacimientoServiceImpl.class);

    @Autowired
    private ArchivoUtil archivoUtil;

    @Autowired
    private NacimientoRepository nacimientoRepository;

    @Autowired
    private IMontaService montaService;

    @Autowired
    private IEjemplarService ejemplarService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Page<NacimientoDTO> findAll(int pagina, int cantidad) {
        Pageable pageable = PageRequest.of(pagina, cantidad, Sort.by("fechaNacimiento").descending());
        Page<NacimientoModel> pageNacimientos = nacimientoRepository.findAll(pageable);
        
        return pageNacimientos.map(nacimientoModel -> {
            NacimientoDTO nacimientoDTO = modelMapper.map(nacimientoModel, NacimientoDTO.class);

            return nacimientoDTO;
        });
    }

    @Override
    public Page<NacimientoDTO> findNacimientosSinEjemplares(int pagina, int cantidad) {
        Pageable pageable = PageRequest.of(pagina, cantidad, Sort.by("fechaNacimiento").descending());
        Page<NacimientoModel> pageNacimientos = nacimientoRepository.findNacimientosSinEjemplares(pageable);

        return pageNacimientos.map(nacimientoModel -> modelMapper.map(nacimientoModel, NacimientoDTO.class));
    }

    @Override
    public Page<NacimientoDTO> findNacimientosConEjemplaresDisponibles(int pagina, int cantidad) {
        Pageable pageable = PageRequest.of(pagina, cantidad, Sort.by("fechaNacimiento").descending());
        Page<NacimientoModel> pageNacimientos = nacimientoRepository.findNacimientosConEjemplaresDisponibles(pageable);

        return pageNacimientos.map(nacimientoModel -> modelMapper.map(nacimientoModel, NacimientoDTO.class));
    }

    @Override
    public Page<NacimientoDTO> findNacimientosConTodosEjemplaresVendidos(int pagina, int cantidad) {
        Pageable pageable = PageRequest.of(pagina, cantidad, Sort.by("fechaNacimiento").descending());
        Page<NacimientoModel> pageNacimientos = nacimientoRepository.findNacimientosConTodosEjemplaresVendidos(pageable);

        return pageNacimientos.map(nacimientoModel -> modelMapper.map(nacimientoModel, NacimientoDTO.class));
    }



    // @Override
    // public List<NacimientoDTO> obtenerNacimientos() {
    //     List<NacimientoModel> entitiesList = (List<NacimientoModel>) nacimientoRepository.findAll();
    //     entitiesList.sort(Comparator.comparing(NacimientoModel::getFechaNacimiento).reversed()); // Obtener nacimientos mas recientes al principio
    //     //entitiesList.sort(Comparator.comparing(item -> item.getFechaNacimiento()));
        
    //     return entitiesList.stream()
    //         .map(model -> modelMapper.map(model, NacimientoDTO.class))
    //         .collect(Collectors.toList());
    // }

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*@Override
    public NacimientoDTO obtenerNacimientoPorIdMonta(Long id) {
        // Obtener MontaModel por id
        MontaModel montaModel = montaRepository.findById(id).orElse(null);
        // Obtener NacimientoModel por MontaModel
        NacimientoModel nacimientoModel = nacimientoRepository.findByMonta(montaModel).orElse(null);

        // Conversion de NacimientoModel a NacimientoDTO
        return modelMapper.map(nacimientoModel, NacimientoDTO.class);
    }*/

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Optional<NacimientoDTO> obtenerNacimientoById(Long id) {
        return nacimientoRepository.findById(id)
            .map(model -> modelMapper.map(model, NacimientoDTO.class));
    }

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Autowired
    private EjemplarRepository ejemplarRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public NacimientoDTO guardarNacimiento(NacimientoDTO nacimientoDTO) { // Funcionando, falta probar
        // 1) validar existencia de la monta
        MontaModel montaModel = montaRepository.findById(nacimientoDTO.getMonta().getId())
            .orElseThrow(() -> new RuntimeException("Monta no encontrada"));

        // 2) actualizar estatus y persistir la monta
        montaModel.setEstatus(EstatusMonta.EFECTIVA);
        montaModel = montaRepository.save(montaModel);

        // 3) crear y persistir nacimiento
        NacimientoModel nacimientoModel = new NacimientoModel();
        nacimientoModel.setFechaNacimiento(nacimientoDTO.getFechaNacimiento());
		nacimientoModel.setGazaposVivos(nacimientoDTO.getGazaposVivos());
		nacimientoModel.setGazaposMuertos(nacimientoDTO.getGazaposMuertos());
		nacimientoModel.setNota(nacimientoDTO.getNota());
        nacimientoModel.setMonta(montaModel);
        nacimientoModel = nacimientoRepository.save(nacimientoModel);

        // 4) preparar ejemplares nuevos
        if(!CollectionUtils.isEmpty(nacimientoDTO.getEjemplares())){
		// if(nacimientoDTO.getEjemplares() != null && !nacimientoDTO.getEjemplares().isEmpty()){
            nacimientoDTO.setEjemplares(filtrarEjemplaresNuevos(nacimientoDTO.getEjemplares()));
        
            // 5) validar si existen ejemplares validos
            if(!CollectionUtils.isEmpty(nacimientoDTO.getEjemplares())){
            // if(nacimientoDTO.getEjemplares() != null && !nacimientoDTO.getEjemplares().isEmpty()){

                // Para cada ejemplar, guardamos el ejemplar y lanzamos las subidas en paralelo.
                for(EjemplarDTO ejemplarDTO : nacimientoDTO.getEjemplares()){
                    
                    EjemplarModel ejemplarModel = new EjemplarModel();
                    ejemplarModel.setSexo(ejemplarDTO.getSexo());
                    ejemplarModel.setPrecio(ejemplarDTO.getPrecio());
                    ejemplarModel.setPrecioOferta(ejemplarDTO.getPrecioOferta());
                    ejemplarModel.setVendido(false);
                    ejemplarModel.setNacimiento(nacimientoModel);
                    ejemplarModel = ejemplarRepository.save(ejemplarModel); // persistido en la transacción principal

                    if (CollectionUtils.isEmpty(ejemplarDTO.getImagenes())) {
                        continue; // no hay imágenes, pasar al siguiente ejemplar
                    }

                    // ----------------------------------------------
                    // 1) Lista de futures para controlar las subidas
                    // ----------------------------------------------
                    List<CompletableFuture<Map<String, Object>>> futures = new ArrayList<>();

                    // ----------------------------------------------
                    // 2) Llamar a subirImagenAsync() por cada archivo
                    //    Esto se ejecuta EN PARALELO automáticamente
                    // ----------------------------------------------
                    for(MultipartFile imagen : ejemplarDTO.getImagenes()){
                        // subirImagenAsync() se ejecuta en otro hilo
                        CompletableFuture<Map<String, Object>> future = 
                            cloudinaryService.subirImagenAsync(imagen, "ejemplares", Optional.empty());
                        
                        futures.add(future);
                    }

                    // ----------------------------------------------
                    // 3) Esperar a que TODAS las subidas terminen
                    //    (sigue siendo fácil de entender)
                    // ----------------------------------------------
                    List<Map<String, Object>> resultados = futures.stream()
                        .map(CompletableFuture::join) // join = esperar
                        .collect(Collectors.toList());

                    // ----------------------------------------------
                    // 4) Convertir cada resultado a FotoEjemplarModel
                    // ----------------------------------------------
                    List<FotoEjemplarModel> fotos = new ArrayList<>();

                    for(Map<String, Object> result : resultados){
                        String publicId = result.get("public_id").toString();

                        FotoEjemplarModel fotoEjemplarModel = new FotoEjemplarModel();
                        fotoEjemplarModel.setPublicId(publicId);
                        fotoEjemplarModel.setSecureUrl(cloudinaryService.getUrlWithPagina(publicId));
                        fotoEjemplarModel.setEjemplar(ejemplarModel);

                        fotos.add(fotoEjemplarModel);
                    }

                    // ----------------------------------------------
                    // 5) Guardas todas las fotos en la BD
                    // ----------------------------------------------
                    fotoEjemplarRepository.saveAll(fotos);
                }
            }
        }

        return modelMapper.map(nacimientoModel, NacimientoDTO.class);
    }

    @Autowired
    private FotoEjemplarRepository fotoEjemplarRepository;

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<EjemplarDTO> filtrarEjemplaresNuevos(List<EjemplarDTO> ejemplares) {
        return ejemplares.stream()
            .filter(item -> 
                item.getId() == null &&
                (item.getImagenes() != null && !item.getImagenes().isEmpty()))
            .collect(Collectors.toList());
    }

    private List<EjemplarDTO> filtrarEjemplaresExistentes(List<EjemplarDTO> ejemplares) {
        return ejemplares.stream()
            .filter(ejemplar -> ejemplar.getId() != null)
            .collect(Collectors.toList());
    }


    // private FotoEjemplarModel persistirNuevo(FotoEjemplarDTO fotoEjemplarDTO){
    //     EjemplarModel ejemplarModel = ejemplarRepository.findById(fotoEjemplarDTO.getEjemplar().getId())
    //         .orElseThrow(() -> new RuntimeException("El ejemplar con id "+fotoEjemplarDTO.getEjemplar().getId()+" no fue encontrado"));
        
    //     FotoEjemplarModel fotoEjemplarModel = new FotoEjemplarModel();
    //     fotoEjemplarModel.setPublicId(fotoEjemplarDTO.getPublicId());
    //     fotoEjemplarModel.setSecureUrl(fotoEjemplarDTO.getSecureUrl());
    //     fotoEjemplarModel.setEjemplar(ejemplarModel);

    //     return fotoEjemplarRepository.save(fotoEjemplarModel);
    // }

    // private List<EjemplarModel> persistirNuevo(List<EjemplarDTO> ejemplarDTO){
    //     if(!CollectionUtils.isEmpty(ejemplarDTO.getImagenes())){
    //         for(MultipartFile imagen : ejemplarDTO.getImagenes()){
                
    //         }
    //     }

    //     NacimientoModel nacimientoModel = nacimientoRepository.findById(ejemplarDTO.getNacimiento().getId())
    //         .orElseThrow(() -> new RuntimeException("El nacimiento con id "+ejemplarDTO.getNacimiento().getId()+" no fue encontrado"));
        
    //     EjemplarModel ejemplarModel = new EjemplarModel();
    //     ejemplarModel.setSexo(ejemplarDTO.getSexo());
    //     ejemplarModel.setPrecio(ejemplarDTO.getPrecio());
    //     ejemplarModel.setPrecioOferta(ejemplarDTO.getPrecioOferta());
    //     ejemplarModel.setVendido(false);
    //     ejemplarModel.setNacimiento(nacimientoModel);

    //     return ejemplarRepository.save(ejemplarModel);
    // }

    // private NacimientoModel persistirNuevo(NacimientoDTO nacimientoDTO){
    //     MontaModel montaModel = montaRepository.findById(nacimientoDTO.getMonta().getId())
    //         .orElseThrow(() -> new RuntimeException("La monta con id "+nacimientoDTO.getMonta().getId()+" no fue encontrada"));
        
    //     NacimientoModel nacimientoModel = new NacimientoModel();
    //     nacimientoModel.setFechaNacimiento(nacimientoDTO.getFechaNacimiento());
    //     nacimientoModel.setGazaposVivos(nacimientoDTO.getGazaposVivos());
    //     nacimientoModel.setGazaposMuertos(nacimientoDTO.getGazaposMuertos());
    //     nacimientoModel.setNota(nacimientoDTO.getNota());
    //     nacimientoModel.setMonta(montaModel);
    //     nacimientoModel.setEjemplares(persistirNuevo(nacimientoDTO.getEjemplares()));

    //     return nacimientoRepository.save(nacimientoModel);
    // }

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    @Transactional
    public NacimientoDTO editarNacimiento(Long id, NacimientoDTO nacimientoDTO, List<Long> ejemplaresEliminados) {

        // 1. Persistir informacion general del nacimiento
        NacimientoModel nacimientoModel = nacimientoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("El nacimiento con id "+id+" no fue encontrado"));

        // 2. Setear informacion general del nacimiento
        nacimientoModel.setFechaNacimiento(nacimientoDTO.getFechaNacimiento());
        nacimientoModel.setGazaposVivos(nacimientoDTO.getGazaposVivos());
        nacimientoModel.setGazaposMuertos(nacimientoDTO.getGazaposMuertos());
        nacimientoModel.setNota(nacimientoDTO.getNota());

        // 3. Obtener ejemplares eliminados en el formulario
        List<EjemplarModel> ejemplares = new ArrayList<>();
        if(!CollectionUtils.isEmpty(ejemplaresEliminados)){
            for(Long idEjemplar : ejemplaresEliminados){
                Optional<EjemplarModel> ejemplarOpt = ejemplarRepository.findById(idEjemplar);
                ejemplares.add(ejemplarOpt.get());
            }
        }

        // 4. Eliminar fotos de ejemplares eliminados y removerlos del nacimiento
        if(!CollectionUtils.isEmpty(ejemplares)){
            ejemplares.forEach(ejemplar ->{

                if(!CollectionUtils.isEmpty(ejemplar.getFotos())){

                    // 🚀 Crear lista de futuros (cada imagen en paralelo)
                    List<CompletableFuture<Void>> futures = new ArrayList<>();

                    ejemplar.getFotos().forEach(foto ->{
                        futures.add(cloudinaryService.eliminarImagenAsync(foto.getPublicId()));
                    });

                    // 🚧 Esperar a que terminen TODAS las imágenes de este ejemplar
                    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();                    
                }

                nacimientoModel.getEjemplares().remove(ejemplar);
            });
        }
        
        // 5. Obtener ejemplares nuevos y existentes (si vienen)
        if(!CollectionUtils.isEmpty(nacimientoDTO.getEjemplares())){
            List<EjemplarDTO> ejemplaresNuevos = filtrarEjemplaresNuevos(nacimientoDTO.getEjemplares());
            List<EjemplarDTO> ejemplaresExistentes = filtrarEjemplaresExistentes(nacimientoDTO.getEjemplares());

            if(!CollectionUtils.isEmpty(ejemplaresNuevos)){
                for(EjemplarDTO ejemplarDTO : ejemplaresNuevos){
                    EjemplarModel ejemplarModel = new EjemplarModel();

                    ejemplarModel.setSexo(ejemplarDTO.getSexo());
                    ejemplarModel.setPrecio(ejemplarDTO.getPrecio());
                    ejemplarModel.setPrecioOferta(ejemplarDTO.getPrecioOferta());
                    ejemplarModel.setVendido(false);
                    ejemplarModel.setNacimiento(nacimientoModel);

                    if(!CollectionUtils.isEmpty(ejemplarDTO.getImagenes())){


                        // -------------------------------------------
                        // 🚀 Crear lista de futuros (cada imagen en paralelo)
                        // List<CompletableFuture<Void>> futures = new ArrayList<>();
                        
                        // // Por cada foto en el ejemplar
                        // for(FotoEjemplarModel foto : ejemplarDTO.getImagenes()){
                        //     futures.add(cloudinaryService.eliminarImagenAsync(foto.getPublicId())); // Eliminar fotos en paralelo
                        // }

                        // // 🚧 Esperar a que terminen TODAS las imágenes de este ejemplar
                        // CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                        // -------------------------------------------

                        // 🚀 Crear lista de futuros (cada imagen en paralelo)
                        List<CompletableFuture<Map<String, Object>>> futures = new ArrayList<>();
                        
                        //Crear lista de fotosEjemplar
                        List<FotoEjemplarModel> fotosEjemplar = new ArrayList<>();

                        for(MultipartFile imagen : ejemplarDTO.getImagenes()){
                            FotoEjemplarModel fotoEjemplarModel = new FotoEjemplarModel();

                            futures.add(cloudinaryService.subirImagenAsync(imagen, "ejemplares", Optional.empty()));
                            Map<String, Object> resultUpload = futures.get(0).join();
                            String publicId = resultUpload.get("public_id").toString();

                            fotoEjemplarModel.setPublicId(publicId);
                            fotoEjemplarModel.setSecureUrl(cloudinaryService.getUrlWithPagina(publicId));
                            
                            fotosEjemplar.add(fotoEjemplarModel);
                        }

                        // 🚧 Esperar a que terminen TODAS las imágenes de este ejemplar
                        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

                    }

                }
            }
        }

        //________________________________________________________________________________
        //________________________________________________________________________________

        // // 1. Persistir informacion general del nacimiento
        // NacimientoModel nacimientoModel = nacimientoRepository.findById(id)
        //     .orElseThrow(() -> new RuntimeException("El nacimiento con id "+id+" no fue encontrado"));

        // nacimientoModel.setFechaNacimiento(nacimientoDTO.getFechaNacimiento());
        // nacimientoModel.setGazaposVivos(nacimientoDTO.getGazaposVivos());
        // nacimientoModel.setGazaposMuertos(nacimientoDTO.getGazaposMuertos());
        // nacimientoModel.setNota(nacimientoDTO.getNota());
        // // nacimientoModel.setMonta(montaModel); Innecesario, la monta no se puede modificar
        // nacimientoModel = nacimientoRepository.save(nacimientoModel);

        // // 2. Eliminar ejemplares eliminados en el formulario, incluyendo las imagenes
        // if(!CollectionUtils.isEmpty(ejemplaresEliminados)){
        //     for(Long idEjemplar : ejemplaresEliminados){

        //         EjemplarModel ejemplarModel = ejemplarRepository.findById(idEjemplar)
        //             .orElseThrow(() -> new RuntimeException("El ejemplar con id "+idEjemplar+" no fue encontrado"));

        //         // Por cada foto en el ejemplar -> Eliminar foto Cloudinary
        //         for(FotoEjemplarModel foto : ejemplarModel.getFotos()){
        //             archivoUtil.eliminarImagenCloudinary(foto.getPublicId());
        //         }

        //         ejemplarModel.getFotos().clear();
        //         nacimientoModel.getEjemplares().remove(ejemplarModel);
        //     }

        //     nacimientoModel = nacimientoRepository.save(nacimientoModel);
        // }

        // 3. ¿El nacimiento tiene ejemplares?
        if(!CollectionUtils.isEmpty(nacimientoDTO.getEjemplares())){
            List<EjemplarDTO> ejemplaresNuevos = filtrarEjemplaresNuevos(nacimientoDTO.getEjemplares());
            List<EjemplarDTO> ejemplaresExistentes = filtrarEjemplaresExistentes(nacimientoDTO.getEjemplares());
            
            // Nombre base para imagenes
            /*String nombreConejo = montaModel.getMacho().getNombre();
            String nombreConeja = montaModel.getHembra().getNombre();
            LocalDate fechaNacimiento = nacimientoModel.getFechaNacimiento();
            String nombreBase = nombreConejo +"_"+ nombreConeja +"_"+ fechaNacimiento;*/

            // 4. Persistir ejemplares nuevos y agregarlos a la lista
            if(!CollectionUtils.isEmpty(ejemplaresNuevos)){
                for(EjemplarDTO ejemplar : ejemplaresNuevos){

                    EjemplarModel ejemplarModel = new EjemplarModel();
                    ejemplarModel.setSexo(ejemplar.getSexo());
                    ejemplarModel.setPrecio(ejemplar.getPrecio());
                    ejemplarModel.setPrecioOferta(ejemplar.getPrecioOferta());
                    ejemplarModel.setVendido(false);
                    ejemplarModel.setNacimiento(nacimientoModel);
                    ejemplarModel = ejemplarRepository.save(ejemplarModel);

                    if(!CollectionUtils.isEmpty(ejemplar.getImagenes())){
                        for(MultipartFile imagen : ejemplar.getImagenes()){
                            FotoEjemplarModel fotoEjemplarModel = new FotoEjemplarModel();
                            
                            Map<String, Object> resultUpload = archivoUtil.subirImagenCloudinary(imagen, "ejemplares", Optional.empty());
                            String publicId = resultUpload.get("public_id").toString();
                            fotoEjemplarModel.setPublicId(publicId);
                            fotoEjemplarModel.setSecureUrl(archivoUtil.getUrlWithPagina(publicId));

                            // fotoEjemplarModel.setPublicId(resultUpload.get("public_id").toString());
                            // fotoEjemplarModel.setSecureUrl(resultUpload.get("secure_url").toString());
                            fotoEjemplarModel.setEjemplar(ejemplarModel);

                            fotoEjemplarRepository.save(fotoEjemplarModel);
                        }
                    }
                }
            }

            // 5. Persistir ejemplares existentes y agregarlos a la lista
            if(!CollectionUtils.isEmpty(ejemplaresExistentes)){
                for(EjemplarDTO ejemplar : ejemplaresExistentes){

                    System.out.println("\n");
                    System.out.println("Ejemplar ID: "+ejemplar.getId());
                    System.out.println("\n");

                    EjemplarModel ejemplarModel = ejemplarRepository.findById(ejemplar.getId())
                        .orElseThrow(() -> new RuntimeException("El ejemplar con id "+ejemplar.getId()+" no fue encontrado"));

                    ejemplarModel.setSexo(ejemplar.getSexo());
                    ejemplarModel.setPrecio(ejemplar.getPrecio());
                    ejemplarModel.setPrecioOferta(ejemplar.getPrecioOferta());
                    // ejemplarModel.setVendido(false); // Modulo nacimiento no modifica la venta de ejemplares
                    // ejemplarModel.setNacimiento(nacimientoModel); // Innecesario, 
                    ejemplarModel = ejemplarRepository.save(ejemplarModel);

                    // Obtener ids de fotos existentes en el formulario
                    List<Long> idsFotosEnFormulario = ejemplar.getFotos() != null ?
                        ejemplar.getFotos().stream()
                            .map(FotoEjemplarDTO::getId)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList())
                        :
                        new ArrayList<>();

                    System.out.println("\n");
                    System.out.println("Fotos en el formulario: "+idsFotosEnFormulario.size());
                    System.out.println("\n");

                    // Obtener fotos actuales del ejemplar en la BD
                    List<FotoEjemplarModel> fotosDB = ejemplarModel.getFotos() != null ?
                        new ArrayList<>(ejemplarModel.getFotos()) : new ArrayList<>();

                    System.out.println("\n");
                    System.out.println("Fotos en DB: "+fotosDB.size());
                    System.out.println("\n");

                    // Determinar fotos a eliminar
                    List<FotoEjemplarModel> fotosAEliminar = fotosDB.stream()
                        .filter(fotoDB -> !idsFotosEnFormulario.contains(fotoDB.getId()))
                        .collect(Collectors.toList());

                    System.out.println("\n");
                    System.out.println("Fotos a eliminar: "+fotosAEliminar);
                    System.out.println("\n");
                        
                    // Eliminar fotos sobrantes (Cloudinary + BD por orphanRemoval)
                    for(FotoEjemplarModel foto : fotosAEliminar){
                        archivoUtil.eliminarImagenCloudinary(foto.getPublicId());
                        ejemplarModel.getFotos().remove(foto);

                    System.out.println("\n");
                    System.out.println("public_id eliminado: "+foto.getPublicId());
                    System.out.println("\n");
                    }

                    ejemplarModel = ejemplarRepository.save(ejemplarModel);

                    // ¿Se agregaron nuevas imagenes al ejemplar existente?
                    if(!CollectionUtils.isEmpty(ejemplar.getImagenes())){

                    System.out.println("\n");
                    System.out.println("Entre al ejemplar.getImagenes de edicion");
                    System.out.println("\n");

                        for(MultipartFile imagen : ejemplar.getImagenes()){
                            if(imagen != null && !imagen.isEmpty()){ // Evitar archivos vacios

                                FotoEjemplarModel fotoEjemplarModel = new FotoEjemplarModel();
                                
                                Map<String, Object> resultUpload = archivoUtil.subirImagenCloudinary(imagen, "ejemplares", Optional.empty());
                                String publicId = resultUpload.get("public_id").toString();
                                fotoEjemplarModel.setPublicId(publicId);
                                fotoEjemplarModel.setSecureUrl(archivoUtil.getUrlWithPagina(publicId));

                                // fotoEjemplarModel.setPublicId(resultUpload.get("public_id").toString());
                                // fotoEjemplarModel.setSecureUrl(resultUpload.get("secure_url").toString());
                                fotoEjemplarModel.setEjemplar(ejemplarModel);

                                fotoEjemplarRepository.save(fotoEjemplarModel);

                            }
                        }
                    }
                    
                }
            }
        }

        // 6. Mapear y retornar
        nacimientoModel = nacimientoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("El nacimiento con id "+id+" no fue encontrado"));

        return modelMapper.map(nacimientoModel, NacimientoDTO.class);
    }

    @Override
    @Transactional
    public boolean eliminarNacimientoById(Long id) {
        // ¿Existe el nacimiento?
        NacimientoModel nacimientoModel = nacimientoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("El nacimiento con id "+id+" no fue encontrado."));

        // Obtener monta
        MontaModel montaModel = montaRepository.findById(nacimientoModel.getMonta().getId())
            .orElseThrow(() -> new RuntimeException("La monta con id "+id+" no fue encontrada."));

        // Ejemplares disponibles en el nacimiento
        if(!CollectionUtils.isEmpty(nacimientoModel.getEjemplares())){

            // ¿Hay ejemplares vendidos? -> cancelar
            if(nacimientoModel.getEjemplares().stream().anyMatch(EjemplarModel::isVendido)) 
                throw new RuntimeException("El nacimiento tiene al menos un ejemplar vendido, debe quitar el nacimiento de la venta para eliminar.");

            // Por cada ejemplar en el nacimiento
            for(EjemplarModel ejemplar : nacimientoModel.getEjemplares()){
                
                // Fotos disponibles en el ejemplar
                if(!CollectionUtils.isEmpty(ejemplar.getFotos())){
                    
                    // 🚀 Crear lista de futuros (cada imagen en paralelo)
                    List<CompletableFuture<Void>> futures = new ArrayList<>();
                    
                    // Por cada foto en el ejemplar
                    for(FotoEjemplarModel foto : ejemplar.getFotos()){
                        futures.add(cloudinaryService.eliminarImagenAsync(foto.getPublicId())); // Eliminar fotos en paralelo
                    }

                    // 🚧 Esperar a que terminen TODAS las imágenes de este ejemplar
                    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                }
            }
        }

        // Cambiar informacion de la monta y persistir
        montaModel.setEstatus(EstatusMonta.PENDIENTE);
        montaModel.setNacimiento(null);
        montaRepository.save(montaModel);

        // Eliminar nacimiento en cascada
        nacimientoRepository.deleteById(id);
        return true;
    }

    @Autowired
    private MontaRepository montaRepository;

    @Override
    public boolean existsById(Long id) {
        return nacimientoRepository.existsById(id);
    }

    @Override
    public Optional<NacimientoDTO> findByMonta(MontaDTO montaDTO) {
        MontaModel montaModel = modelMapper.map(montaDTO, MontaModel.class);

        return nacimientoRepository.findByMonta(montaModel)
            .map(model -> modelMapper.map(model, NacimientoDTO.class));
    }

    // @Override
    // public List<NacimientoDTO> obtenerNacimientosDisponibles() {
    //     List<NacimientoModel> nacimientosConEjemplaresDisponibles = nacimientoRepository.findNacimientosConEjemplaresDisponibles();

    //     return nacimientosConEjemplaresDisponibles.stream()
    //         .map(item -> modelMapper.map(item, NacimientoDTO.class))
    //         .collect(Collectors.toList());
    // }

}
