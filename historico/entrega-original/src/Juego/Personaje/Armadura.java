package Juego.Personaje;

public class Armadura extends Objeto {

  private int defensa;
  private int salud;
  private int energia;
  private String portador;

  public Armadura() {}

  public Armadura(
      String tipo,
      String nombre,
      String descripcion,
      double peso,
      int defensa,
      int salud,
      int energia) {
    super(tipo, nombre, descripcion, peso);
    this.defensa = defensa;
    this.salud = salud;
    this.energia = energia;
  }

  public int getDefensa() {
    return defensa;
  }

  public void setDefensa(int defensa) {
    this.defensa = defensa;
  }

  public int getSalud() {
    return salud;
  }

  public void setSalud(int salud) {
    this.salud = salud;
  }

  public int getEnergia() {
    return energia;
  }

  public void setEnergia(int energia) {
    this.energia = energia;
  }

  public String getPortador() {
    return portador;
  }

  public void setPortador(String portador) {
    this.portador = portador;
  }

  @Override
  public String toString() {
    return "Armadura{"
        + super.toString()
        + "defensa="
        + defensa
        + ", salud="
        + salud
        + ", energia="
        + energia
        + ", portador="
        + portador
        + '}';
  }
}
