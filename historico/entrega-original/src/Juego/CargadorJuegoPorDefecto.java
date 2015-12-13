package juego;

import Juego.Juego;
import Utilidades.ConsolaNormal;
import interfaces.CargadorJuego;

public class CargadorJuegoPorDefecto implements CargadorJuego {

  ConsolaNormal consola = new ConsolaNormal();

  @Override
  public Juego cargarJuego() {
    Juego juego = new Juego();
    try {
      // llamamos al que carga un mapa aleatorio
      juego.cargarMapaAleatorio();
    } catch (Exception ex) {
      consola.imprimir("CargadorJuegoPorDefecto" + ex.toString());
    }
    return juego;
  }
}
