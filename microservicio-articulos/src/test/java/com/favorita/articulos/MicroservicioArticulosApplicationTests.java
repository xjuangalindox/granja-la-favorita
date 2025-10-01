package com.favorita.articulos;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.favorita.articulos.clients.ArticuloVentaClient;

// @Disabled("Ignorado mientras se resuelve el error de ./mvnw clean test jacoco:report")
@SpringBootTest
class MicroservicioArticulosApplicationTests {

	// @MockBean
	// private ArticuloVentaClient articuloVentaClient;

	@Test
	void contextLoads() {
		// El feign cliente queda mockeado
	}

}
