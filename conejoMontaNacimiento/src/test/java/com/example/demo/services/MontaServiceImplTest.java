package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.demo.controllers.dto.ConejoDTO;
import com.example.demo.controllers.dto.MontaDTO;
import com.example.demo.controllers.dto.NacimientoDTO;
import com.example.demo.controllers.dto.RazaDTO;
import com.example.demo.models.ConejoModel;
import com.example.demo.models.EjemplarModel;
import com.example.demo.models.MontaModel;
import com.example.demo.models.NacimientoModel;
import com.example.demo.models.enums.EstatusMonta;
import com.example.demo.models.enums.EstatusVenta;
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
    private EjemplarModel eje1, eje2, eje3, eje4, eje5;
    
    private List<EjemplarModel> ejemplares;
    private NacimientoModel n1, n2, n3, n4;

    @BeforeEach
    void setup(){
        modelMapper2 = new ModelMapper();

        // MiniLop - Inactivos
        semental = new ConejoModel(1L, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://cloudinary.com/semental.png", null, null, null, 1L, null);
        panda = new ConejoModel(2L, "Panda", "Hembra", null, false, 
        "Abuelita, jubilada", "123abc", "https://cloudinary.com/panda.png", null, null, null, 1L, null);
        // Leones - Activos
        peluchin = new ConejoModel(3L, "Peluchin", "Macho", null, true, 
        "Semental, nacido en granja", "123abc", "https://cloudinary.com/rocko.png", null, null, null, 2L, null);
        pelusa = new ConejoModel(4L, "Pelusa", "Hembra", null, true, 
        "Hermana del semental, nacida en granja", "123abc", "https://cloudinary.com/trueno.png", null, null, null, 2L, null);
        // FuzzyLop - Activos
        rata = new ConejoModel(5L, "Rata", "Macho", null, true, 
        "Traido de mexico", "123abc", "https://cloudinary.com/marino.png", null, null, null, 3L, null);
        nube = new ConejoModel(6L, "Nube", "Hembra", null, true, 
        "Traida de jiutepec", "123abc", "https://cloudinary.com/mexicana.png", null, null, null, 3L, null);
        // Enanos - Activos
        castor = new ConejoModel(7L, "castor", "Macho", null, true, 
        "Unico enanito semental en la granja", "123abc", "https://cloudinary.com/Castor.png", null, null, null, 4L, null);
        chocolata = new ConejoModel(8L, "chocolata", "Hembra", null, true, 
        "Enanita chocolata", "123abc", "https://cloudinary.com/Chocolata.png", null, null, null, 4L, null);

        // Ejemplares
        eje1 = new EjemplarModel(1L, "Macho", false, 300.00, null, n4, null);
        eje2 = new EjemplarModel(2L, "Hembra", false, 300.00, null, n4, null);
        eje3 = new EjemplarModel(3L, "Macho", false, 300.00, null, n4, null);
        eje4 = new EjemplarModel(4L, "Hemba", false, 300.00, null, n4, null);
        eje5 = new EjemplarModel(5L, "Macho", false, 300.00, null, n4, null);

        // Ejemplares
        ejemplares = List.of(eje1, eje2, eje3, eje4, eje5);

        // Nacimientos
        n1 = new NacimientoModel(1L, LocalDate.of(2025, 11, 4), 6, 0, "Ratitas", null, ejemplares);
        n2 = new NacimientoModel(2L, LocalDate.of(2025, 12, 4), 6, 0, "Ratitas", null, ejemplares);
        n3 = new NacimientoModel(3L, LocalDate.now(), 6, 0, "Ratitas", null, ejemplares);
        n4 = new NacimientoModel(4L, LocalDate.now(), 6, 0, "Ratitas", null, ejemplares);

        // Montas
        sp = new MontaModel(1L, "Monta de MiniLop", LocalDate.of(2025, 8, 10), 3, EstatusMonta.PENDIENTE, panda, semental, n1);
        pp = new MontaModel(2L, "Monta de Leones", LocalDate.of(2025, 9, 20), 2, EstatusMonta.EFECTIVA, pelusa, peluchin, n2);
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

    @Test
    void testFindAll(){
        List<MontaModel> lista = List.of(sp, pp, rn, cc);
        Page<MontaModel> pageMontas = new PageImpl<>(lista);

        when(montaRepository.findAll(any(Pageable.class))).thenReturn(pageMontas);
        when(modelMapper.map(any(MontaModel.class), eq(MontaDTO.class))).thenAnswer(inv -> modelToDTO(inv.getArgument(0)));

        Page<MontaDTO> pageDTO = montaService.findAll(0, 5);
        assertNotNull(pageDTO);
        assertEquals(0, pageDTO.getNumber()); // page number
        assertEquals(4, pageDTO.getNumberOfElements()); // elements number
        assertEquals(4, pageDTO.getTotalElements()); // elements total db
        assertEquals(1, pageDTO.getTotalPages()); // pages total db

        List<MontaDTO> listaDTO = pageDTO.getContent();
        assertNotNull(listaDTO);
        assertEquals(4, listaDTO.size());
        assertEquals("Monta de MiniLop", listaDTO.get(0).getNota());
        assertEquals("Monta de Leones", listaDTO.get(1).getNota());
        assertEquals("Monta de FuzzyLop", listaDTO.get(2).getNota());
        assertEquals("Monta de Enanos", listaDTO.get(3).getNota());

        verify(montaRepository, times(1)).findAll(any(Pageable.class));
        verify(modelMapper, atMost(4)).map(any(MontaModel.class), eq(MontaDTO.class));
    }

    @Test
    void testEliminarMontaById_Success(){
        when(montaRepository.findById(anyLong())).thenReturn(Optional.of(cc));
        when(nacimientoRepository.findByMonta(any(MontaModel.class))).thenReturn(Optional.empty());
        doNothing().when(montaRepository).deleteById(anyLong());

        boolean eliminado = montaService.eliminarMontaById(4L);
        assertTrue(eliminado);

        verify(montaRepository).findById(anyLong());
        verify(nacimientoRepository, times(1)).findByMonta(any(MontaModel.class));
        verify(montaRepository).deleteById(anyLong());
    }

    @Test
    void testEliminarMontaById_MontaNotFound(){
        when(montaRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> montaService.eliminarMontaById(4L));
        assertNotNull(exception);
        assertEquals("La monta con id "+4L+" no fue encontrada.", exception.getMessage());

        verify(montaRepository).findById(anyLong());
        verify(nacimientoRepository, never()).findByMonta(any(MontaModel.class));
        verify(montaRepository, never()).deleteById(anyLong());
    }

    @Test
    void testEliminarMontaById_NacimientoIsPresent(){
        when(montaRepository.findById(anyLong())).thenReturn(Optional.of(cc));
        when(nacimientoRepository.findByMonta(any(MontaModel.class))).thenReturn(Optional.of(n4));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> montaService.eliminarMontaById(4L));
        assertNotNull(exception);
        assertEquals("La monta tiene un nacimiento registrado, primero elimine el nacimiento.", exception.getMessage());
    
        verify(montaRepository).findById(anyLong());
        verify(nacimientoRepository).findByMonta(any(MontaModel.class));
        verify(montaRepository, never()).deleteById(anyLong());
    }

    @Test
    void testEliminarMontaById_ErrorPersis(){
        when(montaRepository.findById(anyLong())).thenReturn(Optional.of(cc));
        when(nacimientoRepository.findByMonta(any(MontaModel.class))).thenReturn(Optional.empty());
        doThrow(new RuntimeException()).when(montaRepository).deleteById(anyLong());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> montaService.eliminarMontaById(4L));
        assertNotNull(exception);
        assertEquals("Ocurrio un error al eliminar la monta de la base de datos.", exception.getMessage());

        verify(montaRepository).findById(anyLong());
        verify(nacimientoRepository).findByMonta(any(MontaModel.class));
        verify(montaRepository).deleteById(anyLong());
    }

    @Test
    void testFindByEstatus(){
        // given
        List<MontaModel> lista = List.of(pp, cc);
        Page<MontaModel> pageMontas = new PageImpl<>(lista);

        // when
        when(montaRepository.findByEstatus(any(Pageable.class), any(EstatusMonta.class))).thenReturn(pageMontas);
        when(modelMapper.map(any(MontaModel.class), eq(MontaDTO.class))).thenAnswer(inv -> modelToDTO(inv.getArgument(0)));

        // then
        Page<MontaDTO> pageDTOS = montaService.findByEstatus(0, 5, EstatusMonta.EFECTIVA);
        assertNotNull(pageDTOS);
        assertEquals(0, pageMontas.getNumber());
        assertEquals(2, pageDTOS.getNumberOfElements());
        assertEquals(1, pageDTOS.getTotalPages());
        assertEquals(2, pageMontas.getTotalElements());

        List<MontaDTO> listaDTO = pageDTOS.getContent();
        assertNotNull(listaDTO);
        assertEquals(2, listaDTO.size());
        assertEquals(EstatusMonta.EFECTIVA, listaDTO.get(0).getEstatus());
        assertEquals("EFECTIVA", listaDTO.get(1).getEstatus().name());
        assertEquals("Monta de Leones", listaDTO.get(0).getNota());
        assertEquals("Monta de Enanos", listaDTO.get(1).getNota());
    }

    @Test
    void testFindByNacimientoIsNull(){
        List<MontaModel> lista = List.of(rn, cc);

        when(montaRepository.findByNacimientoIsNull()).thenReturn(lista);
        when(modelMapper.map(any(MontaModel.class), eq(MontaDTO.class))).thenAnswer(inv -> modelToDTO(inv.getArgument(0)));

        List<MontaDTO> listaDTO = montaService.findByNacimientoIsNull();
        assertNotNull(listaDTO);
        assertEquals(2, listaDTO.size());
        assertEquals("Monta de FuzzyLop", listaDTO.get(0).getNota());
        assertEquals(null, listaDTO.get(0).getNacimiento());
        assertEquals("Monta de Enanos", listaDTO.get(1).getNota());
        assertEquals(null, listaDTO.get(1).getNacimiento());
    }

    @Test
    void testGuardarMonta(){
        // given
        RazaDTO minilop = new RazaDTO(1L, "MiniLop");

        ConejoDTO sementalDTO = new ConejoDTO(1L, null, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://cloudinary.com/semental.png", null, null, null, minilop, null);
        ConejoDTO pandaDTO = new ConejoDTO(2L, null, "Panda", "Hembra", null, false, 
        "Abuelita, jubilada", "123abc", "https://cloudinary.com/panda.png", null, null, null, minilop, null);

        // when
        MontaDTO spInput = new MontaDTO(null, "Monta de MiniLop", LocalDate.of(2025, 10, 4), 3, EstatusMonta.PENDIENTE, pandaDTO, sementalDTO, null, false);
        MontaModel spBefore = new MontaModel(null, "Monta de MiniLop", LocalDate.of(2025, 10, 4), 3, EstatusMonta.PENDIENTE, panda, semental, null);

        MontaModel spAfter = new MontaModel(1L, "Monta de MiniLop", LocalDate.of(2025, 10, 4), 3, EstatusMonta.PENDIENTE, panda, semental, null);
        MontaDTO spOutput = new MontaDTO(1L, "Monta de MiniLop", LocalDate.of(2025, 10, 4), 3, EstatusMonta.PENDIENTE, pandaDTO, sementalDTO, null, false);
    
        when(modelMapper.map(spInput, MontaModel.class)).thenReturn(spBefore);
        when(montaRepository.save(any(MontaModel.class))).thenReturn(spAfter);
        when(modelMapper.map(spAfter, MontaDTO.class)).thenReturn(spOutput);

        // then
        MontaDTO result = montaService.guardarMonta(spInput);
        assertNotNull(result);
    }

    @Test
    void testEditarMonta_Success(){
        // given
        RazaDTO minilop = new RazaDTO(1L, "MiniLop");

        ConejoDTO sementalDTO = new ConejoDTO(1L, null, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://cloudinary.com/semental.png", null, null, null, minilop, null);
        ConejoDTO pandaDTO = new ConejoDTO(2L, null, "Panda", "Hembra", null, false, 
        "Abuelita, jubilada", "123abc", "https://cloudinary.com/panda.png", null, null, null, minilop, null);

        // when
        MontaModel spOriginal = new MontaModel(1L, "Monta de MiniLop Original", LocalDate.of(2025, 10, 4), 3, EstatusMonta.PENDIENTE, panda, semental, null);

        MontaDTO spInput = new MontaDTO(null, "Monta de MiniLop Update", LocalDate.of(2025, 10, 4), 3, EstatusMonta.PENDIENTE, pandaDTO, sementalDTO, null, false);

        MontaModel spAfter = new MontaModel(1L, "Monta de MiniLop Update", LocalDate.of(2025, 10, 4), 3, EstatusMonta.PENDIENTE, panda, semental, null);
        MontaDTO spOutput = new MontaDTO(1L, "Monta de MiniLop Update", LocalDate.of(2025, 10, 4), 3, EstatusMonta.PENDIENTE, pandaDTO, sementalDTO, null, false);
        
        when(montaRepository.findById(anyLong())).thenReturn(Optional.of(spOriginal));
        when(montaRepository.save(any(MontaModel.class))).thenReturn(spAfter);
        when(modelMapper.map(any(MontaModel.class), eq(MontaDTO.class))).thenReturn(spOutput);

        MontaDTO result = montaService.editarMonta(1L, spInput);
        assertNotNull(result);
        
        verify(montaRepository).findById(anyLong());
        verify(montaRepository).save(any(MontaModel.class));
        verify(modelMapper, times(1)).map(any(MontaModel.class), eq(MontaDTO.class));
    }

    @Test
    void testEditarMonta_NotFound(){
        // given
        RazaDTO minilop = new RazaDTO(1L, "MiniLop");

        ConejoDTO sementalDTO = new ConejoDTO(1L, null, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://cloudinary.com/semental.png", null, null, null, minilop, null);
        ConejoDTO pandaDTO = new ConejoDTO(2L, null, "Panda", "Hembra", null, false, 
        "Abuelita, jubilada", "123abc", "https://cloudinary.com/panda.png", null, null, null, minilop, null);

        MontaDTO spInput = new MontaDTO(null, "Monta de MiniLop Update", LocalDate.of(2025, 10, 4), 3, EstatusMonta.PENDIENTE, pandaDTO, sementalDTO, null, false);

        // when
        when(montaRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> montaService.editarMonta(1L, spInput));
        assertNotNull(exception);
        assertEquals("La monta con id "+1L+" no fue encontrada.", exception.getMessage());

        verify(montaRepository, never()).save(any(MontaModel.class));
        verify(modelMapper, never()).map(any(MontaModel.class), eq(MontaDTO.class));
    }
}
