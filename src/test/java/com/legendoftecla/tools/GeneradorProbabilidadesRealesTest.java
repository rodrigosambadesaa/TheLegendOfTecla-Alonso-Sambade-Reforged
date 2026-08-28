package com.legendoftecla.tools;

import com.legendoftecla.audio.GestorSonido;
import com.legendoftecla.constants.CondicionVictoria;
import com.legendoftecla.constants.Dificultad;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneradorProbabilidadesRealesTest {
    @BeforeAll
    static void silenciarAudio() {
        System.setProperty(GestorSonido.PROPIEDAD_DESACTIVADO, "true");
    }

    @Test
    void matrizCubreTodosLosEjesDeSensibilidad() {
        List<GeneradorProbabilidadesReales.Escenario> matriz =
                GeneradorProbabilidadesReales.construirMatriz();

        assertEquals(32, matriz.size());
        assertEquals(32, matriz.stream().map(
                GeneradorProbabilidadesReales.Escenario::id).distinct().count());
        assertEquals(List.of("condicion", "dificultad", "mapa", "nivel_aliados",
                        "nivel_jugador", "poblacion"),
                matriz.stream().map(GeneradorProbabilidadesReales.Escenario::axis)
                        .distinct().sorted().toList());
    }

    @Test
    void mismaSemillaRepiteElResultadoDeLaPartidaReal() throws Exception {
        GeneradorProbabilidadesReales.Escenario escenario =
                new GeneradorProbabilidadesReales.Escenario(
                        "TEST", "test", "10x10", Dificultad.NORMAL,
                        10, 10, 2, 10, 10, CondicionVictoria.SOLO_JUGADOR);

        var primera = GeneradorProbabilidadesReales.ejecutar(escenario, 814L, false).resultado();
        var segunda = GeneradorProbabilidadesReales.ejecutar(escenario, 814L, false).resultado();

        assertEquals(primera, segunda);
    }

    @Test
    void intervaloWilsonContieneLaProporcionObservada() {
        double[] intervalo = GeneradorProbabilidadesReales.wilson(50, 100);
        assertTrue(intervalo[0] < 0.5);
        assertTrue(intervalo[1] > 0.5);
        assertTrue(intervalo[0] > 0.39);
        assertTrue(intervalo[1] < 0.61);
        assertEquals(0.0, GeneradorProbabilidadesReales.wilson(0, 0)[0]);
    }

    @Test
    void opcionesAceptanSemillaHilosRunsYSalida() {
        var opciones = GeneradorProbabilidadesReales.Opciones.parsear(new String[] {
                "--runs=7", "--seed=99", "--threads=2", "--output=target/probabilidades"
        });
        assertEquals(7, opciones.runs());
        assertEquals(99L, opciones.semilla());
        assertEquals(2, opciones.hilos());
        assertEquals(Path.of("target/probabilidades").toAbsolutePath().normalize(),
                opciones.salida());
        assertThrows(IllegalArgumentException.class,
                () -> GeneradorProbabilidadesReales.Opciones.parsear(
                        new String[] {"--threads=0"}));
    }
}
