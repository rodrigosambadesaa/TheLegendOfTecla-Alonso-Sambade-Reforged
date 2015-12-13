package comandos;

import Juego.Juego;
import Juego.Personaje.Jugador;
import Juego.Personaje.Personaje;
import Utilidades.ConsolaNormal;
import Utilidades.ProcesaOrden;
import excepciones.ComandoExcepcion;
import interfaces.Comando;

public class ComandoCoger implements Comando {
  private String comando;
  private Juego juego;
  private boolean bSoloJugador;
  ConsolaNormal consola = new ConsolaNormal();

  public ComandoCoger(String comando, Juego juego, boolean bSoloJugador) throws ComandoExcepcion {
    this.comando = ProcesaOrden.comando(comando);
    this.juego = juego;
    this.bSoloJugador = bSoloJugador;
  }

  @Override
  public void ejecutar() throws ComandoExcepcion {
    try {
      for (Personaje personaje : this.juego.getMapa().getPersonajes()) {
        if (!bSoloJugador || personaje instanceof Jugador) {
          personaje.cogerObjeto(
              this.juego.getMapa().getCelda(personaje.getPosicionMapa()), comando);
        }
      }
    } catch (Exception ex) {
      consola.imprimir(ex.toString());
      throw new ComandoExcepcion(ex.toString());
    }
  }
}
