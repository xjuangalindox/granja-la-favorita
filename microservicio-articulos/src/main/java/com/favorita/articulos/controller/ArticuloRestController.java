package com.favorita.articulos.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.favorita.articulos.controller.dto.ArticuloDTO;
import com.favorita.articulos.services.IArticuloService;

@RestController
@RequestMapping("/api/articulos")
public class ArticuloRestController {
    
    @Autowired
    private IArticuloService articuloService;

    @GetMapping
    public ResponseEntity<List<ArticuloDTO>> obtenerArticulosStockTrue(){
        List<ArticuloDTO> listaArticulos = articuloService.obtenerArticulosStockTrue();
        if(listaArticulos.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(listaArticulos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticuloDTO> obtenerArticuloPorId(@PathVariable("id") Long id){
        Optional<ArticuloDTO> articuloOpt = articuloService.obtenerArticuloPorId(id);
        if(articuloOpt.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(articuloOpt.get());
    }
}
