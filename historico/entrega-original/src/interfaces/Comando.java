package interfaces;

import excepciones.ComandoExcepcion;

public interface Comando {
  public abstract void ejecutar() throws ComandoExcepcion;
}
