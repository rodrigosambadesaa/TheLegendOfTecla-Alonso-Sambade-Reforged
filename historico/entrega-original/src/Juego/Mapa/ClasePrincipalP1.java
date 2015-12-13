package Juego.Mapa;

import Juego.Juego;
import Juego.Personaje.Francotirador;
import Juego.Personaje.Jugador;
import Juego.Personaje.Marine;
import Juego.Personaje.Mochila;
import Juego.Personaje.Personaje;
import Juego.Personaje.Zapador;
import Utilidades.ConsolaNormal;
import excepciones.ExcepcionFatal;
import interfaces.CargadorJuego;
import java.awt.Point;
import juego.CargadorJuegoDeFicheros;
import juego.CargadorJuegoPorDefecto;

/**
 * @author Rodrigo Sambade Saá y Miguel Alonso Castro
 */
public class ClasePrincipalP1 {

  public static void main(String[] args) {
    ConsolaNormal consola = new ConsolaNormal();
    try {
      // *****************************************************************
      Personaje jugador = null;
      Juego juego = null;
      String orde = "";
      // cargamos el mapa con toda la informacion, personajes y objetos
      orde = consola.leer("cargar mapa de ficheros(s|n):");
      CargadorJuego cargador = null;
      if ("s".equalsIgnoreCase(orde)) {
        cargador = new CargadorJuegoDeFicheros();
        juego = cargador.cargarJuego();
        jugador = juego.getJugador();
        orde = consola.leer("Nombre personaje:");
        jugador.setNombre(orde);

        Jugador jug = null;
        while (true) {
          String tipo_pers = consola.leer("Tipo personaje (zapador|francotirador|marine)(z,f,m):");
          if (tipo_pers.equalsIgnoreCase("zapador") || tipo_pers.equalsIgnoreCase("z")) {
            jug =
                new Zapador(
                    "zapador", jugador.getNombre(), jugador.getEnergia(), jugador.getMochila());
          } else if (tipo_pers.equalsIgnoreCase("francotirador")
              || tipo_pers.equalsIgnoreCase("f")) {
            jug =
                new Francotirador(
                    "francotirador",
                    jugador.getNombre(),
                    jugador.getEnergia(),
                    jugador.getMochila());
          } else if (tipo_pers.equalsIgnoreCase("marine") || tipo_pers.equalsIgnoreCase("m")) {
            jug =
                new Marine(
                    "Marine", jugador.getNombre(), jugador.getEnergia(), jugador.getMochila());
          }
          if (jug != null) {
            break;
          }
        }
        jug.setSalud(jugador.getEnergia());
        jug.setPosicionMapa(jugador.getPosicionMapa());
        jugador = jug;
      } else {
        cargador = new CargadorJuegoPorDefecto();
        orde = consola.leer("Nombre personaje:");
        while (true) {
          String tipo_pers = consola.leer("Tipo personaje (zapador|francotirador|marine)(z,f,m):");
          Mochila mochila =
              new Mochila("mochila", "mochila_jugador", "descripcion mochila jugador", 5, 30);
          if (tipo_pers.equalsIgnoreCase("zapador") || tipo_pers.equalsIgnoreCase("z")) {
            jugador = new Zapador("zapador", orde, 100, mochila);
          } else if (tipo_pers.equalsIgnoreCase("francotirador")
              || tipo_pers.equalsIgnoreCase("f")) {
            jugador = new Francotirador("francotirador", orde, 100, mochila);
          } else if (tipo_pers.equalsIgnoreCase("marine") || tipo_pers.equalsIgnoreCase("m")) {
            jugador = new Marine("marine", orde, 100, mochila);
          }
          if (jugador != null) {
            break;
          }
        }
        jugador.setSalud(100);
        juego = cargador.cargarJuego();
        juego.setJugador(jugador);
        juego.getMapa().setJugador(jugador);
        juego.getMapa().setPersonaje(jugador);
        Point p = new Point(0, 0);
        jugador.setPosicionMapa(p);
      }

      jugador = juego.getJugador();

      while (jugador.getSalud() > 0) {
        try {
          consola.imprimir("**********************************************");
          orde = consola.leer("Indica comando:");
          if (orde.equals("fin")) {
            break;
          } else {
            juego.jugada(orde);
          }
        } catch (ExcepcionFatal ex) {
          consola.imprimir("muerte por agotamiento de salud: " + ex.toString());
          orde = consola.leer("desea continuar: s/n");
          if (orde.equals("n")) {
            break;
          } else {
            jugador.setSalud(100);
          }
        } catch (Exception ex) {
          consola.imprimir(ex.toString());
        }
      }

    } catch (Exception e) {
      consola.imprimir("ERROR:" + e.toString());
    }
  }
}
