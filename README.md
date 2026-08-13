# The Legend of Tecla — Alonso Castro / Sambade Saá Reforged

Reconstrucción moderna y separada de la entrega realizada por Miguel Alonso
Castro y Rodrigo Sambade Saá. El primer commit conserva como código fuente la
entrega original, que obtuvo un 6/10; los commits posteriores construyen la
versión que habría debido entregarse entonces.

Esta edición integra la implementación moderna completa manteniendo las clases
Marine, Francotirador y Zapador: GUI web, editor, dificultades, descanso, mapas
predeterminados, grandes, TXT, JSON y procedurales por semilla, misiones,
persistencia, progresión, entorno destructible, trampas, fabricación, munición y
combate táctico.

Los aliados parten junto al jugador, pueden generarse automáticamente o en una
cantidad y nivel elegidos, puntúan individualmente y priorizan ayudar y explorar.
Algunos cumplen el rol médico y buscan botiquines y Toritos Rojos. Los enemigos
emplean armas y armaduras de su propia facción, se coordinan cuando hay escuadra y
su número se escala de forma justa con los aliados. Cada celda posee ambientación
detallada y `mirar` refleja fielmente suelo, luz, fuego, agua y estructuras visibles.
La partida puede continuar en modo espectador cuando muere el jugador: `Play`
reproduce a ritmo normal los turnos restantes. Mapa, estado, acciones, eventos y
comandos viven en ventanas movibles, redimensionables, minimizables y maximizables.

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
