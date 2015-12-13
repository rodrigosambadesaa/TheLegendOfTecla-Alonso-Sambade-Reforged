package comandos;

import Juego.Juego;
import Juego.Personaje.Jugador;
import Juego.Personaje.Personaje;
import Utilidades.ConsolaNormal;
import excepciones.ComandoExcepcion;
import excepciones.ExcepcionMover;
import interfaces.Comando;

public class ComandoMover implements Comando {

  private String comando;
  private Juego juego;
  private boolean bSoloJugador;
  ConsolaNormal consola = new ConsolaNormal();

  public ComandoMover(String comando, Juego juego, boolean bSoloJugador) {
    this.comando = comando;
    this.juego = juego;
    this.bSoloJugador = bSoloJugador;
  }

  @Override
  public void ejecutar() throws ComandoExcepcion {
    try {
      for (Personaje personaje : this.juego.getMapa().getPersonajes()) {
        if (!bSoloJugador || personaje instanceof Jugador) {
          personaje.mover(this.juego.getMapa(), comando.replace("mover ", ""));
          if (personaje instanceof Jugador) {
            consola.imprimir(
                "personaje "
                    + personaje.getNombre()
                    + " vida:"
                    + personaje.getSalud()
                    + " energia:"
                    + personaje.getEnergia()
                    + ", posicion: "
                    + personaje.getPosicionMapa());
            consola.imprimir(
                juego
                    .getMapa()
                    .pintarMapaParcialDistancia(
                        juego.getMapa().getCelda(personaje.getPosicionMapa()),
                        personaje.rangoVision()));
          }
        }
      }
    } catch (ExcepcionMover ex) {
      consola.imprimir(ex.toString());
      throw new ComandoExcepcion(ex.toString());
    } catch (Exception ex) {
      consola.imprimir(ex.toString());
      throw new ComandoExcepcion(ex.toString());
    }
  }
}
