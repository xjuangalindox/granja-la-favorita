package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.ConejoModel;
import com.example.demo.models.MontaModel;
import com.example.demo.models.enums.EstatusMonta;


@Repository
public interface MontaRepository extends CrudRepository<MontaModel, Long> {
	// Obtener los nacimiento cuyo nacimiento == null
	List<MontaModel> findByNacimientoIsNull();

	// Usado: "/conejos/eliminar/{id}"
	boolean existsByMacho(ConejoModel conejoModel);
	boolean existsByHembra(ConejoModel conejoModel);

	Page<MontaModel> findAll(Pageable pageable); // Obtener todas las montas con paginación
	Page<MontaModel> findByEstatus(Pageable pagebale, EstatusMonta estatus); // Obtener montas por estatus con paginación
}