package Juego.Personaje;

import Utilidades.MiConsola;
import excepciones.ComandoExcepcion;
import java.util.List;

public final class Zapador extends Jugador {

  public Zapador(String nombre) {
    super(nombre);
  }

  public Zapador(String tipo, String nombre, int energia, Mochila mochila) throws ComandoExcepcion {
    super(tipo, nombre, energia, mochila);
  }

  /**
   * Los marines hacen el doble de daño a corta distanciael zapador hace el doble de daño a larga
   * distancia pero no puede atacar a corta distancia (es decir, 1 casilla) de distancia) al tener
   * mala puntería, sólo harán un 5% de daño cuando ataquen con un arma a más de 2 casillas de
   * distancia
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
    if (numCeldas > 2) {
      danho = danho - (int) (danho * 0.5);
    }
    MiConsola.printAmarillo("danho causado por Zapador: " + danho);
    return danho;
  }
}
