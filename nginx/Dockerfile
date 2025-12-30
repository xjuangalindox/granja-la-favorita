# Usamos la imagen oficial de Nginx
FROM nginx:latest

# Variable que llega desde docker-compose / Jenkins
ARG NGINX_CONF_FILE

# Copiamos el archivo de configuración dentro de la imagen (ya no se monta desde el host)
COPY ${NGINX_CONF_FILE} /etc/nginx/nginx.conf