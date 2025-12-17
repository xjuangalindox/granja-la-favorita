package com.favorita.articulos.controllers;

import static org.hamcrest.CoreMatchers.is; // add manually
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;    // add manually
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;  // add manually

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.favorita.articulos.controller.dto.ArticuloDTO;
import com.favorita.articulos.services.ArticuloServiceImpl;

@WebMvcTest
public class ArticuloRestControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // @Mock
    @MockitoBean
    private ArticuloServiceImpl articuloService;

    private ArticuloDTO art1, art2, art3;

    @BeforeEach
    void setup(){
        art1 = new ArticuloDTO(1L, "Campiconejo", "Comida diaria", "KG", 20.0, null, null, "123abc", "https://cloudinary/campiconejo.png");
        art2 = new ArticuloDTO(2L, "Heno", "Complemento de comida", "Bolsa (200 g)", 20.0, null, null, "123abc", "https://cloudinary/heno.png");
        art3 = new ArticuloDTO(3L, "Aserrin", "Limpieza", "KG", 20.0, null, null, "123abc", "https://cloudinary/asserin.png");
    }

    @Test
    void testObtenerArticulos_Success() throws Exception{
        // given
        List<ArticuloDTO> lista = Arrays.asList(art1, art2, art3);

        // when
        when(articuloService.obtenerArticulos()).thenReturn(lista);
        
        // then
        ResultActions response = mockMvc.perform(get("/api/articulos"));

        response.andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.size()", is(lista.size())))
            .andExpect(jsonPath("$[0].nombre", is(lista.get(0).getNombre())))
            .andExpect(jsonPath("$[1].id", is(lista.get(1).getId().intValue())))
            .andExpect(jsonPath("$[2].descripcion", is(lista.get(2).getDescripcion()))
            );
    }

    @Test
    void testObtenerArticulos_noContent() throws Exception{
        when(articuloService.obtenerArticulos()).thenReturn(Collections.emptyList());

        ResultActions response = mockMvc.perform(get("/api/articulos"));

        response.andExpect(status().isNoContent())
            .andDo(print());
    }

    @Test
    void testObtenerArticuloPorId_Success() throws Exception{
        // given
        // when
        when(articuloService.obtenerArticuloPorId(anyLong())).thenReturn(Optional.of(art1));

        // then
        ResultActions response = mockMvc.perform(get("/api/articulos/{id}", 1L));

        response.andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.id", is(art1.getId().intValue())))
            .andExpect(jsonPath("$.nombre", is(art1.getNombre())))
            .andExpect(jsonPath("$.descripcion", is(art1.getDescripcion())));

        verify(articuloService, times(1)).obtenerArticuloPorId(anyLong());
    }

    @Test
    void testObtenerArticuloPorId_NotFound() throws Exception{
        when(articuloService.obtenerArticuloPorId(anyLong())).thenReturn(Optional.empty());

        ResultActions response = mockMvc.perform(get("/api/articulos/{id}", 1L));

        response.andExpect(status().isNotFound())
            .andDo(print());

        verify(articuloService, times(1)).obtenerArticuloPorId(anyLong());
    }
}
