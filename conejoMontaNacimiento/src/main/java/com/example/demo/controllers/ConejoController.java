package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.clients.RazaClient;
import com.example.demo.controllers.dto.ConejoDTO;
import com.example.demo.repositories.MontaRepository;
import com.example.demo.services.IConejoService;
import com.example.demo.util.ArchivoUtil;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/conejos")
public class ConejoController {

	@Autowired
	private ArchivoUtil archivoUtil;

	@Autowired
	private IConejoService conejoService;

	@Autowired
	private RazaClient razaClient;

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@GetMapping
	public String obtenerConejos(@RequestParam(defaultValue = "0") int pagina, 
								 @RequestParam(required = false) String sexo, 
								 Model model){

		int cantidad = 10; // Cantidad de conejos por pagina
		Page<ConejoDTO> pageConejos; // Pagina para almacenar los conejos

		// Condificional simplificado: si hay sexo, buscar por sexo, de lo contrario buscar todos los conejos
		pageConejos = sexo != null && !sexo.isEmpty() ? 
			conejoService.findBySexo(pagina, cantidad, sexo) : conejoService.findAll(pagina, cantidad); 

		model.addAttribute("sexo", sexo);
		model.addAttribute("listaConejos", pageConejos.getContent());
		model.addAttribute("paginaActual", pagina);
		model.addAttribute("totalPaginas", pageConejos.getTotalPages());
		model.addAttribute("totalElementos", pageConejos.getTotalElements());

		return "conejos/lista"; // Retornar al archivo html
	}

	// @GetMapping
	// public String obtenerConejos(@RequestParam(required = false) String sexo, Model model) {
	// 	List<ConejoDTO> listaConejos = new ArrayList<>();

	// 	// Filtrar por sexo si se proporciona, de lo contrario obtener todos los conejos
	// 	if(sexo == null || sexo.isEmpty()){
	// 		listaConejos = conejoService.obtenerConejos();
	// 	}else{
	// 		listaConejos = conejoService.obtenerConejosPorSexo(sexo);
	// 	}

	// 	model.addAttribute("sexo", sexo);
	// 	model.addAttribute("listaConejos", listaConejos);
	// 	return "conejos/lista"; // Retornar al archivo html
	// }

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@GetMapping("/crear")
	public String mostrarFormularioCrear(Model model) {
		ConejoDTO conejoDTO = new ConejoDTO();
		conejoDTO.setActivo(true);

		model.addAttribute("conejoDTO", conejoDTO); // Enviar ConejoDTO vacio
		model.addAttribute("listaRazas", razaClient.obtenerRazas()); // Enviar lista de razas
		//model.addAttribute("listaRazas", razaService.obtenerRazas()); // Enviar lista de razas
		model.addAttribute("titulo", "Registrar Conejo"); // Titulo del formulario
		model.addAttribute("accion", "/conejos/guardar"); // POST. Enviar endpoint para guardar

		return "conejos/formulario"; // Retornar al archivo html
	}

	@PostMapping("/guardar")
	public String guardarConejo(@ModelAttribute("conejoDTO") ConejoDTO conejoDTO, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {

		try {
			conejoService.guardarConejo(conejoDTO);
			redirectAttributes.addFlashAttribute("ok", "Conejo registrado correctamente.");
			// return "redirect:/conejos";
			// return "redirect:"+archivoUtil.getBaseUrl(request)+"/conejos";
			return "redirect:"+archivoUtil.getBaseUrlNginx(request)+"/conejos";

		} catch (Exception e) {
			model.addAttribute("conejoDTO", conejoDTO);
			model.addAttribute("listaRazas", razaClient.obtenerRazas());
			// model.addAttribute("listaRazas", razaService.obtenerRazas());
			model.addAttribute("titulo", "Registrar Conejo");
			model.addAttribute("accion", "/conejos/guardar");

			model.addAttribute("error", e.getMessage());
			return "conejos/formulario";
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@GetMapping("/editar/{id}")
	public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		Optional<ConejoDTO> conejoOpt = conejoService.obtenerConejoById(id);

		if(conejoOpt.isEmpty()){
			redirectAttributes.addFlashAttribute("error", "El conejo con id "+id+" no fue encontrado.");
			// return "redirect:/conejos";
			// return "redirect:"+archivoUtil.getBaseUrl(request)+"/conejos";
			return "redirect:"+archivoUtil.getBaseUrlNginx(request)+"/conejos";
		}

		model.addAttribute("conejoDTO", conejoOpt.get()); // Enviar ConejoDTO con la informacion
		model.addAttribute("listaRazas", razaClient.obtenerRazas()); // Enviar lista de razas
		// model.addAttribute("listaRazas", razaService.obtenerRazas()); // Enviar lista de razas
		model.addAttribute("titulo", "Editar Conejo"); // Enviar titulo del formulario
		model.addAttribute("accion", "/conejos/editar/" + id); // POST. Enviar endpoint para editar			
		return "conejos/formulario"; // Retornar al archivo html
	}

	@PostMapping("/editar/{id}")
	public String editarConejo(@PathVariable("id") Long id, @ModelAttribute("conejoDTO") ConejoDTO conejoDTO, 
		Model model, RedirectAttributes redirectAttributes, HttpServletRequest request){
		Optional<ConejoDTO> conejoOpt = conejoService.obtenerConejoById(id);
		
		try {
			conejoService.editarConejo(id, conejoDTO);
			redirectAttributes.addFlashAttribute("ok", "Conejo modificado correctamente.");
			// return "redirect:/conejos";
			// return "redirect:"+archivoUtil.getBaseUrl(request)+"/conejos";
			return "redirect:"+archivoUtil.getBaseUrlNginx(request)+"/conejos";

		} catch (Exception e) {
			model.addAttribute("conejoDTO", conejoDTO); // Enviar ConejoDTO con la informacion
			model.addAttribute("listaRazas", razaClient.obtenerRazas()); // Enviar lista de razas
			// model.addAttribute("listaRazas", razaService.obtenerRazas()); // Enviar lista de razas
			model.addAttribute("titulo", "Editar Conejo"); // Enviar titulo del formulario
			model.addAttribute("accion", "/conejos/editar/" + id); // POST. Enviar endpoint para editar	
			
			model.addAttribute("error", e.getMessage());
			return "conejos/formulario"; // Retornar al archivo html
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@GetMapping("/eliminar/{id}")
	public String eliminarConejo(@PathVariable("id") Long id, 
								@RequestParam(required = false) String sexo, 
								RedirectAttributes redirectAttributes, 
								HttpServletRequest request) {

		/*if(result.hasErrors()){
			List<String> mensajes = result.getAllErrors().stream()
				.map(error -> error.getDefaultMessage())
				.collect(Collectors.toList());

			redirectAttributes.addFlashAttribute("errores", mensajes);
			return "redirect:/conejos";
		}*/

		try {
			conejoService.eliminarConejoById(id);
			redirectAttributes.addFlashAttribute("ok", "Conejo eliminado correctamente");

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}

        // Si hay filtro, regresar a esa vista filtrada
        if (sexo != null && !sexo.isBlank()) {
            return "redirect:" + archivoUtil.getBaseUrlNginx(request) + "/conejos?sexo=" + sexo;
        }

		// return "redirect:/conejos";
		// return "redirect:"+archivoUtil.getBaseUrl(request)+"/conejos";
		return "redirect:"+archivoUtil.getBaseUrlNginx(request)+"/conejos";
	}	
}
