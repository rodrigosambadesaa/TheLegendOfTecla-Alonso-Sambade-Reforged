package Juego.Personaje;

import excepciones.ComandoExcepcion;

public final class LigthFloater extends Floater {

  public LigthFloater(String nombre) {
    super(nombre);
  }

  public LigthFloater(String tipo, String nombre, int energia, Mochila mochila)
      throws ComandoExcepcion {
    super(tipo, nombre, energia, mochila);
  }
}
