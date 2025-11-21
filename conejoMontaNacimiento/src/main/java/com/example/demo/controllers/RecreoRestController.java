package com.example.demo.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.controllers.dto.RecreoDTO;
import com.example.demo.services.IRecreoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/recreos")
public class RecreoRestController {
    
    @Autowired
    private IRecreoService recreoService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll(
        @RequestParam(defaultValue = "0") int pageNumber, 
        @RequestParam(defaultValue = "10") int pageSize){
        
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
    public ResponseEntity<RecreoDTO> saveRecreo(@RequestBody @Valid RecreoDTO recreoDTO){
        RecreoDTO saved = recreoService.saveRecreo(recreoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecreoDTO> updateRecreo(@PathVariable("id") Long id, @RequestBody @Valid RecreoDTO recreoDTO){
        RecreoDTO saved = recreoService.updateRecreo(id, recreoDTO);
        return ResponseEntity.ok(saved);
        // return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

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

