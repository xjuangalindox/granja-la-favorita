package com.favorita.articulos.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.transformation.TextLayer;
import com.cloudinary.utils.ObjectUtils;

@Component
public class ArchivoUtil {
    
    @Autowired
    private Cloudinary cloudinary;

	@Value("${marca.agua.pagina}")
	private String marcaAguaPagina;

    // private final Cloudinary cloudinary;

    // public ArchivoUtil(Cloudinary cloudinary){
    //     this.cloudinary = cloudinary;
    // }

	// Subir imagen a CLOUDINARY.COM
	public Map<String, Object> subirImagenCloudinary(MultipartFile imagen, String folder, Optional<String> nombreArticulo){	
		List<String> allowedExtensionts = Arrays.asList("jpg", "jpeg", "png", "webp", "avif");
		String extension = ArchivoUtil.obtenerExtensionImagen(imagen);

		// ¿La extensión de la imagen es valida?
		if(!allowedExtensionts.contains(extension)){
            String msg = "La entension "+extension+" de la imagen no es valida."; 
            throw new RuntimeException(msg);
		}

		// Preparar parametros del mapa para subir imagen a CLOUDINARYY.COM
		Map<String, Object> params = new HashMap<>();
		params.put("folder", folder);

		if(nombreArticulo.isPresent()){
			params.put("public_id", nombreArticulo.get());
		}

        // ¿Se subio la imagen a CLOUDINARY.COM?
		try {
			Map<String, Object> resultUpload = cloudinary.uploader().upload(imagen.getBytes(), params);
			return resultUpload;

		} catch (Exception e) {
            String msg = "Ocurrio un error al subir la imagen a CLOUDINARY."; 
			throw new RuntimeException(msg);
		}
	}    

	// Eliminar imagen de CLOUDINARY.COM
	public void eliminarImagenCloudinary(String publicId){
		try {
			cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap()); // Funcionando para modulo conejo

		} catch (Exception e) {
			String msg = "Ocurrio un error al eliminar la imagen en CLOUDINARY.";
			throw new RuntimeException(msg);
		}
	}

	// Crear copia de imagen vieja por imagen nueva
	public Map<String, Object> renombrarImagenCloudinary(String publicId, String toFolder, String nombreArticulo){
		try {
			Map<String, Object> resultRename = cloudinary.uploader().rename(publicId, toFolder+"/"+nombreArticulo, ObjectUtils.emptyMap());
			return resultRename;

		} catch (Exception e) {
			String msg = "Ocurrio un error renombrar o mover la imagen en CLOUDINARY.";
			throw new RuntimeException(msg);
		}
	}

    // Obtener extension de imagen
	public static String obtenerExtensionImagen(MultipartFile imagen){
		return FilenameUtils.getExtension(imagen.getOriginalFilename());
	}

	// Obtener url con marca de agua (granjalafavorita.com)
	public String getUrlWithPagina(String publicId){
		// Crear overlay de texto usando el constructor de TextLayer
		TextLayer marcaAgua = new TextLayer()
				.text(marcaAguaPagina)
				.fontFamily("Arial")
				.fontSize(40)
				.fontWeight("bold");

		return cloudinary.url()
				.transformation(new Transformation<>()
						.overlay(marcaAgua)
						.gravity("center")  // Cambiado a centro
						.opacity(70)
						.color("#FFFFFF")
						// .x(20)
						// .y(20)
				)
				.secure(true)
				.generate(publicId);
	}

	// Obtener url con marca de agua (logotipo)
	public String getUrlWithLogo(String publicId){
		return "";
	}

}
