pipeline {
    agent any // Ejecuta el pipeline en cualquier agente (nodo Jenkins disponible)

    options {
        timestamps() // Agregar la hora a cada línea del log
        disableConcurrentBuilds() // Evitar builds simultáneos
        timeout(time: 30, unit: 'MINUTES') // Si el pipeline dura más de 30 minutos -> aborted / failure
    }

    stages {
        stage('********** Checkout main repo **********') {
            steps {
                echo 'Clonando repositorio principal...'
                checkout scm
            }
        }

        stage('********** Checkout credentials repo **********') {
            steps {
                echo 'Clonando repo de credenciales...'
                dir('credentials') {
                    git url: 'https://github.com/xjuangalindox/credentials.git',
                        branch: 'master',
                        credentialsId: 'fa04f023-0db3-44fa-941c-0efdae20b429'
                }
            }
        }

        stage('********** Levantar MySQL **********'){
            when {branch 'master'}

            steps{
                script{
                    try{
                        echo 'Levantando MySQL...'
                        sh '''
                            docker-compose --env-file credentials/.env.local up -d db-granja
                            docker ps
                        '''

                    }catch(Exception e){
                        echo 'Error levantando MySQL'
                        // currentBuild.result = 'FAILURE' // No necesario
                        throw e
                    }
                } 
            }
        }
        
        stage('********** Levantar Grafana **********'){
            when {branch 'master'}

            steps{
                script{
                    try{
                        echo 'Levantando Grafana...'
                        sh 'docker-compose --env-file credentials/.env.local up -d grafana'
                        sh 'docker ps'

                    }catch(Exception e){
                        echo 'Error al levantar Grafana'
                        throw e
                    }
                }
            }
        }

        stage('********** Levantar Config-Server **********'){
            when{branch: 'master'}

            steps{
                script{
                    try{
                        echo 'Levantando config-server...'
                        sh 'docker-compose --env-file credentials/.env.local up -d --build config-server'
                        sh 'docker ps'

                    }catch(Exception e){
                        echo 'Error al levantar config-server'
                        throw e
                    }
                }
            }
        }

        stage('********** Levantar Eureka-Server **********'){
            when{branch: 'master'}

            steps{
                script{
                    try{
                        echo 'Levantando eureka-server...'
                        sh 'docker-compose --env-file credentials/.env.local up -d --build eureka-server'
                        sh 'docker ps'

                    }catch(Exception e){
                        echo 'Error al levantar eureka-server'
                        throw e
                    }
                }
            }
        }

        stage('********** Levantar Microservicio-Principal **********'){
            when{branch: 'master'}

            steps{
                script{
                    try{
                        echo 'Levantando microservicio-principal...'
                        sh 'docker-compose --env-file credentials/.env.local up -d --build microservicio-principal'
                        sh 'docker ps'

                    }catch(Exception e){
                        echo 'Error al levantar microservicio-principal'
                        throw e
                    }
                }
            }
        }

        stage('********** Levantar Microservicio-Razas **********'){
            when{branch: 'master'}

            steps{
                script{
                    try{
                        echo 'Levantando microservicio-razas...'
                        sh 'docker-compose --env-file credentials/.env.local up -d --build microservicio-razas'
                        sh 'docker ps'

                    }catch(Exception e){
                        echo 'Error al levantar microservicio-razas'
                        throw e
                    }
                }
            }
        }

        stage('********** Levantar Microservicio-Articulos **********'){
            when{branch: 'master'}

            steps{
                script{
                    try{
                        echo 'Levantando microservicio-articulos...'
                        sh 'docker-compose --env-file credentials/.env.local up -d --build microservicio-articulos'
                        sh 'docker ps'

                    }catch(Exception e){
                        echo 'Error al levantar microservicio-articulos'
                        throw e
                    }
                }
            }
        }  

        stage('********** Levantar Gateway-Service **********'){
            when{branch: 'master'}

            steps{
                script{
                    try{
                        echo 'Levantando gateway-service...'
                        sh 'docker-compose --env-file credentials/.env.local up -d --build gateway-service'
                        sh 'docker ps'

                    }catch(Exception e){
                        echo 'Error al levantar gateway-service'
                        throw e
                    }
                }
            }
        }     

        stage('********** Levantar Nginx **********'){
            when{branch: 'master'}

            steps{
                script{
                    try{
                        echo 'Levantando nginx...'
                        sh 'docker-compose --env-file credentials/.env.local up -d nginx'
                        sh 'docker ps'

                    }catch(Exception e){
                        echo 'Error al levantar nginx'
                        throw e
                    }
                }
            }
        }                               
    }

    post {
        always{
            echo 'Fin del pipeline 🧹'
        }

        aborted {
            echo 'Pipeline abortado ⛔'
            echo 'El pipeline fue cancelado por el usuario o excedió el tiempo máximo permitido (30 minutos).'
        }

        // success{
        //     echo 'Pipeline ejecutado correctamente ✅'

        //     mail(
        //         from: 'Jenkins <xjuangalindox@gmail.com>',
        //         to: 'xjuangalindox@gmail.com',                
        //         subject: "🚀 Nueva versión disponible - Granja La Favorita",
        //         body: """
        //         ¡Despliegue exitoso!

        //         La nueva versión de *Granja La Favorita* ya se encuentra disponible.
                
        //         🌐 Accede aquí:
        //         https://granjalafavorita.com

        //         Detalles del despliegue:
        //         - Job: ${env.JOB_NAME}
        //         - Build: ${env.BUILD_NUMBER}
        //         - Rama: ${env.BRANCH_NAME ?: 'N/A'}
        //         - Fecha: ${new Date()}
        //         - URL del build: ${env.BUILD_URL}

        //         Puedes comenzar a usar la nueva versión con normalidad.

        //         Saludos,
        //         Jenkins 🤖
        //         """
        //     )
        // }

        // failure{
        //     echo 'Pipeline falló ❌'

        //     mail(
        //         from: 'Jenkins <xjuangalindox@gmail.com>',
        //         to: 'xjuangalindox@gmail.com',
        //         subject: "❌ Error en despliegue - Granja La Favorita",
        //         body: """
        //         ¡Despliegue fallido!

        //         La nueva versión de *Granja La Favorita* NO está disponible debido a un error durante el proceso.

        //         Detalles del error:
        //         - Job: ${env.JOB_NAME}
        //         - Build: ${env.BUILD_NUMBER}
        //         - Rama: ${env.BRANCH_NAME ?: 'N/A'}
        //         - Fecha: ${new Date()}
        //         - URL del build: ${env.BUILD_URL}

        //         Se requiere revisión del pipeline y los logs para corregir el problema.

        //         Jenkins 🤖
        //         """
        //     )           
        // }
    }
}

// mail bcc: '', body: '', cc: '', from: '', replyTo: '', subject: '', to: ''