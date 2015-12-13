package Juego.Personaje;

import excepciones.ComandoExcepcion;

public final class HeavyFloater extends Floater {

  public HeavyFloater(String nombre) {
    super(nombre);
  }

  public HeavyFloater(String tipo, String nombre, int energia, Mochila mochila)
      throws ComandoExcepcion {
    super(tipo, nombre, energia, mochila);
  }
}
