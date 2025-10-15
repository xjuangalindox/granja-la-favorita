package com.example.demo.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Pageable;

import com.example.demo.models.ConejoModel;
import com.example.demo.models.MontaModel;
import com.example.demo.models.enums.EstatusMonta;

@DataJpaTest
public class MontaRepositoryTest {
    
    @Autowired
    private ConejoRepository conejoRepository;

    @Autowired
    private MontaRepository montaRepository;

    private ConejoModel semental, panda, peluchin, pelusa, rata, nube, castor, chocolata;
    private MontaModel sp, pp, rn;

    @BeforeEach
    void setup(){
        // MiniLop - Inactivos
        semental = new ConejoModel(null, null, null, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://cloudinary.com/semental.png", null, null, null, 1L);
        panda = new ConejoModel(null, null, null, "Panda", "Hembra", null, false, 
        "Abuelita, jubilada", "123abc", "https://cloudinary.com/panda.png", null, null, null, 1L);
        // Leones - Activos
        peluchin = new ConejoModel(null, null, null, "Peluchin", "Macho", null, true, 
        "Semental, nacido en granja", "123abc", "https://cloudinary.com/rocko.png", null, null, null, 2L);
        pelusa = new ConejoModel(null, null, null, "Pelusa", "Hembra", null, true, 
        "Hermana del semental, nacida en granja", "123abc", "https://cloudinary.com/trueno.png", null, null, null, 2L);
        // FuzzyLop - Activos
        rata = new ConejoModel(null, null, null, "Rata", "Macho", null, true, 
        "Traido de mexico", "123abc", "https://cloudinary.com/marino.png", null, null, null, 3L);
        nube = new ConejoModel(null, null, null, "Nube", "Hembra", null, true, 
        "Traida de jiutepec", "123abc", "https://cloudinary.com/mexicana.png", null, null, null, 3L);

        // Enanos - Activos / Registrados pero sin monta
        castor = new ConejoModel(null, null, null, "castor", "Macho", null, true, 
        "Unico enanito semental en la granja", "123abc", "https://cloudinary.com/Castor.png", null, null, null, 4L);
        chocolata = new ConejoModel(null, null, null, "chocolata", "Hembra", null, true, 
        "Enanita chocolata", "123abc", "https://cloudinary.com/Chocolata.png", null, null, null, 4L);

        // save conejos
        semental = conejoRepository.save(semental);
        panda = conejoRepository.save(panda);
        peluchin = conejoRepository.save(peluchin);
        pelusa = conejoRepository.save(pelusa);
        rata = conejoRepository.save(rata);
        nube = conejoRepository.save(nube);
        castor = conejoRepository.save(castor); // Sin monta
        chocolata = conejoRepository.save(chocolata); // Sin monta

        sp = new MontaModel(null, "Monta de MiniLop", LocalDate.of(2025, 8, 10), 3, EstatusMonta.PENDIENTE, panda, semental, null);
        pp = new MontaModel(null, "Monta de Leones", LocalDate.of(2025, 9, 20), 2, EstatusMonta.EFECTIVA, pelusa, peluchin, null);
        rn = new MontaModel(null, "Monta de FuzzyLop", LocalDate.now(), 3, EstatusMonta.PENDIENTE, nube, rata, null);

        // save montas
        sp = montaRepository.save(sp);
        pp = montaRepository.save(pp);
        rn = montaRepository.save(rn);
    }

    @Test
    void testExistsByMacho_True(){
        boolean existe = montaRepository.existsByMacho(semental);
        assertTrue(existe);
    }

    @Test
    void testExistsByMacho_False(){
        boolean existe = montaRepository.existsByMacho(castor);
        assertFalse(existe);
    }

    @Test
    void testExistsByHembra_True(){
        boolean existe = montaRepository.existsByHembra(panda);
        assertTrue(existe);
    }

    @Test
    void testExistsByHembra_False(){
        boolean existe = montaRepository.existsByHembra(chocolata);
        assertFalse(existe);
    }

    // No es necesaria la task 64, ya que actualmente existen 3 montas registradas en la BD h2.
    // Dos montas EstatusMonta.PENDIENTE y una EstatusMonta.EFECTIVA

    @Test
    void testFindAll(){
        Pageable page = PageRequest.of(0, 5);
        Page<MontaModel> pageConejos = montaRepository.findAll(page);

        assertNotNull(pageConejos);
        assertEquals(3, pageConejos.getTotalElements());
        assertEquals(0, pageConejos.getNumber()); // page number
        assertEquals(1, pageConejos.getTotalPages());

        List<MontaModel> lista = pageConejos.getContent();
        assertNotNull(lista);
        assertEquals(3, lista.size());
        assertEquals("Monta de MiniLop", lista.get(0).getNota());
        assertEquals("Monta de Leones", lista.get(1).getNota());
        assertEquals("Monta de FuzzyLop", lista.get(2).getNota());

        assertEquals(EstatusMonta.PENDIENTE, lista.get(0).getEstatus());
        assertEquals(EstatusMonta.EFECTIVA, lista.get(1).getEstatus());
        assertEquals(EstatusMonta.PENDIENTE, lista.get(2).getEstatus());
    }

    @Test
    void testFindByEstatus_PENDIENTE(){
        Pageable page = PageRequest.of(0, 5);
        Page<MontaModel> pageMontas = montaRepository.findByEstatus(page, EstatusMonta.PENDIENTE);

        assertNotNull(pageMontas);
        assertEquals(2, pageMontas.getNumberOfElements());
        assertEquals(0, pageMontas.getNumber());
        assertEquals(2, pageMontas.getTotalElements());
        assertEquals(1, pageMontas.getTotalPages());

        List<MontaModel> lista = pageMontas.getContent();
        assertNotNull(lista);
        assertEquals(2, lista.size());
        assertEquals("Monta de MiniLop", lista.get(0).getNota());
        assertEquals(EstatusMonta.PENDIENTE, lista.get(0).getEstatus());
        assertEquals("Monta de FuzzyLop", lista.get(1).getNota());
        assertEquals(EstatusMonta.PENDIENTE, lista.get(1).getEstatus());
    }

    @Test
    void testFindByEstatus_EFECTIVA(){
        Pageable page = PageRequest.of(0, 5);
        Page<MontaModel> pageMontas = montaRepository.findByEstatus(page, EstatusMonta.EFECTIVA);

        assertNotNull(pageMontas);
        assertEquals(1, pageMontas.getNumberOfElements());
        assertEquals(0, pageMontas.getNumber());
        assertEquals(1, pageMontas.getTotalElements());
        assertEquals(1, pageMontas.getTotalPages());

        List<MontaModel> lista = pageMontas.getContent();
        assertNotNull(lista);
        assertEquals(1, lista.size());
        assertEquals("Monta de Leones", lista.get(0).getNota());
        assertEquals(EstatusMonta.EFECTIVA, lista.get(0).getEstatus());
    }
}
