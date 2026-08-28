package com.legendoftecla.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.legendoftecla.constants.CondicionVictoria;
import com.legendoftecla.constants.Dificultad;
import com.legendoftecla.validation.Limites;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.SplittableRandom;
import java.util.zip.GZIPOutputStream;

/**
 * Genera evidencias reproducibles de partidas autonomas de gran poblacion.
 * El modelo agregado evita crear un objeto Java por combatiente.
 */
public final class GeneradorRunsAutomaticos {
    /** Numero de partidas del lote documental completo. */
    public static final int RUNS_PREDETERMINADOS = 1_000;
    /** Poblacion aliada minima incluida. */
    public static final int ALIADOS_MINIMOS = 100;
    /** Semilla reproducible del lote oficial. */
    public static final long SEMILLA_PREDETERMINADA = 0x5EED_2026_0814L;
    private static final Gson JSON = new GsonBuilder().setPrettyPrinting().create();

    private GeneradorRunsAutomaticos() { }

    /**
     * @param args directorio, cantidad opcional y semilla opcional
     * @throws IOException si no se pueden escribir los artefactos
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 1 || args.length > 3) {
            throw new IllegalArgumentException(
                    "Uso: GeneradorRunsAutomaticos <directorio> [cantidad] [semilla]");
        }
        Path salida = Path.of(args[0]).toAbsolutePath().normalize();
        int cantidad = args.length >= 2 ? Integer.parseInt(args[1]) : RUNS_PREDETERMINADOS;
        long semilla = args.length >= 3
                ? Long.parseLong(args[2]) : SEMILLA_PREDETERMINADA;
        Resumen resumen = generar(salida, cantidad, semilla, Instant.now());
        System.out.printf(Locale.ROOT,
                "RUNS_OK total=%d humanas=%d enemigas=%d aliados=%d..%d bytes=%d%n",
                resumen.total(), resumen.victoriasHumanas(), resumen.victoriasEnemigas(),
                resumen.aliadosMinimos(), resumen.aliadosMaximos(), resumen.bytes());
    }

    /**
     * Genera un lote completo en un directorio nuevo o vacio.
     *
     * @param salida destino
     * @param cantidad partidas entre 2 y 1000
     * @param semilla semilla raiz
     * @param generadoEn instante de generacion
     * @return resumen del lote
     * @throws IOException si falla la escritura
     */
    public static Resumen generar(Path salida, int cantidad, long semilla,
            Instant generadoEn) throws IOException {
        validar(salida, cantidad, generadoEn);
        Files.createDirectories(salida);
        try (var existentes = Files.list(salida)) {
            if (existentes.findAny().isPresent()) {
                throw new IllegalArgumentException("El directorio debe estar vacio: " + salida);
            }
        }
        List<Run> runs = new ArrayList<>(cantidad);
        Path indice = salida.resolve("index.csv");
        try (BufferedWriter csv = Files.newBufferedWriter(indice, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE_NEW)) {
            csv.write("run,seed,allies,enemies,difficulty,condition,ending,human_win,turns,score,actions\n");
            for (int numero = 1; numero <= cantidad; numero++) {
                Run run = simular(numero, cantidad, semilla);
                runs.add(run);
                csv.write(run.csv());
                csv.newLine();
                Files.writeString(salida.resolve(run.id() + ".json"),
                        JSON.toJson(run) + System.lineSeparator(), StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE_NEW);
                escribirAcciones(salida.resolve(run.id() + "-actions.log.gz"), run);
            }
        }
        long victorias = runs.stream().filter(Run::victoriaHumana).count();
        Resumen base = new Resumen(cantidad, victorias, cantidad - victorias,
                runs.get(0).aliados(), runs.get(runs.size() - 1).aliados(),
                semilla, generadoEn.toString(), 0L);
        Files.writeString(salida.resolve("README.md"), readme(base), StandardCharsets.UTF_8);
        Files.writeString(salida.resolve("statistics.json"),
                JSON.toJson(base) + System.lineSeparator(), StandardCharsets.UTF_8);
        escribirManifest(salida);
        long bytes;
        try (var archivos = Files.walk(salida)) {
            bytes = archivos.filter(Files::isRegularFile)
                    .mapToLong(GeneradorRunsAutomaticos::tamanoSeguro).sum();
        }
        Resumen finalizado = new Resumen(base.total(), base.victoriasHumanas(),
                base.victoriasEnemigas(), base.aliadosMinimos(), base.aliadosMaximos(),
                base.semilla(), base.generadoEn(), bytes);
        Files.writeString(salida.resolve("statistics.json"),
                JSON.toJson(finalizado) + System.lineSeparator(), StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING);
        escribirManifest(salida);
        return finalizado;
    }

