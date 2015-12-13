/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
// autores: Miguel Alonso Castro, Rodrigo Sambade Saa
package Utilidades;

import Juego.Mapa.Celda;
import Juego.Mapa.Mapa;
import Juego.Personaje.Arma;
import Juego.Personaje.Armadura;
import Juego.Personaje.Binoculares;
import Juego.Personaje.Botiquin;
import Juego.Personaje.Floater;
import Juego.Personaje.Francotirador;
import Juego.Personaje.HeavyFloater;
import Juego.Personaje.Jugador;
import Juego.Personaje.Marine;
import Juego.Personaje.Mochila;
import Juego.Personaje.Objeto;
import Juego.Personaje.Personaje;
import Juego.Personaje.Sectoid;
import Juego.Personaje.Zapador;
import excepciones.ComandoExcepcion;
import excepciones.ExcepcionMover;
import excepciones.ExcepcionPers;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Utilidades {

  public static void leerDatosMapa(String rutaFichero, Mapa mapa) {
    try {
      Scanner scan = new Scanner(new File(rutaFichero));
      String linea;
      String partes[];
      HashMap<Point, Celda> celdas = new HashMap<>();
      int xMayor = 0;
      int yMayor = 0;
      while (scan.hasNextLine()) {
        linea = scan.nextLine();
        String coordenadas[] = linea.split(",");
        if (coordenadas.length == 2) {
          // conversion de string a punto
          Integer x;
          Integer y;
          x = Integer.parseInt(coordenadas[0]);
          y = Integer.parseInt(coordenadas[1]);
          if (x > xMayor) {
            xMayor = x;
          }
          if (y > yMayor) {
            yMayor = y;
          }
          Point punto = new Point(x, y);
          Celda celda = new Celda("camino", new ArrayList(), true);
          celda.setPosicionMapa(punto);
          celdas.put(punto, celda);
          // System.out.println(celda.toString());
        }
      }
      mapa.setCeldas(celdas);
      for (int x = 0; x <= xMayor; x++) {
        for (int i = 0; i <= yMayor; i++) {
          Point punto = new Point(x, i);
          // si no hay celda la creamos como no transitable
          if (mapa.getCelda(punto) == null) {
            Celda celda = new Celda("obstaculo", new ArrayList(), false);
            celda.setPosicionMapa(punto);
            celdas.put(punto, celda);
          }
        }
      }
      mapa.setMapaTamHorizonal(yMayor + 1);
      mapa.setMapaTamVertical(xMayor + 1);
      mapa.setCeldas(celdas);

    } catch (Exception ex) {
      MiConsola.printRojo("error en leerDatosMapa, " + ex.toString());
    }
  }

  public static void leerDatosPersonajes(String rutaFichero, Mapa mapa)
      throws FileNotFoundException, ExcepcionPers, ExcepcionMover, ComandoExcepcion {
    Scanner scan = new Scanner(new File(rutaFichero));
    String linea;
    String pa[];
    while (scan.hasNextLine()) {
      linea = scan.nextLine();
      if (!linea.startsWith("#")) {
        pa = linea.split(";");
        if (pa.length > 2) {
          Personaje personaje = null;
          if ("jugador".equalsIgnoreCase(pa[1])) {
            int aleatorio = Utiles.aleatorio(1, 4);
            if (aleatorio == 1) {
              personaje = new Zapador(pa[1], pa[2], Integer.parseInt(pa[4]), null);
            } else if (aleatorio == 2) {
              personaje = new Francotirador(pa[1], pa[2], Integer.parseInt(pa[4]), null);
            } else if (aleatorio == 3) {
              personaje = new Marine(pa[1], pa[2], Integer.parseInt(pa[4]), null);
            }
          } else if ("enemigo".equalsIgnoreCase(pa[1])) {
            if (pa[2].contains("sectoid")) {
              personaje = new Sectoid(pa[1], pa[2], Integer.parseInt(pa[4]), null);
            } else if (pa[2].contains("heavy_floater")) {
              personaje = new HeavyFloater(pa[1], pa[2], Integer.parseInt(pa[4]), null);
            } else if (pa[2].contains("floater")) {
              personaje = new Floater(pa[1], pa[2], Integer.parseInt(pa[4]), null);
            }
          }

          personaje.setSalud(Integer.parseInt(pa[3]));
          String coordenadas[] = pa[0].split(",");
          // conversion de string a punto
          Integer x;
          Integer y;
          x = Integer.parseInt(coordenadas[0]);
          y = Integer.parseInt(coordenadas[1]);
          Point p = new Point(x, y);
          mapa.getCelda(p).setPersonaje(personaje);
          personaje.setPosicionMapa(p);
          personaje.setPosicion(p);

          personaje.setMochila(
              new Mochila(
                  "mochila",
                  "mochila_" + personaje.getNombre(),
                  "la mochila de " + personaje.getNombre(),
                  5,
                  100));
          personaje.cogerObjetoAutomaticoEnemigo(mapa.getCelda(p));
          MiConsola.printAmarillo("personaje: " + personaje.getNombre());
          MiConsola.printAmarillo("equipacion: " + personaje.getEquipacion().toString());
          MiConsola.printAmarillo("armasEquipadas: " + personaje.armasEquipadas());
          MiConsola.printAmarillo("armaduraEquipada: " + personaje.armaduraEquipada());

          mapa.setPersonaje(personaje);
          if (personaje instanceof Jugador) {
            mapa.setJugador(personaje);
          }
        }
      }
    }
  }

  public static ArrayList<Objeto> leerDatosObjetos(String rutaFichero, Mapa mapa)
      throws FileNotFoundException {
    ArrayList<Objeto> lista = new ArrayList<>();
    Scanner scan = new Scanner(new File(rutaFichero));
    String linea;
    String pa[];
    while (scan.hasNextLine()) {
      linea = scan.nextLine();
      // solo leemos lineas no comentadas
      if (!linea.startsWith("#")) {
        pa = linea.split(";");
        if (pa.length > 2) {
          String tipo = pa[2];
          String coordenadas[] = pa[0].split(",");
          int x = Integer.parseInt(coordenadas[0]);
          int y = Integer.parseInt(coordenadas[1]);
          Point p = new Point(x, y);
          // Objeto obj = new Objeto(pa[2],pa[3],pa[4]);
          if (tipo.equalsIgnoreCase("mochila")) {
            // Mochila mochila = (Mochila)obj;
            Mochila mochila =
                new Mochila(
                    pa[2], pa[3], pa[4], Integer.parseInt(pa[5]), Double.parseDouble(pa[6]));
            mapa.getCelda(p).setObjeto(mochila);
          } else if (tipo.equalsIgnoreCase("binoculares")) {
            Binoculares binoculares =
                new Binoculares(
                    pa[2], pa[3], pa[4], Double.parseDouble(pa[6]), Integer.parseInt(pa[5]));
            mapa.getCelda(p).setObjeto(binoculares);
          } else if (tipo.equalsIgnoreCase("botiquin")) {
            Botiquin botiquin =
                new Botiquin(
                    pa[2], pa[3], pa[4], Double.parseDouble(pa[6]), Integer.parseInt(pa[5]));
            mapa.getCelda(p).setObjeto(botiquin);
          } else if (tipo.equalsIgnoreCase("arma")) {
            Arma arma =
                new Arma(
                    pa[2],
                    pa[3],
                    pa[4],
                    Integer.parseInt(pa[5]),
                    Integer.parseInt(pa[7]),
                    Double.parseDouble(pa[8]));
            arma.setPortador(pa[1]);
            arma.setAlcance(Integer.parseInt(pa[6]));
            mapa.getCelda(p).setObjeto(arma);
          } else if (tipo.equalsIgnoreCase("armadura")) {
            Armadura armadura =
                new Armadura(
                    pa[2],
                    pa[3],
                    pa[4],
                    Double.parseDouble(pa[8]),
                    Integer.parseInt(pa[5]),
                    Integer.parseInt(pa[6]),
                    Integer.parseInt(pa[7]));
            armadura.setPortador(pa[1]);
            mapa.getCelda(p).setObjeto(armadura);
          }
        }
      }
    }
    return lista;
  }

  public static ArrayList<Objeto> leerComandos(String rutaFichero, Personaje pers, Mapa mapa)
      throws FileNotFoundException, ExcepcionMover, ComandoExcepcion {
    ArrayList<Objeto> lista = new ArrayList<>();
    rutaFichero += "comandos.txt";
    Scanner scan = new Scanner(new File(rutaFichero));
    String linea;
    String movimento = "";
    while (scan.hasNextLine()) {
      linea = scan.nextLine();
      // os comentarios non os procesamos
      if (!linea.startsWith("#")) {
        if (linea.contains("mover")) {
          movimento = linea.substring(6);
          pers.mover(mapa, movimento);
          System.out.println("posicion actual: " + pers.getPosicion());
        }
      }
    }

    return lista;
  }
}
