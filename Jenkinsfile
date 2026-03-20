// =================================================================================================================================
// FUNCIONES
// =================================================================================================================================
def BASE_SERVICES = ['db-granja', 'grafana']
def BASE_IMAGES = [
    'granja/config-server', 'granja/eureka-server', 'granja/microservicio-principal', 
    'granja/microservicio-razas', 'granja/microservicio-articulos', 'granja/gateway-service', 'granja/nginx'
    ]

// =================================================================================================================================
// FUNCIONES
// =================================================================================================================================

def shutdownContainers(profile){
    echo "********** 🛑 Bajando todos los contenedores, perfil: ${profile} **********"

    sh "docker-compose --env-file credentials/.env.${profile} down --remove-orphans || true"
    sh 'docker ps'
}

def showLastLogs(service) {
    echo "********** 🔍 Mostrando últimos 50 logs del servicio: ${service} **********"
    
    sh "docker-compose logs --tail=50 ${service}"
}

def tagAsStable(images, appVersion, stableTag) {
    echo "********** 🏷️ Marcando imágenes como versión estable: ${images} **********"

    images.each { image ->
        sh "docker tag ${image}:${appVersion} ${image}:${appVersion}-${stableTag}"
    }
}

def startBaseServices(services){
    echo '********** 🧱 Start Base Services **********'

    services.each{ service ->
        try{
            sh "docker-compose --env-file credentials/.env.${env.ENV} up -d ${service} || true"

        }catch(Exception e){
            showLastLogs(service)
            throw e
        }
    }

    sh 'docker ps'
}

def removeUnstableImages(images, stableTag){
    echo '********** 🧹 Remove Unstable Images **********'
    
    images.each{ image -> // granja/config-server
        sh """
            docker images ${image} --format '{{.Repository}}:{{.Tag}}' \
            | grep -v '${stableTag}\$' \
            | xargs -r docker rmi || true
        """
    }
}

def startLatestStableImages(images, stableTag){
    echo '********** 🚀 Start Latest Stable Images **********'

    images.each{ image -> // granja/config-server
        def serviceName = image.split('/')[1] // config-server

        def stableImage = sh(
            script: 
                """
                docker images ${image} --format '{{.Repository}}:{{.Tag}}' \
                | grep '${stableTag}\$' \
                | sort -V \
                | tail -1
                """,
                returnStdout: true
            ).trim()

        if(stableImage){
            def tag = stableImage.split(':')[1] // 10-stable
            sh """
                SPRING_PROFILES_ACTIVE=${env.ENV} \
                TAG_VERSION=${tag} \
                docker-compose --env-file credentials/.env.${env.ENV} up -d ${serviceName}
            """
        }else{
            echo "⚠️ No se encontró imagen estable para ${serviceName}, se omite"
        }
    }
}

def rollback(images, services, stableTag, profile) {
    echo '********** 🔄 Rollback a última versión estable **********'

    // 1️⃣ Bajar todos los contenedores
    shutdownContainers(profile)

    // 2️⃣ Remove unstable images
    removeUnstableImages(images, stableTag)

    // 3️⃣ Levantar servicios básicos
    startBaseServices(services)

    // 4️⃣ Levantar ultima version estable de cada imagen
    startLatestStableImages(images, stableTag)
}

// =================================================================================================================================
// =================================================================================================================================
// PIPELINE
// =================================================================================================================================
// =================================================================================================================================

