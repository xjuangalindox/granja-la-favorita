package com.favorita.articulos.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.favorita.articulos.controller.ArticuloController;
import com.favorita.articulos.controller.dto.ArticuloDTO;
import com.favorita.articulos.services.ArticuloServiceImpl;

import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest
public class ArticuloControllerTest{
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArticuloServiceImpl articuloService;

    @Autowired
    private ArticuloController articuloController; // Solo para getBaseUrlNginx

    private MockHttpServletRequest request;
    private ArticuloDTO art1, art2, art3;
    private MockMultipartFile imagen;

    @BeforeEach
    void setup(){
        request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-Proto", "https"); // proto
        request.addHeader("X-Forwarded-Host", "granjalafavorita.com"); // host

        art1 = new ArticuloDTO(1L, "Campiconejo", "Comida diaria", "KG", 20.0, null, "123abc", "https://cloudinary/campiconejo.png");
        art2 = new ArticuloDTO(2L, "Heno", "Complemento de comida", "Bolsa (200 g)", 20.0, null, "123abc", "https://cloudinary/heno.png");
        art3 = new ArticuloDTO(3L, "Aserrin", "Limpieza", "KG", 20.0, null, "123abc", "https://cloudinary/asserin.png");

        imagen = new MockMultipartFile("imagen", "campiconejo.png", "image/png", "fake-image-content".getBytes());
    }

    @Test
    void testGetBaseUrlNginx(){
        // given
        // when
        String url = articuloController.getBaseUrlNginx(request);

        // then
        assertNotNull(url);
        assertEquals("https://granjalafavorita.com", url);
    }

    @Test
    void testObtenerArticulos_Success() throws Exception{
        // given
        List<ArticuloDTO> lista = Arrays.asList(art1, art2, art3);

        // when
        when(articuloService.obtenerArticulos()).thenReturn(lista);

        // then
        ResultActions response = mockMvc.perform(get("/articulos"));

        response.andExpect(status().isOk())
            .andDo(print())
            .andExpect(view().name("articulos/lista"))
            .andExpect(model().attribute("listaArticulos", lista));

        verify(articuloService, times(1)).obtenerArticulos();
    }

    @Test
    void testCrearArticulo_Success() throws Exception{
        // given
        // when
        ResultActions response = mockMvc.perform(get("/articulos/create"));

        // then
        response.andExpect(status().isOk())
            .andDo(print())
            .andExpect(view().name("articulos/formulario"))
            .andExpect(model().attribute("articuloDTO", new ArticuloDTO()))
            .andExpect(model().attribute("titulo", "Crear Articulo"))
            .andExpect(model().attribute("accion", "/articulos/create"));
    }

    @Test
    void testGuardarArticulo_Success() throws Exception{
        // given
        // when
        when(articuloService.guardarArticulo(art1)).thenReturn(art1);

        ResultActions response = mockMvc.perform(multipart("/articulos/create")
            .file(imagen)
            
            .param("nombre", "Campiconejo")
            .param("descripcion", "Comida diaria")
            .param("presentacion", "KG")
            .param("precio", "20.0")
            .param("public_id", "123abc")
            .param("secure_url", "https://cloudinary/campiconejo.png")

            .header("X-Forwarded-Proto", "https")
            .header("X-Forwarded-Host", "granjalafavorita.com")
            .contentType(MediaType.MULTIPART_FORM_DATA));

        // then
        response.andExpect(status().is3xxRedirection())
            .andDo(print())
            .andExpect(redirectedUrl("https://granjalafavorita.com/articulos"));

        verify(articuloService, times(1)).guardarArticulo(any(ArticuloDTO.class));
    }

    @Test
    void testGuardarArticulo_Error() throws Exception{

        // when
        when(articuloService.guardarArticulo(any(ArticuloDTO.class))).thenThrow(new RuntimeException("Ocurrio un error"));

        ResultActions response = mockMvc.perform(multipart("/articulos/create")
            .file(imagen)
            .param("nombre", "Campiconejo")
            .param("descripcion", "Comida diaria")
            .param("presentacion", "KG")
            .param("precio", "20.0")
            .param("public_id", "123abc")
            .param("secure_url", "https://cloudinary/campiconejo.png")
            .header("X-Forwarder-Proto", "https")
            .header("X-Forwarder-Host", "granjalafavorita.com")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            );

        // then
        response.andExpect(status().isOk())
            .andDo(print())
            .andExpect(view().name("articulos/formulario"))
            .andExpect(model().attributeExists("articuloDTO"))
            .andExpect(model().attribute("titulo", "Crear Articulo"))
            .andExpect(model().attribute("accion", "/articulos/create"))
            .andExpect(model().attribute("error", "Ocurrio un error"));

        verify(articuloService, times(1)).guardarArticulo(any(ArticuloDTO.class));
    }

