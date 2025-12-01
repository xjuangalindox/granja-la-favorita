package com.example.demo.controllers;

import java.util.List;

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

import com.example.demo.controllers.dto.RecreoDTO;
import com.example.demo.services.IRecreoService;
import com.example.demo.util.ArchivoUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/recreos")
public class RecreoController {

    @Autowired
    private IRecreoService recreoService;

    @Autowired
    private ArchivoUtil archivoUtil;

    @GetMapping()
    public String findAll(@RequestParam(defaultValue = "0") int pageNumber, Model model){
        int pageSize = 10;
        Page<RecreoDTO> pageRecreos = recreoService.findAll(pageNumber, pageSize);

        model.addAttribute("listaRecreos", pageRecreos.getContent());
        model.addAttribute("pagina", pageNumber);
        model.addAttribute("totalPaginas", pageRecreos.getTotalPages());
        model.addAttribute("totalElementos", pageRecreos.getTotalElements());

        return "recreos/lista";
    } 

    @GetMapping("/crear")
    public String formularioCrear(Model model){
        model.addAttribute("recreoDTO", new RecreoDTO());
        model.addAttribute("titulo", "Crear Recreo");
        model.addAttribute("accion", "/recreos/guardar");

        return "recreos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(
        @Valid @ModelAttribute("recreoDTO") RecreoDTO recreoDTO,
        BindingResult result,
        Model model,
        RedirectAttributes redirectAttributes,
        HttpServletRequest request){

        // VALIDATIONS
        if(result.hasErrors()){
            List<String> errores = result.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .toList();
            
            model.addAttribute("errores", errores);
            model.addAttribute("recreoDTO", recreoDTO);
            model.addAttribute("titulo", "Crear Recreo");
            model.addAttribute("accion", "/recreos/guardar");
            return "recreos/formulario";
        }
        // FIN VALIDATIONS

        try {
            recreoService.saveRecreo(recreoDTO);
            redirectAttributes.addFlashAttribute("ok", "Recreo registrado correctamente");
            return "redirect:"+archivoUtil.getBaseUrlNginx(request)+"/recreos";

        } catch (Exception e) {
            model.addAttribute("recreoDTO", recreoDTO);
            model.addAttribute("titulo", "Crear Recreo");
            model.addAttribute("accion", "/recreos/guardar");

            model.addAttribute("error", e.getMessage());
            return "recreos/formulario";
        }
    }

    @GetMapping("/editar/{id}")
    public String formularioEditar(
        @PathVariable("id") Long id, 
        Model model,
        RedirectAttributes redirectAttributes,
        HttpServletRequest request){

        try {
            RecreoDTO recreoDTO = recreoService.findById(id);
            model.addAttribute("recreoDTO", recreoDTO);
            model.addAttribute("titulo", "Editar Recreo");
            model.addAttribute("accion", "/recreos/editar/"+id);
            return "recreos/formulario";

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:"+archivoUtil.getBaseUrlNginx(request)+"/recreos";
        }
    }

    @PostMapping("/editar/{id}")
    public String editar(
        @PathVariable("id") Long id,
        @Valid @ModelAttribute("recreoDTO") RecreoDTO recreoDTO,
        BindingResult result,
        Model model,
        RedirectAttributes redirectAttributes,
        HttpServletRequest request){

        // VALIDATIONS
        if(result.hasErrors()){
            List<String> errores = result.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .toList();
            
            model.addAttribute("errores", errores);
            model.addAttribute("recreoDTO", recreoDTO);
            model.addAttribute("titulo", "Editar Recreo");
            model.addAttribute("accion", "/recreos/editar/"+id);
            return "recreos/formulario";
        }
        // FIN VALIDATIONS

        try {
            recreoService.updateRecreo(id, recreoDTO);
            redirectAttributes.addFlashAttribute("ok", "Recreo modificado correctamente");
            return "redirect:"+archivoUtil.getBaseUrlNginx(request)+"/recreos";

        } catch (Exception e) {
            model.addAttribute("recreoDTO", recreoDTO);
            model.addAttribute("titulo", "Editar Recreo");
            model.addAttribute("accion", "/recreos/editar/"+id);
            return "recreos/formulario";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(
        @PathVariable("id") Long id,
        RedirectAttributes redirectAttributes,
        HttpServletRequest request){

        try {
            recreoService.deleteById(id);
            redirectAttributes.addFlashAttribute("ok", "Recreo eliminado correctamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:"+archivoUtil.getBaseUrlNginx(request)+"/recreos";
    }
}
