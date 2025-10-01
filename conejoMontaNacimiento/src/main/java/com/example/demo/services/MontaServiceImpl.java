package com.example.demo.services;

import java.util.Comparator;
import java.util.List;
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

import com.example.demo.controllers.dto.MontaDTO;
import com.example.demo.models.MontaModel;
import com.example.demo.models.NacimientoModel;
import com.example.demo.models.enums.EstatusMonta;
import com.example.demo.repositories.MontaRepository;
import com.example.demo.repositories.NacimientoRepository;

@Service
public class MontaServiceImpl implements IMontaService{

	// Logger para capturar errores
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MontaServiceImpl.class);

    @Autowired
    private MontaRepository montaRepository;

    @Autowired
    private ModelMapper modelMapper;

    // @Override
    // public List<MontaDTO> obtenerMontas() {
    //     // Obtener todas las montas
    //     List<MontaModel> montas = (List<MontaModel>) montaRepository.findAll();

    //     return montas.stream()
    //         .map(montaModel -> {
    //             MontaDTO montaDTO = modelMapper.map(montaModel, MontaDTO.class); // Convertir MontaModel a MontaDTO
    //             montaDTO.setTieneNacimiento(montaDTO.getNacimiento() != null); // Verificar si tiene nacimiento

    //             return montaDTO;
    //         })
    //         .collect(Collectors.toList());
    // }

    @Override
    public Page<MontaDTO> findAll(int pagina, int cantidad) {
        Pageable pageable = PageRequest.of(pagina, cantidad, Sort.by("fechaMonta").descending());
        Page<MontaModel> montasPage = montaRepository.findAll(pageable);

        return montasPage.map(montaModel -> {
            MontaDTO montaDTO = modelMapper.map(montaModel, MontaDTO.class); // Convertir MontaModel a MontaDTO
            montaDTO.setTieneNacimiento(montaDTO.getNacimiento() != null); // Verificar si tiene nacimiento

            return montaDTO;
        });
    }

    @Override
    public Page<MontaDTO> findByEstatus(int pagina, int cantidad, EstatusMonta estatus) {
        Pageable pageable = PageRequest.of(pagina, cantidad, Sort.by("fechaMonta").descending());
        Page<MontaModel> montasPage = montaRepository.findByEstatus(pageable, estatus);

        return montasPage.map(montaModel -> {
            MontaDTO montaDTO = modelMapper.map(montaModel, MontaDTO.class); // Convertir MontaModel a MontaDTO
            montaDTO.setTieneNacimiento(montaDTO.getNacimiento() != null); // Verificar si tiene nacimiento

            return montaDTO;
        });
    }

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Optional<MontaDTO> obtenerMontaById(Long id) { // Funciona
        return montaRepository.findById(id)
            .map(model -> modelMapper.map(model, MontaDTO.class));
    }

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    @Transactional
    public MontaDTO guardarMonta(MontaDTO montaDTO) { // Funcionando, falta probar
        montaDTO.setEstatus(EstatusMonta.PENDIENTE); // Por defecto, estatus PENDIENTE

        // Persistir monta en la base de datos
        MontaModel montaModel = modelMapper.map(montaDTO, MontaModel.class);
        MontaModel guardado = montaRepository.save(montaModel);
        return modelMapper.map(guardado, MontaDTO.class);
    }

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    @Transactional
    public MontaDTO editarMonta(Long id, MontaDTO montaDTO) {
        // ¿Existe la monta?
        /*if(!montaRepository.existsById(id)){
            String msg = "La monta con id "+id+" no fue encontrada";
            logger.error(msg);
            throw new RuntimeException(msg);
        }*/

        // ¿Existe la monta?
        MontaModel montaModel = montaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("La monta con id "+id+" no fue encontrada."));
        
        // Setear nueva informacion a la monta
        montaModel.setFechaMonta(montaDTO.getFechaMonta());
        montaModel.setCantidadMontas(montaDTO.getCantidadMontas());
        // montaModel.setEstatus(montaDTO.getEstatus());
        montaModel.setNota(montaDTO.getNota());
        // El nacimiento no se actualiza, ya viene en montaModel

        montaModel = montaRepository.save(montaModel);
        return modelMapper.map(montaModel, MontaDTO.class);


        // Persistir la monta modificada en la base de datos
        /*MontaModel montaModel = modelMapper.map(montaDTO, MontaModel.class);
        MontaModel guardado = montaRepository.save(montaModel);
        return modelMapper.map(guardado, MontaDTO.class);*/
    }

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Autowired
    private NacimientoRepository nacimientoRepository;

    @Override
    @Transactional
    public boolean eliminarMontaById(Long id) { // Funcionando, falta probar

        // ¿Existe la monta?
        Optional<MontaModel> montaOpt = montaRepository.findById(id);
        if(montaOpt.isEmpty()){
            String msg = "La monta con id "+id+" no fue encontrada.";
            logger.error(msg);
            throw new RuntimeException(msg);
        }

        // ¿Tiene nacimiento registrado?
        Optional<NacimientoModel> nacimientoOpt = nacimientoRepository.findByMonta(montaOpt.get());
        if(nacimientoOpt.isPresent()){
            String msg = "La monta tiene un nacimiento registrado, primero elimine el nacimiento.";
            logger.error(msg);
            throw new RuntimeException(msg);
        }

        // ¿Se elimino la monta?
        try {
            montaRepository.deleteById(id);
        } catch (Exception e) {
            String msg = "Ocurrio un error al eliminar la monta de la base de datos.";
            logger.error(msg);
            throw new RuntimeException(msg);
        }

        return true;
    }

    @Override
    public List<MontaDTO> findByNacimientoIsNull() {
        List<MontaModel> montasSinNacimiento = montaRepository.findByNacimientoIsNull();

        return montasSinNacimiento.stream()
            .map(model -> modelMapper.map(model, MontaDTO.class))
            .collect(Collectors.toList());
    }





    /*@Override
    public boolean existsById(Long id) {
        return montaRepository.existsById(id);
    }*/
}
