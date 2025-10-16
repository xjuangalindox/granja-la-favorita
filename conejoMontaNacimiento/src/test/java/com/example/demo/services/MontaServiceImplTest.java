package com.example.demo.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.demo.repositories.MontaRepository;
import com.example.demo.repositories.NacimientoRepository;

@ExtendWith(MockitoExtension.class)
public class MontaServiceImplTest {
    
    @MockitoBean
    private MontaRepository montaRepository;

    @MockitoBean
    private ModelMapper modelMapper;

    @MockitoBean
    private NacimientoRepository nacimientoRepository;

    @InjectMocks
    private MontaServiceImpl montaService;

    @BeforeEach
    void setup(){
        
    }        
}
