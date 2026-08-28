package com.legendoftecla.tools;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.legendoftecla.validation.Limites;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneradorRunsAutomaticosTest {
    @TempDir
    Path temporal;

    @Test
    void generaArtefactosReproduciblesYResumenCoherente() throws IOException {
        Path salida = temporal.resolve("runs");
        GeneradorRunsAutomaticos.Resumen resumen = GeneradorRunsAutomaticos.generar(
                salida, 8, 77L, Instant.parse("2026-08-14T00:00:00Z"));

        assertEquals(8, resumen.total());
        assertEquals(8, resumen.victoriasHumanas() + resumen.victoriasEnemigas());
        assertEquals(GeneradorRunsAutomaticos.ALIADOS_MINIMOS, resumen.aliadosMinimos());
        assertEquals(Limites.ALIADOS_MAXIMOS, resumen.aliadosMaximos());
        assertTrue(resumen.bytes() > 0);
        assertTrue(Files.exists(salida.resolve("index.csv")));
        assertTrue(Files.exists(salida.resolve("statistics.json")));
        assertTrue(Files.exists(salida.resolve("manifest.sha256")));
        assertTrue(Files.exists(salida.resolve("run-0001-actions.log.gz")));
    }

    @Test
    void cubreLosLimitesDePoblacionYAlternaCondicion() throws IOException {
        Path salida = temporal.resolve("limites");
        GeneradorRunsAutomaticos.generar(
                salida, 2, 91L, Instant.parse("2026-08-14T00:00:00Z"));

        JsonObject primero = JsonParser.parseString(Files.readString(
                salida.resolve("run-0001.json"))).getAsJsonObject();
        JsonObject ultimo = JsonParser.parseString(Files.readString(
                salida.resolve("run-0002.json"))).getAsJsonObject();
        assertEquals(100, primero.get("aliados").getAsInt());
        assertEquals(Limites.ALIADOS_MAXIMOS, ultimo.get("aliados").getAsInt());
        assertEquals("SOLO_JUGADOR", primero.get("condicion").getAsString());
        assertEquals("JUGADOR_Y_ALIADOS", ultimo.get("condicion").getAsString());
    }

    @Test
    void mismaSemillaProduceElMismoRun() {
        assertEquals(GeneradorRunsAutomaticos.simular(3, 16, 123L),
                GeneradorRunsAutomaticos.simular(3, 16, 123L));
        assertEquals(GeneradorRunsAutomaticos.mezclarSemilla(123L, 3),
                GeneradorRunsAutomaticos.mezclarSemilla(123L, 3));
    }

    @Test
    void rechazaCantidadFueraDeRango() {
        assertThrows(IllegalArgumentException.class, () -> GeneradorRunsAutomaticos.generar(
                temporal.resolve("invalido"), 1, 1L, Instant.EPOCH));
    }
}
