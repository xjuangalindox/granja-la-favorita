package com.example.demo.util;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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


}
