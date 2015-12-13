package Juego.Personaje;

import Utilidades.ConsolaNormal;
import excepciones.ComandoExcepcion;

public class Botiquin extends Objeto {
  private int salud_recuperada;
  ConsolaNormal c = new ConsolaNormal();

  public Botiquin() {}

  public Botiquin(
      String tipo, String nombre, String descripcion, double peso, int salud_recuperada) {
    super(tipo, nombre, descripcion, peso);
    this.salud_recuperada = salud_recuperada;
  }

  public int getSalud_recuperada() {
    return salud_recuperada;
  }

  public void setSalud_recuperada(int salud_recuperada) {
    this.salud_recuperada = salud_recuperada;
  }

  @Override
  public boolean usar(Personaje pers) throws ComandoExcepcion {
    boolean bUtilizado = pers.incrementaSalud(this.getSalud_recuperada());
    if (!bUtilizado) {
      c.imprimir("no incrementas salud porque estas al máximo");
    }
    return bUtilizado;
  }

  @Override
  public String toString() {
    return "Botiquin{" + super.toString() + ", salud_recuperada=" + salud_recuperada + '}';
  }
}
