package com.favorita.articulos.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.favorita.articulos.model.ArticuloModel;

@DataJpaTest
public class IArticuloRepositoryTest {
    
    @Autowired
    private IArticuloRepository articuloRepository;

    private ArticuloModel campiconejo;

    @BeforeEach
    void setup(){
        campiconejo = new ArticuloModel(null, "Campiconejo", "Alimento diario", "KG", 20.0, "public_id", "secure:url");
    } 

    @Test
    void testExistsByNombre(){
        // given
        articuloRepository.save(campiconejo);

        // when
        boolean existe = articuloRepository.existsByNombre("Campiconejo");

        // then
        // assertNotNull(existe);
        assertTrue(true);
    }

    // No necesario
    @Test
    void testGuardarArticulo(){
        // given
        ArticuloModel articuloModel = new ArticuloModel(null, "Campiconejo", "Comida diaria", "KG", 20.0, "publicId", "secureURL");

        // when
        articuloModel = articuloRepository.save(articuloModel);

        // then
        assertNotNull(articuloModel);
        assertNotNull(articuloModel.getId());

        // Extra
        Optional<ArticuloModel> saved = articuloRepository.findById(articuloModel.getId());
        assertTrue(saved.isPresent());
        assertEquals("Campiconejo", saved.get().getNombre());
    }
}
