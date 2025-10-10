package com.example.demo.controllers;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; // Agregar manualmente (get)
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*; // Agregar manualmente (status)
// import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*; // Agregar manualmente (status)


import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.demo.clients.RazaClient;
import com.example.demo.controllers.dto.ConejoDTO;
import com.example.demo.controllers.dto.RazaDTO;
import com.example.demo.models.ConejoModel;
import com.example.demo.services.ConejoServiceImpl;
import com.example.demo.services.IConejoService;
import com.example.demo.util.ArchivoUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ConejoController.class)
public class ConejoControllerTest {
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    // @MockitoBean
    // private ConejoServiceImpl conejoService;

    @MockitoBean
    private ArchivoUtil archivoUtil;

    @MockitoBean
    private RazaClient razaClient;

    @MockitoBean
    private IConejoService conejoService;

    // 
    RazaDTO minilop;
    ConejoDTO semental, panda, rocko, enojona;

    @BeforeEach
    void setup(){
        minilop = new RazaDTO(1L, "Minilop");
        
        semental = new ConejoDTO(1L, null, null, null, "Semental", "Macho", null, false, 
            "Primer semental de la granja", "123abc", "https://cloudinary.com/semental.png", null, null, null, minilop);
        panda = new ConejoDTO(2L, null, null, null, "Panda", "Hembra", null, false, 
            "Reproductora mas grande en la granja", "123abc", "https://cloudinary.com/panda.png", null, null, null, minilop);
        rocko = new ConejoDTO(3L, null, null, null, "Rocko", "Macho", null, true, 
            "Malito del ojo", "123abc", "https://cloudinary.com/rocko.png", null, null, null, minilop);
        enojona = new ConejoDTO(4L, null, null, null, "Enojona", "Hembra", null, true, 
        "Mordelona de la granja", "123abc", "https://cloudinary.com/enojona.png", null, null, null, minilop);
    }

    @Test
    void testObtenerConejos_filterBySexo() throws Exception{
        // given
        List<ConejoDTO> lista = List.of(semental, rocko);
        Page<ConejoDTO> pageConejos = new PageImpl<>(lista);

        // when
        when(conejoService.findBySexo(anyInt(), anyInt(), anyString(), anyString())).thenReturn(pageConejos);

        ResultActions response = mockMvc.perform(get("/conejos")
            .param("pagina", "0")
            .param("sexo", "Macho")
            .param("ordenarPor", "nombre"));

        response.andExpect(status().isOk())
            .andDo(print())
            .andExpect(view().name("conejos/lista"))
            // .andExpect(model().attribute("pagina", 0))
            .andExpect(model().attribute("sexo", "Macho"))
            .andExpect(model().attribute("ordenarPor", "nombre"))
            .andExpect(model().attribute("listaConejos", pageConejos.getContent()))
            .andExpect(model().attribute("paginaActual", 0))
            .andExpect(model().attribute("totalPaginas", pageConejos.getTotalPages()))
            .andExpect(model().attribute("totalElementos", pageConejos.getTotalElements()));

        verify(conejoService).findBySexo(anyInt(), anyInt(), anyString(), anyString());
    }

    @Test
    void testobtenerConejos_filterByDefault() throws Exception{
        List<ConejoDTO> lista = Arrays.asList(enojona, panda, rocko, semental);
        Page<ConejoDTO> pageConejos = new PageImpl<>(lista);

        when(conejoService.findAll(anyInt(), anyInt(), anyString())).thenReturn(pageConejos);

        ResultActions response = mockMvc.perform(get("/conejos")
            .param("pagina", "0")
            .param("sexo", "")
            .param("ordenarPor", ""));

        response.andExpect(status().isOk())
            .andDo(print())
            .andExpect(view().name("conejos/lista"))
            .andExpect(model().attribute("sexo", ""))
            .andExpect(model().attribute("ordenarPor", "nombre"))
            .andExpect(model().attribute("listaConejos", pageConejos.getContent()))
            .andExpect(model().attribute("paginaActual", 0))
            .andExpect(model().attribute("totalPaginas", pageConejos.getTotalPages()))
            .andExpect(model().attribute("totalElementos", pageConejos.getTotalElements()));

        verify(conejoService, times(1)).findAll(anyInt(), anyInt(), anyString());
    }

    // @Test
    // void testObtenerConejos() throws Exception{
    //     List<ConejoDTO> lista = Arrays.asList(semental, panda, mexicana, peluchin, pelusa, peluchina);
    //     Page<ConejoDTO> pageConejos = new PageImpl<>(lista);

    //     when(conejoService.findAll(anyInt(), anyInt())).thenReturn(pageConejos);

    //     ResultActions response = mockMvc.perform(get("/conejos"));

    //     response.andExpect(status().isOk())
    //         .andDo(print())
    //         .andExpect(view().name("conejos/lista"));
    //         .andExpect(model().attribute("sexo", response))
    // }
}
