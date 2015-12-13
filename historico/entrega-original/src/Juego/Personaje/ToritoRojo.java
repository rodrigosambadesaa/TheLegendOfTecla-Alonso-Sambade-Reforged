package Juego.Personaje;

import Utilidades.ConsolaNormal;
import excepciones.ComandoExcepcion;

public class ToritoRojo extends Objeto {

  private int energia;
  private ConsolaNormal c = new ConsolaNormal();

  public ToritoRojo() {}

  public ToritoRojo(
      String tipo, String nombre, String descripcion, double peso, int salud_recuperada) {
    super(tipo, nombre, descripcion, peso);
  }

  public int getEnergia() {
    return energia;
  }

  /**
   * como maximo ponemo de energia 100
   *
   * @param energia
   */
  public void setEnergia(int energia) {
    this.energia = energia;
    if (this.energia > 100) {
      this.energia = 100;
    }
  }

  /**
   * utiliza el objeto, devuelve true si lo ha utilizado y false si no
   *
   * @param pers
   * @return
   * @throws excepciones.ComandoExcepcion
   */
  @Override
  public boolean usar(Personaje pers) throws ComandoExcepcion {
    boolean bUtilizado = false;
    try {
      bUtilizado = pers.incrementaEnergia(this.getEnergia());
      if (!bUtilizado) {
        c.imprimir("no incrementas energia porque estas al máximo");
      }
      return bUtilizado;
    } catch (ComandoExcepcion ex) {
      c.imprimir("error usando toritoRojo:" + ex.toString());
    }
    return bUtilizado;
  }

  @Override
  public String toString() {
    return "ToritoRojo{ " + super.toString() + ", energia=" + energia + '}';
  }
}
