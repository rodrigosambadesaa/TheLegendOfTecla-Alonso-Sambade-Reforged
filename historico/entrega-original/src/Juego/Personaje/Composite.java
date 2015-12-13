package Juego.Personaje;

import excepciones.ComandoExcepcion;
import interfaces.Comando;

// public class Composite implements ComandoAtacar, ComandoCoger, ComandoCompuesto, ComandoMirar,
// ComandoMover, ComandoRepetir, ComandoTirar {
public class Composite implements Comando {

  @Override
  public void ejecutar() throws ComandoExcepcion {
    throw new UnsupportedOperationException(
        "Not supported yet."); // To change body of generated methods, choose Tools | Templates.
  }
}
