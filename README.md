# RallyRound BackEnd API

Este repositorio contiene el back-end de la aplicación **RallyRound**, desarrollada como parte de mi proyecto final de la Tecnicatura Universitaria en Programación en la Universidad Tecnológica Nacional - Facultad Regional Córdoba, durante el año 2024. Este proyecto se realizó en el marco de la materia Práctica Supervisada y tiene como objetivo demostrar el aprendizaje adquirido durante los dos años de carrera.

## Descripción del Proyecto

**RallyRound** es una aplicación diseñada para resolver el problema común de organizar eventos sociales, deportivos, musicales, entre otros, cuando no se cuenta con la cantidad suficiente de participantes. La aplicación permite a los usuarios crear y promocionar eventos, así como unirse a eventos existentes, fomentando la interacción social y fortaleciendo la comunidad local.

### Objetivos Principales

- **Facilitar la Organización de Eventos:** Brindar una plataforma donde los usuarios puedan crear y promover eventos de manera sencilla.
- **Resolver la Dificultad para Reunir Participantes:** Permitir a los usuarios encontrar y unirse a eventos relevantes en su localidad.
- **Fomentar la Interacción Social:** Proporcionar un espacio para descubrir eventos, conectar con otros participantes y compartir experiencias.
- **Promover la Comunidad Local:** Fortalecer las comunidades locales mediante la organización de eventos en diferentes localidades.

### Objetivo del Sistema de Información

Brindar información para la gestión de usuarios, reportes de usuarios, actividades, eventos y chats para facilitar la gestión de eventos sociales. Además, el sistema permitirá generar estadísticas sobre los usuarios, actividades, eventos, reportes y chats para la toma de decisiones estratégicas de la gerencia gestora de la aplicación.

## Tecnologías Utilizadas

Este proyecto fue desarrollado utilizando las siguientes tecnologías:

- **Spring Boot**: Para la construcción del framework principal y las APIS.
- **Spring Security**: Para la implementación de seguridad en la aplicación.
- **Spring Data JPA**: Para la gestión y acceso a los datos.
- **PostgreSQL**: Como base de datos relacional.
- **JWT (jsonwebtoken)**: Para la autenticación y autorización de usuarios.
- **WebSockets**: Para la comunicación en tiempo real de los chats y notificaciones.
- **Mercado Pago Java SDK**: Integración para la gestión de pagos.
- **Bing Maps API**: Integración para el uso de sugerencias de ubicación.
- **Reactor**: Para la programación reactiva utilizando WebClient.

## Límites del Proyecto

- **Desde:** El registro de un nuevo usuario en la plataforma.
- **Hasta:** La generación de estadísticas y emisión de reportes sobre eventos realizados, usuarios, reportes, actividades y chats.

## Alcances del Proyecto

### Gestión de Usuarios
- Actualizar reputación.
- Registrar, modificar, eliminar y consultar participantes de actividades.
- Registrar, modificar, eliminar y consultar miembros del staff.
- Consultar usuarios por cantidad de reportes.
- Consultar usuarios por localidad y cantidad de reportes.
- Generar estadísticas de cantidad de usuarios registrados por mes y año.
- Generar estadísticas de cantidad de usuarios participantes por localidad.
- Generar estadísticas de usuarios más reportados por localidad.

### Gestión de Reportes
- Actualizar motivo.
- Registrar, eliminar y consultar reportes de usuarios.
- Generar estadísticas de cantidad de reportes registrados por motivo y año.
- Generar estadísticas de reportes registrados por motivo y mes.
- Generar estadísticas de reportes registrados por localidad y mes.

### Gestión de Actividades
- Registrar, modificar, eliminar y consultar actividades.
- Generar informe de cantidad de actividades registradas por mes.

### Gestión de Pagos
- Registrar pago.
- Eliminar pago (reembolso/cancelación).
- Consultar pago.
- Emitir listado de pagos realizados por evento.

### Gestión de Votos de Horarios
- Registrar, eliminar y consultar votos.
- Consultar todos los votos.

### Gestión de Eventos
- Registrar, modificar, eliminar y consultar eventos.
- Actualizar eventos.
- Emitir listado de cantidad de eventos por actividad, ubicación, fecha y horarios.
- Generar estadísticas de cantidad de eventos creados por mes y actividad.
- Generar estadísticas de cantidad de eventos creados por localidad y actividad cada mes.
- Generar estadísticas de cantidad de eventos cancelados por mes.

### Gestión de Chats
- Registrar, modificar, eliminar y consultar chats.
- Registrar, eliminar y consultar mensajes.
- Generar estadísticas de cantidad de chats creados por mes.
- Generar estadísticas de mensajes enviados por mes.

### Gestión de Ubicaciones
- Actualizar calle.
- Actualizar barrio.
- Actualizar localidad.
- Actualizar provincia.

## Requerimientos No Funcionales
- Para que un usuario se una a un evento debe pagar la inscripción al mismo (si este fuere pago) y votar un horario de los designados como posibles para el evento.
- El acceso al sistema para cualquier tipo de usuario se realiza mediante usuario (email) y contraseña.
- Para poder crear un evento, el usuario debe proporcionar acceso a su cuenta de Mercado Pago, así la aplicación puede gestionar los pagos y reembolsos.
