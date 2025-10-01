package com.favorita.razas.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*; // given
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.favorita.razas.controller.dto.RazaDTO;
import com.favorita.razas.service.RazaServiceImpl;

@WebMvcTest
class RazaRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RazaServiceImpl razaService;

    @Autowired
    private ObjectMapper objectMapper;
 
    @Test
    void testGuardarRaza() throws Exception{
        // given
        RazaDTO razaDTO = new RazaDTO(1L, "MiniLop");
        given(razaService.guardarRaza(any(RazaDTO.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        ResultActions response = mockMvc.perform(post("/api/razas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(razaDTO)));

        // then
        // response.andDo(print())
            // .andExpect(status().isCreated())
        response.andExpect(status().isCreated())
            .andDo(print())
            .andExpect(jsonPath("$.id", is(razaDTO.getId().intValue())))
            .andExpect(jsonPath("$.nombre", is(razaDTO.getNombre())));
    }

    @Test
    void testObtenerRazas() throws Exception{
        // given
        List<RazaDTO> razas = new ArrayList<>();
        razas.add(new RazaDTO(1L, "MiniLop"));
        razas.add(new RazaDTO(2L, "Cabeza de León"));
        razas.add(new RazaDTO(3L, "Enano Holandés"));
        
        given(razaService.obtenerRazas()).willReturn(razas);

        // when
        ResultActions response = mockMvc.perform(get("/api/razas"));

        // then
        response.andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.size()", is(razas.size())))
            .andExpect(jsonPath("$[0].id", is(razas.get(0).getId().intValue())))
            .andExpect(jsonPath("$[0].nombre", is(razas.get(0).getNombre())))
            .andExpect(jsonPath("$[1].id", is(razas.get(1).getId().intValue())))
            .andExpect(jsonPath("[1].nombre", is(razas.get(1).getNombre())));
    }

    @Test
    void testObtenerRazasNoContent() throws Exception{
        // given
        given(razaService.obtenerRazas()).willReturn(Collections.emptyList());

        // when
        ResultActions response = mockMvc.perform(get("/api/razas"));

        // then
        response.andExpect(status().isNoContent())
            .andDo(print());
    }

    @Test
    void testObtenerRazaPorId() throws Exception{
        // given
        RazaDTO razaDTO = new RazaDTO(1L, "MiniLop");
        given(razaService.obtenerRazaPorId(anyLong())).willReturn(Optional.of(razaDTO));

        // when
        ResultActions response = mockMvc.perform(get("/api/razas/{id}",anyLong()));

        // then
        response.andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.id", is(razaDTO.getId().intValue())))
            .andExpect(jsonPath("$.nombre", is(razaDTO.getNombre()))
        );       
    }

    @Test
    void testObtenerRazaPorIdNoEncontrado() throws Exception{
        // given
        given(razaService.obtenerRazaPorId(anyLong())).willReturn(Optional.empty());

        // when
        ResultActions response = mockMvc.perform(get("/api/razas/{id}",anyLong()));

        // then 
        response.andExpect(status().isNotFound())
            .andDo(print());
    }

    @Test
    void testEliminarRazaPorId() throws Exception{
        // given
        RazaDTO razaDTO = new RazaDTO(1L, "MiniLop");
        given(razaService.obtenerRazaPorId(anyLong())).willReturn(Optional.of(razaDTO));
        given(razaService.eliminarRazaPorId(anyLong())).willReturn(true);

        // when
        ResultActions response = mockMvc.perform(delete("/api/razas/{id}", anyLong()));
        
        // then
        response.andExpect(status().isNoContent())
            .andDo(print());
    }

    @Test
    void testEliminarRazaPorIdNotFound() throws Exception{
        // given
        given(razaService.obtenerRazaPorId(anyLong())).willReturn(Optional.empty());

        // when
        ResultActions response = mockMvc.perform(delete("/api/razas/{id}",anyLong()));
        
        // then
        response.andExpect(status().isNotFound())
            .andDo(print());
    }

    @Test
    void testEditarRaza() throws Exception{
        // given
        RazaDTO original = new RazaDTO(1L, "Cabeza de León");
        RazaDTO update = new RazaDTO(1L, "Enano Holandés");

        given(razaService.obtenerRazaPorId(original.getId())).willReturn(Optional.of(original));
        given(razaService.editarRaza(eq(original.getId()), any(RazaDTO.class))).willAnswer(invocation -> invocation.getArgument(1));

        // when
        ResultActions response = mockMvc.perform(put("/api/razas/{id}",original.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(update)));

        // then
        response.andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.id", is(update.getId().intValue())))
            .andExpect(jsonPath("$.nombre", is(update.getNombre())));
    }

    @Test
    void testEditarRazaNotFound() throws Exception{
        // given
        RazaDTO update = new RazaDTO(1L, "Enano Holandés");
        given(razaService.obtenerRazaPorId(anyLong())).willReturn(Optional.empty());

        // when
        ResultActions response = mockMvc.perform(put("/api/razas/{id}", anyLong())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(update)));

        // then
        response.andExpect(status().isNotFound())
            .andDo(print());
    }

    @Test
    void testVerHost() throws Exception{
        // given
        
        // when
        ResultActions response = mockMvc.perform(get("/api/razas/headers")
            .header("Host", "c26824c3c415:8082")
            .header("X-Forwarded-Host", "localhost")
            .header("X-Forwarded-Port", "8080")
            .header("X-Forwarded-Proto", "http"));
            
        // then
        response.andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.Host").value("c26824c3c415:8082"))
            .andExpect(jsonPath("$.X-Forwarded-Host").value("localhost"))
            .andExpect(jsonPath("$.X-Forwarded-Port").value("8080"))
            .andExpect(jsonPath("$.X-Forwarded-Proto").value("http"));
    }
}
