package com.legendoftecla.engine;

import com.legendoftecla.TestFixtures;
import com.legendoftecla.commands.ComandoAtacar;
import com.legendoftecla.commands.CommandContext;
import com.legendoftecla.model.characters.Enemigo;
import com.legendoftecla.model.characters.Mochila;
import com.legendoftecla.model.characters.Sectoid;
import com.legendoftecla.model.world.Juego;
import com.legendoftecla.model.world.Posicion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReglasClasicasTest {
    @Test
    void elNombreSeleccionaUnSoloEnemigo() throws Exception {
        TestFixtures.CapturingConsole consola = TestFixtures.consola();
        Juego juego = TestFixtures.juegoBasico(consola);
        Posicion posicion = juego.getJugador().getPosicion();
        Enemigo primero = agregarEnemigo(juego, "Primero", posicion);
        Enemigo segundo = agregarEnemigo(juego, "Segundo", posicion);
        int saludPrimero = primero.getSalud();
        int saludSegundo = segundo.getSalud();

        new ComandoAtacar(new CommandContext(juego), null, "Primero").ejecutar();

        assertTrue(primero.getSalud() < saludPrimero);
        assertEquals(saludSegundo, segundo.getSalud());
        assertTrue(consola.salida().contains("Atacas a Primero"));
    }

    @Test
    void todosSeleccionaTodosLosEnemigosDeLaCelda() throws Exception {
        Juego juego = TestFixtures.juegoBasico(TestFixtures.consola());
        Posicion posicion = juego.getJugador().getPosicion();
        Enemigo primero = agregarEnemigo(juego, "Primero", posicion);
        Enemigo segundo = agregarEnemigo(juego, "Segundo", posicion);
        int saludPrimero = primero.getSalud();
        int saludSegundo = segundo.getSalud();

        new ComandoAtacar(new CommandContext(juego), null, "todos").ejecutar();

        assertTrue(primero.getSalud() < saludPrimero);
        assertTrue(segundo.getSalud() < saludSegundo);
    }

    private Enemigo agregarEnemigo(Juego juego, String nombre, Posicion posicion) {
        Enemigo enemigo = new Sectoid(nombre, posicion, new Mochila(2, 10), 3);
        juego.agregarEnemigo(enemigo);
        juego.getMapa().getCelda(posicion).agregarEnemigo(enemigo);
        return enemigo;
    }
}
