# Api Franquicias
En el siguiente documento encontraras la solucion de la api de franquicias:

* Se utilizo una simplifiación de la arquitectura de [entrypoints](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a) o adaptadores
* El punto de entrada es el controlador [FranquiciaController](src/main/java/com/accenture/franquicias_api/infraestructura/entrypoints/web/controller/FranquiciaController.java).
* Los casos de uso se encuentra en [FranquiciaUseCase](src/main/java/com/accenture/franquicias_api/domain/usecases/FranquiciaUseCase.java).
* Se utilizo una [base de datos](FranquiciaRepositoryAdapter) mongo en la nube, se conecta mediante cadena de strings.
* En el archivo yaml se encuentran las variables de entorno o configuracion.
* Las variables de entorno o ambiente se encuentran en [.env](src/main/resources/.env)
* Para utitilizar la aplicacion se debe correr el [FranquiciasApiApplication](src/main/java/com/accenture/franquicias_api/FranquiciasApiApplication.java) y esta listo para probar. Si hay errores en la base de datos posiblmente sean por la IP que se conecta a la db, puedes simplemente agregar cualquer otra de mongo o conectarte a la existente y agregando tu IP. (No siempre mongo deja permitir todas las ips)
* Para correr(Run), tener en cuenta de asociarle el archivo .env con el contenido de la variable de entorno
* Se Utiliza Java 21
* Se añaden colleccion de [postman](java/com/accenture/franquicias_api/postman/Accenture Franquicias.postman_collection.json) para probar la api.
* Se crearon pruebas unitarias de los flujos principales.
