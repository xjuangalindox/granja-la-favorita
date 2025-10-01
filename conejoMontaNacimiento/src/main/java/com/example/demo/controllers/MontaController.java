package com.example.demo.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.controllers.dto.MontaDTO;
import com.example.demo.models.enums.EstatusMonta;
import com.example.demo.services.IConejoService;
import com.example.demo.services.IMontaService;
import com.example.demo.util.ArchivoUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/montas")
public class MontaController {

	@Autowired
	private ArchivoUtil archivoUtil;

	@Autowired
	private IMontaService montaService;

	@Autowired
	private IConejoService conejoService;

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@GetMapping
	public String obtenerMontas(@RequestParam(defaultValue = "0") int pagina, 
								@RequestParam(required = false) String estatus, 
								Model model){
	
		int cantidad = 10; // Cantidad de montas por pagina
		Page<MontaDTO> pageMontas; // Pagina para almacenar las montas

		pageMontas = estatus != null && !estatus.isEmpty() ?
			montaService.findByEstatus(pagina, cantidad, EstatusMonta.valueOf(estatus.toUpperCase())) : montaService.findAll(pagina, cantidad);

		model.addAttribute("listaEstatus", EstatusMonta.values()); // Enviar lista de estatus
		model.addAttribute("estatus", estatus);
		model.addAttribute("listaMontas", pageMontas.getContent());
		model.addAttribute("paginaActual", pagina);
		model.addAttribute("totalPaginas", pageMontas.getTotalPages());
		model.addAttribute("totalElements", pageMontas.getTotalElements());
	
		return "montas/lista";
	}


	// @GetMapping
	// public String obtenerMontasPaginadas(@RequestParam(defaultValue = "0") int pagina, Model model) {
	// 	// Número de montas por página, se puede cambiar según necesidad
	// 	int montasPagina = 10;

	// 	// Obtener las montas paginadas
	// 	Page<MontaDTO> montasPage = montaService.obtenerMontasPaginadas(pagina, montasPagina);

	// 	model.addAttribute("listaMontas", montasPage.getContent()); // montas de la página actual
	// 	model.addAttribute("paginaActual", montasPage.getNumber()); // Página actual
	// 	model.addAttribute("totalPaginas", montasPage.getTotalPages()); // Total de páginas

	// 	return "montas/lista"; // Retorna la vista con la lista de montas paginadas
	// }

	// @GetMapping
	// public String obtenerMontas(Model model){
	// 	List<MontaDTO> listaMontas = montaService.obtenerMontas();
	// 	model.addAttribute("listaMontas", listaMontas);

	// 	return "montas/lista";
	// }

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	@GetMapping("/crear")
	public String formularioCrear(Model model){
		model.addAttribute("montaDTO", new MontaDTO());	// Enviar DTO
		model.addAttribute("titulo", "Registrar Monta");	// Enviar titulo del formulario
		model.addAttribute("accion", "/montas/guardar");	// Enviar endpoint para guardar
		model.addAttribute("listaEstatus", EstatusMonta.values());	// Enviar valores del enum
		model.addAttribute("listaMachos", conejoService.obtenerConejosActivosPorSexo("Macho")); // Enviar lista de machos
		model.addAttribute("listaHembras", conejoService.obtenerConejosActivosPorSexo("Hembra")); // Enviar lista de hembras

		return "montas/formulario";	// Retornar al html
	}

