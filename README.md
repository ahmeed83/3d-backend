# webshop3d

Webstore application 3D Electroincs 

* start docker with:  `docker run --name webshop3d -p 5432:5432 -e POSTGRES_PASSWORD=docker3d -d postgres`
------------------------------------------
To run the application locally:

- choose profile "local" on application.yml
- run postgres on docker
- run the app with SPRING BOOT
------------------------------------------
To run the application locally with docker compose:

- choose profile "dev" on application.yml
- run docker-compose up
------------------------------------------
To push image to docker HUB

- docker build -t 3d-webshop .
- docker tag "image-id" ahmed83/webshop3dapp:latest
- docker push ahmed83/webshop3dapp

If you want to run the image from docker HUB, then:

- change the file: docker-compose-hub.yml to docker-compose.yml
- then: docker-compose up
------------------------------------------

