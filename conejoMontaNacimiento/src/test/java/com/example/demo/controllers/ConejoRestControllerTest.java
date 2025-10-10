package com.example.demo.controllers;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.demo.services.IConejoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ConejoRestController.class)
public class ConejoRestControllerTest {

    // @Autowired
    // private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IConejoService conejoService;

    @Test
    void testExistsByRazaId_True() throws Exception{
        when(conejoService.existsByRazaId(anyLong())).thenReturn(true);

        ResultActions response = mockMvc.perform(get("/api/conejos/existe-por-raza/{id}", 1L));

        response.andExpect(status().isOk())
            .andDo(print())
            .andExpect(content().string("true"));

        verify(conejoService).existsByRazaId(anyLong());
    }

    @Test
    void testExistsByRazaId_False() throws Exception{
        when(conejoService.existsByRazaId(anyLong())).thenReturn(false);

        ResultActions response = mockMvc.perform(get("/api/conejos/existe-por-raza/{id}", 1L));
        response.andExpect(status().isOk())
            .andDo(print())
            .andExpect(content().string("false"));

        verify(conejoService, times(1)).existsByRazaId(anyLong());
    }
}