	@PostMapping("/guardar")
	public String guardarMonta(@ModelAttribute("montaDTO") @Valid MontaDTO montaDTO, 
		BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		
		/*if(result.hasErrors()){
			List<String> errores = result.getAllErrors().stream()
				.map(error -> error.getDefaultMessage())
				.collect(Collectors.toList());
			model.addAttribute("errores", errores);
		}*/

		try {
			montaService.guardarMonta(montaDTO);
			redirectAttributes.addFlashAttribute("ok", "Monta registrada correctamente.");
			// return "redirect:/montas";
			// return "redirect:"+archivoUtil.getBaseUrl(request)+"/montas";
			return "redirect:"+archivoUtil.getBaseUrlNginx(request)+"/montas";

		} catch (Exception e) {
			model.addAttribute("montaDTO", montaDTO);
			model.addAttribute("titulo", "Registrar Monta");
			model.addAttribute("accion", "/montas/guardar");
			model.addAttribute("listaEstatus", EstatusMonta.values());
			model.addAttribute("listaMachos", conejoService.obtenerConejosPorSexo("Macho"));
			model.addAttribute("listaHembras", conejoService.obtenerConejosPorSexo("Hembra"));

			model.addAttribute("error", e.getMessage());
			return "montas/formulario";
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@GetMapping("/editar/{id}")
	public String formularioEditar(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request){

		Optional<MontaDTO> montaOpt = montaService.obtenerMontaById(id);
		if(montaOpt.isEmpty()){
			redirectAttributes.addFlashAttribute("error", "La monta con ID "+id+" no fue encontrada.");
			// return "redirect:/montas";
			// return "redirect:"+archivoUtil.getBaseUrl(request)+"/montas";
			return "redirect:"+archivoUtil.getBaseUrlNginx(request)+"/montas";
		}

		// ¿Tiene nacimiento asociado?
		MontaDTO montaDTO = montaOpt.get();
		montaDTO.setTieneNacimiento(montaDTO.getNacimiento() != null);

		model.addAttribute("montaDTO", montaOpt.get());	// Enviar plantilla con la informacion
		model.addAttribute("titulo", "Editar Monta");	// Enviar titulo del formulario
		model.addAttribute("accion", "/montas/editar/"+id);	// Enviar endpoint para editar
		model.addAttribute("listaEstatus", EstatusMonta.values()); // Enviar lista de EstatusMonta
		model.addAttribute("listaMachos", conejoService.obtenerConejosPorSexo("Macho")); // Enviar lista de hembras
		model.addAttribute("listaHembras", conejoService.obtenerConejosPorSexo("Hembra")); // Enviar lista de machos

		return "montas/formulario";	// Retornar al html
	}

	@PostMapping("/editar/{id}")
	public String editarMonta(@PathVariable("id") Long id, @ModelAttribute("montaDTO") @Valid MontaDTO montaDTO, 
		BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request){

		/*if(result.hasErrors()){
			List<String> errores = result.getAllErrors().stream()
				.map(error -> error.getDefaultMessage())
				.collect(Collectors.toList());
			model.addAttribute("errores", errores);
		}*/

		try {
			montaService.editarMonta(id, montaDTO);
			redirectAttributes.addFlashAttribute("ok", "Monta modificada correctamente.");
			// return "redirect:/montas";
			// return "redirect:"+archivoUtil.getBaseUrl(request)+"/montas";
			return "redirect:"+archivoUtil.getBaseUrlNginx(request)+"/montas";

		} catch (Exception e) {
			model.addAttribute("montaDTO", montaDTO);
			model.addAttribute("titulo", "Editar Monta");
			model.addAttribute("accion", "/montas/editar/"+id);
			model.addAttribute("listaEstatus", EstatusMonta.values());
			model.addAttribute("listaMachos", conejoService.obtenerConejosPorSexo("Macho"));
			model.addAttribute("listaHembras", conejoService.obtenerConejosPorSexo("Hembra"));	

			model.addAttribute("error", e.getMessage());
			return "montas/formulario";
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@GetMapping("/eliminar/{id}")
	public String eliminarMonta(@PathVariable("id") Long id, 
								@RequestParam(required = false) String estatus, 
								RedirectAttributes redirectAttributes, 
								HttpServletRequest request){
		try {
			montaService.eliminarMontaById(id);
			redirectAttributes.addFlashAttribute("ok", "Monta eliminada correctamente");

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}

        // Si hay filtro, regresar a esa vista filtrada
        if (estatus != null && !estatus.isBlank()) {
            return "redirect:" + archivoUtil.getBaseUrlNginx(request) + "/montas?estatus=" + estatus;
        }

		// return "redirect:/montas";
		// return "redirect:"+archivoUtil.getBaseUrl(request)+"/montas";
		return "redirect:"+archivoUtil.getBaseUrlNginx(request)+"/montas";
	}
}