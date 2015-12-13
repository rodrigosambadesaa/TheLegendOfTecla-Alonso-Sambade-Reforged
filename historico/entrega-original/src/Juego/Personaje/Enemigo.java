package Juego.Personaje;

import Juego.Mapa.Mapa;
import Utilidades.MiConsola;
import excepciones.ComandoExcepcion;
import excepciones.ExcepcionFatal;
import excepciones.ExcepcionMover;
import java.awt.Point;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Enemigo extends Personaje {

  public Enemigo(String nombre) {
    super(nombre);
  }

  public Enemigo(String tipo, String nombre, int energia, Mochila mochila) throws ComandoExcepcion {
    super(tipo, nombre, energia, mochila);
  }

  @Override
  public void atacar2(String comandoAtaque, Mapa mapa)
      throws ExcepcionFatal, ComandoExcepcion, ExcepcionMover {
    StringBuilder mensajes = new StringBuilder();
    Jugador jugador = (Jugador) mapa.getJugador();
    String movimientos = "";
    try {

      if (this.armasEquipadas().isEmpty()) {
        mensajes.append("no se puede atacar sin armas=").append(this.nombre);
      }

      Point p2 = null;

      // en caso de que estén en la misma celda creamos un comando 0sur
      if (this.getPosicionMapa() == jugador.getPosicionMapa()) {
        movimientos = "0este";
      } else {
        // creamos el movimiento hacia el jugador
        // misma fila 1 movimiento
        int num;
        if (this.getPosicionMapa().y != jugador.getPosicionMapa().y) {
          if (this.getPosicionMapa().y > jugador.getPosicionMapa().y) {
            num = this.getPosicionMapa().y - jugador.getPosicionMapa().y;
            movimientos += num++ + "oeste";
          } else {
            num = jugador.getPosicionMapa().y - this.getPosicionMapa().y;
            movimientos += num++ + "este";
          }
        }
        // comprobamos la fila
        if (this.getPosicionMapa().x != jugador.getPosicionMapa().x) {
          if (this.getPosicionMapa().x > jugador.getPosicionMapa().x) {
            num = this.getPosicionMapa().x - jugador.getPosicionMapa().x;
            movimientos += num++ + "norte";
          } else {
            num = jugador.getPosicionMapa().x - this.getPosicionMapa().x;
            movimientos += num++ + "sur";
          }
        }
      }

      if (movimientos.matches(".*(norte|sur|este|oeste).+(norte|sur|este|oeste).*")) {
        Pattern p = Pattern.compile("([0-9]+)(norte|sur|este|oeste)([0-9]+)(norte|sur|este|oeste)");
        Matcher m = p.matcher(movimientos);
        String mov1 = "";
        String direc1 = "";
        String mov2 = "";
        String direc2 = "";

        if (m.find()) {
          mov1 = m.group(1);
          direc1 = m.group(2);
          mov2 = m.group(3);
          direc2 = m.group(4);

          if (Integer.parseInt(mov1) > jugador.rangoVision()
              || Integer.parseInt(mov2) > jugador.rangoVision()) {
            mensajes
                .append("jugador (")
                .append(jugador.getNombre())
                .append(") situado en ")
                .append(jugador.getPosicionMapa())
                .append(" fuera de rango de alcance=")
                .append(jugador.rangoVision())
                .append(" de enemigo ")
                .append(this.getNombre())
                .append(", situado en ")
                .append(this.getPosicionMapa())
                .append(", movimiento de ataque=")
                .append(movimientos);
          } else {
            p2 = new Point(this.getPosicionMapa().x, this.getPosicionMapa().y);
            if (direc1.matches("norte")) {
              p2.move(p2.x - Integer.parseInt(mov1), p2.y);
            } else if (direc1.matches("sur")) {
              p2.move(p2.x + Integer.parseInt(mov1), p2.y);
            } else if (direc1.matches("oeste")) {
              p2.move(p2.x, p2.y - Integer.parseInt(mov1));
            } else if (direc1.matches("este")) {
              p2.move(p2.x, p2.y + Integer.parseInt(mov1));
            }
            bHayObstaculos2(mapa, this.getPosicionMapa(), p2, direc1);

            if (direc2.matches("norte")) {
              p2.move(p2.x - Integer.parseInt(mov2), p2.y);
            } else if (direc2.matches("sur")) {
              p2.move(p2.x + Integer.parseInt(mov2), p2.y);
            } else if (direc2.matches("oeste")) {
              p2.move(p2.x, p2.y - Integer.parseInt(mov2));
            } else if (direc2.matches("este")) {
              p2.move(p2.x, p2.y + Integer.parseInt(mov2));
            }
            bHayObstaculos2(mapa, this.getPosicionMapa(), p2, direc2);
          }
        }
      } else if (movimientos.matches(".*(norte|sur|este|oeste)")) {
        Pattern p = Pattern.compile("([0-9]+)(norte|sur|este|oeste)");
        Matcher m = p.matcher(movimientos);
        String mov1 = "";
        String direc1 = "";

        if (m.find()) {
          mov1 = m.group(1);
          direc1 = m.group(2);

          if (Integer.parseInt(mov1) > jugador.rangoVision()) {
            mensajes
                .append("jugador (")
                .append(jugador.getNombre())
                .append(") situado en ")
                .append(this.getPosicionMapa())
                .append(" fuera de rango de alcance=")
                .append(jugador.rangoVision())
                .append(" de enemigo ")
                .append(this.getNombre())
                .append(", situado en ")
                .append(this.getPosicionMapa())
                .append(", movimiento de ataque=")
                .append(movimientos);
          } else {
            p2 = new Point(this.getPosicionMapa().x, this.getPosicionMapa().y);
            if (direc1.matches("norte")) {
              p2.move(p2.x - Integer.parseInt(mov1), p2.y);
            } else if (direc1.matches("sur")) {
              p2.move(p2.x + Integer.parseInt(mov1), p2.y);
            } else if (direc1.matches("oeste")) {
              p2.move(p2.x, p2.y - Integer.parseInt(mov1));
            } else if (direc1.matches("este")) {
              p2.move(p2.x, p2.y + Integer.parseInt(mov1));
            }
            bHayObstaculos2(mapa, this.getPosicionMapa(), p2, direc1);
          }
        }
      }

      if (!mensajes.toString().isEmpty()) {
        c.imprimir(mensajes.toString());
      } else {
        int danho =
            danhoCausado(jugador, this.armasEquipadas(), mapa.getCelda(p2).getPersonajes().size());
        jugador.restaSalud(danho);
        c.imprimir(
            "atacado el jugador ("
                + jugador.nombre
                + ") situado en la celda "
                + jugador.getPosicionMapa()
                + " por el enemigo: "
                + this.getNombre()
                + " situado en "
                + this.getPosicionMapa()
                + ", movimiento: "
                + movimientos
                + ", dano causado: "
                + danho);
        if (jugador.getSalud() < 1) {
          MiConsola.printRojo("juego acabado por muerte del jugador");
          throw new ExcepcionFatal("final por muerte del jugador");
        }
      }
    } catch (ComandoExcepcion ex) {
      c.imprimir(
          ex.toString()
              + " -- atacando "
              + this.nombre
              + " en "
              + this.getPosicionMapa()
              + " a jugador ("
              + jugador.nombre
              + ") en : "
              + jugador.getPosicionMapa()
              + ", movimiento: "
              + movimientos);
    }
  }

  /**
   * los enemigos no tienen rangoVision, devuelven 0, sólo lo tienen los jugadores
   *
   * @return
   */
  @Override
  public int rangoVision() {
    c.imprimir("es un enemigo y no tiene rangoVision, sólo lo tienen los jugadores");

    return 0;
  }
}
