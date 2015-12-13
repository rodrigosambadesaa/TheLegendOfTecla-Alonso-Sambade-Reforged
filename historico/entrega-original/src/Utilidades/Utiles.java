package Utilidades;

public class Utiles {

  public static int aleatorio(int Min, int Max) {
    return (int) (Math.random() * (Max - Min)) + Min;
  }
}
