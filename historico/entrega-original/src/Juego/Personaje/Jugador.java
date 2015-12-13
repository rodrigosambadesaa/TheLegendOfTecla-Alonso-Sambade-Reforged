package Juego.Personaje;

import excepciones.ComandoExcepcion;

public class Jugador extends Personaje {

  public Jugador(String nombre) {
    super(nombre);
  }

  public Jugador(String tipo, String nombre, int energia, Mochila mochila) throws ComandoExcepcion {
    super(tipo, nombre, energia, mochila);
  }

  public int getAlcance() {
    return alcance;
  }

  public void setAlcance(int alcance) {
    this.alcance = alcance;
  }
}
