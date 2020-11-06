# webshop3d

Webstore application 3D Electroincs 

* start docker with:  `docker run --name webshop3d -p 5432:5432 -e POSTGRES_PASSWORD=docker3d -d postgres`


To push image to docker HUB

- docker build -t 3d-webshop .
- docker tag e914b86074e0 ahmed83/webshop3dapp:firsttry (or secondtry etc)
- docker push ahmed83/webshop3dapp

Then you can start the application with 

- docker-compose up