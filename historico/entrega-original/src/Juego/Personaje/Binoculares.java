package Juego.Personaje;

import Utilidades.ConsolaNormal;
import excepciones.ComandoExcepcion;

public class Binoculares extends Objeto {

  private int alcance;
  private ConsolaNormal c = new ConsolaNormal();

  public Binoculares() {}

  public Binoculares(String tipo, String nombre, String descripcion, double peso, int alcance) {
    super(tipo, nombre, descripcion, peso);
    this.alcance = alcance;
  }

  public int getAlcance() {
    return alcance;
  }

  public void setAlcance(int alcance) {
    this.alcance = alcance;
  }

  /**
   * su método usar permite incrementar el rango de visión durante el turno actual. En caso de tener
   * un binocular ya equipado, se mostrará el de mayor rango
   *
   * @param pers
   * @return
   * @throws ComandoExcepcion
   */
  @Override
  public boolean usar(Personaje pers) throws ComandoExcepcion {
    boolean bUsado = false;
    if (pers instanceof Jugador) {
      Jugador jugador = (Jugador) pers;
      // si tiene Mochila miramos en ella si tiene estos binoculares
      if (jugador.mochila != null && jugador.mochila.getObjetos().contains(this)) {
        // solo usamos los nuevos binoculares si son superiores en alcance al que ya tiene
        if (this.getAlcance() > jugador.getAlcance()) {
          c.imprimir(
              "aumentado alcance con binoculares de "
                  + jugador.rangoVision()
                  + " a "
                  + this.getAlcance());
          jugador.setAlcance(this.getAlcance());
        } else {
          c.imprimir(
              "---el alcance que tenia el jugador: "
                  + jugador.rangoVision()
                  + " era superior al que le aportan estos binoculares "
                  + this.getNombre()
                  + ": "
                  + this.getAlcance());
        }
      } else {
        c.imprimir("jugador sin mochila");
      }
    }
    return bUsado;
  }

  @Override
  public String toString() {
    return "Binoculares{" + super.toString() + ", alcance=" + alcance + '}';
  }
}
