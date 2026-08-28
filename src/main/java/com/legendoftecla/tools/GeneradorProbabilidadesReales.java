package com.legendoftecla.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.legendoftecla.audio.GestorSonido;
import com.legendoftecla.console.Consola;
import com.legendoftecla.constants.CondicionVictoria;
import com.legendoftecla.constants.Dificultad;
import com.legendoftecla.engine.ConfiguracionPartida;
import com.legendoftecla.engine.FabricaJuego;
import com.legendoftecla.engine.MotorPartida;
import com.legendoftecla.exceptions.JuegoException;
import com.legendoftecla.model.world.DimensionesMapa;
import com.legendoftecla.model.world.Juego;
import com.legendoftecla.model.world.SistemaPuntuacion;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.GZIPOutputStream;

/**
 * Ejecuta partidas reales con {@link MotorPartida} para medir probabilidades
 * empiricas. No usa el simulador agregado de cohortes.
 */
public final class GeneradorProbabilidadesReales {
    static final long SEMILLA_PREDETERMINADA = 20_260_814_01L;
    static final int RUNS_PREDETERMINADOS = 200;
    private static final Gson JSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Escenario BASE = new Escenario("BASE", "base", "referencia",
            Dificultad.NORMAL, 25, 25, 10, 10, 10,
            CondicionVictoria.SOLO_JUGADOR);

    private GeneradorProbabilidadesReales() { }

    /**
     * Genera muestras, agregados, trazas y un informe reproducible.
     *
     * @param args admite --runs=N, --seed=N, --threads=N y --output=ruta
     * @throws Exception si una partida o la escritura fallan
     */
    public static void main(String[] args) throws Exception {
        System.setProperty(GestorSonido.PROPIEDAD_DESACTIVADO, "true");
        generar(Opciones.parsear(args));
    }

    static void generar(Opciones opciones) throws Exception {
        Files.createDirectories(opciones.salida());
        List<Escenario> escenarios = construirMatriz();
        List<Resumen> resumenes = new ArrayList<>();
        Path muestras = opciones.salida().resolve("samples.csv.gz");
        ExecutorService ejecutor = Executors.newFixedThreadPool(opciones.hilos());
        try (BufferedWriter csv = gzip(muestras)) {
            csv.write(Resultado.cabecera());
            csv.newLine();
            for (int indice = 0; indice < escenarios.size(); indice++) {
                Escenario escenario = escenarios.get(indice);
                List<Callable<Ejecucion>> trabajos = new ArrayList<>();
                for (int run = 0; run < opciones.runs(); run++) {
                    long semilla = mezclarSemilla(opciones.semilla(), indice, run);
                    boolean trazar = run == 0;
                    trabajos.add(() -> ejecutar(escenario, semilla, trazar));
                }
                int victoriasMision = 0;
                int victoriasBando = 0;
                long turnos = 0;
                int censuradas = 0;
                List<Future<Ejecucion>> futuros = ejecutor.invokeAll(trabajos);
                for (int run = 0; run < futuros.size(); run++) {
                    Ejecucion ejecucion = futuros.get(run).get();
                    Resultado resultado = ejecucion.resultado();
                    csv.write(resultado.csv());
                    csv.newLine();
                    victoriasMision += resultado.victoriaMision() ? 1 : 0;
                    victoriasBando += resultado.victoriaBando() ? 1 : 0;
                    censuradas += resultado.censurada() ? 1 : 0;
                    turnos += resultado.turnos();
                    if (run == 0) {
                        escribirGzip(opciones.salida().resolve("trace-"
                                + escenario.id().toLowerCase(Locale.ROOT) + ".log.gz"),
                                ejecucion.traza());
                    }
                }
                resumenes.add(new Resumen(escenario, opciones.runs(), victoriasMision,
                        victoriasBando, censuradas, turnos / (double) opciones.runs()));
            }
        } finally {
            ejecutor.shutdownNow();
        }
        escribirResumen(opciones.salida().resolve("summary.csv"), resumenes);
        Files.writeString(opciones.salida().resolve("statistics.json"),
                JSON.toJson(new Estadisticas(opciones.semilla(), opciones.runs(), resumenes))
                        + System.lineSeparator(), StandardCharsets.UTF_8);
        Files.writeString(opciones.salida().resolve("README.md"),
                informe(opciones, resumenes), StandardCharsets.UTF_8);
        escribirManifest(opciones.salida());
    }

