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
La partida puede continuar en modo espectador cuando muere el jugador: `Turbo`
reproduce turnos cada 100 ms y muestra victoria humana o enemiga. Los enemigos se
dispersan fuera de un radio de preparación escalable y todas las familias de objetos
crecen con el total de aliados y enemigos. Mapa, estado, acciones, eventos y
comandos viven en ventanas movibles, redimensionables, minimizables y maximizables.

## Arsenal, cooperación y escala

- El nivel inicial del jugador se elige en consola y GUI, tanto en solitario como con aliados.
- Por defecto los aliados pueden sustituir sus armas y armaduras por equipo objetivamente mejor
  y transfieren al jugador munición compatible cuando la necesita; ambas ayudas son configurables.
- El arsenal diferencia espadas de una y dos manos, cuchillos, arrojadizas, arcos, ballestas,
  granadas, pistolas, subfusiles, escopetas, rifles de asalto y precisión, ametralladoras,
  lanzadores y armas de energía, con cargadores, daño, alcance y penetración propios.
- El catálogo determinista rota variantes para evitar partidas con el mismo lote de armas.
  Jugador, aliados, enemigos normales y jefes eligen y emplean armas compatibles con su rol.
- La precisión depende del tirador, su clase y nivel; el daño final también considera la
  cobertura, el flanqueo y la defensa efectiva de la armadura tras aplicar penetración.
- El despliegue nunca permite más enemigos que combatientes aliados contando al jugador.
  El máximo validado es 5.000 contra 5.000 (4.999 aliados más el jugador).
- La fila numérica y el teclado numérico funcionan en configuración, editor y comando de partida.

CLI: `--nivel-jugador N`, `--sin-mejoras-aliados` y `--sin-municion-aliada`.

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

## Calidad verificable

`mvn clean verify` exige al menos 75 % de cobertura de líneas, ejecuta contratos
ArchUnit para impedir dependencias del dominio y motor hacia Swing, limita cada
archivo Java a 1.700 líneas, aplica Checkstyle y SpotBugs y genera Javadoc y un
SBOM CycloneDX (`target/bom.json`). GitHub Actions añade OWASP Dependency Check
y CodeQL. La política de seguridad está en [SECURITY.md](SECURITY.md), las normas
de colaboración en [CONTRIBUTING.md](CONTRIBUTING.md) y el contrato completo en
[docs/CALIDAD_10_DE_10.md](docs/CALIDAD_10_DE_10.md).

La prueba extrema ejecuta un turno completo con 4.999 aliados más el jugador
contra 5.000 enemigos bajo un heap de 512 MiB y bloquea regresiones que superen
10 segundos. El motor usa índices espaciales para percepción y ruido y evita las
copias cuadráticas de estados, inspecciones, celdas y evacuaciones.
