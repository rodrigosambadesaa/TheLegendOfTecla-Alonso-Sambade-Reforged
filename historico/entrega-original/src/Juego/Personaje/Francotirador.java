package Juego.Personaje;

import Utilidades.MiConsola;
import excepciones.ComandoExcepcion;
import java.util.List;

public final class Francotirador extends Jugador {

  public Francotirador(String nombre) {
    super(nombre);
  }

  public Francotirador(String tipo, String nombre, int energia, Mochila mochila)
      throws ComandoExcepcion {
    super(tipo, nombre, energia, mochila);
  }

  /**
   * los francotiradores hacen el (casillas de separación)^(1.2) más de daño a larga distancia
   *
   * @param enemigo
   * @param armas
   * @param persAtacables
   * @return
   */
  @Override
  protected int danhoCausado(Personaje enemigo, List<Arma> armas, int persAtacables) {
    int danho = super.danhoCausado(enemigo, armas, persAtacables);
    int numCeldas = this.celdasSeparacion(enemigo);
    if (numCeldas > 0) {
      danho = (int) (danho * (Math.pow(numCeldas, 1.2)));
      MiConsola.printAmarillo("danho causado por Francotirador: " + danho);
    }
    return danho;
  }
}
