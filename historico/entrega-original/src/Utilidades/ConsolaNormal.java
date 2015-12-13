package Utilidades;

import interfaces.Consola;
import java.util.Scanner;

public final class ConsolaNormal implements Consola {

  @Override
  public void imprimir(String mensaje) {
    System.out.println(mensaje);
  }

  @Override
  public String leer(String mensaje) {
    this.imprimir(mensaje);
    Scanner keyboard = new Scanner(System.in);
    return keyboard.nextLine();
  }
}
