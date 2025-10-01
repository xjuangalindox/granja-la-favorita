package com.example.demo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.MontaModel;
import com.example.demo.models.NacimientoModel;

@Repository
public interface NacimientoRepository extends CrudRepository<NacimientoModel, Long> {

    boolean existsByMontaId(Long id);

    // Usado: "/montas/eliminar/{id}"
    Optional<NacimientoModel> findByMonta(MontaModel montaModel);

    boolean existsById(Long id);

    // @Query("SELECT n FROM NacimientoModel n WHERE EXISTS (SELECT e FROM EjemplarModel e WHERE e.nacimiento = n AND e.vendido = false)")
    // List<NacimientoModel> findNacimientosConEjemplaresDisponibles();

    // Todos los nacimientos
    Page<NacimientoModel> findAll(Pageable pageable);

    // Nacimientos sin ejemplares
    @Query("SELECT n FROM NacimientoModel n WHERE n.ejemplares IS EMPTY")
    Page<NacimientoModel> findNacimientosSinEjemplares(Pageable pageable);

    // Nacimientos con ejempalres disponibles
    @Query("SELECT DISTINCT n FROM NacimientoModel n JOIN n.ejemplares e WHERE e.vendido = false")
    Page<NacimientoModel> findNacimientosConEjemplaresDisponibles(Pageable pageable);

    // Nacimientos con todos sus ejemplares vendidos
    @Query("SELECT n FROM NacimientoModel n WHERE n.ejemplares IS NOT EMPTY AND NOT EXISTS " +
            "(SELECT e FROM EjemplarModel e WHERE e.nacimiento = n AND e.vendido = false)")
    Page<NacimientoModel> findNacimientosConTodosEjemplaresVendidos(Pageable pageable);
}
