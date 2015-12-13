package Juego.Personaje;

import excepciones.ComandoExcepcion;

public class Floater extends Enemigo {

  public Floater(String nombre) {
    super(nombre);
  }

  public Floater(String tipo, String nombre, int energia, Mochila mochila) throws ComandoExcepcion {
    super(tipo, nombre, energia, mochila);
  }
}
