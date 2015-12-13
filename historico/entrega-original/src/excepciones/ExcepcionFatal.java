package excepciones;

public class ExcepcionFatal extends Exception {

  public ExcepcionFatal(String message) {
    super(message);
  }

  @Override
  public String toString() {
    return this.getMessage();
  }
}
