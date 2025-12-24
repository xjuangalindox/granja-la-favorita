pipeline {                  // Define que este job es un pipeline declarativo
    agent any               // Ejecuta el pipeline en cualquier agente (nodo Jenkins disponible)

    stages {                // Bloque que contiene todas las etapas del pipeline

        // stage('Docker test') {
        //     steps {
        //         sh 'docker version'
        //         sh 'docker ps'
        //     }
        // }  

        stage('Checkout repos') { // Etapa: clonar los repositorios
            steps {
                checkout scm // Clonar repo que contiene Jenkinsfile

                // Segundo repo (credentials)
                dir('credentials') { // Descargar repo en directorio en el servidor Jenkins
                    git url: 'https://github.com/xjuangalindox/credentials.git',
                        branch: 'master',
                        credentialsId: 'fa04f023-0db3-44fa-941c-0efdae20b429' // credentials configurados en UI de Jenkins
                }
            }
        }

        // stage('Debug workspace') {
        //     steps {
        //         sh 'ls -la'
        //         sh 'ls -la credentials || echo "credentials NO existe"'
        //     }
        // }

        // stage('Build') {
        //     steps {
        //         sh 'echo "Build inicial OK"'
        //     }
        // }

        stage('Levantar MySQL') { // Etapa: levantar el contenedor MySQL
            steps {
                sh '''
                    docker-compose --env-file credentials/.env.local up -d db-granja
                '''
            }
        }

        stage('Levantar Grafana') { // Etapa: levantar el contenedor MySQL
            steps {
                sh 'docker-compose --env-file credentials/.env.local up -d grafana'
            }
        }
    }
}