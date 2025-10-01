package com.favorita.razas.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.favorita.razas.controller.dto.RazaDTO;
import com.favorita.razas.service.IRazaService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/razas")
public class RazaRestController {

    @Autowired
    private IRazaService razaService;

    @GetMapping
    public ResponseEntity<List<RazaDTO>> obtenerRazas(){
        List<RazaDTO> listaRazas = razaService.obtenerRazas();
        if(listaRazas.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(listaRazas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RazaDTO> obtenerRazaPorId(@PathVariable("id") Long id){
        Optional<RazaDTO> razaOpt = razaService.obtenerRazaPorId(id);
        if(razaOpt.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(razaOpt.get());
    }

    @PostMapping
    public ResponseEntity<RazaDTO> guardarRaza(@RequestBody RazaDTO razaDTO){
        RazaDTO result = razaService.guardarRaza(razaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RazaDTO> editarRaza(@RequestBody RazaDTO razaDTO, @PathVariable("id") Long id){
        Optional<RazaDTO> razaOpt = razaService.obtenerRazaPorId(id);
        if(razaOpt.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        RazaDTO result = razaService.editarRaza(id, razaDTO);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRazaPorId(@PathVariable("id") Long id){
        Optional<RazaDTO> razaOpt = razaService.obtenerRazaPorId(id);
        if(razaOpt.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        razaService.eliminarRazaPorId(id);
        return ResponseEntity.noContent().build(); // 204 sin cuerpo
    }

    @GetMapping("/headers")
    public ResponseEntity<?> verHost(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Host", request.getHeader("Host")); // c26824c3c415:8082
        headers.put("X-Forwarded-Host", request.getHeader("X-Forwarded-Host")); // localhost
        headers.put("X-Forwarded-Port", request.getHeader("X-Forwarded-Port")); // 8080
        headers.put("X-Forwarded-Proto", request.getHeader("X-Forwarded-Proto")); // http
        
        return ResponseEntity.ok(headers);    
    }

    // @GetMapping("/redirect")
    // public ResponseEntity<?> verHostLocal(HttpServletRequest request) {
    //     Map<String, String> headers = new HashMap<>();
    //     headers.put("Scheme", request.getScheme());
    //     headers.put("Sever Name", request.getServerName());
    //     headers.put("Server Port", String.valueOf(request.getServerPort()));
        
    //     return ResponseEntity.ok(headers);    
    // }
}
