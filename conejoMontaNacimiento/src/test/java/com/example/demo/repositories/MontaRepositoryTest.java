package com.example.demo.repositories;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.demo.models.ConejoModel;
import com.example.demo.models.MontaModel;
import com.example.demo.models.enums.EstatusMonta;

@DataJpaTest
public class MontaRepositoryTest {
    
    private ConejoModel semental, panda, peluchin, pelusa, rata, nube;
    private MontaModel sp, pp, rn;

    @BeforeEach
    void setup(){
        // MiniLop - Inactivos
        semental = new ConejoModel(1L, null, null, "Semental", "Macho", null, false, 
        "Primer semental de la granja", "123abc", "https://cloudinary.com/semental.png", null, null, null, 1L);
        panda = new ConejoModel(2L, null, null, "Panda", "Hembra", null, false, 
        "Abuelita, jubilada", "123abc", "https://cloudinary.com/panda.png", null, null, null, 1L);
        // Leones - Activos
        peluchin = new ConejoModel(3L, null, null, "Peluchin", "Macho", null, true, 
        "Semental, nacido en granja", "123abc", "https://cloudinary.com/rocko.png", null, null, null, 2L);
        pelusa = new ConejoModel(4L, null, null, "Pelusa", "Hembra", null, true, 
        "Hermana del semental, nacida en granja", "123abc", "https://cloudinary.com/trueno.png", null, null, null, 2L);
        // FuzzyLop - Activos
        rata = new ConejoModel(5L, null, null, "Rata", "Macho", null, true, 
        "Traido de mexico", "123abc", "https://cloudinary.com/marino.png", null, null, null, 3L);
        nube = new ConejoModel(6L, null, null, "Nube", "Hembra", null, true, 
        "Traida de jiutepec", "123abc", "https://cloudinary.com/mexicana.png", null, null, null, 3L);

        sp = new MontaModel(1L, "Monta de MiniLop", LocalDate.of(2025, 8, 10), 3, EstatusMonta.PENDIENTE, panda, semental, null);
        pp = new MontaModel(2L, "Monta de Leones", LocalDate.of(2025, 9, 50), 2, EstatusMonta.EFECTIVA, pelusa, peluchin, null);
        rn = new MontaModel(3L, "Monta de FuzzyLop", LocalDate.now(), 3, EstatusMonta.PENDIENTE, nube, rata, null);
    }
}
