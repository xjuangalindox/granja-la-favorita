package com.example.demo.services;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.clients.RazaClient;
import com.example.demo.controllers.dto.ConejoDTO;
import com.example.demo.controllers.dto.RazaDTO;
import com.example.demo.models.ConejoModel;
import com.example.demo.models.MontaModel;
import com.example.demo.repositories.ConejoRepository;
import com.example.demo.repositories.MontaRepository;
import com.example.demo.util.ArchivoUtil;

@Service
public class ConejoServiceImpl implements IConejoService{

	// Logger para capturar errores
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ConejoServiceImpl.class);

    @Autowired
    private ArchivoUtil archivoUtil;

    @Autowired
    private ConejoRepository conejoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RazaClient razaClient;

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Pageable orderBy(int pagina, int cantidad, String ordenarPor){
        Pageable pageable;
        
        switch (ordenarPor) {
            case "nombre":
                pageable = PageRequest.of(pagina, cantidad, Sort.by("nombre").ascending());
                break;

            case "inicioRecreo":
                pageable = PageRequest.of(pagina, cantidad, Sort.by("inicioRecreo").descending());
                break;
        
            default:
                pageable = PageRequest.of(pagina, cantidad, Sort.by(ordenarPor).ascending());
                break;
        }

        return pageable;
    }

    @Override
    public Page<ConejoDTO> findAll(int pagina, int cantidad, String ordenarPor) {
        Page<ConejoModel> pageConejos = conejoRepository.findAll(orderBy(pagina, cantidad, ordenarPor));

        // Pageable pageable = PageRequest.of(pagina, cantidad, Sort.by("nombre").ascending());
        // Page<ConejoModel> pageConejos = conejoRepository.findAll(pageable);

        return pageConejos.map(conejoModel -> {
            RazaDTO razaDTO = razaClient.obtenerRazaPorId(conejoModel.getRazaId());
            ConejoDTO conejoDTO = modelMapper.map(conejoModel, ConejoDTO.class);
            conejoDTO.setRaza(razaDTO);

            return conejoDTO;
        });
    }

    @Override
    public Page<ConejoDTO> findBySexo(int pagina, int cantidad, String sexo, String ordenarPor) {
        Page<ConejoModel> pageConejos = conejoRepository.findBySexo(orderBy(pagina, cantidad, ordenarPor), sexo);

        // Pageable pageable = PageRequest.of(pagina, cantidad, Sort.by("nombre").ascending());
        // Page<ConejoModel> pageConejos = conejoRepository.findBySexo(pageable, sexo);

        return pageConejos.map(conejoModel -> {
            RazaDTO razaDTO = razaClient.obtenerRazaPorId(conejoModel.getRazaId());
            ConejoDTO conejoDTO = modelMapper.map(conejoModel, ConejoDTO.class);
            conejoDTO.setRaza(razaDTO);

            return conejoDTO;
        });
    }

    @Override
    public Optional<ConejoDTO> obtenerConejoById(Long id) {
        return conejoRepository.findById(id)
            .map(model -> {
                // Obtener RazaDTO
                RazaDTO razaDTO = razaClient.obtenerRazaPorId(model.getRazaId());
                // RazaDTO razaDTO = razaService.obtenerRazaPorId(model.getRazaId());

                // Mapear ConejoModel a ConejoDTO
                ConejoDTO conejoDTO = modelMapper.map(model, ConejoDTO.class);
                conejoDTO.setRaza(razaDTO);

                return conejoDTO;
            });

        // return conejoRepository.findById(id)
        //     .map(model -> modelMapper.map(model, ConejoDTO.class));
    }

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<ConejoDTO> obtenerConejos() {
        List<ConejoModel> entitiesList = (List<ConejoModel>) conejoRepository.findAll();
        entitiesList.sort(Comparator.comparing(ConejoModel::getNombre)); // Filtar conejos por nombre ASC
        //entitiesList.sort(Comparator.comparing(item -> item.getNombre()));

        return entitiesList.stream()
            .map(model -> {
                RazaDTO razaDTO = razaClient.obtenerRazaPorId(model.getRazaId());
                // RazaDTO razaDTO = razaService.obtenerRazaPorId(model.getRazaId());

                ConejoDTO conejoDTO = modelMapper.map(model, ConejoDTO.class);
                conejoDTO.setRaza(razaDTO);
                return conejoDTO;
            })
            .collect(Collectors.toList());

        // return entitiesList.stream()
        //     .map(model -> modelMapper.map(model, ConejoDTO.class))
        //     .collect(Collectors.toList());
    }

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    @Transactional
    public ConejoDTO guardarConejo(ConejoDTO conejoDTO) {

        // ¿El conejo ya esta registrado?
        // if(existsByNombre(conejoDTO.getNombre())){
        if(conejoRepository.existsByNombre(conejoDTO.getNombre())){ // Corregido para el test
            String msg = "El conejo "+conejoDTO.getNombre()+" ya se encuentra registrado";
            logger.error(msg);
            throw new RuntimeException(msg);
        }

        // ¿Se subio la imagen a CLOUDIINARY.COM?
        try {
            Map<String, Object> resultUpload = archivoUtil.subirImagenCloudinary(conejoDTO.getImagen(), "conejos", Optional.of(conejoDTO.getNombre()));
            String publicId = resultUpload.get("public_id").toString();
            conejoDTO.setPublicId(publicId);
            conejoDTO.setSecureUrl(archivoUtil.getUrlWithPagina(publicId));
            // conejoDTO.setSecureUrl(resultUpload.get("secure_url").toString());

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        } 

        // Obtener RazaDTO para mapear
        RazaDTO razaDTO = razaClient.obtenerRazaPorId(conejoDTO.getRaza().getId());
        // RazaDTO razaDTO = razaService.obtenerRazaPorId(conejoDTO.getRaza().getId());

        // Mapeo con razaId
        ConejoModel conejoModel = modelMapper.map(conejoDTO, ConejoModel.class);
        conejoModel.setRazaId(razaDTO.getId());

        // Persistir
        conejoModel = conejoRepository.save(conejoModel);

        // Mapeo con RazaDTO
        ConejoDTO conejo = modelMapper.map(conejoModel, ConejoDTO.class);
        conejo.setRaza(razaDTO);

        return conejo;
    }

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    @Transactional
    public ConejoDTO editarConejo(Long id, ConejoDTO conejoDTO) {
        // ¿Existe el conejo?
        ConejoDTO conejoOriginal = obtenerConejoById(id)
            .orElseThrow(() -> {
                String msg = "El conejo con id "+id+" no fue encontrado.";
                logger.error(msg);
                return new RuntimeException(msg);
            });


        // SE CAMBIO EL NOMBRE DEL CONEJO EN EL FORMULARIO
        if(!conejoDTO.getNombre().equals(conejoOriginal.getNombre())){
            
            // ¿EL CONEJO YA SE ENCUENTRA REGISTRADO?
            if(conejoRepository.existsByNombre(conejoDTO.getNombre())){
                String msg = "El conejo "+conejoDTO.getNombre()+" ya se encuentra registrado";
                logger.error(msg);
                throw new RuntimeException(msg);
            }

            // if(existsByNombre(conejoDTO.getNombre())){
            //     String msg = "El conejo "+conejoDTO.getNombre()+" ya se encuentra registrado";
            //     logger.error(msg);
            //     throw new RuntimeException(msg);
            // }

            // SE CAMBIO LA IMAGEN DEL CONEJO EN EL FORMULARIO (funcionando)
            if(conejoDTO.getImagen() != null && !conejoDTO.getImagen().isEmpty()){
                try {
                    // Subir imagen nueva a CLOUDINARY.COM
                    Map<String, Object> resultUpload = archivoUtil.subirImagenCloudinary(conejoDTO.getImagen(), "conejos", Optional.of(conejoDTO.getNombre()));
                    String publicId = resultUpload.get("public_id").toString();
                    conejoDTO.setPublicId(publicId);
                    conejoDTO.setSecureUrl(archivoUtil.getUrlWithPagina(publicId));
                    // conejoDTO.setSecureUrl(resultUpload.get("secure_url").toString());
                    
                    // Eliminar imagen antigua en CLOUDINARY.COM
                    archivoUtil.eliminarImagenCloudinary(conejoOriginal.getPublicId());

                } catch (Exception e) {
                    logger.error(e.getMessage());
                    throw new RuntimeException(e.getMessage());
                }
            
            // NO SE CAMBIO LA IMAGEN DEL CONEJO EN EL FORMULARIO
            }else{
                try {
                    // Crear copia de la imagen antigua
                    Map<String, Object> resultRename = archivoUtil.renombrarImagenCloudinary(conejoOriginal.getPublicId(), "conejos", conejoDTO.getNombre());
                    // conejoDTO.setPublicId(resultRename.get("public_id").toString());
                    // conejoDTO.setSecureUrl(resultRename.get("secure_url").toString());

                    String publicId = resultRename.get("public_id").toString();
                    conejoDTO.setPublicId(publicId);
                    conejoDTO.setSecureUrl(archivoUtil.getUrlWithPagina(publicId));
                    

                } catch (Exception e) {
                    logger.error(e.getMessage());
                    throw new RuntimeException(e.getMessage());
                }
            }
        }

        // NO SE CAMBIO EL NOMBRE DEL CONEJO EN EL FORMULARIO
        if(conejoDTO.getNombre().equals(conejoOriginal.getNombre())){

            // SE CAMBIO LA IMAGEN DEL CONEJO EN EL FORMULARIO (funcionando)
            if(conejoDTO.getImagen() != null && !conejoDTO.getImagen().isEmpty()){
                try {
                    // Eliminar imagen antigua del conejo
                    archivoUtil.eliminarImagenCloudinary(conejoDTO.getPublicId());

                    // Crear nueva imagen
                    Map<String, Object> resultUpload = archivoUtil.subirImagenCloudinary(conejoDTO.getImagen(), "conejos", Optional.of(conejoDTO.getNombre()));
                    String publicId = resultUpload.get("public_id").toString();
                    conejoDTO.setPublicId(publicId);
                    conejoDTO.setSecureUrl(archivoUtil.getUrlWithPagina(publicId));
                    // conejoDTO.setSecureUrl(resultUpload.get("secure_url").toString());

                } catch (Exception e) {
                    logger.error(e.getMessage());
                    throw new RuntimeException(e.getMessage());
                }
            }
        }

        // Obtener RazaDTO para mapear
        RazaDTO razaDTO = razaClient.obtenerRazaPorId(conejoDTO.getRaza().getId());
        // RazaDTO razaDTO = razaService.obtenerRazaPorId(conejoDTO.getRaza().getId());

        // Mapeo con razaId
        ConejoModel conejoModel = modelMapper.map(conejoDTO, ConejoModel.class);
        conejoModel.setRazaId(razaDTO.getId());

        // Persistir
        conejoModel = conejoRepository.save(conejoModel);

        // Mapeo con RazaDTO
        ConejoDTO conejo = modelMapper.map(conejoModel, ConejoDTO.class);
        conejo.setRaza(razaDTO);

        return conejo;
    }

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	@Autowired 
	private MontaRepository montaRepository;

    @Override
    @Transactional
    public boolean eliminarConejoById(Long id) {

        // ¿Existe el conejo?
        Optional<ConejoModel> conejoOpt = conejoRepository.findById(id);
        if(conejoOpt.isEmpty()){
            String msg = "El conejo con id "+id+" no fue encontrado.";
            logger.error(msg);
            throw new RuntimeException(msg);
        }

        // ¿El conejo se encuentra en uso?
        ConejoModel conejoModel = conejoOpt.get();
        String sexoConejo = conejoModel.getSexo();
        boolean enUso;

        if(sexoConejo.equalsIgnoreCase("macho")){
            enUso = montaRepository.existsByMacho(conejoModel);
        }else{
            enUso = montaRepository.existsByHembra(conejoModel);
        }

        if(enUso){
            String msg = conejoModel.getNombre()+" está en uso actualmente, puede darlo de baja o desvincularlo para eliminar.";
            logger.error(msg);
            throw new RuntimeException(msg);
        }

        // ¿Se elimino la imagen en CLODUINARY?
        try {
            archivoUtil.eliminarImagenCloudinary(conejoModel.getPublicId());
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

        try {
            conejoRepository.deleteById(id);
        } catch (Exception e) {
            String msg = "Ocurrio un error el eliminar el conejo de la base de datos.";
            logger.error(msg);
            throw new RuntimeException(msg);
        }

        // // ¿El conejo existe?
        // if(!conejoRepository.existsById(id)){
        //     String msg = "El conejo con id "+id+" no se encuentra registrado en la base de datos";
        //     logger.error(msg);
        //     throw new RuntimeException(msg);
        // }

        // // ¿Se elimino la imagen en CLOUDINARY.COM?
        // ConejoDTO conejoDTO = obtenerConejoById(id)
        //     .orElseThrow(() -> {
        //         String msg = "El conejo con id "+id+" no se encuentra registrado en la base de datos";
        //         logger.error(msg);
        //         return new RuntimeException(msg);
        //     });

        // try {
        //     archivoUtil.eliminarImagenCloudinary(conejoDTO.getPublicId());

        // } catch (Exception e) {
        //     logger.error(e.getMessage());
        //     throw new RuntimeException(e.getMessage());
        // }

        // ¿Se elimino el conejo de la base de datos?
        // try {
        //     conejoRepository.deleteById(id);

        // } catch (Exception e) {
        //     String msg = "Ocurrio un error al eliminar el conejo de la base de datos";
        //     logger.error(msg);
        //     throw new RuntimeException(msg);
        // }

        return true;
    }

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<ConejoDTO> obtenerConejosPorSexo(String sexo) {
        List<ConejoModel> entitiesList = conejoRepository.findBySexoIgnoreCase(sexo);
        entitiesList.sort(Comparator.comparing(ConejoModel::getNombre));

        // Mapear ConejoModel a ConejoDTO y obtener RazaDTO
        List<ConejoDTO> conejoDTOList = entitiesList.stream()
            .map(conejoModel -> {
                RazaDTO razaDTO = razaClient.obtenerRazaPorId(conejoModel.getRazaId());

                ConejoDTO conejoDTO = modelMapper.map(conejoModel, ConejoDTO.class);
                conejoDTO.setRaza(razaDTO);
                return conejoDTO;
            })
            .collect(Collectors.toList());

        // Retorna la lista de ConejoDTO
        return conejoDTOList;
    }

    @Override
    public List<ConejoDTO> obtenerConejosActivosPorSexo(String sexo) {
        List<ConejoModel> entitiesList = conejoRepository.findBySexoIgnoreCaseAndActivoTrue(sexo);
        entitiesList.sort(Comparator.comparing(ConejoModel::getNombre));
        //entitiesList.sort(Comparator.comparing(item -> item.getNombre()));

        return entitiesList.stream()
            .map(model -> modelMapper.map(model, ConejoDTO.class))
            .collect(Collectors.toList());
    }

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean existsByNombre(String nombre) {
        return conejoRepository.existsByNombre(nombre);
    }

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean existsById(Long id) {
        return conejoRepository.existsById(id);
    }

    @Override
    public boolean existsByRazaId(Long id){
        return conejoRepository.existsByRazaId(id);
    }


}

