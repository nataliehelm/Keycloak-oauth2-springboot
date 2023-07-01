# Implementación de seguridad Keycloak y OAuth 2.0
Implementación de seguridad con Keycloak y OAuth 2.0 en una arquitectura de microservicios para un e-commerce usando Spring Boot. Dicha arquitectura está compuesta por:

− Servidor Eureka para registro y descubrimiento de servicios.
− API gateway para enrutamiento de solicitudes
− Microservicio que se encarga de gestionar las facturas de los clientes y descarga de facturas
− Microservicio que se encarga de gestionar los usuarios

También disponemos de un Runner para la creación y configuración del reino de Keycloak.

## Documentación API
https://documenter.getpostman.com/view/18610779/2s93z9ah8v

## Requisitos
− JDK 8 o superior
− Maven

## Configuración de Keycloak
Ejecutar la clase KeycloakApplication del proyecto ubicado en el directorio Keycloak.
Dicho runner se encargará de la creación del reino, clientes, usuarios, grupos, roles y configuraciones necesarias para que el resto de los microservicios y el IAM se integren correctamente.
Keycloak levantará en el puerto 8082.

### Características del Realm:
- Clients: api-gateway-client para el gateway y backend para los microservicios de bills y user, ambos con Client authentication activa.

El Gateway tiene configurada las redirecciones para que si el usuario no está autenticado sea redirigido al login de Keycloak

- Client Scopes: user_groups mapeado en el JWT para luego poder restringir endpoints según su pertenencia a determinados grupos.
  
- Rol de USER
  
- Groups: creación de grupo PROVIDERS
  
- Usuarios:
  ⋅ Username: testuser
  ⋅ Password: password
  ⋅ Perteneciente al grupo USER
  
  ⋅ Username: proveedor1
  ⋅ Password: password
  ⋅ Perteneciente al grupo PROVIDERS
  
  ⋅ Username: proveedor2
  ⋅ Password: password
  ⋅ Perteneciente al grupo PROVIDERS
  
  ⋅ Username: proveedor3
  ⋅ Password: password
  ⋅ Perteneciente al grupo PROVIDERS

En el usuario por default del cliente Backend se agrega el permiso view-users de realm-management para que desde dicho microservicio se puedan consultar usuarios de keycloak.

## Configuración de Microservicios

1. Eureka server levantará en el puerto 8761
2. API Gateway levantará en el puerto 8090
Se agregan las configuraciones de seguridad necesarias en donde se restringe el consumo de la API solo a los usuarios autenticados y se aplica el filtro necesario para enviar el token al resto de los microservicios.
Se rutean los microservicios ms-bills y users-service
3. Microservicio ms-bills
A efectos de poder realizar pruebas con postman el mismo se dejó fijo en el puerto 8081. Se le agregan las configuraciones de seguridad Keycloak y OAuth necesarias
Se agrega el package security el cual contiene un Jwt converter customizado para poder extraer roles, grupos y scope del JWT token y utilizarlo para restringir el acceso a los endpoints en las configuraciones web de seguridad
El converter obtiene primero los claims del token, y luego a través de diferentes métodos, arma una colección en donde se agregan las authorities customizadas que deseamos utilizar para restringir los endpoints.
Ver endpoints y sus restricciones en https://documenter.getpostman.com/view/18610779/2s93z9ah8v#95cff964-a45c-4032-9d75-2e635868c54c
4. Microservicio users-service
El microservicio users-services contiene endpoints para realizar búsqueda de usuarios de Keycloak, tanto por su username como por su ID.
Tambien permite la búsqueda de un usuario y sus facturas consumiendo esta última información del microservicio bills. Se configura el cliente de Feign para obtener el token de la autenticación en Keycloak agregándoselo a la request.
Ver endpoints de este microservicio y sus restricciones en https://documenter.getpostman.com/view/18610779/2s93z9ah8v#7732e48e-18ca-480a-b651-a31bb9dad56f
