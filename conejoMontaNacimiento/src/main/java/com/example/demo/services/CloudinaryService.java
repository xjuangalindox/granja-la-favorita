package com.example.demo.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.transformation.TextLayer;
import com.cloudinary.utils.ObjectUtils;
import com.example.demo.util.ArchivoUtil;

@Service
public class CloudinaryService {
    
    private static final Logger logger = LoggerFactory.getLogger(CloudinaryService.class);

    @Autowired
    private ArchivoUtil archivoUtil;

    @Autowired
    private Cloudinary cloudinary;

    // Método asíncrono para subir una sola imagen
    @Async("asyncExecutor")
    public CompletableFuture<Map<String, Object>> subirImagenAsync(MultipartFile imagen, String folder, Optional<String> publicId) {
        List<String> allowedExtensionts = Arrays.asList("jpg", "jpeg", "png", "webp", "avif");
		String extension = archivoUtil.obtenerExtensionImagen(imagen);

        if(!allowedExtensionts.contains(extension)) throw new RuntimeException("Extension de imagen no permitida");

        Map<String, Object> datos = new HashMap<>();
        datos.put("folder", folder);
        if(publicId.isPresent())  datos.put("public_id", publicId.get());

        Map<String, Object> resultUpload = new HashMap<>();
        try {
            resultUpload = cloudinary.uploader().upload(imagen.getBytes(), datos);
            logger.info("Imagen subida a Cloudinary {}", publicId);

        } catch (Exception e) {
            logger.error("Error subiendo imagen {}: {}", publicId, e.getMessage());
            throw new RuntimeException("Error al subir la imagen a Cloudinary");
        }

        return CompletableFuture.completedFuture(resultUpload);
    }

    @Async("asyncExecutor")
    public CompletableFuture<Void> eliminarImagenAsync(String publicId){
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            logger.info("Imagen eliminada de Cloudinary {}",publicId);

        } catch (Exception e) {
            logger.error("Error eliminando imagen {}: {}",publicId, e.getMessage());
            throw new RuntimeException("Error al eliminar la imagen de Cloudinary");
        }

        return CompletableFuture.completedFuture(null);
    }

	@Value("${marca.agua.pagina}")
	private String marcaAguaPagina;

	// Obtener url con marca de agua (granjalafavorita.com)
	public String getUrlWithPagina(String publicId){
		TextLayer marcaAgua = new TextLayer()
				.text(marcaAguaPagina)
				.fontFamily("Arial")
				.fontSize(40)
				.fontWeight("bold");

		return cloudinary.url()
				.transformation(new Transformation<>()
						.overlay(marcaAgua)
						.gravity("center")
						.opacity(70)
						.color("#FFFFFF")
				)
				.secure(true)
				.generate(publicId);
	}    
}
