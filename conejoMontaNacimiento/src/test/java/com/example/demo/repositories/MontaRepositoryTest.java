package com.example.demo.repositories;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
}
