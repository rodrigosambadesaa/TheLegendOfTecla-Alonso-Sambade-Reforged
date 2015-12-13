package comandos;

import Juego.Juego;
import Juego.Personaje.Jugador;
import Juego.Personaje.Personaje;
import Utilidades.ConsolaNormal;
import excepciones.ComandoExcepcion;
import interfaces.Comando;

public class ComandoInventario implements Comando {

  private String comando;
  private Juego juego;
  private boolean bSoloJugador;
  ConsolaNormal consola = new ConsolaNormal();

  public ComandoInventario(String comando, Juego juego, boolean bSoloJugador)
      throws ComandoExcepcion {
    this.comando = comando;
    this.juego = juego;
    this.bSoloJugador = bSoloJugador;
  }

  @Override
  public void ejecutar() throws ComandoExcepcion {
    try {
      for (Personaje personaje : this.juego.getMapa().getPersonajes()) {
        if (!bSoloJugador || personaje instanceof Jugador) {
          consola.imprimir(personaje.getMochila().inventario());
        }
      }
    } catch (Exception ex) {
      consola.imprimir(ex.toString());
      throw new ComandoExcepcion(ex.toString());
    }
  }
}