    static List<Escenario> construirMatriz() {
        List<Escenario> escenarios = new ArrayList<>();
        for (Dificultad dificultad : Dificultad.values()) {
            escenarios.add(BASE.con("D-" + dificultad.name(), "dificultad",
                    dificultad.name(), dificultad, BASE.filas(), BASE.columnas(),
                    BASE.aliados(), BASE.nivelAliados(), BASE.nivelJugador(), BASE.condicion()));
        }
        for (int aliados : new int[] {0, 1, 5, 10, 25, 50, 100, 250}) {
            escenarios.add(BASE.con(String.format(Locale.ROOT, "P-%03d", aliados),
                    "poblacion", Integer.toString(aliados), BASE.dificultad(),
                    BASE.filas(), BASE.columnas(), aliados, BASE.nivelAliados(),
                    BASE.nivelJugador(), BASE.condicion()));
        }
        for (int[] dimensiones : new int[][] {{10, 10}, {15, 25}, {30, 30}, {50, 50}}) {
            String valor = dimensiones[0] + "x" + dimensiones[1];
            escenarios.add(BASE.con("M-" + valor, "mapa", valor, BASE.dificultad(),
                    dimensiones[0], dimensiones[1], BASE.aliados(), BASE.nivelAliados(),
                    BASE.nivelJugador(), BASE.condicion()));
        }
        for (int nivel : new int[] {1, 5, 10, 25, 50, 100}) {
            escenarios.add(BASE.con(String.format(Locale.ROOT, "LA-%03d", nivel),
                    "nivel_aliados", Integer.toString(nivel), BASE.dificultad(),
                    BASE.filas(), BASE.columnas(), BASE.aliados(), nivel,
                    BASE.nivelJugador(), BASE.condicion()));
        }
        for (int nivel : new int[] {1, 10, 25, 50, 100}) {
            escenarios.add(BASE.con(String.format(Locale.ROOT, "LJ-%03d", nivel),
                    "nivel_jugador", Integer.toString(nivel), BASE.dificultad(),
                    BASE.filas(), BASE.columnas(), BASE.aliados(), BASE.nivelAliados(),
                    nivel, BASE.condicion()));
        }
        for (CondicionVictoria condicion : CondicionVictoria.values()) {
            escenarios.add(BASE.con("C-" + condicion.name(), "condicion",
                    condicion.name(), BASE.dificultad(), BASE.filas(), BASE.columnas(),
                    BASE.aliados(), BASE.nivelAliados(), BASE.nivelJugador(), condicion));
        }
        return List.copyOf(escenarios);
    }

    static Ejecucion ejecutar(Escenario escenario, long semilla, boolean trazar)
            throws JuegoException {
        ConsolaMedicion consola = new ConsolaMedicion(trazar);
        ConfiguracionPartida configuracion = new ConfiguracionPartida(
                "Auto", "marine", "procedural", escenario.dificultad(),
                new DimensionesMapa(escenario.filas(), escenario.columnas()), null,
                escenario.aliados(), escenario.condicion(), 1);
        configuracion.setNivelAliados(escenario.nivelAliados());
        configuracion.setNivelJugador(escenario.nivelJugador());
        configuracion.setSeed(semilla);
        Juego juego = FabricaJuego.crear(consola, configuracion);
        MotorPartida motor = new MotorPartida(juego);
        motor.setRandom(new Random(semilla ^ 0x5EEDC0DEL));
        JugadorAutomatico automatico = new JugadorAutomatico();
        int limite = Math.max(500, Math.min(5_000, juego.getPasosMaximos() + 1_000));
        int turnos = 0;
        boolean muerteJugador = false;
        while (!motor.isFinalizada() && turnos < limite) {
            turnos++;
            if (motor.isModoEspectadorDisponible()) {
                consola.accion(turnos, "ESPECTADOR");
                motor.avanzarTurnoEspectador();
            } else {
                String comando = automatico.decidir(motor);
                consola.accion(turnos, comando);
                motor.ejecutarComando(comando);
            }
            muerteJugador |= juego.getJugador().getSalud() <= 0;
        }
        boolean censurada = !motor.isFinalizada();
        boolean victoriaBando = motor.getResultadoBatalla()
                == MotorPartida.ResultadoBatalla.VICTORIA_HUMANA;
        boolean victoriaMision = !censurada && !muerteJugador
                && motor.getEstadoFinal() == SistemaPuntuacion.EstadoFinalPartida.VICTORIA
                && juego.jugadorGano();
        Resultado resultado = new Resultado(escenario.id(), escenario.axis(), escenario.valor(),
                semilla, turnos, victoriaMision, victoriaBando, censurada,
                juego.getAliadosIniciales(), juego.getEnemigos().size());
        return new Ejecucion(resultado, consola.contenido());
    }

