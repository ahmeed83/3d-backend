FROM adoptopenjdk/openjdk12:latest
VOLUME /tmp
ADD target/webshop3d-1.0.0.jar app.jar
RUN apt-get update
RUN apt-get install -y postgresql-client
COPY wait-for-postgres.sh ./opt/wait-for-postgres.sh
RUN ["chmod", "+x", "./opt/wait-for-postgres.sh"]