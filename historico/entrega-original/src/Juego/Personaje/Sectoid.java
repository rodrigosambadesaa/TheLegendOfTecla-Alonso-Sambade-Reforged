package Juego.Personaje;

import excepciones.ComandoExcepcion;

public final class Sectoid extends Enemigo {

  public Sectoid(String nombre) {
    super(nombre);
  }

  public Sectoid(String tipo, String nombre, int energia, Mochila mochila) throws ComandoExcepcion {
    super(tipo, nombre, energia, mochila);
  }
}
