package com.example.demo.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class ArchivoUtilTest {
    
    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @InjectMocks
    private ArchivoUtil archivoUtil;

    // Instancias necesarias
    private MockMultipartFile imagen, invalidImagen;
    private Map<String, Object> mapa = new HashMap<>();

    @BeforeEach
    void setup(){
        ReflectionTestUtils.setField(archivoUtil, "marcaAguaPagina", "granjalafavorita.com");
        imagen = new MockMultipartFile("imagen", "semental.jpg", "image/jpeg", new byte[]{1, 2, 3});
        invalidImagen = new MockMultipartFile("imagen", "semental.txt", "text/plain", new byte[]{1, 2, 3});

        mapa.put("public_id", "123abc");
        mapa.put("secure_url", "https://www.cloudinary.com/semental.jpg");
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

    @Test
    void testSubirImagenCloudinary_Success() throws IOException{
        // when(archivoUtil.obtenerExtensionImagen(any())).thenReturn("jpg");
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), anyMap())).thenReturn(mapa);

        Map<String, Object> result = archivoUtil.subirImagenCloudinary(imagen, "conejos", Optional.of("Semental"));

        assertNotNull(result);
        assertEquals("123abc", result.get("public_id"));
        assertEquals("https://www.cloudinary.com/semental.jpg", result.get("secure_url"));
    }

    @Test
    void testSubirImagenCloudinary_InvalidExtension(){
        RuntimeException exception = assertThrows(RuntimeException.class, () -> archivoUtil.subirImagenCloudinary(invalidImagen, "conejos", Optional.empty()));

        assertNotNull(exception);
        assertEquals("La entension txt de la imagen no es valida", exception.getMessage());
    }

    @Test
    void testSubirImagenCloudinary_Error() throws IOException{
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), anyMap())).thenThrow(new RuntimeException());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> archivoUtil.subirImagenCloudinary(imagen, "conejos", Optional.empty()));

        assertNotNull(exception);
        assertEquals("Ocurrio un error al subir la imagen a CLOUDINARY", exception.getMessage());
    }

    @Test
    void testEliminarImagenCloudinary_Success() throws IOException{
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(anyString(), anyMap())).thenReturn(new HashMap<>()); // Mapa vacio (sin uso)

        archivoUtil.eliminarImagenCloudinary("123abc");

        verify(uploader, times(1)).destroy(anyString(), anyMap());
    }

    @Test
    void testEliminarImagenCloudinary_Error() throws IOException{
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(anyString(), anyMap())).thenThrow(new RuntimeException());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> archivoUtil.eliminarImagenCloudinary("123abc"));

        assertNotNull(exception);
        assertEquals("Ocurrio un error al eliminar la imagen en CLOUDINARY", exception.getMessage());

        verify(uploader, times(1)).destroy(anyString(), anyMap());
    }

    @Test
    void testRenombrarImagenCloudinary() throws IOException{
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.rename(anyString(), anyString(), anyMap())).thenReturn(mapa);

        Map<String, Object> result = archivoUtil.renombrarImagenCloudinary("123abc", "conejos", "Semental");
        assertNotNull(result);
        
        verify(uploader).rename(anyString(), anyString(), anyMap());
    }

    @Test
    void testRenombrarImagenCloudinary_Error() throws IOException{
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.rename(anyString(), anyString(), anyMap())).thenThrow(new RuntimeException());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> archivoUtil.renombrarImagenCloudinary("123abc", "conejos", "Semental"));
        assertNotNull(exception);
        assertEquals("Ocurrio un error renombrar o mover la imagen en CLOUDINARY.", exception.getMessage());
    }

    @Test
    void testGetBaseUrlNginx(){
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-Proto", "https");
        request.addHeader("X-Forwarded-Host", "granjalafavorita.com");

        // when
        String urlNginx = archivoUtil.getBaseUrlNginx(request);

        // then
        assertNotNull(urlNginx);
        assertEquals("https://granjalafavorita.com", urlNginx);
    }
}