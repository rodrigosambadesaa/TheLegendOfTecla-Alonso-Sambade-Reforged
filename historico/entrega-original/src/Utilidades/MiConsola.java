package Utilidades;

public class MiConsola {
  public static void imprime(String msg, String color) {
    if (color == null || "".equals(color)) {
      System.out.println(msg);
    } else if (color.equals("rojo")) {
      System.out.println((char) 27 + "[31m" + msg + (char) 27 + "[0m");
    } else if (color.equals("verde")) {
      System.out.println((char) 27 + "[32m" + msg + (char) 27 + "[0m");
    } else if (color.equals("amarillo")) {
      System.out.println((char) 27 + "[33m" + msg + (char) 27 + "[0m");
    } else if (color.equals("azul")) {
      System.out.println((char) 27 + "[34m" + msg + (char) 27 + "[0m");
    } else {
      System.out.println(msg);
    }
  }

  public static void print(String msg) {
    imprime(msg, "");
  }

  public static void printRojo(String msg) {
    imprime(msg, "rojo");
  }

  public static void printVerde(String msg) {
    imprime(msg, "verde");
  }

  public static void printAmarillo(String msg) {
    imprime(msg, "amarillo");
  }

  public static void printAzul(String msg) {
    imprime(msg, "azul");
  }
}
