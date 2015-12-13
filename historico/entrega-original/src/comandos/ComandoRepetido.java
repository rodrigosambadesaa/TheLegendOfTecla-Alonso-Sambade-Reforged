package comandos;

import excepciones.ComandoExcepcion;
import interfaces.Comando;

public class ComandoRepetido implements Comando {

  private final Comando cmd;
  private final int veces;

  public ComandoRepetido(Comando cmd, int veces) throws ComandoExcepcion {
    this.cmd = cmd;
    this.veces = veces;
  }

  @Override
  public void ejecutar() throws ComandoExcepcion {
    for (int i = 0; i < this.veces; i++) {
      cmd.ejecutar();
    }
  }
}
