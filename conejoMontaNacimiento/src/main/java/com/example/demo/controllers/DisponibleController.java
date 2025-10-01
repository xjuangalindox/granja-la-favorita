package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.demo.clients.ArticuloClient;
import com.example.demo.clients.RazaClient;
import com.example.demo.controllers.dto.EjemplarDTO;
import com.example.demo.controllers.dto.NacimientoDTO;
import com.example.demo.controllers.dto.RazaDTO;
import com.example.demo.services.IEjemplarService;
import com.example.demo.services.INacimientoService;

@Controller
@RequestMapping("/disponible")
public class DisponibleController {
    
    @Autowired
    private IEjemplarService ejemplarService;

    @Autowired
    private INacimientoService nacimientoService;

    @Autowired
    private RazaClient razaClient;

    @Autowired
    private ArticuloClient articuloClient;

    @GetMapping("/articulos")
    public String getDisponibleArticulos(@RequestHeader(value = "User-Authenticated", required = false) String authHeader, Model model){
        boolean isAuthenticated = "true".equalsIgnoreCase(authHeader);
        model.addAttribute("isAuthenticated", isAuthenticated);
        
        model.addAttribute("listaArticulos", articuloClient.obtenerArticulos());
        return "disponibles/articulos";
    }

    @GetMapping("/ejemplares")
    public String getDisponibleNacimientos(@RequestHeader(value = "User-Authenticated", required = false) String authHeader, Model model) {
        boolean isAuthenticated = "true".equalsIgnoreCase(authHeader);
        model.addAttribute("isAuthenticated", isAuthenticated);

        // Obtener ejemplares disponibles (vendido = false)
        List<EjemplarDTO> ejemplaresDisponibles = ejemplarService.ejemplaresDisponibles(false);
        
        // Obtener el idNacimiento de cada ejemplar (sin duplicados)
        Set<Long> idsNacimientos = ejemplaresDisponibles.stream()
            .map(ejemplar -> ejemplar.getNacimiento()) // Obtener nacimiento
            .map(nacimiento -> nacimiento.getId()) // Obtener idNacimiento
            .collect(Collectors.toSet()); // eliminar duplicados automaticamente

        // Obtener nacimientos
        List<NacimientoDTO> listaNacimientos = new ArrayList<>();
        idsNacimientos.forEach(idNacimiento -> {
            Optional<NacimientoDTO> nacimientoOpt = nacimientoService.obtenerNacimientoById(idNacimiento);
            if(nacimientoOpt.isPresent()){
                listaNacimientos.add(nacimientoOpt.get());
            }
        });

        // Asignar raza a los padres del nacimiento
        listaNacimientos.forEach(nacimiento -> {
            Long idRazaMacho = nacimiento.getMonta().getMacho().getRaza().getId();
            Long idRazaHembra = nacimiento.getMonta().getHembra().getRaza().getId();

            nacimiento.getMonta().getMacho().setRaza(obtenerRaza(idRazaMacho));
            nacimiento.getMonta().getHembra().setRaza(obtenerRaza(idRazaHembra));
        });

        // Filtrar nacimientos con ejemplares disponibles (vendido = false)
        model.addAttribute("listaNacimientos", filtrarNacimientosConEjemplaresDisponibles(listaNacimientos));
        return "disponibles/ejemplares";
    }
    

    // *****************************************************************************************************************************
    // *****************************************************************************************************************************
    
    // Consumir microservicio-razas para obetener el nombre de la raza
    private RazaDTO obtenerRaza(Long id){
        return razaClient.obtenerRazaPorId(id);
    }

    // Filtar nacimientos con ejempalres disponibles (vendido = false)
    private List<NacimientoDTO> filtrarNacimientosConEjemplaresDisponibles(List<NacimientoDTO> listaNacimientos){
        if(listaNacimientos == null || listaNacimientos.isEmpty()){
            return listaNacimientos;
        }

        return listaNacimientos.stream()
            .map(nac -> {
                List<EjemplarDTO> disponibles = nac.getEjemplares().stream()
                    .filter(eje -> !eje.isVendido()) // vendido = false
                    .collect(Collectors.toList());
                    
                nac.setEjemplares(disponibles);
                return nac;
            })
            .filter(nac -> nac.getEjemplares() != null && !nac.getEjemplares().isEmpty())
            .collect(Collectors.toList());
    }
}
