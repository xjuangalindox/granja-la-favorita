pipeline {
    agent any // Ejecuta el pipeline en cualquier agente (nodo Jenkins disponible)

    stages {

        // Clonar repo granja-la-favorita
        stage('Checkout main repo') {
            steps {
                checkout scm
            }
        }

        // Clonar repo credentials en carpeta credentials
        stage('Checkout credentials repo') {
            steps {
                dir('credentials') {
                    git url: 'https://github.com/xjuangalindox/credentials.git',
                        branch: 'master',
                        credentialsId: 'fa04f023-0db3-44fa-941c-0efdae20b429'
                }
            }
        }

        // Levantar servicio db-granja
        stage('Levantar MySQL'){
            steps{
                sh 'docker-compose --env-file credentials/.env.local up -d db-granja'
                sh 'docker ps'
            }
        }
        
        // Levantar servicio grafana
        stage('Levantar Grafana'){
            steps{
                sh '''
                    docker-compose --env-file credentials/.env.local up -d grafana
                    docker ps
                '''
            }
        }

        // stage('Docker test') {
        //     steps {
        //         sh 'docker version'
        //         sh 'docker ps'
        //     }
        // }

        // stage('Checkout repos') { // Etapa: clonar los repositorios
        //     steps {
        //         checkout scm // Clonar repo que contiene Jenkinsfile

        //         // Segundo repo (credentials)
        //         dir('credentials') { // Descargar repo en directorio en el servidor Jenkins
        //             git url: 'https://github.com/xjuangalindox/credentials.git',
        //                 branch: 'master',
        //                 credentialsId: 'fa04f023-0db3-44fa-941c-0efdae20b429' // credentials configurados en UI de Jenkins
        //         }
        //     }
        // }

        // stage('Docker test') {
        //     steps {
        //         sh 'docker version'
        //         sh 'docker ps'
        //     }
        // } 

        // stage('Levantar MySQL') { // Etapa: levantar el contenedor MySQL
        //     steps {
        //         sh '''
        //             docker-compose --env-file credentials/.env.local up -d db-granja
        //         '''
        //     }
        // }

        // stage('Levantar Grafana') { // Etapa: levantar el contenedor MySQL
        //     steps {
        //         sh 'docker-compose --env-file credentials/.env.local up -d grafana'
        //     }
        // }
    }
}