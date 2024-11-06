Image Organizer, una aplicación para tener organizadas tus fotos de una manera cómoda y en tu propio servidor.


REQUISITOS:

- JavaJDK
- Maven

INSTALACIÓN:

- descargar con "git clone https://github.com/Mel0nABC/ImageOrganizer/" o descargando el proyecto en zip:
  ![imagen](https://github.com/user-attachments/assets/444d9c92-6b91-4d99-bc43-88a3b3d8bc49)

- Entramos en el directorio creado "ImageOrganizer".
- Ejecutamos aplicación mediante "mvn spring-boot:run". Esto inicializará spring boot, iniciando tomcat en el puerto 8080.
- Ahora podemos acceder a nuestra aplicación mediante la url http://localhost:8080/
- Al ser la primera vez, nos solicitará que indiquemos un nuevo nombre de usuario y contraseña para el administrador.
  
![imagen](https://github.com/user-attachments/assets/3c8008e2-4b6a-4f71-92e8-00170129ce4a)

- Una  vez hechos los cambios, podremos hacer login normal con el usuario configurado como administrador.

![imagen](https://github.com/user-attachments/assets/803ca5b4-aa76-4cd7-895f-c6c47190d511)

Ahora, ya estamos listos para utilizar image organizer, tiene un uso fácil e intuitivo, ¡adelante!

¿Qué hace la aplicación?

- Podemos crear bibliotecas donde están ubicadas nuestras imágenes y gestionar su contenido.
- Podemos crear carpetas.
- Subir múltiples imágenes a la vez. Cuando se sube una imagen, en segundo plano se genera una preview de menor definición, que es la que se mostrará en la previsualización.
- Cada vez que hagamos un cambio en el directorio, se logeará en el archivo ./logs/logs.log
- Disponemos de monitorización de cualquier cambio realizado en la ruta de la bibliotecas configuradas y sus subdirectorios.
- Añadir usuarios (por el momento la única diferencia de roles es ADMIN y el resto, que sería un usuario básico).


¡ATENCIÓN!

Tener extremo cuidado, actualmente cualquier usuario puede borrar cualquier imagen, directorio y su contenido.