pipeline {
    agent any // Ejecuta el pipeline en cualquier agente (nodo Jenkins disponible)

    environment {
        APP_VERSION = "${env.BUILD_NUMBER}"
        STABLE_TAG = "stable"
        COMPOSE_PROJECT_NAME = "granja_favorita"
    }

    options {
        skipDefaultCheckout(true) // No hacer el checkout scm automático
        timestamps() // Agregar la hora a cada línea del log
        // disableConcurrentBuilds() // Evitar builds simultáneos
        timeout(time: 30, unit: 'MINUTES') // Pipeline dura más de 30 minutos -> aborted / failure
    }

    stages {
        stage('Release Gate 🚧'){
            steps{
                echo "🚀 Rama pusheada: ${env.BRANCH_NAME}"

                script{
                    if(env.BRANCH_NAME != 'master'){
                        currentBuild.result = 'ABORTED'
                        error('STOP_PIPELINE')
                        // catchError(buildResult: 'ABORTED', stageResult: 'ABORTED') {
                            // error('')
                        // }
                    }
                }
            }
        }

        stage('🔑 Docker Login') {
            steps {
                script{
                    sh "echo ${env.DOCKER_PASSWORD} | docker login -u ${env.DOCKER_USER} --password-stdin"
                }
            }
        }

        stage('🧠 Decide deploy'){
            steps{
                script{
                    env.DO_DEPLOY = (env.DEPLOY_TARGET == 'VPS') ? 'true' : 'false'

                    echo "DEPLOY_TARGET: ${env.DEPLOY_TARGET}" // "VPS" o "LOCAL"
                    echo "BRANCH_NAME  : ${env.BRANCH_NAME}"
                    echo "DO_DEPLOY    : ${env.DO_DEPLOY}" // "true" o "false"
                }
            }
        }

        stage('🧠 Deploy context'){
            steps{
                script{
                    if(env.DO_DEPLOY == 'true'){
                        env.ENV = 'prod'
                        env.DOCKER_COMPOSE = "-f docker-compose.yml -f docker-compose.${env.ENV}.yml"
                        env.GIT_CREDS = '2dd51f03-81cf-4c7d-9a92-2a888b94fc72'

                    }else{
                        env.ENV = 'dev'
                        env.DOCKER_COMPOSE = ''
                        env.GIT_CREDS = '2dd51f03-81cf-4c7d-9a92-2a888b94fc72'
                    }
                }

                echo "DOCKER_COMPOSE: ${env.DOCKER_COMPOSE}"
                echo "ENV           : ${env.ENV}" // "prod" o "dev"
            }
        }

        stage('⬇️ Stop running containers') {
            steps{
                shutdownContainers(env.ENV)
            }
        }

        stage('🧹 Prune Docker images (VPS)'){
            steps{
                script{
                    if(env.DO_DEPLOY == 'true'){
                        sh 'docker image prune -af'
                    }
                }

                sh 'docker images'
            }
        }
        
        stage('🧹 Clean workspace') {
            steps{
                deleteDir()
                sh 'ls'
            }
        }

        stage('📥 Checkout granja-la-favorita') {
            steps {
                checkout scm
                sh 'ls'
            }
        }

        stage('🔐 Fetch credentials') {
            steps {
                dir('credentials') {
                    git url: 'https://github.com/xjuangalindox/credentials.git',
                        branch: 'profiles',
                        credentialsId: env.GIT_CREDS
                }

                sh 'ls'
            }
        }

        stage('🧱 Start Base Services'){
            steps{
                script{
                    startBaseServices(BASE_SERVICES)
                }
            }
        }

        stage('⚙️ Start Config-Server'){
            steps{
                script{
                    try{
                        sh """
                            TAG_VERSION=${env.APP_VERSION} \
                            docker-compose --env-file credentials/.env.${env.ENV} up -d --build config-server
                        """
                        sh 'docker ps'

                    }catch(Exception e){
                        showLastLogs('config-server')
                        throw e
                    }
                }
            }
        }

        stage('📡 Start Eureka-Server'){
            steps{
                script{
                    try{
                        sh """
                            SPRING_PROFILES_ACTIVE=${env.ENV} \
                            TAG_VERSION=${env.APP_VERSION} \
                            docker-compose --env-file credentials/.env.${env.ENV} up -d --build eureka-server
                        """
                        sh 'docker ps'
                        
                    }catch(Exception e){
                        showLastLogs('eureka-server')
                        throw e
                    }
                }
            }
        }

        stage('🧠 Start Microservicio-Principal'){
            steps{
                script{
                    // throw new Exception("Force Exception")

                    try{
                        sh """
                            SPRING_PROFILES_ACTIVE=${env.ENV} \
                            TAG_VERSION=${env.APP_VERSION} \
                            docker-compose --env-file credentials/.env.${env.ENV} up -d --build microservicio-principal
                        """
                        sh 'docker ps'
                        
                    }catch(Exception e){
                        showLastLogs('microservicio-principal')
                        throw e
                    }
                }
            }
        }

        stage('🐇 Start Microservicio-Razas'){
            steps{
                script{
                    try{
                        sh """
                            SPRING_PROFILES_ACTIVE=${env.ENV} \
                            TAG_VERSION=${env.APP_VERSION} \
                            docker-compose --env-file credentials/.env.${env.ENV} up -d --build microservicio-razas
                        """
                        sh 'docker ps'
                        
                    }catch(Exception e){
                        showLastLogs('microservicio-razas')
                        throw e
                    }
                }
            }
        }

        stage('📦 Start Microservicio-Articulos'){
            steps{
                script{
                    try{
                        sh """
                            SPRING_PROFILES_ACTIVE=${env.ENV} \
                            TAG_VERSION=${env.APP_VERSION} \
                            docker-compose --env-file credentials/.env.${env.ENV} up -d --build microservicio-articulos
                        """
                        sh 'docker ps'
                        
                    }catch(Exception e){
                        showLastLogs('microservicio-articulos')
                        throw e
                    }
                }
            }
        }

        stage('🚪 Start Gateway-Service'){
            steps{
                script{
                    try{
                        sh """
                            SPRING_PROFILES_ACTIVE=${env.ENV} \
                            TAG_VERSION=${env.APP_VERSION} \
                            docker-compose --env-file credentials/.env.${env.ENV} up -d --build gateway-service
                        """
                        sh 'docker ps'
                        
                    }catch(Exception e){
                        showLastLogs('gateway-service')
                        throw e
                    }
                }
            }
        }

        stage('🔀 Start Nginx'){
            steps{
                script{
                    try{
                        sh """
                            TAG_VERSION=${env.APP_VERSION} \
                            docker-compose ${env.DOCKER_COMPOSE} --env-file credentials/.env.${env.ENV} up -d --build nginx
                        """
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
            echo "El pipeline ${env.JOB_NAME} ha finalizado."
        }

        aborted {
            echo '********** ⛔ POST: ABORTED **********'
            echo "🚫 Deploy bloqueado: Solo 'master' puede realizar despliegues."

            // 1️⃣ Bajar todos los contenedores
            // sh "docker-compose --env-file credentials/.env.${env.ENV} down --remove-orphans || true"
            // sh 'docker ps'

            // script{
                // 1️⃣ Bajar todos los contenedores
                // shutdownContainers(env.ENV)

                // 2️⃣ Remove unstable images
                // removeUnstableImages(BASE_IMAGES, env.STABLE_TAG)
            // }
        }

        success {   
            echo '********** ✅ POST: SUCCESS **********'

            script {                                   
                // 1️⃣ Marcar como stable
                tagAsStable(BASE_IMAGES, env.APP_VERSION, env.STABLE_TAG)

                // 2️⃣ Remover imágenes inestables
                removeUnstableImages(BASE_IMAGES, env.STABLE_TAG)
                
                // 3️⃣ Enviar success mail
                if(env.DO_DEPLOY == 'true'){ sendSuccessMail() }
            }
        }

        failure {
            echo '********** 💥 POST: FAILURE **********'

            script {
                // 1️⃣ Bajar todos los contenedores, Remove unstable images, Levantar servicios básicos, Levantar ultima version estable de cada imagen                
                rollback(BASE_IMAGES, BASE_SERVICES, env.STABLE_TAG, env.ENV)
                
                // 2️⃣ Enviar failure mail
                if(env.DO_DEPLOY == 'true'){ sendFailureMail() }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// throw new Exception("Fallo forzado")
// ---------------------------------------------------------------------------
// 1️⃣ 2️⃣ 3️⃣ 4️⃣ 5️⃣
// 6️⃣ 7️⃣ 8️⃣ 9️⃣ 🔟
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

// =================================================================================================================================
// =================================================================================================================================
// MAILS
// =================================================================================================================================
// =================================================================================================================================

def sendSuccessMail() {
    echo '********** ✅📧 Enviando correo de DEPLOY EXITOSO **********'

    mail(
        from: 'Jenkins <xjuangalindox@gmail.com>',
        to: 'xjuangalindox@gmail.com, romannancynayely@gmail.com',
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
        to: 'xjuangalindox@gmail.com, romannancynayely@gmail.com',
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