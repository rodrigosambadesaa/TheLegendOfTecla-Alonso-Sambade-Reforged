/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ayuda;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author miguel.alonso
 */
public final class Ayuda {

  public static String Mostrar() {
    String sRetorno = "*********************************************************\n";
    sRetorno = "comandos:\n";
    sRetorno += "-movimiento:\n";
    sRetorno += "norte\n";
    sRetorno += "sur\n";
    sRetorno += "este\n";
    sRetorno += "oeste\n";
    sRetorno += "\n-finalizar juego\n";
    sRetorno += "fin\n";
    sRetorno += "\n-mirar\n";
    sRetorno +=
        "mirar: permite mirar en la celda en la que está situado o personaxe se hai algún objeto"
            + " para recoger\n";
    sRetorno += "\n-mapa\n";
    sRetorno +=
        "mapa: permite ver el mapa del juego\n"
            + " \u263A jugador\n"
            + " X celda no transitable\n"
            + " + objetivo\n";
    sRetorno += "*********************************************************\n";
    return sRetorno;
  }
}