    static long mezclarSemilla(long raiz, int escenario, int run) {
        long valor = raiz + 0x9E3779B97F4A7C15L * (escenario + 1L)
                + 0xBF58476D1CE4E5B9L * (run + 1L);
        valor = (valor ^ (valor >>> 30)) * 0xBF58476D1CE4E5B9L;
        valor = (valor ^ (valor >>> 27)) * 0x94D049BB133111EBL;
        return valor ^ (valor >>> 31);
    }

    static double[] wilson(int exitos, int total) {
        if (total <= 0) return new double[] {0.0, 0.0};
        double z = 1.959963984540054;
        double p = exitos / (double) total;
        double z2 = z * z;
        double centro = (p + z2 / (2.0 * total)) / (1.0 + z2 / total);
        double margen = z * Math.sqrt((p * (1.0 - p) + z2 / (4.0 * total)) / total)
                / (1.0 + z2 / total);
        return new double[] {Math.max(0.0, centro - margen), Math.min(1.0, centro + margen)};
    }

    private static void escribirResumen(Path archivo, List<Resumen> resumenes) throws IOException {
        StringBuilder csv = new StringBuilder("scenario,axis,value,runs,mission_wins,mission_pct,"
                + "human_wins,human_pct,censored,mean_turns,wilson_low,wilson_high\n");
        for (Resumen resumen : resumenes) csv.append(resumen.csv()).append('\n');
        Files.writeString(archivo, csv, StandardCharsets.UTF_8);
    }

    private static String informe(Opciones opciones, List<Resumen> resumenes) {
        StringBuilder md = new StringBuilder("# Probabilidades empiricas de una partida autonoma\n\n")
                .append("Resultados de partidas completas ejecutadas por `MotorPartida`. ")
                .append("Cada escenario usa ").append(opciones.runs()).append(" semillas.\n\n")
                .append("| Variable | Valor | Mision | Bando humano | Turnos medios |\n")
                .append("|---|---:|---:|---:|---:|\n");
        for (Resumen resumen : resumenes) {
            md.append('|').append(resumen.escenario().axis()).append('|')
                    .append(resumen.escenario().valor()).append('|')
                    .append(String.format(Locale.ROOT, "%.1f%%", resumen.porcentajeMision()))
                    .append('|')
                    .append(String.format(Locale.ROOT, "%.1f%%", resumen.porcentajeBando()))
                    .append('|')
                    .append(String.format(Locale.ROOT, "%.1f", resumen.turnosMedios()))
                    .append("|\n");
        }
        md.append("\nSemilla raiz: `").append(opciones.semilla()).append("`. ")
                .append("`samples.csv.gz` conserva las muestras y `manifest.sha256` ")
                .append("permite comprobar los artefactos.\n");
        return md.toString();
    }

