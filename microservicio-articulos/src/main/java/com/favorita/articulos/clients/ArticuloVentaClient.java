package com.favorita.articulos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "conejomontanacimiento")
public interface ArticuloVentaClient {
    
    // Usado: "/articulos/eliminar/{id}"
    @GetMapping("/api/articulos/venta/existe-por-articulo/{id}")
    boolean existsByArticuloId(@PathVariable("id") Long id);

}
