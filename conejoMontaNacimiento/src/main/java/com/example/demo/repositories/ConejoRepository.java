package com.example.demo.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.ConejoModel;

import java.util.List;

@Repository
public interface ConejoRepository extends CrudRepository<ConejoModel, Long> {

	// Retornar todos los conejos que contengan "nombre"
	//List<ConejoModel> findByNombreContainingIgnoreCase(String nombre);

	// Retornar todos los conejos que contengan "nombre" y "macho" o "hembra"
	//List<ConejoModel> findByNombreContainingIgnoreCaseAndSexoIgnoreCase(String nombre, String sexo);

	// Obtener todos los conejos por sexo (macho o hembra)
	List<ConejoModel> findBySexoIgnoreCase(String sexo);

	//Obtener todos los conejos activos por sexo (macho o hembra)
	List<ConejoModel> findBySexoIgnoreCaseAndActivoTrue(String sexo);

	boolean existsByNombre(String nombre);
	
	boolean existsById(Long id);

	// Usado "razas/eliminar/{id}"
	boolean existsByRazaId(Long id);	


	Page<ConejoModel> findAll(Pageable pageable);
	Page<ConejoModel> findBySexo(Pageable pageable, String sexo);
}
