package com.example.demo.controllers;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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

import com.example.demo.controllers.dto.ConejoDTO;
import com.example.demo.controllers.dto.RazaDTO;
import com.example.demo.models.ConejoModel;
import com.example.demo.services.ConejoServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ConejoController.class)
public class ConejoControllerTest {
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    // @Autowired
    // private ConejoController conejoController;

    @MockitoBean
    private ConejoServiceImpl conejoService;

    // Instancia beforeEach
    private ConejoDTO semental, panda, mexicana, peluchin, pelusa, peluchina;
    private RazaDTO minilop, leon;

    @BeforeEach
    void setup(){
        // Razas
        minilop = new RazaDTO(1L, "Minilop");
        leon = new RazaDTO(2L, "Cabeza de León");

        // Conejos minilop
        semental = new ConejoDTO(1L, null, null, null, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://cloudinary.com/semental.png", null, null, null, minilop);
        panda = new ConejoDTO(2L, null, null, null, "Panda", "Hembra", null, false, 
        "Abuelita, jubilada", "123abc", "https://cloudinary.com/panda.png", null, null, null, minilop);
        mexicana = new ConejoDTO(3L, null, null, null, "Mexicana", "Hembra", null, true, 
        "Primera satinada", "123abc", "https://cloudinary.com/mexicana.png", null, null, null, minilop);

        // Conejos leones
        peluchin = new ConejoDTO(4L, null, null, null, "Peluchin", "Macho", null, true, 
        "Semental de la granja estado", "123abc", "https://cloudinary.com/peluchin.png", null, null, null, leon);
        pelusa = new ConejoDTO(5L, null, null, null, "Pelusa", "Hembra", null, true, 
        "Hermana del semental de la granja", "123abc", "https://cloudinary.com/pelusa.png", null, null, null, leon);
        peluchina = new ConejoDTO(6L, null, null, null, "Peluchina", "Hembra", null, true, 
        "Hoja del semental de la granja", "123abc", "https://cloudinary.com/peluchina.png", null, null, null, leon);
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
