package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.session.SessionInformation;
// import org.springframework.security.core.session.SessionRegistry;
// import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.clients.ArticuloClient;
import com.example.demo.controllers.dto.ArticuloDTO;
import com.example.demo.controllers.dto.EjemplarDTO;
import com.example.demo.controllers.dto.ItemHomeDTO;
import com.example.demo.services.IEjemplarService;
import com.netflix.discovery.converters.Auto;

@Controller
// @RestController
@RequestMapping("/")
// @RequestMapping("/home")
public class HomeController {
    
    // @Autowired
    // private SessionRegistry sessionRegistry;

    // @GetMapping
    // public String home(@RequestHeader(value = "User-Authenticated", required = false) String authHeader, Model model) {
    //     boolean isAuthenticated = "true".equalsIgnoreCase(authHeader);
        
    //     model.addAttribute("isAuthenticated", isAuthenticated);
    //     return "index";
    // }

    @Autowired
    private IEjemplarService ejemplarService;

    @Autowired
    private ArticuloClient articuloClient;

    @GetMapping
    public String home(Model model) {
        List<EjemplarDTO> ejemplaresDisponibles = ejemplarService.ejemplaresDisponibles(false);
        List<ArticuloDTO> articulosDisponibles = articuloClient.obtenerArticulos();

        List<ItemHomeDTO> items = new ArrayList<>();
        Random random = new Random(); // Random para obtener el index de la foto del ejemplar

        if(!CollectionUtils.isEmpty(ejemplaresDisponibles)){
            ejemplaresDisponibles.forEach(ejemplar -> {
                ItemHomeDTO item = new ItemHomeDTO();
                item.setTipo("ejemplar");
                item.setSexo(ejemplar.getSexo());
                
                // Elegir imagen aleatorio del ejemplar
                if(!CollectionUtils.isEmpty(ejemplar.getFotos())){
                    int index = random.nextInt(ejemplar.getFotos().size());
                    String secureUrl = ejemplar.getFotos().get(index).getSecureUrl();
                    item.setSecureUrl(secureUrl);
                }else{
                    item.setSecureUrl("");
                }

                items.add(item);
            });
        }

        if(!CollectionUtils.isEmpty(articulosDisponibles)){
            articulosDisponibles.forEach(articulo -> {
                        ItemHomeDTO item = new ItemHomeDTO();
                        item.setTipo("articulo");
                        item.setSecureUrl(articulo.getSecureUrl());
                        item.setNombre(articulo.getNombre());

                        items.add(item);
                    });
        }

        // Mezclar los items de ejemplares y articulos
        Collections.shuffle(items);

        model.addAttribute("itemsHome", items);
        return "index";
    }
    
    // @GetMapping
    // public ResponseEntity<Map<String, String>> home(@RequestHeader Map<String, String> headers) {
    //     return ResponseEntity.ok(headers);
    // }

    // @GetMapping("/session")
    // public ResponseEntity<?> getDetailtsSession() {
    //     String sessionId = "";
    //     User userObject = null;

    //     List<Object> sessions = sessionRegistry.getAllPrincipals();
    //     for(Object session : sessions){
    //         if(session instanceof User){
    //             userObject = (User) session;
    //         }

    //         List<SessionInformation> sessionInformations = sessionRegistry.getAllSessions(session, false);
    //         for(SessionInformation sessionInformation : sessionInformations){
    //             sessionId = sessionInformation.getSessionId();
    //         }
    //     }

    //     Map<String, Object> response = new HashMap<>();
    //     response.put("sessionId", sessionId);
    //     response.put("userObject", userObject);

    //     return ResponseEntity.ok(response);
    // }
    
    
}
