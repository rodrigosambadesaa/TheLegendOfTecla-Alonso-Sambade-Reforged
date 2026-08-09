# The Legend of Tecla — Alonso Castro / Sambade Saá Reforged

Reconstrucción moderna y separada de la entrega realizada por Miguel Alonso
Castro y Rodrigo Sambade Saá. El primer commit conserva como código fuente la
entrega original, que obtuvo un 6/10; los commits posteriores construyen la
versión que habría debido entregarse entonces.

Esta edición mantiene el alcance clásico de P1–P3 y la parte opcional de interfaz/editor,
con la ampliación táctica solicitada de aliados y condiciones de victoria. No incorpora
dificultades, descanso ni mapas grandes con variantes. Incluye consola y GUI Swing accesible desde web
mediante noVNC, editor gráfico, escenarios predeterminados/TXT/JSON, las clases
Marine, Francotirador y Zapador, inventario, equipo, combate y comandos
compuestos.

Los aliados se activan en el asistente gráfico o con `--aliados si`; se puede elegir
`--victoria solo_jugador|jugador_y_aliados`. Durante la partida, `reagrupar defensiva`
y `reagrupar ofensiva` hacen que acompañen al jugador. Si faltan suministros, el aliado
en mejor estado explora sin alejarse más de tres celdas, y los enemigos reaccionan al
detectar la formación. Los binoculares se reservan hasta que revelan una amenaza y se
consumen tras ese único uso.

## Ejecutar

Requisitos: Java 17+ y Maven 3.9+.

```bash
mvn verify
java -jar target/the-legend-of-tecla.jar --rapido
java -jar target/the-legend-of-tecla.jar --gui
java -jar target/the-legend-of-tecla.jar --editor
```

Con Docker, `docker compose up --build gui` publica la GUI en
`http://localhost:6080/vnc.html?autoconnect=1&resize=scale`.

## Estructura histórica

- `historico/entrega-original`: copia formateada de la entrega auténtica.
- `src/main`: reconstrucción mantenible.
- `src/test`: pruebas rehechas para el alcance clásico.
- Los cuatro PDF de la raíz son los enunciados usados como fuente.

Autores: Miguel Alonso Castro y Rodrigo Sambade Saá.
