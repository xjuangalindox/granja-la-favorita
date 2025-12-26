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
            when {
                branch 'main'
            }
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
        success{
            echo 'Pipeline ejecutado correctamente ✅'
            emailext(
                from: 'Jenkins <xjuangalindox@gmail.com>',
                to: 'xjuangalindox@gmail.com',                
                subject: "✅ Pipeline OK - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
                    El pipeline terminó correctamente.

                    Job: ${env.JOB_NAME}
                    Build: ${env.BUILD_NUMBER}
                    Rama: ${env.BRANCH_NAME}
                    URL: ${env.BUILD_URL}
                """
            )
        }

        failure{
            echo 'Pipeline falló ❌'
            emailext(
                from: 'Jenkins <xjuangalindox@gmail.com>',
                to: 'xjuangalindox@gmail.com',
                subject: "❌ Pipeline FALLÓ - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
                    Ocurrió un error en el pipeline.

                    Job: ${env.JOB_NAME}
                    Build: ${env.BUILD_NUMBER}
                    Rama: ${env.BRANCH_NAME}
                    URL: ${env.BUILD_URL}

                    Revisa los logs en Jenkins.
                """
            )            
        }

        always{
            echo 'Fin del pipeline 🧹'
        }
    }
}