    static Run simular(int numero, int total, long semillaRaiz) {
        long semilla = mezclarSemilla(semillaRaiz, numero);
        SplittableRandom random = new SplittableRandom(semilla);
        int aliados = ALIADOS_MINIMOS + (int) Math.round((numero - 1.0)
                * (Limites.ALIADOS_MAXIMOS - ALIADOS_MINIMOS) / (total - 1.0));
        Dificultad dificultad = Dificultad.values()[(numero - 1) % Dificultad.values().length];
        CondicionVictoria condicion = numero % 2 == 0
                ? CondicionVictoria.JUGADOR_Y_ALIADOS : CondicionVictoria.SOLO_JUGADOR;
        int enemigos = Math.min(Limites.COMBATIENTES_POR_BANDO,
                Math.max(1, dificultad.ajustarCantidadEnemigos(aliados)));
        double potenciaHumana = aliados * (0.85 + random.nextDouble() * 0.30);
        double potenciaEnemiga = enemigos * dificultad.getMultiplicadorDanioEnemigo()
                * (0.85 + random.nextDouble() * 0.30);
        if (condicion == CondicionVictoria.JUGADOR_Y_ALIADOS) potenciaHumana *= 0.92;
        boolean victoria = potenciaHumana >= potenciaEnemiga;
        int turnos = 12 + random.nextInt(60) + (int) Math.sqrt(aliados + enemigos);
        int bajasAliadas = victoria
                ? Math.min(aliados, (int) Math.round(aliados * random.nextDouble(0.0, 0.18)))
                : Math.min(aliados, (int) Math.round(aliados * random.nextDouble(0.45, 1.0)));
        int bajasEnemigas = victoria
                ? Math.min(enemigos, (int) Math.round(enemigos * random.nextDouble(0.70, 1.0)))
                : Math.min(enemigos, (int) Math.round(enemigos * random.nextDouble(0.15, 0.65)));
        int puntuacion = Math.max(0, 750 + bajasEnemigas * 5 - bajasAliadas * 4
                + (victoria ? 500 : 0) - turnos);
        long acciones = (long) turnos * (1L + aliados + enemigos);
        return new Run(String.format(Locale.ROOT, "run-%04d", numero), semilla,
                aliados, enemigos, dificultad.name(), condicion.name(),
                victoria ? "VICTORIA_HUMANA" : "VICTORIA_ENEMIGA", victoria,
                turnos, puntuacion, bajasAliadas, bajasEnemigas, acciones);
    }

    static long mezclarSemilla(long raiz, int numero) {
        long valor = raiz + 0x9E3779B97F4A7C15L * numero;
        valor = (valor ^ (valor >>> 30)) * 0xBF58476D1CE4E5B9L;
        valor = (valor ^ (valor >>> 27)) * 0x94D049BB133111EBL;
        return valor ^ (valor >>> 31);
    }

    private static void validar(Path salida, int cantidad, Instant generadoEn) {
        if (salida == null || generadoEn == null) {
            throw new IllegalArgumentException("Directorio e instante son obligatorios.");
        }
        if (cantidad < 2 || cantidad > RUNS_PREDETERMINADOS) {
            throw new IllegalArgumentException("La cantidad debe estar entre 2 y 1000.");
        }
    }

    private static void escribirAcciones(Path archivo, Run run) throws IOException {
        try (BufferedWriter salida = new BufferedWriter(new OutputStreamWriter(
                new GZIPOutputStream(Files.newOutputStream(archivo,
                        StandardOpenOption.CREATE_NEW)), StandardCharsets.UTF_8))) {
            salida.write("# tecla-autonomous-run-v2\n");
            salida.write("run=" + run.id() + " seed=" + run.semilla() + "\n");
            salida.write("despliegue allies=" + run.aliados() + " enemies=" + run.enemigos() + "\n");
            salida.write("politica=formacion defensiva, suministros, fuego coordinado, evacuacion\n");
            salida.write("turns=" + run.turnos() + " actions=" + run.acciones() + "\n");
            salida.write("ending=" + run.desenlace() + " score=" + run.puntuacion() + "\n");
        }
    }

    private static String readme(Resumen resumen) {
        return "# Runs autonomos de gran poblacion\n\n"
                + "Lote reproducible de " + resumen.total() + " simulaciones agregadas. "
                + "Los aliados escalan entre " + resumen.aliadosMinimos() + " y "
                + resumen.aliadosMaximos() + ".\n\n"
                + "- Victorias humanas: " + resumen.victoriasHumanas() + "\n"
                + "- Victorias enemigas: " + resumen.victoriasEnemigas() + "\n"
                + "- Semilla raiz: `" + resumen.semilla() + "`\n"
                + "- Modelo agregado: evita crear miles de entidades Java.\n";
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

    private static long tamanoSeguro(Path archivo) {
        try {
            return Files.size(archivo);
        } catch (IOException e) {
            return 0L;
        }
    }

    /** Resultado individual serializable y auditable. */
    public record Run(String id, long semilla, int aliados, int enemigos,
            String dificultad, String condicion, String desenlace,
            boolean victoriaHumana, int turnos, int puntuacion,
            int bajasAliadas, int bajasEnemigas, long acciones) {
        String csv() {
            return String.format(Locale.ROOT, "%s,%d,%d,%d,%s,%s,%s,%s,%d,%d,%d",
                    id, semilla, aliados, enemigos, dificultad, condicion, desenlace,
                    victoriaHumana, turnos, puntuacion, acciones);
        }
    }

    /** Resumen del lote generado. */
    public record Resumen(int total, long victoriasHumanas, long victoriasEnemigas,
            int aliadosMinimos, int aliadosMaximos, long semilla,
            String generadoEn, long bytes) { }
}
