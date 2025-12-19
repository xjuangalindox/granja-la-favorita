package com.favorita.articulos.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.favorita.articulos.controller.dto.ArticuloDTO;
import com.favorita.articulos.services.ArticuloServiceImpl;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ArticuloController {
    
    @Autowired
    private ArticuloServiceImpl articuloService;

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

    @GetMapping("/articulos")
    public String obtenerArticulos(Model model){
        List<ArticuloDTO> listaArticulos = articuloService.obtenerArticulos();
        model.addAttribute("listaArticulos", listaArticulos);

        return "articulos/lista";
    }

    @GetMapping("/articulos/create")
    public String crearArticulo(Model model) {
        model.addAttribute("articuloDTO", new ArticuloDTO());
        model.addAttribute("titulo", "Crear Articulo");
        model.addAttribute("accion", "/articulos/create");
        return "articulos/formulario";
    }

    @PostMapping("/articulos/create")
    public String guardarArticulo(@ModelAttribute("articuloDTO") ArticuloDTO articuloDTO, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request){
        try {
            articuloService.guardarArticulo(articuloDTO);
            redirectAttributes.addFlashAttribute("ok", "Articulo registrado correctamente");
            // return "redirect:/articulos";
            // return "redirect:"+getBaseUrl(request)+"/articulos";
            return "redirect:"+getBaseUrlNginx(request)+"/articulos";

        } catch (Exception e) {
            model.addAttribute("articuloDTO", articuloDTO);
            model.addAttribute("titulo", "Crear Articulo");
            model.addAttribute("accion", "/articulos/create");

            // model.addAttribute("error", "Ocurrio un error al registrar el articulo");
            model.addAttribute("error", e.getMessage());
            return "articulos/formulario";
        }
    }

    @GetMapping("/articulos/update/{id}")
    public String buscarArticulo(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        Optional<ArticuloDTO> articuloOpt = articuloService.obtenerArticuloPorId(id);
        if(articuloOpt.isEmpty()){
            redirectAttributes.addFlashAttribute("error", "El articulo con id "+id+" no fue encontrado");
            // return "redirect:/articulos";
            // return "redirect:"+getBaseUrl(request)+"/articulos";
            return "redirect:"+getBaseUrlNginx(request)+"/articulos";
        }

        model.addAttribute("articuloDTO", articuloOpt.get());
        model.addAttribute("titulo", "Editar Articulo");
        model.addAttribute("accion", "/articulos/update/"+id);
        return "articulos/formulario";
    }

    @PostMapping("/articulos/update/{id}")
    public String editarArticulo(@PathVariable("id") Long id, @ModelAttribute("articuloDTO") ArticuloDTO articuloDTO, 
        Model model, RedirectAttributes redirectAttributes, HttpServletRequest request){
        try {
            articuloService.editarArticulo(id, articuloDTO);
            redirectAttributes.addFlashAttribute("ok", "Articulo modificado correctamente");
            // return "redirect:/articulos";
            // return "redirect:"+getBaseUrl(request)+"/articulos";
            return "redirect:"+getBaseUrlNginx(request)+"/articulos";

        } catch (Exception e) {
            model.addAttribute("articuloDTO", articuloDTO);
            model.addAttribute("titulo", "Editar Articulo");
            model.addAttribute("accion", "/articulos/update/"+id);
            model.addAttribute("error", e.getMessage());
            return "articulos/formulario";
        }
    }
    
    @GetMapping("/articulos/delete/{id}")
    public String eliminarArticulo(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        try {
            articuloService.eliminarArticuloPorId(id);
            redirectAttributes.addFlashAttribute("ok", "Articulo eliminado correctamente.");
        
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        // return "redirect:/articulos";
        // return "redirect:"+getBaseUrl(request)+"/articulos";
        return "redirect:"+getBaseUrlNginx(request)+"/articulos";
    }
}
