package com.favorita.articulos.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

import com.cloudinary.Cloudinary;

@ExtendWith(MockitoExtension.class)
public class AppConfigTest {

    // @InjectMocks
    private AppConfig appConfig = new AppConfig();

    @Test
    void testModelMapper(){
        // given
        // when
        // then
        ModelMapper modelMapper = appConfig.modelMapper();

        assertNotNull(modelMapper);
        assertInstanceOf(ModelMapper.class, modelMapper);
    }

    @Test
    void testCloudinary(){
        // given
        ReflectionTestUtils.setField(appConfig, "cloudName", "nombre");
        ReflectionTestUtils.setField(appConfig, "apiKey", "llave");
        ReflectionTestUtils.setField(appConfig, "apiSecret", "secreto");

        // when
        // then
        Cloudinary cloudinary = appConfig.cloudinary();

        assertNotNull(cloudinary);
        assertEquals("nombre", cloudinary.config.cloudName);
        assertEquals("llave", cloudinary.config.apiKey);
        assertEquals("secreto", cloudinary.config.apiSecret);
    }
}
