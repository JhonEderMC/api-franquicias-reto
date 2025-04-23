# Etapa de construcción
FROM eclipse-temurin:22-jdk AS builder

WORKDIR /app

# Copiamos los archivos de gradle/maven y construimos el JAR
COPY build/libs/*.jar app.jar

# Etapa final
FROM eclipse-temurin:22-jdk-jammy

WORKDIR /app

# Copiamos el jar generado
COPY --from=builder /app/app.jar app.jar

# Puerto por defecto que Render usará (puedes cambiarlo si usas otro)
EXPOSE 8080

# Comando para correr la app
ENTRYPOINT ["java", "-jar", "app.jar"]