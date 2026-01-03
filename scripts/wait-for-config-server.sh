#!/bin/bash

# URL del Config Server (puedes ajustar según tu configuración)
CONFIG_SERVER_URL="http://config-server:8888/actuator/health"

# Intentar acceder al Config Server hasta que esté disponible
until $(curl --silent --fail $CONFIG_SERVER_URL); do
    echo "Esperando a que Config Server esté disponible..."
    sleep 5
done

echo "Config Server está disponible. Continuando con el despliegue..."
