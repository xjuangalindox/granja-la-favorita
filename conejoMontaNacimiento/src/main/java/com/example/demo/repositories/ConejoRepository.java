package com.example.demo.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.models.ConejoModel;

import java.util.List;
import java.util.Optional;

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

	// Nombres de conejos para el filtrado en el modulo conejos
	// @Query("SELECT c.nombre FROM ConejoModel c ORDER BY c.nombre ASC")
	// List<String> findAllNombresOrderByNombreAsc();

	// Todos los nombres de los conejos
	@Query("SELECT c.nombre FROM ConejoModel c WHERE c.sexo LIKE %:sexo% ORDER BY c.nombre ASC")
	List<String> findNombresBySexoOrderByNombreAsc(@Param("sexo") String sexo);
	// Todos los conejos
	Page<ConejoModel> findBySexoLike(String sexo, Pageable pageable);
	// Solo un conejo
	Page<ConejoModel> findByNombre(String nombre, Pageable pageable);

	// Filtros en modulo conejos
	// Page<ConejoModel> findBySexoContainingAndNombreContainingOrderByNombreAsc(String sexo, String nombre, Pageable pageable);
	// Page<ConejoModel> findBySexoContainingAndNombreContainingOrderByNombreDesc(String sexo, String nombre, Pageable pageable);
}
