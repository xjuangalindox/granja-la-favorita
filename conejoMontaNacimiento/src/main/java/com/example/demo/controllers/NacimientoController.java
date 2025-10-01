package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
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

import com.example.demo.controllers.dto.EjemplarDTO;
import com.example.demo.controllers.dto.MontaDTO;
import com.example.demo.controllers.dto.NacimientoDTO;
import com.example.demo.services.IMontaService;
import com.example.demo.services.INacimientoService;
import com.example.demo.util.ArchivoUtil;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/nacimientos")
public class NacimientoController {

	@Autowired
	private ArchivoUtil archivoUtil;

	@Autowired
	private INacimientoService nacimientoService;

	@Autowired
	private IMontaService montaService;

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@GetMapping
	public String obtenerNacimientos(@RequestParam(defaultValue = "0") int pagina, 
									@RequestParam(defaultValue = "") String filtrado,
									Model model){

		int cantidad = 10;
		Page<NacimientoDTO> pageNacimientos;

		switch (filtrado) {
			case "":
				pageNacimientos = nacimientoService.findAll(pagina, cantidad);
				break;
			case "Ninguno":
				pageNacimientos = nacimientoService.findNacimientosSinEjemplares(pagina, cantidad);
				break;
			case "Disponibles":
				pageNacimientos = nacimientoService.findNacimientosConEjemplaresDisponibles(pagina, cantidad);
				break;
			case "Vendidos":
				pageNacimientos = nacimientoService.findNacimientosConTodosEjemplaresVendidos(pagina, cantidad);
				break;
			default:
				// Si viene algo que no esperas, mejor traer todos
				pageNacimientos = nacimientoService.findAll(pagina, cantidad);
				break;
		}

		// Informacion enviada a la vista
		model.addAttribute("filtrado", filtrado); // Enviar el valor del filtrado
		model.addAttribute("listaNacimientos", pageNacimientos.getContent()); // Enviar lista de nacimientos
		model.addAttribute("paginaActual", pagina); // Enviar pagina actual
		model.addAttribute("totalPaginas", pageNacimientos.getTotalPages()); // Enviar total de paginas
		model.addAttribute("totalElementos", pageNacimientos.getTotalElements()); // Enviar total de elementos

		return "nacimientos/lista"; // Retornar al html
	}

	// @GetMapping
	// public String obtenerNacimientos(Model model) {
	// 	List<NacimientoDTO> listaNacimientos = nacimientoService.obtenerNacimientos();
	// 	model.addAttribute("listaNacimientos", listaNacimientos);

	// 	return "nacimientos/lista"; // Retornar al html
	// }

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@GetMapping("/crear")
	public String formularioCrear(Model model, 
								RedirectAttributes redirectAttributes, 
								HttpServletRequest request,
								@RequestParam(name = "idMonta", required = false) Long idMonta) { // idMonta viene desde montas/lista.html

		NacimientoDTO nacimientoDTO = new NacimientoDTO();
		
		if(idMonta != null){
			Optional<MontaDTO> montaOpt = montaService.obtenerMontaById(idMonta);
			if(montaOpt.isEmpty()){
				redirectAttributes.addFlashAttribute("error", "La monta con ID "+idMonta+" no fue encontrada.");
				// return "redirect:/montas";
				// return "redirect:"+archivoUtil.getBaseUrl(request)+"/montas";
				return "redirect:"+archivoUtil.getBaseUrlNginx(request)+"/montas";
			}
			nacimientoDTO.setMonta(montaOpt.get());
		}

		model.addAttribute("nacimientoDTO", nacimientoDTO);	// Enviar nacimientoDTO
		model.addAttribute("titulo", "Registrar Nacimiento");	// Enviar titulo del formulario
		model.addAttribute("accion", "/nacimientos/guardar");	// Enviar endpoint para guardar el nacimiento
		model.addAttribute("listaMontas", montaService.findByNacimientoIsNull()); // Enviar lista de montas sin nacimiento

		return "nacimientos/formulario";	//	Retornar al formulario html
	}

