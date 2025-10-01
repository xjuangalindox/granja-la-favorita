package com.favorita.articulos.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.Uploader;
import com.cloudinary.Url;
import com.cloudinary.utils.ObjectUtils;

import lombok.ToString;

@ExtendWith(MockitoExtension.class)
public class ArchivoUtilTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @Mock
    private Url url;

    // @Value("${marca.agua.pagina}")
    // private String marcaAguaPagina = "granjalafavorita.com";

    @InjectMocks
    private ArchivoUtil archivoUtil;

    private MockMultipartFile validImage, invalidImage;
    private Map<String, Object> mapa;

    @BeforeEach
    void setup(){
        validImage = new MockMultipartFile("conejo", "conejo.png", "image/png", new byte[]{1, 2, 3});
        invalidImage = new MockMultipartFile("conejo", "conejo.txt", "text/plain", new byte[]{1, 2, 3});
        mapa = new HashMap<>(Map.of("public_id", "123abc", "secure_url", "https://www.cloudinary.com/conejo.png")); // mutable
    }

    @Test
    void testSubirImagenCloudinary_Success() throws IOException{
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), anyMap())).thenReturn(mapa);
        // when(uploader.upload(any(byte[].class), anyMap())).thenReturn(resultUpload);
        
        Map<String, Object> result = archivoUtil.subirImagenCloudinary(validImage, "articulos", Optional.of("Campiconejo"));

        assertNotNull(result);
        assertEquals("123abc", result.get("public_id").toString());
        assertEquals("https://www.cloudinary.com/conejo.png", result.get("secure_url").toString());
        verify(uploader, times(1)).upload(any(), anyMap());
    }

    @Test
    void testSubirImagenCloudinary_InvalidImage() throws IOException{
        RuntimeException exception = assertThrows(RuntimeException.class, () -> archivoUtil.subirImagenCloudinary(invalidImage, "articulos", Optional.of("Campiconejo")));

        assertNotNull(exception);

        verify(uploader, never()).upload(any(), anyMap());
    }

    @Test
    void testSubirImagenCloudinary_NombreArticuloEmpty() throws IOException{
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), anyMap())).thenReturn(mapa);

        Map<String, Object> result = archivoUtil.subirImagenCloudinary(validImage, "articulos", Optional.empty());

        assertNotNull(result);
        assertEquals("123abc", result.get("public_id").toString());

        verify(uploader, times(1)).upload(any(), anyMap());
    }

    @Test
    void testSubirImagenCloudinary_ErrorImageUpload() throws IOException{
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), anyMap())).thenThrow(new RuntimeException());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> archivoUtil.subirImagenCloudinary(validImage, "articulos", Optional.of("Campiconejo")));

        assertNotNull(exception);

        verify(uploader, times(1)).upload(any(), anyMap());
    }

    @Test
    void testEliminarImagenCloudinary_Success() throws IOException{
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(anyString(), anyMap())).thenReturn(new HashMap<>());

        archivoUtil.eliminarImagenCloudinary("123abc");

        verify(uploader, times(1)).destroy(anyString(), anyMap());
    }

    @Test
    void testEliminarImagenCloudinary_Error() throws IOException{
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(anyString(), anyMap())).thenThrow(new RuntimeException());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> archivoUtil.eliminarImagenCloudinary("123abc"));

        assertNotNull(exception);

        verify(uploader, times(1)).destroy(anyString(), anyMap());
    }

    @Test
    void testRenombrarImagenCloudinary() throws IOException{
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.rename(anyString(), anyString(), anyMap())).thenReturn(mapa);

        Map<String, Object> result = archivoUtil.renombrarImagenCloudinary("123abc", "articulos", "Campiconejo");

        assertNotNull(result);
        assertEquals("123abc", result.get("public_id").toString());

        verify(uploader, times(1)).rename(anyString(), anyString(), anyMap());
    }

    @Test
    void testRenombrarImagenCloudinary_Error() throws IOException{
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.rename(anyString(), anyString(), anyMap())).thenThrow(new RuntimeException());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            archivoUtil.renombrarImagenCloudinary("123abc", "articulos", "Campiconejo"));

        assertNotNull(exception);

        verify(uploader, times(1)).rename(anyString(), anyString(), anyMap());
    }

    @Test
    void testGetUrlWithPagina_Success(){
        // Inyectar manualmente el valor de marcaAguaPagina
        ReflectionTestUtils.setField(archivoUtil, "marcaAguaPagina", "www.mipagina.com");

        when(cloudinary.url()).thenReturn(url);
        when(url.transformation(any(Transformation.class))).thenReturn(url);
        when(url.secure(anyBoolean())).thenReturn(url);
        when(url.generate(anyString())).thenReturn("123abc-granjalafavorita.com");

        String result = archivoUtil.getUrlWithPagina("123abc");

        assertNotNull(result);
        assertEquals("123abc-granjalafavorita.com", result);
    }

    @Test
    void testGetUrlWithPagina_Error(){
        // Inyectar manualmente el valor de marcaAguaPagina
        ReflectionTestUtils.setField(archivoUtil, "marcaAguaPagina", "www.mipagina.com");

        when(cloudinary.url()).thenReturn(url);
        when(url.transformation(any(Transformation.class))).thenThrow(new RuntimeException());
        // when(url.transformation(any(Transformation.class))).thenReturn(url);
        // when(url.secure(anyBoolean())).thenReturn(url);
        // when(url.generate(anyString())).thenThrow(new RuntimeException());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> archivoUtil.getUrlWithPagina("123abc"));

        assertNotNull(exception);
        verify(url, times(1)).transformation(any(Transformation.class));
    }

    @Test
    void testGetUrlWithLogo(){
        String publicId = archivoUtil.getUrlWithLogo("123abc");

        assertNotNull(publicId);
        assertEquals("", publicId);
    }
}
