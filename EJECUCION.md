# Ejecución y comandos

Compila y valida con `mvn verify`. La consola se inicia con
`java -jar target/the-legend-of-tecla.jar`; `--rapido` evita el asistente.
La GUI usa `--gui` y el editor `--editor`.

Los modos disponibles son el mapa predeterminado y la carga desde ficheros
TXT/JSON. Los comandos de juego son `mover`, `mirar`, `coger`, `tirar`,
`inventario`, `usar`, `equipar`, `desequipar`, `atacar`, `lanzar`, `recorrido`,
`cargar`, `ayuda` y `salir`. La entrada de comandos de la GUI utiliza exactamente
el mismo intérprete que la consola.

Ejemplos compuestos: `mover norte 3`, `atacar 2e Sectoid_A 2` y
`equipar lanzacohetes ametralladora`.

## Depuracion remota opcional en Docker (JDWP)

La GUI **no habilita JDWP de forma predeterminada**. Este protocolo puede
controlar la JVM sin autenticacion; por seguridad, solo debe activarse para
depuracion local. Docker Compose publica el puerto 5005 en `127.0.0.1` y,
sin la variable de activacion, no existe un listener JDWP.

Para activarlo de manera explicita:

```bash
TECLA_GUI_ENABLE_DEBUG=true docker compose up --build gui
```

Para volver al comportamiento seguro:

```bash
docker compose down
docker compose up --build gui
```

No exponga el puerto 5005 a una red publica ni utilice JDWP en partidas
remotas sin aislamiento adicional. La CI verifica tanto el funcionamiento de
noVNC como la ausencia de JDWP por defecto.
