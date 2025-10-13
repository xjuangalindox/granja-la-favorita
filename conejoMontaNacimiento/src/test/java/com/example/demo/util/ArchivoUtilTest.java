package com.example.demo.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;

@ExtendWith(MockitoExtension.class)
public class ArchivoUtilTest {
    
    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @InjectMocks
    private ArchivoUtil archivoUtil;

    @BeforeEach
    void setup(){
        ReflectionTestUtils.setField(archivoUtil, "marcaAguaPagina", "granjalafavorita.com");
    }   

    @Test
    void testGetUrlWithLogo(){
        String url = archivoUtil.getUrlWithLogo("123abc");

        assertEquals("", archivoUtil.getUrlWithLogo("123abc"));
    }
}
