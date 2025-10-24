package com.example.demo.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; // Agregar manualmente (get)
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.internal.verification.AtMost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.demo.controllers.dto.ConejoDTO;
import com.example.demo.controllers.dto.EjemplarDTO;
import com.example.demo.controllers.dto.MontaDTO;
import com.example.demo.controllers.dto.NacimientoDTO;
import com.example.demo.controllers.dto.RazaDTO;
import com.example.demo.models.ConejoModel;
import com.example.demo.models.EjemplarModel;
import com.example.demo.models.MontaModel;
import com.example.demo.models.NacimientoModel;
import com.example.demo.models.enums.EstatusMonta;
import com.example.demo.services.IConejoService;
import com.example.demo.services.IMontaService;
import com.example.demo.util.ArchivoUtil;

import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest(MontaController.class)
public class MontaControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArchivoUtil archivoUtil;

    @MockitoBean
    private IMontaService montaService;

    @MockitoBean
    private IConejoService conejoService;

    // Objects
    private RazaDTO minilop, leon, fuzzylop, enano;
    private ConejoDTO semental, panda, peluchin, pelusa, rata, nube, castor, chocolata;
    private MontaDTO sp, pp, rn, cc;
    private EjemplarDTO eje1, eje2, eje3, eje4, eje5;
    
    private List<EjemplarDTO> ejemplares;
    private NacimientoDTO n1, n2, n3, n4;

    @BeforeEach
    void setup(){
        minilop = new RazaDTO(1L, "MiniLop");
        minilop = new RazaDTO(1L, "Cabeza de León");
        minilop = new RazaDTO(1L, "FuzzyLop");
        minilop = new RazaDTO(1L, "Enano Holandés");

        // MiniLop - Inactivos
        semental = new ConejoDTO(1L, null, null, null, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://cloudinary.com/semental.png", null, null, null, minilop);
        panda = new ConejoDTO(2L, null, null, null, "Panda", "Hembra", null, false, 
        "Abuelita, jubilada", "123abc", "https://cloudinary.com/panda.png", null, null, null, minilop);
        // Leones - Activos
        peluchin = new ConejoDTO(3L, null, null, null, "Peluchin", "Macho", null, true, 
        "Semental, nacido en granja", "123abc", "https://cloudinary.com/rocko.png", null, null, null, leon);
        pelusa = new ConejoDTO(4L, null, null, null, "Pelusa", "Hembra", null, true, 
        "Hermana del semental, nacida en granja", "123abc", "https://cloudinary.com/trueno.png", null, null, null, leon);
        // FuzzyLop - Activos
        rata = new ConejoDTO(5L, null, null, null, "Rata", "Macho", null, true, 
        "Traido de mexico", "123abc", "https://cloudinary.com/marino.png", null, null, null, fuzzylop);
        nube = new ConejoDTO(6L, null, null, null, "Nube", "Hembra", null, true, 
        "Traida de jiutepec", "123abc", "https://cloudinary.com/mexicana.png", null, null, null, fuzzylop);
        // Enanos - Activos
        castor = new ConejoDTO(7L, null, null, null, "castor", "Macho", null, true, 
        "Unico enanito semental en la granja", "123abc", "https://cloudinary.com/Castor.png", null, null, null, enano);
        chocolata = new ConejoDTO(8L, null, null, null, "chocolata", "Hembra", null, true, 
        "Enanita chocolata", "123abc", "https://cloudinary.com/Chocolata.png", null, null, null, enano);

        // Ejemplares
        eje1 = new EjemplarDTO(1L, null, null, "Macho", false, 300.00, null, n4, null);
        eje2 = new EjemplarDTO(2L, null, null, "Hembra", false, 300.00, null, n4, null);
        eje3 = new EjemplarDTO(3L, null, null, "Macho", false, 300.00, null, n4, null);
        eje4 = new EjemplarDTO(4L, null, null, "Hembra", false, 300.00, null, n4, null);
        eje5 = new EjemplarDTO(5L, null, null, "Macho", false, 300.00, null, n4, null);

        // // Ejemplares
        ejemplares = List.of(eje1, eje2, eje3, eje4, eje5);

        // // Nacimientos
        n1 = new NacimientoDTO(1L, LocalDate.of(2025, 11, 4), 6, 0, "Ratitas", null, ejemplares);
        n2 = new NacimientoDTO(2L, LocalDate.of(2025, 12, 4), 6, 0, "Ratitas", null, ejemplares);
        n3 = new NacimientoDTO(3L, LocalDate.now(), 6, 0, "Ratitas", null, ejemplares);
        n4 = new NacimientoDTO(4L, LocalDate.now(), 6, 0, "Ratitas", null, ejemplares);

        // Montas
        sp = new MontaDTO(1L, "Monta de MiniLop", LocalDate.of(2025, 8, 10), 3, EstatusMonta.PENDIENTE, panda, semental, n1, true);
        pp = new MontaDTO(2L, "Monta de Leones", LocalDate.of(2025, 9, 20), 2, EstatusMonta.EFECTIVA, pelusa, peluchin, n2, true);
        rn = new MontaDTO(3L, "Monta de FuzzyLop", LocalDate.of(2025, 10, 4), 3, EstatusMonta.PENDIENTE, nube, rata, null, false);
        cc = new MontaDTO(4L, "Monta de Enanos", LocalDate.now(), 1, EstatusMonta.EFECTIVA, chocolata, castor, null, false);
    }

    @Test
    void testObtenerMontas_Success_WithEstatus() throws Exception{
        // given
        int pagina = 0;
        String estatus = "EFECTIVA";

        List<MontaDTO> lista = List.of(sp, rn);
        Page<MontaDTO> pageMontas = new PageImpl<>(lista);

        // when
        when(montaService.findByEstatus(anyInt(), anyInt(), any(EstatusMonta.class))).thenReturn(pageMontas);

        // then
        ResultActions result = mockMvc.perform(get("/montas")
            .param("pagina", String.valueOf(pagina))
            .param("estatus", estatus)
        );

        result.andExpect(status().isOk())
            .andDo(print())
            .andExpect(view().name("montas/lista"))
            .andExpect(model().attribute("listaEstatus", EstatusMonta.values()))
            .andExpect(model().attribute("estatus", estatus))
            .andExpect(model().attribute("listaMontas", pageMontas.getContent()))
            .andExpect(model().attribute("paginaActual", pagina))
            .andExpect(model().attribute("totalPaginas", pageMontas.getTotalPages()))
            .andExpect(model().attribute("totalElements", pageMontas.getTotalElements()));
    }

    @Test
    void testObtenerMontas_Success_NullEstatus() throws Exception{
        // given
        int pagina = 0;
        String estatus = null;

        List<MontaDTO> lista = List.of(sp, pp, rn, cc);
        Page<MontaDTO> pageMontas = new PageImpl<>(lista);

        // when
        when(montaService.findAll(anyInt(), anyInt())).thenReturn(pageMontas);

        // then
        ResultActions result = mockMvc.perform(get("/montas")
            .param("pagina", String.valueOf(pagina))
            .param("estatus", estatus)
        );

        result.andExpect(status().isOk())
            .andDo(print())
            .andExpect(view().name("montas/lista"))
            .andExpect(model().attribute("listaEstatus", EstatusMonta.values()))
            .andExpect(model().attribute("estatus", estatus))
            .andExpect(model().attribute("listaMontas", pageMontas.getContent()))
            .andExpect(model().attribute("paginaActual", pageMontas.getNumber()))
            .andExpect(model().attribute("totalPaginas", pageMontas.getTotalPages()))
            .andExpect(model().attribute("totalElements", pageMontas.getTotalElements()));
    }

    @Test
    void testObtenerMontas_Success_EmptyEstatus() throws Exception{
        // given
        int pagina = 0;
        String estatus = "";

        List<MontaDTO> lista = List.of(sp, pp, rn, cc);
        Page<MontaDTO> pageMontas = new PageImpl<>(lista);

        // when
        when(montaService.findAll(anyInt(), anyInt())).thenReturn(pageMontas);

        // then
        ResultActions result = mockMvc.perform(get("/montas")
            .param("pagina", String.valueOf(pagina))
            .param("estatus", estatus)
        );

        result.andExpect(status().isOk())
            .andDo(print())
            .andExpect(view().name("montas/lista"))
            .andExpect(model().attribute("listaEstatus", EstatusMonta.values()))
            .andExpect(model().attribute("estatus", estatus))
            .andExpect(model().attribute("listaMontas", pageMontas.getContent()))
            .andExpect(model().attribute("paginaActual", pagina))
            .andExpect(model().attribute("totalPaginas", pageMontas.getTotalPages()))
            .andExpect(model().attribute("totalElements", pageMontas.getTotalElements()));

    }

    @Test
    void testFormularioCrear() throws Exception{
        List<ConejoDTO> machos = List.of(peluchin, rata, nube);
        List<ConejoDTO> hembras = List.of(pelusa, nube, chocolata);

        when(conejoService.obtenerConejosActivosPorSexo("Macho")).thenReturn(machos);
        when(conejoService.obtenerConejosActivosPorSexo("Hembra")).thenReturn(hembras);

        ResultActions result = mockMvc.perform(get("/montas/crear"));

        result.andExpect(status().isOk())
            .andDo(print())
            .andExpect(view().name("montas/formulario"))
            .andExpect(model().attributeExists("montaDTO"))
            .andExpect(model().attribute("titulo", "Registrar Monta"))
            .andExpect(model().attribute("accion", "/montas/guardar"))
            .andExpect(model().attribute("listaEstatus", EstatusMonta.values()))
            .andExpect(model().attributeExists("listaMachos"))
            .andExpect(model().attributeExists("listaHembras"));

        verify(conejoService, atMost(1)).obtenerConejosActivosPorSexo("Macho");
        verify(conejoService, atMost(1)).obtenerConejosActivosPorSexo("Hembra");
        verify(conejoService, times(2)).obtenerConejosActivosPorSexo(anyString());
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void testFormularioEditar_IdValido(boolean conNacimiento) throws Exception{
        Long id = 1L;
        List<ConejoDTO> machos = List.of(semental, peluchin, rata, castor);
        List<ConejoDTO> hembras = List.of(panda, pelusa, nube, chocolata);

        MontaDTO montaDTO = conNacimiento ? sp : rn;

        when(montaService.obtenerMontaById(anyLong())).thenReturn(Optional.of(montaDTO)); // with and without nacimientoDTO
        when(conejoService.obtenerConejosPorSexo("Macho")).thenReturn(machos);
        when(conejoService.obtenerConejosPorSexo("Hembra")).thenReturn(hembras);


        ResultActions result = mockMvc.perform(get("/montas/editar/{id}",id));

        result.andExpect(status().isOk())
            .andDo(print())
            .andExpect(view().name("montas/formulario"))
            .andExpect(model().attributeExists("montaDTO"))
            .andExpect(model().attribute("titulo", "Editar Monta"))
            .andExpect(model().attribute("accion", "/montas/editar/"+id))
            .andExpect(model().attribute("listaEstatus", EstatusMonta.values()))
            .andExpect(model().attributeExists("listaMachos"))
            .andExpect(model().attributeExists("listaHembras"));

        verify(conejoService, atMost(1)).obtenerConejosPorSexo("Macho");
        verify(conejoService, atMost(1)).obtenerConejosPorSexo("Hembra");
        verify(conejoService, atLeast(2)).obtenerConejosPorSexo(anyString());
    }

    @Test
    void testFormularioEditar_IdInvalido() throws Exception{
        Long id = 1L;
        String urlNginx = "https://granjalafavorita.com";

        when(montaService.obtenerMontaById(anyLong())).thenReturn(Optional.empty());
        when(archivoUtil.getBaseUrlNginx(any(HttpServletRequest.class))).thenReturn(urlNginx);

        ResultActions result = mockMvc.perform(get("/montas/editar/{id}", id));

        result.andExpect(status().is3xxRedirection())
            .andDo(print())
            .andExpect(redirectedUrl(urlNginx+"/montas"));
    }

    @Test
    void testGuardarMonta_Success() throws Exception{
        // sp = new MontaDTO(1L, "Monta de MiniLop", LocalDate.of(2025, 8, 10), 3, EstatusMonta.PENDIENTE, panda, semental, n1, true);
        String urlNginx = "https://www.granjalafavorita.com";

        when(montaService.guardarMonta(any(MontaDTO.class))).thenReturn(sp);
        when(archivoUtil.getBaseUrlNginx(any(HttpServletRequest.class))).thenReturn(urlNginx);

        ResultActions result = mockMvc.perform(post("/montas/guardar")
            .param("id", String.valueOf(sp.getId()))
            .param("nota", sp.getNota())
            .param("fechaMonta", sp.getFechaMonta().toString())
            .param("cantidadMontas", String.valueOf(sp.getCantidadMontas()))
            .param("estatus", sp.getEstatus().name())
            .param("hembra", sp.getHembra().toString())
            .param("macho", sp.getMacho().toString())
            .param("nacimiento", sp.getNacimiento().toString())
            .param("tieneNacimiento", String.valueOf(sp.isTieneNacimiento()))
        );

        result.andExpect(status().is3xxRedirection())
            .andDo(print())
            .andExpect(redirectedUrl(urlNginx + "/montas"));

        verify(conejoService, never()).obtenerConejosActivosPorSexo(anyString());
        verify(montaService).guardarMonta(any(MontaDTO.class));
        verify(archivoUtil, atMost(1)).getBaseUrlNginx(any(HttpServletRequest.class));
    }

    @Test
    void testGuardarMonta_Error() throws Exception{
        // sp = new MontaDTO(1L, "Monta de MiniLop", LocalDate.of(2025, 8, 10), 3, EstatusMonta.PENDIENTE, panda, semental, n1, true);
        List<ConejoDTO> machos = List.of(semental, peluchin, rata, castor);
        List<ConejoDTO> hembras = List.of(panda, pelusa, nube, chocolata);
        
        when(montaService.guardarMonta(any(MontaDTO.class))).thenThrow(new RuntimeException("Error al persistir la monta."));
        when(conejoService.obtenerConejosPorSexo("Macho")).thenReturn(machos);
        when(conejoService.obtenerConejosPorSexo("Hembra")).thenReturn(hembras);
        

        ResultActions result = mockMvc.perform(post("/montas/guardar")
            .param("id", String.valueOf(sp.getId()))
            .param("nota", sp.getNota())
            .param("fechaMonta", sp.getFechaMonta().toString())
            .param("cantidadMontas", String.valueOf(sp.getCantidadMontas()))
            .param("estatus", sp.getEstatus().name())
            .param("hembra", sp.getHembra().toString())
            .param("macho", sp.getMacho().toString())
            .param("nacimiento", sp.getNacimiento().toString())
            .param("tieneNacimiento", String.valueOf(sp.isTieneNacimiento()))
        );

        result.andExpect(status().isOk())
            .andDo(print())
            .andExpect(view().name("montas/formulario"))
            .andExpect(model().attributeExists("montaDTO"))
            .andExpect(model().attribute("titulo", "Registrar Monta"))
            .andExpect(model().attribute("accion", "/montas/guardar"))
            .andExpect(model().attribute("listaEstatus", EstatusMonta.values()))
            .andExpect(model().attributeExists("listaMachos"))
            .andExpect(model().attributeExists("listaHembras"))
            .andExpect(model().attribute("error", "Error al persistir la monta."));
    }

    @Test
    void testEliminarMonta_Success_WithEstatus() throws Exception{
        Long id = 1L;
        String estatus = "EFECTIVA";
        String urlNginx = "https://granjalafavorita.com";

        when(montaService.eliminarMontaById(anyLong())).thenReturn(true);
        when(archivoUtil.getBaseUrlNginx(any(HttpServletRequest.class))).thenReturn(urlNginx);

        ResultActions result = mockMvc.perform(get("/montas/eliminar/{id}", id)
            .param("estatus", estatus)
        );

        result.andExpect(status().is3xxRedirection())
            .andDo(print())
            .andExpect(redirectedUrl(urlNginx+"/montas?estatus="+estatus))
            .andExpect(flash().attribute("ok", "Monta eliminada correctamente"))
            ;
    }

    @Test
    void testEliminarMonta_Error_NullEstatus() throws Exception{
        Long id = 1L;
        String estatus = null;
        String urlNginx = "https://granjalafavorita.com";

        when(montaService.eliminarMontaById(anyLong())).thenReturn(true);
        when(archivoUtil.getBaseUrlNginx(any(HttpServletRequest.class))).thenReturn(urlNginx);

        ResultActions result = mockMvc.perform(get("/montas/eliminar/{id}", id)
            .param("estatus", estatus)
        );

        result.andExpect(status().is3xxRedirection())
            .andDo(print())
            .andExpect(redirectedUrl(urlNginx+"/montas"))
            .andExpect(flash().attribute("ok", "Monta eliminada correctamente"))
            ;
    }

    @Test
    void testEliminarMonta_Error_BlankEstatus() throws Exception{
        Long id = 1L;
        String estatus = "";
        String urlNginx = "https://granjalafavorita.com";

        when(montaService.eliminarMontaById(anyLong())).thenReturn(true);
        when(archivoUtil.getBaseUrlNginx(any(HttpServletRequest.class))).thenReturn(urlNginx);

        ResultActions result = mockMvc.perform(get("/montas/eliminar/{id}", id)
            .param("estatus", estatus)
        );

        result.andExpect(status().is3xxRedirection())
            .andDo(print())
            .andExpect(redirectedUrl(urlNginx+"/montas"))
            .andExpect(flash().attribute("ok", "Monta eliminada correctamente"))
            ;
    }

    @Test
    void testEliminarMonta_Error_Delete() throws Exception{
        Long id = 1L;
        String estatus = "PENDIENTE";
        String urlNginx = "https://granjalafavorita.com";
        
        when(montaService.eliminarMontaById(anyLong())).thenThrow(new RuntimeException("Ocurrio un error al eliminar la monta"));
        when(archivoUtil.getBaseUrlNginx(any(HttpServletRequest.class))).thenReturn(urlNginx);
        
        ResultActions result = mockMvc.perform(get("/montas/eliminar/{id}", id)
            .param("estatus", estatus)
        );

        result.andExpect(status().is3xxRedirection())
            .andDo(print())
            .andExpect(redirectedUrl(urlNginx+"/montas?estatus="+estatus))
            .andExpect(flash().attribute("error", "Ocurrio un error al eliminar la monta"));
    }
}
