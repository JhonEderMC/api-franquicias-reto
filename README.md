# Api Franquicias
En el siguiente documento encontraras la solucion de la api de franquicias:

* Se utilizó una simplificación de la arquitectura de [entrypoints](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a) o adaptadores
* El punto de entrada es el controlador [FranquiciaController](src/main/java/com/accenture/franquicias_api/infraestructura/entrypoints/web/controller/FranquiciaController.java).
* Los casos de uso se encuentra en [FranquiciaUseCase](src/main/java/com/accenture/franquicias_api/domain/usecases/FranquiciaUseCase.java).
* Se utilizo una [base de datos](FranquiciaRepositoryAdapter) mongo en la nube, se conecta mediante cadena de strings.
* En el archivo yaml se encuentran las variables de entorno o configuración.
* Las variables de entorno o ambiente se encuentran en [.env](src/main/resources/.env)
* Para utilizar la aplicación se debe correr el [FranquiciasApiApplication](src/main/java/com/accenture/franquicias_api/FranquiciasApiApplication.java) y esta listo para probar. Si hay errores en la base de datos posiblemente sean por la IP que se conecta a la db, puedes simplemente agregar cualquier otra de mongo o conectarte a la existente y agregando tu IP. (No siempre mongo deja permitir todas las ips)
* Para correr(Run), tener en cuenta de asociarle el archivo .env con el contenido de la variable de entorno
* Se Utiliza Java 17.
* Se añade colección de [postman](postman/Accenture Franquicias.postman_collection.json) para probar la api.
* En la colección de postman se encuentran los endpoints de la api para probarla tanto en la nube como en localhost, lee la documentación asociada si quieres cambiar entre uno y otro.
* Tener en cuenta que la primera petición puede ser muy lenta la respuesta, si la aplicación lleva mucho tiempo sin usar y el servidor puede tardar un poco en iniciar.
* Se crearon pruebas unitarias de los flujos principales.
