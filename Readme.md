# Read Me First
The following was discovered as part of building this project:

* The original package name 'com.accenture.franquicias-api' is invalid and this project uses 'com.accenture.franquicias_api' instead.

# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.4/gradle-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.4.4/gradle-plugin/packaging-oci-image.html)
* [Spring Reactive Web](https://docs.spring.io/spring-boot/3.4.4/reference/web/reactive.html)
* [Spring Data Reactive MongoDB](https://docs.spring.io/spring-boot/3.4.4/reference/data/nosql.html#data.nosql.mongodb)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)
* [Accessing Data with MongoDB](https://spring.io/guides/gs/accessing-data-mongodb/)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

# Api Franquicias
En el siguiente documento encontraras la solucion de la api de franquicias:

* Se utilizo una simplifiación de la arquitectura de [entrypoints](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a) o adaptadores
* El punto de entrada es el controlador [FranquiciaController](src/main/java/com/accenture/franquicias_api/infraestructura/entrypoints/web/controller/FranquiciaController.java).
* Los casos de uso se encuentra en [FranquiciaUseCase](src/main/java/com/accenture/franquicias_api/domain/usecases/FranquiciaUseCase.java).
* Se utilizo una [base de datos](FranquiciaRepositoryAdapter) mongo en la nube, se conecta mediante cadena de strings.
* En el archivo yaml se encuentran las variables de entorno o configuracion. No se utilizo un enviroment ya que no se alcanzo a desplegar en la nube.
* Se añaden colleccion de [postman](java/com/accenture/franquicias_api/postman/Accenture Franquicias.postman_collection.json) para probar la api.
* Se crearon pruebas unitarias de los flujos principales.
