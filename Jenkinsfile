    // showLastLogs('config-server')
    // showLastLogs('eureka-server')
    // showLastLogs('microservicio-principal')
    // showLastLogs('microservicio-razas')
    // showLastLogs('microservicio-articulos')
    // showLastLogs('gateway-service')

// ================== FUNCIONES ==================
def tagAsStable(images, appVersion, stableTag) {
    echo "********** 🏷️ Marcando imágenes como versión estable: ${images} **********"

    images.each { image ->
        sh "docker tag ${image}:${appVersion} ${image}:${stableTag}"
    }
}

def deleteOldImages(){

}

def rollback() {
    echo '********** 🔄 Rollback a última versión estable **********'

    sh 'docker-compose down || true' // aunque falle, continúa
    sh 'APP_VERSION=stable docker-compose up -d' // usa imágenes ya existentes (las stable)
}

def showLastLogs(service) {
    echo "********** 🔍 Mostrando últimos 50 logs del servicio: ${service} **********"
    
    sh "docker-compose logs --tail=50 ${service}"
}

def sendSuccessMail() {
    echo '********** ✅📧 Enviando correo de DEPLOY EXITOSO **********'

    mail(
        from: 'Jenkins <xjuangalindox@gmail.com>',
        to: 'xjuangalindox@gmail.com',
        subject: "🚀 Nueva versión disponible - Granja La Favorita",
        body: """
        ¡Despliegue exitoso! 🎉

    La nueva versión de Granja La Favorita ya se encuentra disponible.

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

def sendFailureMail() {
    echo '********** ❌📧 Enviando correo de DEPLOY FALLIDO **********'

    mail(
        from: 'Jenkins <xjuangalindox@gmail.com>',
        to: 'xjuangalindox@gmail.com',
        subject: "❌ Error en despliegue - Granja La Favorita",
        body: """
        ¡Despliegue fallido! ❌

    La nueva versión de Granja La Favorita no está disponible debido a un error durante el proceso.

🌐 Pipeline:
${env.BUILD_URL}

Detalles del error:
- Job: ${env.JOB_NAME}
- Build: ${env.BUILD_NUMBER}
- Rama: ${env.BRANCH_NAME ?: 'N/A'}
- Fecha: ${new Date()}
- URL del build: ${env.BUILD_URL}

Se requiere revisión del pipeline y los logs para corregir el problema.

Saludos,
Jenkins 🤖
"""
    )
}

// ================== PIPELINE ===================
pipeline {
    agent any // Ejecuta el pipeline en cualquier agente (nodo Jenkins disponible)

    environment {
        APP_VERSION = "${env.BUILD_NUMBER}" // Cada deploy tiene su versión
        STABLE_TAG = "stable"
    }

    options {
        timestamps() // Agregar la hora a cada línea del log
        disableConcurrentBuilds() // Evitar builds simultáneos
        timeout(time: 30, unit: 'MINUTES') // Si el pipeline dura más de 30 minutos -> aborted / failure
    }

    stages {
        stage('********** 📥 Checkout main repo **********') {
            steps {
                checkout scm
            }
        }

        stage('********** 📥 Checkout credentials repo **********') {
            steps {
                dir('credentials') {
                    git url: 'https://github.com/xjuangalindox/credentials.git',
                        branch: 'master',
                        credentialsId: 'fa04f023-0db3-44fa-941c-0efdae20b429'
                }
            }
        }

        stage('********** 🗄️ Levantar MySQL **********'){
            when {branch 'master'}

            steps{
                script{
                    try{
                        sh 'docker-compose --env-file credentials/.env.local up -d db-granja'
                        sh 'docker ps'

                    }catch(Exception e){
                        showLastLogs('db-granja')
                        throw e
                    }
                } 
            }
        }
        
        stage('********** 📊 Levantar Grafana **********'){
            when {branch 'master'}

            steps{
                script{
                    try{
                        sh 'docker-compose --env-file credentials/.env.local up -d grafana'
                        sh 'docker ps'

                    }catch(Exception e){
                        showLastLogs('grafana')
                        throw e
                    }
                }
            }
        }

        stage('********** ⚙️ Levantar Config-Server **********'){
            when {branch 'master'}

            steps{
                script{
                    try{
                        withEnv(["APP_VERSION=${env.BUILD_NUMBER}"]) {
                            sh 'docker-compose --env-file credentials/.env.local up -d --build config-server'
                            sh 'docker ps'
                        }

                    }catch(Exception e){
                        showLastLogs('config-server')
                        throw e
                    }
                }
            }
        }

        stage('********** 📡 Levantar Eureka-Server **********'){
            when {branch 'master'}

            steps{
                script{
                    try{
                        withEnv(["APP_VERSION=${env.BUILD_NUMBER}"]) {
                            sh 'docker-compose --env-file credentials/.env.local up -d --build eureka-server'
                            sh 'docker ps'
                        }

                    }catch(Exception e){
                        showLastLogs('eureka-server')
                        throw e
                    }
                }
            }
        }

        stage('********** 🧠 Levantar Microservicio-Principal **********'){
            when {branch 'master'}

            steps{
                script{
                    try{
                        withEnv(["APP_VERSION=${env.BUILD_NUMBER}"]) {
                            sh 'docker-compose --env-file credentials/.env.local up -d --build microservicio-principal'
                            sh 'docker ps'
                        }

                    }catch(Exception e){
                        showLastLogs('microservicio-principal')
                        throw e
                    }
                }
            }
        }

        stage('********** 🐇 Levantar Microservicio-Razas **********'){
            when {branch 'master'}

            steps{
                script{
                    try{
                        withEnv(["APP_VERSION=${env.BUILD_NUMBER}"]) {
                            sh 'docker-compose --env-file credentials/.env.local up -d --build microservicio-razas'
                            sh 'docker ps'
                        }

                    }catch(Exception e){
                        showLastLogs('microservicio-razas')
                        throw e
                    }
                }
            }
        }

        stage('********** 📦 Levantar Microservicio-Articulos **********'){
            when {branch 'master'}

            steps{
                script{
                    try{
                        withEnv(["APP_VERSION=${env.BUILD_NUMBER}"]) {
                            sh 'docker-compose --env-file credentials/.env.local up -d --build microservicio-articulos'
                            sh 'docker ps'
                        }

                    }catch(Exception e){
                        showLastLogs('microservicio-articulos')
                        throw e
                    }
                }
            }
        }  

        stage('********** 🚪 Levantar Gateway-Service **********'){
            when {branch 'master'}

            steps{
                script{
                    try{
                        withEnv(["APP_VERSION=${env.BUILD_NUMBER}"]) {
                            sh 'docker-compose --env-file credentials/.env.local up -d --build gateway-service'
                            sh 'docker ps'
                        }

                    }catch(Exception e){
                        showLastLogs('gateway-service')
                        throw e
                    }
                }
            }
        }     

        stage('********** 🔀 Levantar Nginx **********'){
            when {branch 'master'}

            steps{
                script{
                    try{
                        sh 'docker-compose --env-file credentials/.env.local up -d nginx'
                        sh 'docker ps'

                    }catch(Exception e){
                        showLastLogs('nginx')
                        throw e
                    }
                }
            }
        }                               
    }

    post {
        always{
            echo '********** 🧹 POST: ALWAYS **********'
        }

        aborted {
            echo '********** ⛔ POST: ABORTED **********'
            echo 'El pipeline fue cancelado por el usuario o excedió el tiempo máximo permitido (30 minutos).'
        }

        success {
            echo '********** ✅ POST: SUCCESS **********'
            script {
                def images = [
                    'granja/config-server', 'granja/eureka-server', 'granja/microservicio-principal', 
                    'granja/microservicio-razas', 'granja/microservicio-articulos','granja/gateway-service'
                    ]
                tagAsStable(images, env.APP_VERSION, env.STABLE_TAG)
            }
            sendSuccessMail() // Enviar success mail
        }

        failure {
            echo '********** 💥 POST: FAILURE **********'
            rollback()
            sendFailureMail() // Enviar failure mail
        }
    }
}

// ---------------------------------------------------------------------------
// mail bcc: '', body: '', cc: '', from: '', replyTo: '', subject: '', to: ''
// ---------------------------------------------------------------------------
// currentBuild.result = 'FAILURE'
// ---------------------------------------------------------------------------
// mimeType: 'text/html',
// body: """
// <html>
// <body style="font-family: Arial, sans-serif;">
//     <h2 style="color:#2ecc71;">✅ Despliegue exitoso</h2>

//     <p>La nueva versión de <b>Granja La Favorita</b> fue desplegada correctamente.</p>

//     <ul>
//         <li><b>Job:</b> ${env.JOB_NAME}</li>
//         <li><b>Build:</b> #${env.BUILD_NUMBER}</li>
//         <li><b>Rama:</b> ${env.BRANCH_NAME ?: 'N/A'}</li>
//         <li><b>Fecha:</b> ${new Date()}</li>
//     </ul>

//     <p style="margin-top:20px;">
//         <a href="${env.BUILD_URL}"
//            style="
//                 background:#2ecc71;
//                 color:white;
//                 padding:12px 20px;
//                 text-decoration:none;
//                 border-radius:6px;
//                 font-weight:bold;
//            ">
//            🚀 Ver pipeline
//         </a>
//     </p>

//     <p style="margin-top:30px;">Jenkins 🤖</p>
// </body>
// </html>
// """
// ---------------------------------------------------------------------------