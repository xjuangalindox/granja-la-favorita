pipeline {
    agent any // Ejecuta el pipeline en cualquier agente (nodo Jenkins disponible)

    // 👉 Agrega la hora a cada línea del log del pipeline.
    options {
        timestamps()
    }

    stages {

        // Clonar repo granja-la-favorita
        stage('Checkout main repo') {
            steps {
                echo 'Clonando repositorio principal...'
                checkout scm
            }
        }

        // Clonar repo credentials en carpeta credentials
        stage('Checkout credentials repo') {
            steps {
                echo 'Clonando repo de credenciales...'
                dir('credentials') {
                    git url: 'https://github.com/xjuangalindox/credentials.git',
                        branch: 'master',
                        credentialsId: 'fa04f023-0db3-44fa-941c-0efdae20b429'
                }
            }
        }

        // Levantar servicio db-granja
        stage('Levantar MySQL'){
            when {branch 'master'}

            steps{
                script{
                    try{
                        echo 'Levantando MySQL...'
                        sh 'docker-compose --env-file credentials/.env.local up -d db-granja'
                        sh 'docker ps'

                    }catch(Exception e){
                        echo 'Error levantando MySQL'
                        // currentBuild.result = 'FAILURE' // No necesario
                        throw e
                    }
                } 
            }
        }
        
        // Levantar servicio grafana
        stage('Levantar Grafana'){
            when {branch 'master'}
            
            steps{
                script{
                    try{
                        echo 'Levantando Grafana...'
                        sh '''
                            docker-compose --env-file credentials/.env.local up -d grafana
                            docker ps
                        '''

                    }catch(Exception e){
                        echo 'Error al levantar Grafana'
                        // currentBuild.result = 'FAILURE' // No necesario
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

        // SUCCESS    
        success{
            echo 'Pipeline ejecutado correctamente ✅'

            mail(
                from: 'Jenkins <xjuangalindox@gmail.com>',
                to: 'xjuangalindox@gmail.com',                
                subject: "🚀 Nueva versión disponible - Granja La Favorita",
                body: """
                ¡Despliegue exitoso!

                La nueva versión de *Granja La Favorita* ya se encuentra disponible.
                
                🌐 Accede aquí:
                https://granjalafavorita.com

                Detalles del despliegue:
                - Job: ${env.JOB_NAME}
                - Build: ${env.BUILD_NUMBER}
                - Rama: ${env.BRANCH_NAME ?: 'N/A'}
                - Fecha: ${new Date()}
                - URL del build: ${env.BUILD_URL}

                Puedes comenzar a usar la nueva versión con normalidad.

                Saludos,
                Jenkins 🤖
                """
            )
        }

        // FAILURE
        failure{
            echo 'Pipeline falló ❌'

            mail(
                from: 'Jenkins <xjuangalindox@gmail.com>',
                to: 'xjuangalindox@gmail.com',
                subject: "❌ Error en despliegue - Granja La Favorita",
                body: """
                ¡Despliegue fallido!

                La nueva versión de *Granja La Favorita* NO está disponible debido a un error durante el proceso.

                Detalles del error:
                - Job: ${env.JOB_NAME}
                - Build: ${env.BUILD_NUMBER}
                - Rama: ${env.BRANCH_NAME ?: 'N/A'}
                - Fecha: ${new Date()}
                - URL del build: ${env.BUILD_URL}

                Se requiere revisión del pipeline y los logs para corregir el problema.

                Jenkins 🤖
                """
            )           
        }
    }
}

// mail bcc: '', body: '', cc: '', from: '', replyTo: '', subject: '', to: ''