    @Test
    void testBuscarArticulo_Success() throws Exception{
        // given
        when(articuloService.obtenerArticuloPorId(anyLong())).thenReturn(Optional.of(art1));
        
        // when
        ResultActions response = mockMvc.perform(get("/articulos/update/{id}", 1L));

        // then
        response.andExpect(status().isOk())
            .andDo(print())
            .andExpect(view().name("articulos/formulario"))
            .andExpect(model().attributeExists("articuloDTO"))
            .andExpect(model().attribute("titulo", "Editar Articulo"))
            .andExpect(model().attribute("accion", "/articulos/update/"+1L));

        verify(articuloService, times(1)).obtenerArticuloPorId(anyLong());
    }

    @Test
    void testObtenerArticulo_Error() throws Exception{
        when(articuloService.obtenerArticuloPorId(anyLong())).thenReturn(Optional.empty());

        ResultActions response = mockMvc.perform(get("/articulos/update/{id}",1L)
            .header("X-Forwarded-Proto", "https")
            .header("X-Forwarded-Host", "granjalafavorita.com"));

        response.andExpect(status().is3xxRedirection())
            .andDo(print())
            .andExpect(flash().attribute("error", "El articulo con id 1 no fue encontrado"))
            .andExpect(redirectedUrl("https://granjalafavorita.com/articulos"));

        verify(articuloService, times(1)).obtenerArticuloPorId(anyLong());
    }

    @Test
    void testEditarArticulo_Success() throws Exception{
        when(articuloService.editarArticulo(anyLong(), any(ArticuloDTO.class))).thenReturn(art1);

        ResultActions response = mockMvc.perform(multipart("/articulos/update/{id}", 1L)
            .file(imagen)
            .param("nombre", "Campiconejo")
            .param("descripcion", "Comida diaria")
            .param("presentacion", "KG")
            .param("precio", "20.0")
            .param("public_id", "123abc")
            .param("secure_url", "https://cloudinary/campiconejo.png")
            .header("X-Forwarded-Proto", "https")
            .header("X-Forwarded-Host", "granjalafavorita.com")
            );

        response.andExpect(status().is3xxRedirection()) // 302
            .andDo(print())
            .andExpect(flash().attribute("ok", "Articulo modificado correctamente"))
            .andExpect(redirectedUrl("https://granjalafavorita.com/articulos"));

        verify(articuloService, times(1)).editarArticulo(anyLong(), any(ArticuloDTO.class));
    }

    @Test
    void testEditarArticulo_Error() throws Exception{
        when(articuloService.editarArticulo(anyLong(), any(ArticuloDTO.class))).thenThrow(new RuntimeException());

        ResultActions response = mockMvc.perform(multipart("/articulos/update/{id}", 1L)
            .file(imagen)
            
            .param("nombre", "Campiconejo")
            .param("descripcion", "Comida diaria")
            .param("presentacion", "KG")
            .param("precio", "20.0")
            .param("public_id", "123abc")
            .param("secure_url", "https://cloudinary/campiconejo.png")

            .header("X-Forwarded-Proto", "https")
            .header("X-Forwarded-Host", "granjalafavorita.com")
            );

        response.andExpect(status().isOk())
            .andDo(print())
            .andExpect(view().name("articulos/formulario"))
            .andExpect(model().attributeExists("articuloDTO"))
            .andExpect(model().attribute("titulo", "Editar Articulo"))
            .andExpect(model().attribute("accion", "/articulos/update/"+1L))
            .andExpect(model().attribute("error", "Ocurrio un error al modificar el articulo"));

        verify(articuloService, times(1)).editarArticulo(anyLong(), any(ArticuloDTO.class));
    }

    @Test
    void testEliminarArticulo_Success() throws Exception{
        when(articuloService.eliminarArticuloPorId(anyLong())).thenReturn(true);

        ResultActions response = mockMvc.perform(get("/articulos/delete/{id}", 1L)
            .header("X-Forwarded-Proto", "https")
            .header("X-Forwarded-Host", "granjalafavorita.com")
            );

        response.andExpect(status().is3xxRedirection())
            .andDo(print())
            .andExpect(flash().attribute("ok", "Articulo eliminado correctamente."))
            .andExpect(redirectedUrl("https://granjalafavorita.com/articulos"));

        verify(articuloService, times(1)).eliminarArticuloPorId(anyLong());
    }

    @Test
    void testEliminarArticulo_Error() throws Exception{
        when(articuloService.eliminarArticuloPorId(anyLong())).thenThrow(new RuntimeException("Ocurrio un error al eliminar el articulo"));

        ResultActions response = mockMvc.perform(get("/articulos/delete/{id}", 1L)
            .header("X-Forwarded-Proto", "https")
            .header("X-Forwarded-Host", "granjalafavorita.com")
            );

        response.andExpect(status().is3xxRedirection())
            .andDo(print())
            .andExpect(flash().attribute("error", "Ocurrio un error al eliminar el articulo"))
            .andExpect(redirectedUrl("https://granjalafavorita.com/articulos"));

        verify(articuloService, times(1)).eliminarArticuloPorId(anyLong());
    }

}
