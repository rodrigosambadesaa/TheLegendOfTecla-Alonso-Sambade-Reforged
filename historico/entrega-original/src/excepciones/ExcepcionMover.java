package excepciones;

public class ExcepcionMover extends Exception {

  public ExcepcionMover(String message) {
    super(message);
  }

  @Override
  public String toString() {
    return this.getMessage();
  }
}