    private static BufferedWriter gzip(Path archivo) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(
                Files.newOutputStream(archivo, StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING)), StandardCharsets.UTF_8));
    }

    private static void escribirGzip(Path archivo, String contenido) throws IOException {
        try (BufferedWriter salida = gzip(archivo)) {
            salida.write(contenido);
        }
    }

    private static void escribirManifest(Path directorio) throws IOException {
        StringBuilder manifest = new StringBuilder();
        try (var archivos = Files.list(directorio)) {
            for (Path archivo : archivos.filter(Files::isRegularFile).sorted().toList()) {
                if (archivo.getFileName().toString().equals("manifest.sha256")) continue;
                manifest.append(sha256(archivo)).append("  ")
                        .append(archivo.getFileName()).append('\n');
            }
        }
        Files.writeString(directorio.resolve("manifest.sha256"), manifest,
                StandardCharsets.UTF_8);
    }

    private static String sha256(Path archivo) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(Files.readAllBytes(archivo)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    record Opciones(int runs, long semilla, int hilos, Path salida) {
        static Opciones parsear(String[] args) {
            int runs = RUNS_PREDETERMINADOS;
            long seed = SEMILLA_PREDETERMINADA;
            int threads = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
            Path output = Path.of("docs", "runs", "monte-carlo-real");
            for (String arg : args) {
                if (arg.startsWith("--runs=")) runs = Integer.parseInt(arg.substring(7));
                else if (arg.startsWith("--seed=")) seed = Long.parseLong(arg.substring(7));
                else if (arg.startsWith("--threads=")) threads = Integer.parseInt(arg.substring(10));
                else if (arg.startsWith("--output=")) output = Path.of(arg.substring(9));
                else throw new IllegalArgumentException("Opcion desconocida: " + arg);
            }
            if (runs < 1 || runs > 10_000) throw new IllegalArgumentException("runs fuera de rango");
            if (threads < 1 || threads > 64) throw new IllegalArgumentException("threads fuera de rango");
            return new Opciones(runs, seed, threads, output.toAbsolutePath().normalize());
        }
    }

    record Escenario(String id, String axis, String valor, Dificultad dificultad,
            int filas, int columnas, int aliados, int nivelAliados,
            int nivelJugador, CondicionVictoria condicion) {
        Escenario con(String nuevoId, String nuevoAxis, String nuevoValor,
                Dificultad nuevaDificultad, int nuevasFilas, int nuevasColumnas,
                int nuevosAliados, int nuevoNivelAliados, int nuevoNivelJugador,
                CondicionVictoria nuevaCondicion) {
            return new Escenario(nuevoId, nuevoAxis, nuevoValor, nuevaDificultad,
                    nuevasFilas, nuevasColumnas, nuevosAliados, nuevoNivelAliados,
                    nuevoNivelJugador, nuevaCondicion);
        }
    }

    record Resultado(String escenario, String axis, String valor, long semilla,
            int turnos, boolean victoriaMision, boolean victoriaBando,
            boolean censurada, int aliados, int enemigos) {
        static String cabecera() {
            return "scenario,axis,value,seed,turns,mission_win,human_win,censored,allies,enemies";
        }
        String csv() {
            return String.format(Locale.ROOT, "%s,%s,%s,%d,%d,%s,%s,%s,%d,%d",
                    escenario, axis, valor, semilla, turnos, victoriaMision,
                    victoriaBando, censurada, aliados, enemigos);
        }
    }

    record Resumen(Escenario escenario, int runs, int victoriasMision,
            int victoriasBando, int censuradas, double turnosMedios) {
        double porcentajeMision() { return 100.0 * victoriasMision / runs; }
        double porcentajeBando() { return 100.0 * victoriasBando / runs; }
        String csv() {
            double[] intervalo = wilson(victoriasMision, runs);
            return String.format(Locale.ROOT, "%s,%s,%s,%d,%d,%.3f,%d,%.3f,%d,%.3f,%.5f,%.5f",
                    escenario.id(), escenario.axis(), escenario.valor(), runs,
                    victoriasMision, porcentajeMision(), victoriasBando,
                    porcentajeBando(), censuradas, turnosMedios,
                    intervalo[0], intervalo[1]);
        }
    }

    record Ejecucion(Resultado resultado, String traza) { }
    record Estadisticas(long rootSeed, int runsPerScenario, List<Resumen> scenarios) { }

    private static final class ConsolaMedicion implements Consola {
        private final boolean capturar;
        private final StringBuilder contenido = new StringBuilder();

        ConsolaMedicion(boolean capturar) { this.capturar = capturar; }

        @Override
        public void imprimir(String mensaje) {
            if (capturar && mensaje != null) contenido.append(mensaje).append('\n');
        }

        @Override
        public String leer(String descripcion) { return ""; }

        void accion(int turno, String comando) {
            if (capturar) contenido.append("[turno ").append(turno).append("] accion> ")
                    .append(comando).append('\n');
        }

        String contenido() { return contenido.toString(); }
    }
}
