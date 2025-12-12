package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@EnableAsync
public class ConejoMontaNacimientoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConejoMontaNacimientoApplication.class, args);
	}

}

/*
 * git rm -r --cached target (eliminar carpeta de GitHub)
 * git rm --cached .env (eliminar archivo de GitHub)
 * git commit -m ""
 * git push
 */

/*
 * ./mvnw clean install -DskipTests
 */

/*
 * git reset --hard HEAD (volver al ultimo commit) 
 */

/*
 * git remote add origin rutaRepositorio.git (vincular local con online)
 */

/*
 * git branch (mostrar rama actual)
 * git branch -a (mostrar ramas locales)
 * git branch -r (mostrar ramas remotas)
 */

/*
 * git switch modulo-conejo (cambiar de rama)
 * git switch -c modulo-conejo (crear y cambiar de rama)
 */

/*
 * git init
 * git add .
 * git commit -m "Mi mensaje de commit"
 */

/*
 * git remote add origin https://github.com/usuario/nombre-repo.git
 * git push -u origin main (subir rama por primera vez)
 * git push (subir cambios)
 */

/*
 * git switch main (cambiar a rama principal)
 * git pull (traer ultimos cambios del remoto)
 * 
 * git merge modulo-conejo (unir rama modulo-conejo a main)
 * git push (subir main completo)
 * 
 * git branch -d modulo-final-conejo (eliminar rama local)
 * git push origin --delete modulo-final-conejo (eliminar rama remota)
 */