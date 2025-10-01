package com.favorita.razas.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.favorita.razas.model.RazaModel;

@DataJpaTest
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Usar BD real para testing
public class IRazaRepositoryTests {
    
    @Autowired
    private IRazaRepository razaRepository;

    @DisplayName("Tests para guardar una raza")
    @Test
    void testGuardarRaza(){
        // Given - dado o condicion previa o configuración
        RazaModel razaModel = new RazaModel(null, "Cabeza de León");

        // When - accion
        RazaModel guardado = razaRepository.save(razaModel);

        // Then - Verificar la salida
        assertNotNull(guardado);
        assertNotNull(guardado.getId()); // validar que JPA asigno el ID
        assertEquals("Cabeza de León", guardado.getNombre());

        // Extra: verificar que efectivamente está en la BD de pruebas
        Optional<RazaModel> encontrado = razaRepository.findById(guardado.getId());
        assertTrue(encontrado.isPresent());
        assertEquals("Cabeza de León", encontrado.get().getNombre());
    }

    @Test
    void testListarRazas(){
        // given
        RazaModel raza1 = new RazaModel(null, "MiniLop");
        RazaModel raza2 = new RazaModel(null, "Cabeza de León");
        RazaModel raza3 = new RazaModel(null, "MiniRex");

        razaRepository.saveAll(List.of(raza1, raza2, raza3));

        // when
        List<RazaModel> razas = (List<RazaModel>) razaRepository.findAll();

        // then
        assertEquals(3, razas.size());
        assertTrue(razas.stream().anyMatch(raza -> raza.getNombre().equals("MiniLop")));
        assertTrue(razas.stream().anyMatch(raza -> raza.getNombre().equals("Cabeza de León")));
        assertTrue(razas.stream().anyMatch(raza -> raza.getNombre().equals("MiniRex")));
    }

    @Test
    void testObtenerRazaPorId(){
        // given
        RazaModel razaModel = new RazaModel(null, "Rex");
        razaModel = razaRepository.save(razaModel);

        // when
        Optional<RazaModel> encontrado = razaRepository.findById(razaModel.getId());

        // then
        assertTrue(encontrado.isPresent());
        assertEquals("Rex", encontrado.get().getNombre());
    }

    @Test
    void testActualizarRaza(){
        // given
        RazaModel razaModel = new RazaModel(null, "MiniLop");
        razaModel = razaRepository.save(razaModel);

        razaModel.setNombre("Enano Holandés");

        // when
        razaModel = razaRepository.save(razaModel);

        // then
        assertEquals("Enano Holandés", razaModel.getNombre());
    }

    @Test
    void testEliminarRazaPorId(){
        // given
        RazaModel razaModel = new RazaModel(null, "Cabeza de Leon");
        razaModel = razaRepository.save(razaModel);

        // when
        razaRepository.deleteById(razaModel.getId());
        Optional<RazaModel> encontrado = razaRepository.findById(razaModel.getId());

        // then
        assertTrue(encontrado.isEmpty());
    }

}
