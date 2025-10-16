package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.demo.controllers.dto.ConejoDTO;
import com.example.demo.controllers.dto.MontaDTO;
import com.example.demo.controllers.dto.NacimientoDTO;
import com.example.demo.models.ConejoModel;
import com.example.demo.models.MontaModel;
import com.example.demo.models.enums.EstatusMonta;
import com.example.demo.repositories.MontaRepository;
import com.example.demo.repositories.NacimientoRepository;

@ExtendWith(MockitoExtension.class)
public class MontaServiceImplTest {
    
    @Mock
    private MontaRepository montaRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private NacimientoRepository nacimientoRepository;

    @InjectMocks
    private MontaServiceImpl montaService;

    @Autowired
    private ModelMapper modelMapper2;

    // Objects
    private ConejoModel semental, panda, peluchin, pelusa, rata, nube, castor, chocolata;
    private MontaModel sp, pp, rn, cc;

    @BeforeEach
    void setup(){
        modelMapper2 = new ModelMapper();

        // MiniLop - Inactivos
        semental = new ConejoModel(1L, null, null, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://cloudinary.com/semental.png", null, null, null, 1L);
        panda = new ConejoModel(2L, null, null, "Panda", "Hembra", null, false, 
        "Abuelita, jubilada", "123abc", "https://cloudinary.com/panda.png", null, null, null, 1L);
        // Leones - Activos
        peluchin = new ConejoModel(3L, null, null, "Peluchin", "Macho", null, true, 
        "Semental, nacido en granja", "123abc", "https://cloudinary.com/rocko.png", null, null, null, 2L);
        pelusa = new ConejoModel(4L, null, null, "Pelusa", "Hembra", null, true, 
        "Hermana del semental, nacida en granja", "123abc", "https://cloudinary.com/trueno.png", null, null, null, 2L);
        // FuzzyLop - Activos
        rata = new ConejoModel(5L, null, null, "Rata", "Macho", null, true, 
        "Traido de mexico", "123abc", "https://cloudinary.com/marino.png", null, null, null, 3L);
        nube = new ConejoModel(6L, null, null, "Nube", "Hembra", null, true, 
        "Traida de jiutepec", "123abc", "https://cloudinary.com/mexicana.png", null, null, null, 3L);
        // Enanos - Activos
        castor = new ConejoModel(7L, null, null, "castor", "Macho", null, true, 
        "Unico enanito semental en la granja", "123abc", "https://cloudinary.com/Castor.png", null, null, null, 4L);
        chocolata = new ConejoModel(8L, null, null, "chocolata", "Hembra", null, true, 
        "Enanita chocolata", "123abc", "https://cloudinary.com/Chocolata.png", null, null, null, 4L);

        sp = new MontaModel(1L, "Monta de MiniLop", LocalDate.of(2025, 8, 10), 3, EstatusMonta.PENDIENTE, panda, semental, null);
        pp = new MontaModel(2L, "Monta de Leones", LocalDate.of(2025, 9, 20), 2, EstatusMonta.EFECTIVA, pelusa, peluchin, null);
        rn = new MontaModel(3L, "Monta de FuzzyLop", LocalDate.of(2025, 10, 4), 3, EstatusMonta.PENDIENTE, nube, rata, null);
        cc = new MontaModel(4L, "Monta de Enanos", LocalDate.now(), 1, EstatusMonta.EFECTIVA, chocolata, castor, null);
    }      

    // Model to DTO
    private MontaDTO modelToDTO(MontaModel montaModel){
        return modelMapper2.map(montaModel, MontaDTO.class);
        // MontaDTO montaDTO = new MontaDTO();
        // montaDTO.setId(montaModel.getId());
        // montaDTO.setNota(montaModel.getNota());
        // montaDTO.setFechaMonta(montaModel.getFechaMonta());
        // montaDTO.setEstatus(montaModel.getEstatus());

        // ConejoDTO hembra = modelMapper2.map(montaModel.getHembra(), ConejoDTO.class);
        // ConejoDTO macho = modelMapper2.map(montaModel.getMacho(), ConejoDTO.class);
        // NacimientoDTO nacimiento = modelMapper2.map(montaModel.getNacimiento(), NacimientoDTO.class);

        // montaDTO.setHembra(hembra);
        // montaDTO.setMacho(macho);
        // montaDTO.setNacimiento(nacimiento);        

        // return montaDTO;
    }
    
    @Test
    void testobtenerMontaById(){
        when(montaRepository.findById(anyLong())).thenReturn(Optional.of(cc));
        // when(modelMapper.map(any(MontaModel.class), eq(MontaDTO.class))).thenReturn(modelToDTO(cc));
        when(modelMapper.map(any(MontaModel.class), eq(MontaDTO.class))).thenAnswer(inv -> modelToDTO(inv.getArgument(0)));

        Optional<MontaDTO> montaOpt = montaService.obtenerMontaById(4L);
        assertTrue(montaOpt.isPresent());
        assertEquals("Monta de Enanos", montaOpt.get().getNota());

        verify(montaRepository, times(1)).findById(anyLong());
        verify(modelMapper, times(1)).map(any(MontaModel.class), eq(MontaDTO.class));
    }      
}
