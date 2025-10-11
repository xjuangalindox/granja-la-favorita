package com.example.demo.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; // Agregar manualmente (get)
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*; // Agregar manualmente (status)
// import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*; // Agregar manualmente (status)


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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

import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest(ConejoController.class)
public class ConejoControllerTest {
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    ///////////////////////////////////////////////////////////////////////////////////

    @MockitoBean
    private ArchivoUtil archivoUtil;

    @MockitoBean
    private RazaClient razaClient;

    @MockitoBean
    private IConejoService conejoService;

    ///////////////////////////////////////////////////////////////////////////////////

    MockMultipartFile imagen;
    String url;
    RazaDTO minilop;
    ConejoDTO semental, panda, rocko, enojona;

    @BeforeEach
    void setup(){
        imagen = new MockMultipartFile("imagen", "imagen.png", url, new byte[]{1, 2, 3});
        url = "http://localhost:8080";

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
    void testObtenerConejos_filterBySexoEmpty() throws Exception{
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
    void testobtenerConejos_filterByDefault_EmptySexo() throws Exception{
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

    @Test
    void testObtenerConejos_filterByDefault_NullSexo() throws Exception{
        List<ConejoDTO> lista = Arrays.asList(enojona, panda, rocko, semental);
        Page<ConejoDTO> pageConejos = new PageImpl<>(lista);

        when(conejoService.findAll(anyInt(), anyInt(), anyString())).thenReturn(pageConejos);

        ResultActions response = mockMvc.perform(get("/conejos")
            .param("pagina", "0")
            // .param("sexo", "")
            .param("ordenarPor", ""));

        response.andExpect(status().isOk())
            .andDo(print())
            .andExpect(view().name("conejos/lista"))
            .andExpect(model().attribute("sexo", nullValue()))
            .andExpect(model().attribute("ordenarPor", "nombre"))
            .andExpect(model().attribute("listaConejos", pageConejos.getContent()))
            .andExpect(model().attribute("paginaActual", 0))
            .andExpect(model().attribute("totalPaginas", pageConejos.getTotalPages()))
            .andExpect(model().attribute("totalElementos", pageConejos.getTotalElements()))            ;

        verify(conejoService, times(1)).findAll(anyInt(), anyInt(), anyString());
    }

/////////////////////////////////////////////////////////////////////////////////////
    
    @Test
    void testEditarConejo_Success() throws Exception{
        ConejoDTO sementalEditado = new ConejoDTO(1L, null, null, imagen, "SementalEdi", "Macho", null, false, 
            "Primer semental de la granja", "123abc", "https://cloudinary.com/semental.png", null, null, null, minilop);

        when(conejoService.obtenerConejoById(anyLong())).thenReturn(Optional.of(semental));
        when(conejoService.editarConejo(anyLong(), any(ConejoDTO.class))).thenReturn(sementalEditado);
        when(archivoUtil.getBaseUrlNginx(any(HttpServletRequest.class))).thenReturn(url);

        ResultActions response = mockMvc.perform(multipart("/conejos/editar/{id}", 1L)
            .file(imagen)
            .param("id", "1")
            .param("nombre", "Semental")
            .param("sexo", "Macho")
            .param("activo", "true")
            .param("nota", "Primer semental de la granja")
            .param("publicId", "123abc")
            .param("secureUrl", "https://cloudinary.com/semental.png")
            .param("raza.id", "1")
            .contentType(MediaType.MULTIPART_FORM_DATA) // opcional
            );

        response.andExpect(status().is3xxRedirection())
            .andDo(print())
            .andExpect(redirectedUrl(url+"/conejos"));
    }

    @Test
    void testEditarConejo_Error() throws Exception{
        List<RazaDTO> lista = List.of(minilop);
        // DTO sin uso, solo como referencia de informacion
        ConejoDTO sementalEditado = new ConejoDTO(1L, null, null, imagen, "SementalEdi", "Macho", null, false, 
            "Primer semental de la granja", "123abc", "https://cloudinary.com/semental.png", null, null, null, minilop);
        
        when(conejoService.obtenerConejoById(anyLong())).thenReturn(Optional.of(semental));
        when(conejoService.editarConejo(anyLong(), any(ConejoDTO.class))).thenThrow(new RuntimeException("Ocurrio un error al editar el ejemplar."));
        when(razaClient.obtenerRazas()).thenReturn(lista);

        ResultActions response = mockMvc.perform(multipart("/conejos/editar/{id}", 1L)
            .file(imagen)
            .param("id", "1")
            .param("nombre", "Semental")
            .param("sexo", "Macho")
            .param("activo", "true")
            .param("nota", "Primer semental de la granja")
            .param("publicId", "123abc")
            .param("secureUrl", "https://cloudinary.com/semental.png")
            .param("raza.id", "1")
            .contentType(MediaType.MULTIPART_FORM_DATA) // opcional
        );

        response.andExpect(status().isOk())
            .andDo(print())
            .andExpect(view().name("conejos/formulario"))
            .andExpect(model().attributeExists("conejoDTO"))
            .andExpect(model().attribute("listaRazas", lista))
            .andExpect(model().attribute("titulo", "Editar Conejo"))
            .andExpect(model().attribute("accion", "/conejos/editar/"+1L))
            .andExpect(model().attribute("error", "Ocurrio un error al editar el ejemplar."));

            verify(archivoUtil, never()).getBaseUrlNginx(any(HttpServletRequest.class));
    }
    
    /////////////////////////////////////////////////////////////////////////////////////

    @Test
    void testEliminarConejo_Error_EmptySexo() throws Exception{
        when(conejoService.eliminarConejoById(anyLong())).thenThrow(new RuntimeException());
        when(archivoUtil.getBaseUrlNginx(any(HttpServletRequest.class))).thenReturn(url);
        
        ResultActions response = mockMvc.perform(get("/conejos/eliminar/{id}", 1L)
            .param("sexo", "")); // Empty sexo

        response.andExpect(status().is3xxRedirection())
            .andDo(print())
            .andExpect(redirectedUrl(url+"/conejos")); // No se usa "redirect" en tests

        verify(conejoService).eliminarConejoById(anyLong());
        verify(archivoUtil, times(1)).getBaseUrlNginx(any(HttpServletRequest.class));
    }

    @Test
    void testEliminarConejo_Error_NullSexo() throws Exception{
        when(conejoService.eliminarConejoById(anyLong())).thenThrow(new RuntimeException());
        when(archivoUtil.getBaseUrlNginx(any(HttpServletRequest.class))).thenReturn(url);

        ResultActions response = mockMvc.perform(get("/conejos/eliminar/{id}",1L)
            // .param("sexo", "Macho") // Null sexo
            );

        response.andExpect(status().is3xxRedirection())
            .andDo(print())
            .andExpect(redirectedUrl(url+"/conejos"));

        verify(conejoService).eliminarConejoById(anyLong());
        verify(archivoUtil, times(1)).getBaseUrlNginx(any(HttpServletRequest.class));
    }

    @Test
    void testEliminarConejo_Success_WithSexo() throws Exception{
        String sexo = "Macho";
        when(conejoService.eliminarConejoById(anyLong())).thenReturn(true);
        when(archivoUtil.getBaseUrlNginx(any(HttpServletRequest.class))).thenReturn(url);

        ResultActions response = mockMvc.perform(get("/conejos/eliminar/{id}", 1L)
            .param("sexo", sexo));

        response.andExpect(status().is3xxRedirection())
            .andDo(print())
            .andExpect(redirectedUrl(url+"/conejos?sexo="+sexo));

        verify(conejoService, times(1)).eliminarConejoById(anyLong());
        verify(archivoUtil).getBaseUrlNginx(any(HttpServletRequest.class));
    }

    @Test
    void tetsGuardarconejo_Success() throws Exception{
        // DTO sin uso, solo como referencia de informacion
        ConejoDTO nuevoSemental = new ConejoDTO(null, null, null, imagen, "Semental", "Macho", null, false, 
            "Primer semental de la granja", null, null, null, null, null, minilop);

        when(conejoService.guardarConejo(any(ConejoDTO.class))).thenReturn(semental);
        when(archivoUtil.getBaseUrlNginx(any(HttpServletRequest.class))).thenReturn(url);

        ResultActions response = mockMvc.perform(multipart("/conejos/guardar")
            .file(imagen)
            .param("nombre", "Semental")
            .param("sexo", "Macho")
            .param("activo", "false")
            .param("nota", "Primer semental de la granja")
            .param("raza.id", "1")
            .contentType(MediaType.MULTIPART_FORM_DATA)
        );

        response.andExpect(status().is3xxRedirection())
            .andDo(print())
            .andExpect(redirectedUrl(url+"/conejos"));

        verify(conejoService, times(1)).guardarConejo(any(ConejoDTO.class));
        verify(razaClient, never()).obtenerRazas();
    }

    @Test
    void testGuardarConejo_Error() throws Exception{
        List<RazaDTO> lista = Arrays.asList(minilop);

        when(conejoService.guardarConejo(any(ConejoDTO.class))).thenThrow(new RuntimeException("Ocurrio un error al guardar el ejemplar."));
        when(razaClient.obtenerRazas()).thenReturn(lista);

        ResultActions response = mockMvc.perform(multipart("/conejos/guardar")
            .file(imagen)
            .param("nombre", "Semental")
            .param("sexo", "Macho")
            .param("activo", "false")
            .param("nota", "Primer semental de la granja")
            .param("raza.id", "1")
            .contentType(MediaType.MULTIPART_FORM_DATA)
        );

        response.andExpect(status().isOk())
            .andDo(print())
            .andExpect(view().name("conejos/formulario"))
            .andExpect(model().attributeExists("conejoDTO"))
            .andExpect(model().attribute("listaRazas", lista))
            .andExpect(model().attribute("titulo", "Registrar Conejo"))
            .andExpect(model().attribute("accion", "/conejos/guardar"))
            .andExpect(model().attribute("error", "Ocurrio un error al guardar el ejemplar."));

        verify(archivoUtil, never()).getBaseUrlNginx(any(HttpServletRequest.class));
        verify(conejoService, times(1)).guardarConejo(any(ConejoDTO.class));
        verify(razaClient).obtenerRazas();
    }

    @Test
    void testMostrarFormularioEditar_EmptyConejo() throws Exception{
        when(conejoService.obtenerConejoById(anyLong())).thenReturn(Optional.empty());
        when(archivoUtil.getBaseUrlNginx(any(HttpServletRequest.class))).thenReturn(url);

        ResultActions response = mockMvc.perform(get("/conejos/editar/{id}", 1L));

        response.andExpect(status().is3xxRedirection())
            .andDo(print())
            .andExpect(redirectedUrl(url+"/conejos"));

        verify(razaClient, never()).obtenerRazas();
        verify(conejoService, times(1)).obtenerConejoById(anyLong());
    }     
    
    @Test
    void testMostrarFormularioEditar_WithConejo() throws Exception{
        List<RazaDTO> lista = List.of(minilop);

        when(conejoService.obtenerConejoById(anyLong())).thenReturn(Optional.of(semental));
        when(razaClient.obtenerRazas()).thenReturn(lista);

        ResultActions response = mockMvc.perform(get("/conejos/editar/{id}", 1L));

        response.andExpect(status().isOk())
            .andDo(print())
            .andExpect(view().name("conejos/formulario"))
            .andExpect(model().attribute("conejoDTO", semental))
            .andExpect(model().attribute("listaRazas", lista))
            .andExpect(model().attribute("titulo", "Editar Conejo"))
            .andExpect(model().attribute("accion", "/conejos/editar/"+1L));
    }   

}