	@PostMapping("/guardar")
	public String guardarNacimiento(@ModelAttribute("nacimientoDTO") NacimientoDTO nacimientoDTO, 
									Model model, 
									RedirectAttributes redirectAttributes, 
									HttpServletRequest request) {
		
		try {
			nacimientoService.guardarNacimiento(nacimientoDTO);
			redirectAttributes.addFlashAttribute("ok", "Nacimiento registrado correctamente.");
			// return "redirect:/nacimientos";
			// return "redirect:"+archivoUtil.getBaseUrl(request)+"/nacimientos";
			return "redirect:"+archivoUtil.getBaseUrlNginx(request)+"/nacimientos";

		} catch (Exception e) {
			model.addAttribute("nacimientoDTO", nacimientoDTO);	// Enviar nacimientoDTO con informacion
			model.addAttribute("titulo", "Registrar Nacimiento");	// Enviar titulo del formulario
			model.addAttribute("accion", "/nacimientos/guardar");	// Enviar endpoint para guardar el nacimiento
			model.addAttribute("listaMontas", montaService.findByNacimientoIsNull()); // Enviar lista de montas sin nacimiento
			// model.addAttribute("listaMontas", montaService.obtenerMontas()); // Enviar lista de montas
			
			model.addAttribute("error", e.getMessage()); // Enviar mensaje de error
			return "nacimientos/formulario";
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@GetMapping("/editar/por-monta/{idMonta}")
	public String obtenerNacimientoPorMonta(@PathVariable("idMonta") Long idMonta, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {

		Optional<MontaDTO> montaOpt = montaService.obtenerMontaById(idMonta);
		if(montaOpt.isEmpty()){
			redirectAttributes.addFlashAttribute("error", "La monta con ID " + idMonta + " no fue encontrada.");
			// return "redirect:/montas";
			// return "redirect:"+archivoUtil.getBaseUrl(request)+"/montas";
			return "redirect:"+archivoUtil.getBaseUrlNginx(request)+"/montas";
		}

		MontaDTO montaDTO = montaOpt.get();
		Optional<NacimientoDTO> nacimientoOpt = nacimientoService.findByMonta(montaDTO);

		if(nacimientoOpt.isEmpty()){
			redirectAttributes.addFlashAttribute("error", "La monta con ID "+ idMonta+" no pertenece a ningún nacimiento.");
			// return "redirect:/montas";
			// return "redirect:"+archivoUtil.getBaseUrl(request)+"/montas";
			return "redirect:"+archivoUtil.getBaseUrlNginx(request)+"/montas";
		}

		NacimientoDTO nacimientoDTO = nacimientoOpt.get();

		model.addAttribute("nacimientoDTO", nacimientoDTO);
		model.addAttribute("titulo", "Editar Nacimiento");
		model.addAttribute("accion", "/nacimientos/editar/"+nacimientoDTO.getId());
		model.addAttribute("listaMontas", montaService.findByNacimientoIsNull()); // Enviar lista de montas sin nacimiento
		// model.addAttribute("listaMontas", montaService.obtenerMontas());
		return "nacimientos/formulario";
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@GetMapping("/editar/{id}")
	public String formularioEditar(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		Optional<NacimientoDTO> nacimientoOpt = nacimientoService.obtenerNacimientoById(id);
		
		if(nacimientoOpt.isEmpty()){
			redirectAttributes.addFlashAttribute("error", "El nacimiento con ID " + id + " no fue encontrado.");
			// return "redirect:/nacimientos";
			// return "redirect:"+archivoUtil.getBaseUrl(request)+"/nacimientos";
			return "redirect:"+archivoUtil.getBaseUrlNginx(request)+"/nacimientos";
		}

		model.addAttribute("nacimientoDTO", nacimientoOpt.get());
		model.addAttribute("titulo", "Editar Nacimiento");
		model.addAttribute("accion", "/nacimientos/editar/"+id);
		// model.addAttribute("listaMontas", montaService.findByNacimientoIsNull()); // Enviar lista de montas sin nacimiento
		// model.addAttribute("listaMontas", montaService.obtenerMontas());
		return "nacimientos/formulario";
	}
	
	@PostMapping("/editar/{id}")
	public String editarNacimiento(@PathVariable("id") Long id, @ModelAttribute("nacimientoDTO") NacimientoDTO nacimientoDTO, HttpServletRequest request,
		@RequestParam(name = "ejemplaresEliminados", required = false) List<Long> ejemplaresEliminados,
		Model model, RedirectAttributes redirectAttributes) {
	
		// ¿Existe el nacimiento?
		Optional<NacimientoDTO> nacimientoOpt = nacimientoService.obtenerNacimientoById(id);
		if(nacimientoOpt.isEmpty()){
			redirectAttributes.addFlashAttribute("error", "El nacimiento con ID " + id + " no fue encontrado.");
			return "redirect:"+archivoUtil.getBaseUrlNginx(request)+"/nacimientos";
		}

		try {
			nacimientoService.editarNacimiento(id, nacimientoDTO, ejemplaresEliminados);
			redirectAttributes.addFlashAttribute("ok", "Nacimiento modificado correctamente.");

		} catch (Exception e) {
			model.addAttribute("nacimientoDTO", nacimientoOpt.get());
			model.addAttribute("titulo", "Editar Nacimiento");
			model.addAttribute("accion", "/nacimientos/editar/"+id);
			// model.addAttribute("listaMontas", montaService.findByNacimientoIsNull()); // Enviar lista de montas sin nacimiento
			// model.addAttribute("listaMontas", montaService.obtenerMontas());

			model.addAttribute("error", "Ocurrio un error al modificar el nacimiento.");
			return "nacimientos/formulario";
		}

		// return "redirect:/nacimientos";	// Redireccionar al endpoint: /nacimientos
		// return "redirect:"+archivoUtil.getBaseUrl(request)+"/nacimientos";
		return "redirect:"+archivoUtil.getBaseUrlNginx(request)+"/nacimientos";
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@GetMapping("/eliminar/{id}")
	public String eliminarNacimiento(@PathVariable("id") Long id, 
									@RequestParam(defaultValue = "") String filtrado,
									RedirectAttributes redirectAttributes, 
									HttpServletRequest request) {
		try {
			nacimientoService.eliminarNacimientoById(id);
			redirectAttributes.addFlashAttribute("ok", "Nacimiento eliminado correctamente.");

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}

		if(filtrado.equals("")){
			return "redirect:"+archivoUtil.getBaseUrlNginx(request)+"/nacimientos?filtrado="+filtrado;
		}
		
		// return "redirect:/nacimientos";
		// return "redirect:"+archivoUtil.getBaseUrl(request)+"/nacimientos";
		return "redirect:"+archivoUtil.getBaseUrlNginx(request)+"/nacimientos";
	}
	







	/*
	// Obtener nacimientos
	@GetMapping("/nacimientos")
	public ResponseEntity<List<NacimientoModel>> obtenerNacimientos() {
		List<NacimientoModel> lista = nacimientoService.obtenerNacimientos();

		if (lista.isEmpty()) {
			return ResponseEntity.noContent().build(); // no content
		}
		return ResponseEntity.ok(lista); // ok
	}

	// Buscar nacimiento por id
	@GetMapping("/nacimientos/{id}")
	public ResponseEntity<NacimientoModel> buscarNacimientoPorId(@PathVariable("id") Long id) {
		NacimientoModel nacimientoBuscado = nacimientoService.buscarNacimientoPorId(id);

		if (nacimientoBuscado == null) {
			return ResponseEntity.notFound().build(); // not found
		}
		return ResponseEntity.ok(nacimientoBuscado); // ok
	}

	// Guardar o modificar nacimiento
	@PostMapping("/nacimientos")
	public ResponseEntity<?> guardarNacimiento(@RequestBody NacimientoModel nacimientoModel) {
		return ResponseEntity.ok(nacimientoService.guardarNacimiento(nacimientoModel));/*
		NacimientoModel creado = nacimientoService.guardarNacimiento(nacimientoModel);
		
		// Crear la URI del nuevo recurso creado
		URI location = URI.create("/nacimientos/"+creado.getId());
		
		// Retornar la respuesta con 201 Created y la URI del nuevo nacimiento
		return ResponseEntity.created(location).body(creado);
			
	}
	
	// Modifcar nacimiento
	@PutMapping("/nacimientos/{id}")
	public ResponseEntity<?> modificarNacimientoPorId(@PathVariable("id") Long id, @RequestBody NacimientoModel nacimientoModel){
		try {
			NacimientoModel actualizado = nacimientoService.modificarNacimientoPorId(id, nacimientoModel);
			return ResponseEntity.ok(actualizado);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	// Eliminar nacimiento
	@DeleteMapping("/nacimientos/{id}")
	public ResponseEntity<?> eliminarNacimiento(@PathVariable("id") Long id) {
		boolean eliminado = nacimientoService.eliminarNacimientoPorId(id);
		if (eliminado) {
			return ResponseEntity.noContent().build(); // 204 Eliminacion correctamente
		}
		return ResponseEntity.notFound().build(); // 404 Error el eliminar
	}*/

}
