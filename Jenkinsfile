// ================== MAILS ==================
// ===========================================
def sendSuccessMail() {
    echo '********** ✅📧 Enviando correo de DEPLOY EXITOSO **********'

    mail(
        from: 'Jenkins <xjuangalindox@gmail.com>',
        to: 'xjuangalindox@gmail.com',
        subject: "🚀 Nueva versión disponible - Granja La Favorita",
        body: 
"""
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
        body: 
"""
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

// ================== FUNCIONES ==================
// ===============================================
def tagAsStable(images, appVersion, stableTag) {
    echo "********** 🏷️ Marcando imágenes como versión estable: ${images} **********"

    images.each { image ->
        sh "docker tag ${image}:${appVersion} ${image}:${appVersion}-${stableTag}"
    }
}

def deleteOldImages(images, appVersion, stableTag){
    echo '********** 🧹 Eliminando imágenes antiguas (solo queda image:appVersion-stableTag) **********'

    // Para cada imagen, lista sus tags → quita stable (ejecutandose) → borra el resto → no rompas el pipeline
    images.each{ image ->
        def stableImage = "${image}:${appVersion}-${stableTag}"

        sh """
            docker images ${image} --format "{{.Repository}}:{{.Tag}}" \
            | grep -vF "${stableImage}" \
            | xargs -r docker rmi || true
        """
    }
}

def rollback(services, stableTag) {
    echo '********** 🔄 Rollback a última versión estable **********'

    // 1️⃣ Bajar todos los contenedores
    sh 'docker-compose --env-file credentials/.env.local down --remove-orphans || true'

    // 2️⃣ Por cada servicio, levantar su última imagen estable
    services.each { service ->

        def imageName = "granja/${service}"

        // Buscar la última versión estable para esta imagen
        def stableImage = sh(
            script: """
                docker images --format '{{.Repository}}:{{.Tag}}' \
                | grep '^${imageName}:' \
                | grep '${stableTag}\$' \
                | sort -V \
                | tail -1
            """,
            returnStdout: true
        ).trim()
    
        if (stableImage) {  
            def tagOnly = stableImage.split(':')[1]
            sh "TAG_VERSION=${tagOnly} docker-compose --env-file credentials/.env.local up -d ${service}"

        } else {
            echo "⚠️ No se encontró imagen estable para ${service}, se omite"
        }
    }
}

def showLastLogs(service) {
    echo "********** 🔍 Mostrando últimos 50 logs del servicio: ${service} **********"
    
    sh "docker-compose logs --tail=50 ${service}"
}

// ================== PIPELINE ===================
// ===============================================
pipeline {
    agent any // Ejecuta el pipeline en cualquier agente (nodo Jenkins disponible)

    environment {
        PROFILE = "${env.PROFILE}" // parameterized
        DEPLOY_BRANCH = "${env.DEPLOY_BRANCH}" // parameterized

        APP_VERSION = "${env.BUILD_NUMBER}"
        STABLE_TAG = "stable"
    }

    options {
        timestamps() // Agregar la hora a cada línea del log
        disableConcurrentBuilds() // Evitar builds simultáneos
        timeout(time: 30, unit: 'MINUTES') // Si el pipeline dura más de 30 minutos -> aborted / failure
    }

    stages {
        stage('********** 🐞 DEBUG DEV **********') {
            steps {
                echo "PROFILE=${env.PROFILE}"
                echo "DEPLOY_BRANCH=${env.DEPLOY_BRANCH}"

                echo "BRANCH_NAME=${env.BRANCH_NAME}"
                
                echo "APP_VERSION=${env.APP_VERSION}"
                echo "STABLE_TAG=${env.STABLE_TAG}"
            }
        }

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

        stage('********** 📦 Bajar contenedores actuales **********') {
            // when {branch 'master'}

            steps{
                sh 'docker-compose --env-file credentials/.env.local down --remove-orphans || true'
                sh 'docker ps'
            }
        }

        stage('********** 🗄️ Levantar MySQL **********'){
            // when {branch 'master'}

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
            // when {branch 'master'}

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
            // when {branch 'master'}

            steps{
                script{
                    try{
                        sh "TAG_VERSION=${env.APP_VERSION} docker-compose --env-file credentials/.env.local up -d --build config-server"
                        sh 'docker ps'

                    }catch(Exception e){
                        showLastLogs('config-server')
                        throw e
                    }
                }
            }
        }

        stage('********** 📡 Levantar Eureka-Server **********'){
            // when {branch 'master'}

            steps{
                script{
                    try{
                        sh "TAG_VERSION=${env.APP_VERSION} docker-compose --env-file credentials/.env.local up -d --build eureka-server"
                        sh 'docker ps'
                        
                    }catch(Exception e){
                        showLastLogs('eureka-server')
                        throw e
                    }
                }
            }
        }

        stage('********** 🧠 Levantar Microservicio-Principal **********'){
            // when {branch 'master'}

            steps{
                script{
                    try{
                        sh "TAG_VERSION=${env.APP_VERSION} docker-compose --env-file credentials/.env.local up -d --build microservicio-principal"
                        sh 'docker ps'
                        
                    }catch(Exception e){
                        showLastLogs('microservicio-principal')
                        throw e
                    }
                }
            }
        }

        stage('********** 🐇 Levantar Microservicio-Razas **********'){
            // when {branch 'master'}

            steps{
                script{
                    try{
                        sh "TAG_VERSION=${env.APP_VERSION} docker-compose --env-file credentials/.env.local up -d --build microservicio-razas"
                        sh 'docker ps'
                        
                    }catch(Exception e){
                        showLastLogs('microservicio-razas')
                        throw e
                    }
                }
            }
        }

        stage('********** 📦 Levantar Microservicio-Articulos **********'){
            // when {branch 'master'}

            steps{
                script{
                    try{
                        sh "TAG_VERSION=${env.APP_VERSION} docker-compose --env-file credentials/.env.local up -d --build microservicio-articulos"
                        sh 'docker ps'
                        
                    }catch(Exception e){
                        showLastLogs('microservicio-articulos')
                        throw e
                    }
                }
            }
        }  

        stage('********** 🚪 Levantar Gateway-Service **********'){
            // when {branch 'master'}

            steps{
                script{
                    try{
                        sh "TAG_VERSION=${env.APP_VERSION} docker-compose --env-file credentials/.env.local up -d --build gateway-service"
                        sh 'docker ps'
                        
                    }catch(Exception e){
                        showLastLogs('gateway-service')
                        throw e
                    }
                }
            }
        }     

        stage('🔎 Verificar nginx config') {
            steps {
                sh 'ls -l nginx/' // Mostrar archivos del directorio (con detalles)
                sh 'cat nginx/nginx.local.conf' // Abrir archivo
            }
        }

        stage('********** 🔀 Levantar Nginx **********'){
            // when {branch 'master'}

            steps{
                script{
                    try{
                        sh "TAG_VERSION=${env.APP_VERSION} docker-compose --env-file credentials/.env.local up -d nginx"
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
                    'granja/microservicio-razas', 'granja/microservicio-articulos', 'granja/gateway-service', 'granja/nginx'
                    ]
                    
                // 1️⃣ Marcar como stable
                tagAsStable(images, env.APP_VERSION, env.STABLE_TAG)

                // 2️⃣ Limpiar imágenes viejas
                deleteOldImages(images, env.APP_VERSION, env.STABLE_TAG)
            }

            // sendSuccessMail() // Enviar success mail
        }

        failure {
            echo '********** 💥 POST: FAILURE **********'

            // 1️⃣ Bajar todos los contenedores
            // sh 'docker-compose --env-file credentials/.env.local down --remove-orphans || true'

            script{
                def services = [
                    'config-server', 'eureka-server', 'microservicio-principal',
                    'microservicio-razas', 'microservicio-articulos', 'gateway-service', 'nginx'
                ]
                
                // 1️⃣ Levantar versiones estables
                rollback(services, env.STABLE_TAG)
            }
            
            // sendFailureMail() // Enviar failure mail
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