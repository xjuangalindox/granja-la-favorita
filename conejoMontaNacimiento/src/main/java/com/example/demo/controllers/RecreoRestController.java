package com.example.demo.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.RecreoModel;
import com.cloudinary.http5.api.Response;
import com.example.demo.controllers.dto.RecreoDTO;
import com.example.demo.services.IRecreoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/recreos")
public class RecreoRestController {
    private static final int PAGE_SIZE = 10;

    @Autowired
    private IRecreoService recreoService;

    @GetMapping("/conejo/{conejoId}")
    public ResponseEntity<Map<String, Object>> findByConejoId(
        @PathVariable("conejoId") Long conejoId,
        @RequestParam(defaultValue = "0") int pageNumber
        ){

        Page<RecreoDTO> pageRecreos = recreoService.findByConejoId(conejoId, pageNumber, PAGE_SIZE);

        Map<String, Object> response = Map.of(
            "conejoId", conejoId,
            "pageNumber", pageRecreos.getNumber(),
            "pageSize", pageRecreos.getSize(),
            "recreos", pageRecreos.getContent(),
            "totalPages", pageRecreos.getTotalPages(),
            "totalElements", pageRecreos.getTotalElements()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll(@RequestParam(defaultValue = "0") int pageNumber){
        int pageSize = 10;
        Page<RecreoDTO> pageRecreos = recreoService.findAll(pageNumber, pageSize);

        if(pageRecreos.getContent().isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(Map.of(
            "pageNumber", pageRecreos.getNumber(),
            "pageSize", pageRecreos.getSize(),
            "recreos", pageRecreos.getContent(),
            "totalPages", pageRecreos.getTotalPages(),
            "totalElements", pageRecreos.getTotalElements()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecreoDTO> findById(@PathVariable("id") Long id){
        try {
            RecreoDTO recreoDTO = recreoService.findById(id);    
            return ResponseEntity.ok(recreoDTO);
        
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<RecreoDTO> saveFromHTML(@RequestBody @Valid RecreoDTO recreoDTO){
        RecreoDTO saved = recreoService.saveRecreo(recreoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecreoDTO> updateRecreo(@PathVariable("id") Long id, @RequestBody @Valid RecreoDTO recreoDTO){
        RecreoDTO saved = recreoService.updateRecreo(id, recreoDTO);
        
        return ResponseEntity.status(HttpStatus.OK).body(saved);
    }

    // Grafana Endpoints
    @PostMapping("/grafana")
    public ResponseEntity<RecreoDTO> saveFromGrafana(@RequestBody RecreoDTO recreoDTO){
        recreoDTO.setInicioRecreo(LocalDateTime.now(ZoneId.of("America/Mexico_City")));
        
        RecreoDTO saved = recreoService.saveRecreo(recreoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/grafana/{recreo_id}")
    public ResponseEntity<RecreoDTO> updateFromGrafana(@PathVariable("recreo_id") Long id){
        RecreoDTO recreoDTO = recreoService.findById(id);
        recreoDTO.setFinRecreo(LocalDateTime.now(ZoneId.of("America/Mexico_City")));

        recreoDTO = recreoService.updateRecreo(id, recreoDTO);
        return ResponseEntity.status(HttpStatus.OK).body(recreoDTO);
    }

    // Extra (por si @PutMapping("/grafana/{recreo_id}") no funciona)
    @PutMapping("/grafana")
    public ResponseEntity<RecreoDTO> updateFromGrafana(@RequestBody RecreoDTO recreoDTO){
        RecreoDTO recreo = recreoService.findById(recreoDTO.getId());
        recreo.setFinRecreo(LocalDateTime.now(ZoneId.of("America/Mexico_City")));

        recreo = recreoService.updateRecreo(recreoDTO.getId(), recreo);
        return ResponseEntity.status(HttpStatus.OK).body(recreo);
    }
    // Fin Grafana Endpoints

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id){
        try {
            recreoService.deleteById(id);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

