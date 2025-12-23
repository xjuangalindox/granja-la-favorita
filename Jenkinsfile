pipeline {                  // Define que este job es un pipeline declarativo
    agent any               // Ejecuta el pipeline en cualquier agente (nodo Jenkins disponible)

    stages {                // Bloque que contiene todas las etapas del pipeline

        stage('Checkout') { // Etapa: clonar el repositorio
            steps {         // Acciones que se ejecutan en esta etapa
                checkout scm // Jenkins clona el repo definido en "Pipeline from SCM"
            }
        }

        stage('Build') {
            steps {
                sh 'echo "Build inicial OK"'
            }
        }

        stage('Levantar MySQL') { // Etapa: levantar el contenedor MySQL
            steps {
                sh 'docker-compose --env-file credentials/.env.local up -d db-granja' // Levanta SOLO el servicio db-granja en segundo plano
            }
        }
    }
}