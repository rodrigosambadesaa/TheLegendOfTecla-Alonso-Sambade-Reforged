package com.legendoftecla.engine;

import com.legendoftecla.TestFixtures;
import com.legendoftecla.constants.CondicionVictoria;
import com.legendoftecla.model.characters.Aliado;
import com.legendoftecla.model.characters.Marine;
import com.legendoftecla.model.characters.Mochila;
import com.legendoftecla.model.world.Celda;
import com.legendoftecla.model.world.Juego;
import com.legendoftecla.model.world.Mapa;
import com.legendoftecla.model.world.Posicion;
import com.legendoftecla.model.world.SistemaPuntuacion;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Regresiones de comportamiento reimplementado sobre el motor propio de Reforged. */
class ParidadMotorReforgedTest {
    @Test
    void elRandomInyectadoSeComparteConLosComandos() {
        Juego juego = TestFixtures.juegoBasico(TestFixtures.consola());
        MotorPartida motor = new MotorPartida(juego);
        Random reproducible = new Random(20260828L);

        motor.setRandom(reproducible);

        assertSame(reproducible, motor.getRandom());
        assertSame(reproducible, motor.getContexto().getRandom());
    }

    @Test
    void perderUnAliadoHaceImposibleLaEvacuacionCompleta() {
        Juego juego = juegoConUnAliado();
        juego.setCondicionVictoria(CondicionVictoria.JUGADOR_Y_ALIADOS);
        juego.getAliados().get(0).setSalud(0);

        MotorPartida motor = new MotorPartida(juego);

        assertTrue(motor.isFinalizada());
        assertEquals(SistemaPuntuacion.EstadoFinalPartida.DERROTA_MISION,
                motor.getEstadoFinal());
        assertEquals(MotorPartida.ResultadoBatalla.VICTORIA_ENEMIGA,
                motor.getResultadoBatalla());
    }

    private Juego juegoConUnAliado() {
        Mapa mapa = new Mapa("Paridad", "Evacuacion completa", 5, 5,
                new Posicion(0, 0), new Posicion(4, 4));
        for (int fila = 0; fila < 5; fila++) {
            for (int columna = 0; columna < 5; columna++) {
                mapa.setCelda(fila, columna, new Celda("Suelo", true));
            }
        }
        Marine jugador = new Marine("Jugador", mapa.getInicio(), new Mochila(5, 30), 3);
        Juego juego = new Juego(TestFixtures.consola(), mapa, jugador, 100);
        Posicion posicionAliado = new Posicion(2, 2);
        Aliado aliado = new Aliado("Aliado", posicionAliado, new Mochila(8, 40), 3);
        mapa.getCelda(posicionAliado).agregarAliado(aliado);
        juego.agregarAliado(aliado);
        return juego;
    }
}
