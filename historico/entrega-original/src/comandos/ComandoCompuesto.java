package comandos;

import Juego.Juego;
import excepciones.ComandoExcepcion;
import interfaces.Comando;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComandoCompuesto implements Comando {

  private String comandos;
  private Juego juego;
  private String[] comandosPermitidos = {
    "mover",
    "coger",
    "atacar",
    "mirar",
    "tirar",
    "inventario",
    "mover",
    "usar",
    "equipar",
    "desequipar"
  };

  public ComandoCompuesto() {}

  public ComandoCompuesto(String comando, Juego juego, boolean bSoloJugador)
      throws ComandoExcepcion {
    this.comandos = comando;
    this.juego = juego;
    // this.bSoloJugador = bSoloJugador;
  }

  public List<String> partirComandos(String comandos) {
    List<String> cmds = new ArrayList<>();
    String cadAux = comandos;
    List<Integer> pos = new ArrayList<>();
    int p = -1;
    int apariciones = 0;
    while (true) {
      for (String cmd : comandosPermitidos) {
        p = cadAux.indexOf(cmd);
        if (p > -1) {
          apariciones++;
          if (apariciones > 1) {
            cmds.add(cadAux.substring(0, p));
            cadAux = cadAux.substring(p + cmd.length());
          }

          break;
        }
      }
      if (p == -1) {
        break;
      }
      pos.add(p);
    }
    return cmds;
  }

  @Override
  public void ejecutar() throws ComandoExcepcion {
    List<String> cmdsPermitidos = Arrays.asList(comandosPermitidos);
    // partirComandos(comandos);
    // String cad = "(" + cmdsPermitidos.toString().replace(",", "|").replace("\"", "").replace("[",
    // "").replace("]", "").replace(" ", "") + ")";
    String[] cmds = comandos.split(",");
    // partimos los comandos por las posibles ordenes
    for (String cmd : cmds) {
      cmd = cmd.trim();
      String cmd1 = cmd.split(" ")[0];
      int veces = 1;
      if (cmd.contains("mover")) {
        String[] partesMover = cmd.split(" ");
        if (partesMover.length == 3) {
          if (partesMover[2].matches("[0-9]+")) {
            veces = Integer.parseInt(partesMover[2]);
            cmd = partesMover[0] + " " + partesMover[1];
          }
        }
      }
      if (cmd.contains("atacar")) {
        String[] pt = cmd.split(" ");
        if (pt.length == 3) {
          if (pt[2].matches("[0-9]+")) {
            veces = Integer.parseInt(pt[2]);
            cmd = pt[0] + " " + pt[1];
          }
        } else if (pt.length == 4) {
          if (pt[3].matches("[0-9]+")) {
            veces = Integer.parseInt(pt[3]);
            cmd = pt[0] + " " + pt[1] + " " + pt[2];
          }
        }
      }

      if (cmdsPermitidos.contains(cmd1)) {

        if (veces > 1) {
          if (cmd.contains("mover")) {
            cmd = cmd.replaceAll(" [0-9]+", "").trim();
            new ComandoRepetido(new ComandoMover(cmd, juego, true), veces).ejecutar();
          } else if (cmd.contains("atacar")) {
            cmd = cmd.replaceAll(" [0-9]+", "").trim();
            new ComandoRepetido(new ComandoAtacar(cmd, juego, true), veces).ejecutar();
          }
        } else if (cmd.contains("coger")) {
          new ComandoCoger(cmd, juego, true).ejecutar();
        } else if (cmd.contains("tirar")) {
          new ComandoTirar(cmd, juego, true).ejecutar();
        } else if (cmd.contains("mover")) {
          new ComandoMover(cmd, juego, true).ejecutar();
        } else if (cmd.contains("mirar")) {
          new ComandoMirar(cmd, juego, true).ejecutar();
        } else if (cmd.contains("atacar")) {
          new ComandoAtacar(cmd, juego, true).ejecutar();
        } else if (cmd.contains("equipar")) {
          new ComandoEquipar(cmd, juego, true).ejecutar();
        } else if (cmd.contains("desequipar")) {
          new ComandoDesequipar(cmd, juego, true).ejecutar();
        } else if (cmd.contains("usar")) {
          new ComandoUsar(cmd, juego, true).ejecutar();
        } else if (cmd.contains("inventario")) {
          new ComandoUsar(cmd, juego, true).ejecutar();
        }
      }
    }
  }
}
