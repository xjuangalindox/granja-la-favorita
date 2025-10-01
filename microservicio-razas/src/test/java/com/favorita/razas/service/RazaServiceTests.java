package com.favorita.razas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.favorita.razas.clients.ConejoClient;
import com.favorita.razas.controller.dto.RazaDTO;
import com.favorita.razas.model.RazaModel;
import com.favorita.razas.repository.IRazaRepository;

@ExtendWith(MockitoExtension.class)
public class RazaServiceTests {

    @Mock
    private IRazaRepository razaRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RazaServiceImpl razaService;

    @Test
    void testGuardarRaza(){
        // given
        RazaDTO razaDTO = new RazaDTO(null, "MiniLop"); // Envio dto
        RazaModel razaModel = new RazaModel(null, "MiniLop"); // dto -> model
        RazaModel modelGuardado = new RazaModel(1L, "MiniLop"); // model -> dto
        RazaDTO dtoGuardado = new RazaDTO(1L, "MiniLop"); // Respuesta dto

        // Simulacion
        given(modelMapper.map(razaDTO, RazaModel.class)).willReturn(razaModel); // Simulacion de mapeo (dto -> model)
        given(razaRepository.save(razaModel)).willReturn(modelGuardado); // Simulacion de persistencia
        given(modelMapper.map(modelGuardado, RazaDTO.class)).willReturn(dtoGuardado); // Simulacion de mapeo (model -> dto)

        // when
        RazaDTO resultado = razaService.guardarRaza(razaDTO);

        // then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("MiniLop", resultado.getNombre());
    }

    @Test
    void testObtenerRazas(){
        // given
        RazaModel razaModel1 = new RazaModel(1L, "MiniLop");
        RazaModel razaModel2 = new RazaModel(2L, "Cabeza de León");
        RazaModel razaModel3 = new RazaModel(3L, "Enano Holandés");

        RazaDTO razaDTO1 = new RazaDTO(1L, "MiniLop");
        RazaDTO razaDTO2 = new RazaDTO(1L, "Cabeza de León");
        RazaDTO razaDTO3 = new RazaDTO(1L, "Enano Holandés");

        // List.of() = lista inmutable, new ArrayList<>(List.of()) = lista mutable
        given(razaRepository.findAll()).willReturn(new ArrayList<>(List.of(razaModel1, razaModel2, razaModel3)));
        given(modelMapper.map(razaModel1, RazaDTO.class)).willReturn(razaDTO1);
        given(modelMapper.map(razaModel2, RazaDTO.class)).willReturn(razaDTO2);
        given(modelMapper.map(razaModel3, RazaDTO.class)).willReturn(razaDTO3);

        // when
        List<RazaDTO> result = razaService.obtenerRazas();

        // then
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testObtenerRazaPorId(){
        // given
        RazaDTO razaDTO = new RazaDTO(1L, "Cabeza de León");
        RazaModel razaModel = new RazaModel(1L, "Cabeza de León");

        given(razaRepository.findById(1L)).willReturn(Optional.of(razaModel));
        given(modelMapper.map(razaModel, RazaDTO.class)).willReturn(razaDTO);

        // when
        Optional<RazaDTO> result = razaService.obtenerRazaPorId(1L);

        // then
        assertTrue(result.isPresent());
        assertEquals("Cabeza de León", result.get().getNombre());
    }
    
    @Test
    void testEditarRaza(){
        // given
        Long id = 1L;
        RazaModel razaModel = new RazaModel(1L, "Cabeza de León");
        RazaDTO razaDTO = new RazaDTO(1L, "Cabeza de León");
        given(razaRepository.findById(id)).willReturn(Optional.of(razaModel));
        given(razaRepository.save(razaModel)).willReturn(razaModel);
        given(modelMapper.map(razaModel, RazaDTO.class)).willReturn(razaDTO);

        // when
        RazaDTO result = razaService.editarRaza(id, razaDTO);

        // then
        assertNotNull(result);
        assertEquals("Cabeza de León", result.getNombre());
    }

    @Test
    void testEditarRazaThrowRuntimeException(){
        // given
        Long id = 1L;
        RazaDTO razaDTO = new RazaDTO(1L, "MiniLop");
        given(razaRepository.findById(id)).willReturn(Optional.empty());
        
        // when
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            razaService.editarRaza(id, razaDTO);
        });

        assertEquals("La raza con id "+id+" no fue encontrada", exception.getMessage());

        // then
        verify(razaRepository, times(1)).findById(id);
        verify(razaRepository, never()).save(any());
    }

    @Mock
    private ConejoClient conejoClient;

    @Test
    void testEliminarRazaPorId(){
        // given
        Long id = 1L;
        RazaModel razaModel = new RazaModel(1L, "MiniLop");

        given(razaRepository.findById(id)).willReturn(Optional.of(razaModel));
        given(conejoClient.existsByRazaId(id)).willReturn(false);
        doNothing().when(razaRepository).deleteById(id);
        // willDoNothing().given(razaRepository).deleteById(id);

        // when
        razaService.eliminarRazaPorId(id);

        // then
        verify(razaRepository, times(1)).deleteById(id);
    }

    @Test
    void textEliminarRazaPorIdThrowRuntimewxception(){
        // given
        given(razaRepository.findById(1L)).willReturn(Optional.empty());

        // when
        RuntimeException exception = assertThrows(RuntimeException.class, () ->{
            razaService.eliminarRazaPorId(1L);
        });

        // then
        verify(razaRepository, times(1)).findById(1L);
        verify(razaRepository, never()).deleteById(any());
    }

    @Test
    void eliminarRazaPorIdConejoExistsByRazaId(){
        // given
        Long id = 1L;
        RazaModel razaModel = new RazaModel(1L, "MiniLop");
        given(razaRepository.findById(id)).willReturn(Optional.of(razaModel));
        given(conejoClient.existsByRazaId(id)).willReturn(true);

        // when
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            razaService.eliminarRazaPorId(id);
        });

        // then
        verify(razaRepository, never()).deleteById(id);
    }
}
