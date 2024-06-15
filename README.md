# Anagrafica Management System

This project implements a backend system for managing Anagrafiche using Java 17 with Spring Boot 3 and Spring Security and DB Postgres. 

Below are the APIs available for managing Anagrafiche:


### Steps to Start the Project Eclipse

- Simply comment out the Docker file and insert the PostgreSQL database values in the application.yml file under these properties:

	```bash 
	spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
	spring.datasource.url=${SPRING_DATASOURCE_URL}
	spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
	 ```
	An example:
	
	```bash 
	spring.datasource.url=jdbc:postgresql://localhost:5432/test_neo
	 ```

- Run the following command to build the project:

	```bash 
	mvn clean package install
	 ```
	 
- Then start the application with:

```bash 
Run As > Spring Boot App
 ```

### Steps to Start the Project:

1. **Install Docker**: Ensure Docker is installed on your computer. You can download it from [docker.com]  (https://www.docker.com/get-started) and follow the installation instructions for your operating system.

2. **Clone the Repository**: Clone the project repository from your console or terminal:

    ```bash 
    git clone https://github.com/drcimux/anagrafica-api.git
    ```
   
2. **Start Docker Containers:**:  Navigate to the root directory of your cloned project and start the Docker containers using the following command:

    ```bash 
  	docker-compose up -d --build
  	```

3. **Access the Application:**:  Once the containers are started, the application will be available at [localhost](http://localhost:8081). You can test the APIs using Postman or any other HTTP client.
or you can access to  [OpenAPI definition](http://localhost:8081/swagger-ui/index.html#/) and see [api-docs](http://localhost:8081/v3/api-docs)
       

5. **Additional Notes**: Make sure to customize environment variables ( `bash POSTGRES_DB, POSTGRES_USER, etc.` ) according to your specific needs in the docker-compose.yaml file and in the Dockerfile (Dockerfile) used for building the backend service.:


### Anagrafiche APIs

#### GET /api/anagrafiche/ping

- Endpoint to check if the API is reachable.

#### GET /api/anagrafiche/download/{id}

- Download an Anagrafiche file by ID.

#### GET /api/anagrafiche/anagrafiche

- Retrieve all Anagrafiche stored in the system or filtered by idFile

#### POST /api/anagrafiche/upload

- Upload Anagrafiche via a CSV file.

You can download the CSV file containing sample data from the link below:

[Download CSV File](./test_anagrafiche_valid_end.csv)

Feel free to use this file for testing or reference purposes.


## Authentication API

Operations related to Authentication Management.

This project also includes authentication functionalities using JWT (JSON Web Token) authentication with 

Spring Security.

### POST /auth/v1/login

- Endpoint to authenticate users and obtain a JWT token.
- To test the authentication API:
  1. Open the Swagger UI: `http://localhost:8080/swagger-ui/index.html`
  3. Use the following curl command in Postman to perform a login:
     ```bash
     curl -X 'POST' \
     'http://localhost:8080/auth/v1/login' \
     -H 'accept: */*' \
     -H 'Content-Type: application/json' \
     -d '{
       "userName": "admin",
       "password": "admin"
     }'
     ```
  4. Copy the JWT token from the response and use it in the Authorization header with Bearer token type for subsequent API calls.
