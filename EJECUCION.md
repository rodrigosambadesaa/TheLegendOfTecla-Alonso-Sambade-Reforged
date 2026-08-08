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
