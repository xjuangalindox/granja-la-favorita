package com.example.demo.util;

import java.nio.file.Path;
import java.nio.file.Paths;
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

import jakarta.servlet.http.HttpServletRequest;

@Component
public class ArchivoUtil {
    
	// Obtener nombre base de imagen
	public static String obtenerNombreBaseImagen(MultipartFile imagen){
		return FilenameUtils.getBaseName(imagen.getOriginalFilename());
	}

    // Obtener extension de imagen
	public static String obtenerExtensionImagen(MultipartFile imagen){
		return FilenameUtils.getExtension(imagen.getOriginalFilename());
	}

	// Obtener extension de texto
	public static String obtenerExtensionTexto(String texto){
		return FilenameUtils.getExtension(texto);
	}

	// Crear nombre de la imagen
	public static String crearNombreImagen(String nombre, String extension){
		return nombre + "." + extension;
	}

	// Crear ruta con nombreImagen
	public static Path crearRuta(String ruta, String nombreImagen){
		return Paths.get(ruta, nombreImagen);
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Autowired
	private Cloudinary cloudinary;

	@Value("${marca.agua.pagina}")
	private String marcaAguaPagina;

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Subir imagen a CLOUDINARY.COM
	public Map<String, Object> subirImagenCloudinary(MultipartFile imagen, String folder, Optional<String> publicId){	
		List<String> allowedExtensionts = Arrays.asList("jpg", "jpeg", "png", "webp", "avif");
		String extension = ArchivoUtil.obtenerExtensionImagen(imagen);

		// ¿La extensión de la imagen es valida?
		if(!allowedExtensionts.contains(extension)) throw new RuntimeException("La extensión "+extension+" de la imagen es inavlida");

		// Preparar parametros del mapa para subir imagen a CLOUDINARYY.COM
		Map<String, Object> params = new HashMap<>();
		params.put("folder", folder);
		if(publicId.isPresent()) params.put("public_id", publicId.get());

        // Subir imagen a Cloudinary
		try {
			Map<String, Object> resultUpload = cloudinary.uploader().upload(imagen.getBytes(), params);
			return resultUpload;

		} catch (Exception e) {
			throw new RuntimeException("Ocurrio un error al subir la imagen a CLOUDINARY");
		}
	}
		
	// Eliminar imagen de CLOUDINARY.COM
	public void eliminarImagenCloudinary(String publicId){
		try {
			cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap()); // Funcionando para modulo conejo

		} catch (Exception e) {
			throw new RuntimeException("Ocurrio un error al eliminar la imagen en CLOUDINARY");
		}
	}

	// Crear copia de imagen vieja por imagen nueva
	public Map<String, Object> renombrarImagenCloudinary(String publicId, String toFolder, String nombreConejo){
		try {
			Map<String, Object> resultRename = cloudinary.uploader().rename(publicId, toFolder+"/"+nombreConejo, ObjectUtils.emptyMap());
			return resultRename;

		} catch (Exception e) {
			throw new RuntimeException("Ocurrio un error renombrar o mover la imagen en CLOUDINARY.");
		}
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
						.gravity("center")  // Centrar marca de agua
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

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getBaseUrlNginx(HttpServletRequest request){
        String proto = request.getHeader("X-Forwarded-Proto"); // http o https
        String host = request.getHeader("X-Forwarded-Host"); // dominio o IP
        
        return proto +"://"+ host;
    }
}
