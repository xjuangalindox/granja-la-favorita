package com.example.demo.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.nio.file.Path;

import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
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

    // Instancias necesarias
    private MockMultipartFile imagen;

    @BeforeEach
    void setup(){
        ReflectionTestUtils.setField(archivoUtil, "marcaAguaPagina", "granjalafavorita.com");
        imagen = new MockMultipartFile("imagen", "semental.jpg", "image/jpeg", new byte[]{1, 2, 3});
    }   

    @Test
    void testGetUrlWithLogo(){
        String url = archivoUtil.getUrlWithLogo("123abc");

        assertEquals("", archivoUtil.getUrlWithLogo("123abc"));
    }

    @Test
    void testObtenerNombreBaseImagen(){
        String nombreBaseImagen = archivoUtil.obtenerNombreBaseImagen(imagen);
        
        assertEquals("semental", nombreBaseImagen);
    }

    @Test
    void testObtenerExtensionImagen(){
        String imageExtension = archivoUtil.obtenerExtensionImagen(imagen);

        assertEquals("jpg", imageExtension);
    }

    @Test
    void testObtenerExtensionTexto(){
        String textExtension = archivoUtil.obtenerExtensionTexto("Semental.jpeg");

        assertEquals("jpeg", textExtension);
    }

    @Test
    void testCrearNombreImagen(){
        String nameImage = archivoUtil.crearNombreImagen("Semental", "jpg");

        assertEquals("Semental.jpg", nameImage);
    }

    @Test
    void testCrearRuta(){
        Path path = archivoUtil.crearRuta("/conejoMontaNacimiento/src/main/resources/static/", "Semental.jpg");

        assertEquals("\\conejoMontaNacimiento\\src\\main\\resources\\static\\Semental.jpg", path.toString());
    }
}