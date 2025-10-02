package com.example.demo.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.example.demo.models.ConejoModel;

@DataJpaTest
public class ConejoRepositoryTest {

    @Autowired
    private ConejoRepository conejoRepository; 

    @BeforeEach
    void setup(){
        // Inactivos
        ConejoModel semental = new ConejoModel(null, null, null, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://cloudinary.com/semental.png", null, null, null, 1L);
        ConejoModel panda = new ConejoModel(null, null, null, "Panda", "Hembra", null, false, 
        "Abuelita, jubilada", "123abc", "https://cloudinary.com/panda.png", null, null, null, 1L);

        // Activos
        ConejoModel rocko = new ConejoModel(null, null, null, "Rocko", "Macho", null, true, 
        "Tiene malito el ojo", "123abc", "https://cloudinary.com/rocko.png", null, null, null, 1L);
        ConejoModel trueno = new ConejoModel(null, null, null, "Trueno", "Macho", null, true, 
        "Tiene lloroso el ojo", "123abc", "https://cloudinary.com/trueno.png", null, null, null, 1L);
        ConejoModel marino = new ConejoModel(null, null, null, "Marino", "Macho", null, true, 
        "Tiene gripa", "123abc", "https://cloudinary.com/marino.png", null, null, null, 1L);

        ConejoModel mexicana = new ConejoModel(null, null, null, "Mexicana", "Hembra", null, true, 
        "Perfecto estado", "123abc", "https://cloudinary.com/mexicana.png", null, null, null, 1L);
        ConejoModel enojona = new ConejoModel(null, null, null, "Enojona", "Hembra", null, true, 
        "Perfecto estado", "123abc", "https://cloudinary.com/enojona.png", null, null, null, 1L);
        
        List<ConejoModel> lista = Arrays.asList(semental, panda, rocko, trueno, marino, mexicana, enojona);
        conejoRepository.saveAll(lista);
    }

    @Test
    void testFindBySexoIgnoreCase(){
        // given
        // when
        List<ConejoModel> machos = conejoRepository.findBySexoIgnoreCase("Macho");
        
        // then
        assertNotNull(machos);
        assertEquals(4, machos.size());
        assertEquals("Semental", machos.get(0).getNombre());
        assertEquals("Rocko", machos.get(1).getNombre());
        assertEquals("Trueno", machos.get(2).getNombre());
        assertEquals("Marino", machos.get(3).getNombre());
    }

    @Test
    void testFindBySexoIgnoreCaseAndActivoTrue(){
        // given
        // when
        List<ConejoModel> activos = conejoRepository.findBySexoIgnoreCaseAndActivoTrue("Macho");

        // then
        assertNotNull(activos);
        assertEquals(3, activos.size());
        assertEquals("Rocko", activos.get(0).getNombre());
        assertEquals("Trueno", activos.get(1).getNombre());
        assertEquals("Marino", activos.get(2).getNombre());
    }

    @Test
    void testExistsByNombre(){

        boolean existe = conejoRepository.existsByNombre("Marino");

        assertNotNull(existe);
        assertTrue(existe); // existe conejo
    }

    @Test
    void testExistsById(){
        // given
        ConejoModel princesa = conejoRepository.save(new ConejoModel(null, null, null, "Princesa", "Hembra", null, true, 
        "Cariñosa", "123abc", "https://cloudinary.com/princesa.png", null, null, null, 1L));

        // when
        boolean existe = conejoRepository.existsById(princesa.getId());

        // then
        // assertNotNull(existe);
        assertTrue(existe);

        Optional<ConejoModel> opt = conejoRepository.findById(princesa.getId());

        assertTrue(opt.isPresent());
        assertEquals("Cariñosa", opt.get().getNota());
    }

    @Test
    void testExistsByRazaId(){
        boolean existe = conejoRepository.existsByRazaId(1L);

        // ¿Existe algun conejo con razaId = 1?
        assertNotNull(existe);
        assertTrue(existe);
    }

    @Test
    void testFindAll_pageable(){
        // given
        Pageable pagina = PageRequest.of(0, 5, Sort.by("nombre").ascending());
        
        // when
        Page<ConejoModel> pageConejos = conejoRepository.findAll(pagina);

        // then
        assertNotNull(pageConejos);
        assertEquals(2, pageConejos.getTotalPages()); // total de hojas en la bd
        assertEquals(5, pageConejos.getNumberOfElements()); // total de elementos en la hoja actual
        assertEquals(7, pageConejos.getTotalElements()); // total de elemento en la bd
        
        List<ConejoModel> lista = pageConejos.getContent();

        assertNotNull(lista);
        assertEquals(5, lista.size());
        assertEquals("Enojona", lista.get(0).getNombre());
        assertEquals("Marino", lista.get(1).getNombre());
        assertEquals("Mexicana", lista.get(2).getNombre());
        assertEquals("Panda", lista.get(3).getNombre());
        assertEquals("Rocko", lista.get(4).getNombre());
    }

    @Test
    void testFindBySexo_Pageable(){
        // given
        Pageable pagina = PageRequest.of(0, 5, Sort.by("nombre").descending());

        // when
        Page<ConejoModel> pageConejos = conejoRepository.findBySexo(pagina, "Hembra");

        // then
        assertNotNull(pageConejos);
        assertEquals(3, pageConejos.getNumberOfElements()); // Total de elementos de la pagina actual
        assertEquals(5, pageConejos.getSize()); // Cantidad de elementos de la pagina
        assertEquals(1, pageConejos.getTotalPages()); // Total de paginas en la bd
        assertEquals(3, pageConejos.getTotalElements()); // Total de hembras en la BD

        List<ConejoModel> lista = pageConejos.getContent();

        assertNotNull(lista);
        assertEquals("Panda", lista.get(0).getNombre());
        assertEquals("Mexicana", lista.get(1).getNombre());
        assertEquals("Enojona", lista.get(2).getNombre());
    }    
}
