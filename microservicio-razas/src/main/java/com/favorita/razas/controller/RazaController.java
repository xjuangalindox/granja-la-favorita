package com.favorita.razas.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.favorita.razas.controller.dto.RazaDTO;
import com.favorita.razas.service.IRazaService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class RazaController {
    
    @Autowired
    private IRazaService razaService;

    // public String getBaseUrl(HttpServletRequest request){
    //     String proto = request.getHeader("X-Forwarded-Proto"); // http o https
    //     String host = request.getHeader("X-Forwarded-Host"); // dominio o IP
    //     String port = request.getHeader("X-Forwarded-Port"); // puerto visible
        
    //     return proto +"://"+ host +":" + port;
    // }

    public String getBaseUrlNginx(HttpServletRequest request){
        String proto = request.getHeader("X-Forwarded-Proto"); // http o https
        String host = request.getHeader("X-Forwarded-Host"); // dominio o IP
        
        return proto +"://"+ host;
    }

    @GetMapping("/razas")
    public String obtenerRazas(Model model){
        List<RazaDTO> listaRazas = razaService.obtenerRazas();
        model.addAttribute("listaRazas", listaRazas);

        return "razas/lista";
    }

    @GetMapping("/razas/create")
    public String crearRaza(Model model) {
        model.addAttribute("razaDTO", new RazaDTO());
        model.addAttribute("titulo", "Crear Raza");
        model.addAttribute("accion", "/razas/create");
        return "razas/formulario";
    }

    @PostMapping("/razas/create")
    public String guardarRaza(@ModelAttribute("razaDTO") RazaDTO razaDTO, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request){
        try {
            razaService.guardarRaza(razaDTO);
            redirectAttributes.addFlashAttribute("ok", "Raza registrada correctamente");
            // return "redirect:/razas";
            // return "redirect:http://localhost:8080/razas";
            // return "redirect:"+getBaseUrl(request)+"/razas";
            return "redirect:"+getBaseUrlNginx(request)+"/razas";

        } catch (Exception e) {
            model.addAttribute("razaDTO", razaDTO);
            model.addAttribute("titulo", "Crear Raza");
            model.addAttribute("accion", "/razas/create");

            model.addAttribute("error", "Ocurrio un error al registrar la raza");
            return "razas/formulario";
        }
    }

    @GetMapping("/razas/update/{id}")
    public String buscarRaza(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        Optional<RazaDTO> razaOpt = razaService.obtenerRazaPorId(id);
        if(razaOpt.isEmpty()){
            redirectAttributes.addFlashAttribute("error", "La raza con id "+id+" no fue encontrada");
            // return "redirect:/razas";
            // return "redirect:http://localhost:8080/razas";
            // return "redirect:"+getBaseUrl(request)+"/razas";
            return "redirect:"+getBaseUrlNginx(request)+"/razas";
        }

        model.addAttribute("razaDTO", razaOpt.get());
        model.addAttribute("titulo", "Editar Raza");
        model.addAttribute("accion", "/razas/update/"+id);
        return "razas/formulario";
    }

    @PostMapping("/razas/update/{id}")
    public String editarRaza(@PathVariable("id") Long id, @ModelAttribute("razaDTO") RazaDTO razaDTO, 
        Model model, RedirectAttributes redirectAttributes, HttpServletRequest request){
        try {
            razaService.editarRaza(id, razaDTO);
            redirectAttributes.addFlashAttribute("ok", "Raza modificada correctamente");
            // return "redirect:/razas";
            // return "redirect:http://localhost:8080/razas";
            // return "redirect:"+getBaseUrl(request)+"/razas";
            return "redirect:"+getBaseUrlNginx(request)+"/razas";

        } catch (Exception e) {
            model.addAttribute("razaDTO", razaDTO);
            model.addAttribute("titulo", "Editar Raza");
            model.addAttribute("accion", "/razas/update/"+id);
            model.addAttribute("error", "Ocurrio un error al modificar la raza");
            return "razas/formulario";
        }
    }
    
    @GetMapping("/razas/delete/{id}")
    public String eliminarRaza(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        try {
            razaService.eliminarRazaPorId(id);
            redirectAttributes.addFlashAttribute("ok", "Raza eliminada correctamente");
        
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        // return "redirect:/razas";
        // return "redirect:http://localhost:8080/razas";
        // return "redirect:"+getBaseUrl(request)+"/razas";
        return "redirect:"+getBaseUrlNginx(request)+"/razas";
    }
}
