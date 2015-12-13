package excepciones;

public class ComandoExcepcion extends Exception {

  public ComandoExcepcion(String message) {
    super(message);
  }

  @Override
  public String toString() {
    return this.getMessage();
  }
}
