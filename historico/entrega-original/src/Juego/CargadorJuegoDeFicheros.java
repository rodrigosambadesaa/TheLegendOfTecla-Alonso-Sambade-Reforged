package juego;

import Juego.Juego;
import Utilidades.ConsolaNormal;
import interfaces.CargadorJuego;

public class CargadorJuegoDeFicheros implements CargadorJuego {
  ConsolaNormal consola = new ConsolaNormal();

  @Override
  public Juego cargarJuego() {
    Juego juego = new Juego();
    try {
      // llamamos al que carga los ficheros que es el que lleva como parámetro la ruta donde están
      juego.cargarMapaDeFichero("");
    } catch (Exception ex) {
      consola.imprimir("CargadorJuegoDeFicheros " + ex.toString());
    }
    return juego;
  }
}
