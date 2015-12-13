package Juego.Personaje;


public class Arma extends Objeto {
  private int danho;
  private int alcance;
  // manejo a 1 o dos manos: notacion 1_manos/2_manos
  private int manos;
  private String portador;

  public Arma() {}

  public Arma(String tipo, String nombre, String descripcion, int danho, int manos, double peso) {
    super(tipo, nombre, descripcion);
    this.setPeso(peso);
    this.danho = danho;
    this.manos = manos;
  }

  public int getDanho() {
    return danho;
  }

  public void setDanho(int danho) {
    this.danho = danho;
  }

  public int getAlcance() {
    return alcance;
  }

  public void setAlcance(int alcance) {
    this.alcance = alcance;
  }

  public int getManos() {
    return manos;
  }

  public void setManos(int manos) {
    this.manos = manos;
  }

  public String getPortador() {
    return portador;
  }

  public void setPortador(String portador) {
    this.portador = portador;
  }

  @Override
  public String toString() {
    return "Arma{"
        + super.toString()
        + ", danho="
        + danho
        + ", alcance="
        + alcance
        + ", manos="
        + manos
        + ", portador="
        + portador
        + '}';
  }
}
