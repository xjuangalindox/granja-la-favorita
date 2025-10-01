package com.favorita.razas.controller;

import static org.mockito.ArgumentMatchers.any; // Importante
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType; // Importante
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.favorita.razas.controller.dto.RazaDTO;
import com.favorita.razas.service.RazaServiceImpl;

@WebMvcTest
class RazaControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RazaServiceImpl razaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testObtenerRazas() throws Exception{
        // given
        List<RazaDTO> lista = new ArrayList<>();
        lista.add(new RazaDTO(1L, "MiniLop"));
        lista.add(new RazaDTO(2L, "Cabeza de León"));
        lista.add(new RazaDTO(3L, "Enano Holandés"));
        given(razaService.obtenerRazas()).willReturn(lista);

        // when
        ResultActions response = mockMvc.perform(get("/razas"));

        // then
        response.andExpect(status().isOk())
            .andExpect(view().name("razas/lista"))
            .andExpect(model().attribute("listaRazas", lista));
    }

    @Test
    void testCrearRaza() throws Exception{
        // given

        // when
        ResultActions response = mockMvc.perform(get("/razas/create"));
        
        // then
        response.andExpect(status().isOk())
            .andExpect(view().name("razas/formulario"))
            .andExpect(model().attribute("razaDTO", new RazaDTO()))
            .andExpect(model().attribute("titulo", "Crear Raza"))
            .andExpect(model().attribute("accion", "/razas/create"));
    }

    @Test
    void testGuardarRaza() throws Exception{
        // given
        RazaDTO guardado = new RazaDTO(1L, "MiniLop");
        given(razaService.guardarRaza(any(RazaDTO.class))).willReturn(guardado);

        // when
        ResultActions response = mockMvc.perform(post("/razas/create")
            .flashAttr("ok", "Raza registrada correctamente")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // then
        response.andExpect(status().is3xxRedirection())    
            .andExpect(redirectedUrlPattern("**/razas"))
            .andDo(print());
    }

    @Test
    void textGuardarRazaError() throws Exception{
        // given
        doThrow(new RuntimeException("Error")).when(razaService).guardarRaza(any(RazaDTO.class));

        // when
        ResultActions response = mockMvc.perform(post("/razas/create"));

        // then
        response.andExpect(status().isOk())
            .andExpect(view().name("razas/formulario"))
            // .andExpect(model().attribute("razaDTO", razaDTO))
            .andExpect(model().attribute("razaDTO", new RazaDTO()))
            .andExpect(model().attribute("titulo", "Crear Raza"))
            .andExpect(model().attribute("accion", "/razas/create"))
            .andExpect(model().attribute("error", "Ocurrio un error al registrar la raza"));
    }

    @Test
    void testBuscarRaza() throws Exception{
        // given
        Long id = 1L;
        RazaDTO razaDTO = new RazaDTO(1L, "MiniLop");
        given(razaService.obtenerRazaPorId(id)).willReturn(Optional.of(razaDTO));

        // when
        ResultActions response = mockMvc.perform(get("/razas/update/{id}",id));

        // then
        response.andExpect(status().isOk())
            .andDo(print())
            .andExpect(view().name("razas/formulario"))
            .andExpect(model().attribute("razaDTO", razaDTO))
            .andExpect(model().attribute("titulo", "Editar Raza"))
            .andExpect(model().attribute("accion", "/razas/update/"+id));
    }

    @Test
    void testBuscarRazaIsEmpty() throws Exception{
        // given
        Long id = 1L;
        given(razaService.obtenerRazaPorId(id)).willReturn(Optional.empty());

        // when
        ResultActions response = mockMvc.perform(get("/razas/update/{id}",id)
            .flashAttr("error", "La raza con id "+id+" no fue encontrada")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // then
        response.andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/razas"))
            .andDo(print());
    }

    @Test
    void testEditarRaza() throws Exception{
        // given
        RazaDTO original = new RazaDTO(1L, "MiniLop");
        RazaDTO update = new RazaDTO(1L, "Enano Holandés");

        given(razaService.editarRaza(original.getId(), original)).willReturn(update);

        // when
        ResultActions response = mockMvc.perform(post("/razas/update/{id}",original.getId())
            .flashAttr("ok", "Raza modificada correctamente")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // then
        response.andExpect(status().is3xxRedirection())
            .andDo(print())
            .andExpect(redirectedUrlPattern("**/razas"));
    }

    @Test
    void testEditarRazaError() throws Exception{
        // given
        doThrow(new RuntimeException("Error")).when(razaService).editarRaza(anyLong(), any(RazaDTO.class));

        // when
        ResultActions response = mockMvc.perform(post("/razas/update/{id}", 1L));

        // then
        response.andExpect(status().isOk())
            .andDo(print())
            .andExpect(view().name("razas/formulario"))
            .andExpect(model().attribute("titulo", "Editar Raza"))
            .andExpect(model().attribute("accion", "/razas/update/" + 1L))
            .andExpect(model().attribute("error", "Ocurrio un error al modificar la raza"));
    }

    @Test
    void testEliminarRaza() throws Exception{
        // given
        Long id = 1L;
        given(razaService.eliminarRazaPorId(id)).willReturn(true);

        // when
        ResultActions response = mockMvc.perform(get("/razas/delete/{id}",id)
            .flashAttr("ok", "Raza eliminada correctamente")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // then
        response.andExpect(status().is3xxRedirection())
            .andDo(print())
            .andExpect(redirectedUrlPattern("**/razas"));
    }

    @Test
    void testEliminarRazaError() throws Exception{
        // given
        Long id = 1L;
        doThrow(new RuntimeException("Error")).when(razaService).eliminarRazaPorId(id);

        // when
        ResultActions response = mockMvc.perform(get("/razas/delete/{id}",id)
            // .flashAttr("error", "")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // then
        response.andExpect(status().is3xxRedirection())
            .andDo(print())
            .andExpect(redirectedUrlPattern("**/razas"));
    }

}
