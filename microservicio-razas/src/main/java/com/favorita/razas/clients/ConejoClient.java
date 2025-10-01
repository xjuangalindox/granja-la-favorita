package com.favorita.razas.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "conejomontanacimiento")
public interface ConejoClient {

    // Usado "razas/eliminar/{id}"
    @GetMapping("/api/conejos/existe-por-raza/{id}")
    boolean existsByRazaId(@PathVariable("id") Long id);
}
