# Usare un'immagine base di OpenJDK
FROM openjdk:17

# Impostare la directory di lavoro nel container
WORKDIR /app

# Copiare il file jar nel container
COPY target/anagrafica-api-0.0.1-SNAPSHOT.jar app.jar

# Comando per eseguire l'applicazione
ENTRYPOINT ["java", "-jar", "app.jar"]