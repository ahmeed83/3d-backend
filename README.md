# webshop3d
------------------------------------------
Webstore application 3D Electroincs 
------------------------------------------


------------------------------------------
To run the application locally:
------------------------------------------

1. Run
    ```bash
    mvn clean package -P local -DsecretProperty=@secretProperty@

2. Start docker for the database with:
    ```bash
    docker run --name webshop3d -p 5432:5432 -e POSTGRES_DB=webshop3d -e POSTGRES_PASSWORD=password -d postgres:alpine
   
3. run the class [Webshop3dApplication](src/main/java/com/baghdadfocusit/webshop3d/Webshop3dApplication.java) 

------------------------------------------
To run the application locally with docker compose:
------------------------------------------
1. Run
    ```bash
    mvn clean package -P dev -DsecretProperty=@secretProperty@

2. Run
    ```bash
    docker-compose up
   
------------------------------------------
To push image to docker HUB
------------------------------------------
- docker build -t 3d-webshop .
- docker tag "image-id" ahmed83/webshop3dapp:latest
- docker push ahmed83/webshop3dapp

------------------------------------------
If you want to run the image from docker HUB, then:
------------------------------------------
- change the file: docker-compose-hub.yml to docker-compose.yml
- then: docker-compose up
------------------------------------------

###### Replace @secretProperty@ with jasypt password
