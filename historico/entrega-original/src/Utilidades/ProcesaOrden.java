package Utilidades;

import excepciones.ComandoExcepcion;

public class ProcesaOrden {
  public static String getParam(String orden) {
    String param = "";
    if (orden != null && !orden.isEmpty()) {
      String[] palabras = orden.split(" ");
      if (palabras.length > 1) {
        param = palabras[1];
      }
    }

    return param;
  }

  public static String tipoComando(String orden) throws ComandoExcepcion {
    String tipo = "normal";
    if (orden.contains("atacar")) {
      String numVeces = "";
      String[] partes = orden.split(" ");
      if (partes.length == 3) {
        if (partes[3].matches("[0-9]")) {
          tipo = "repetido";
          numVeces = partes[3];
        }
      }
      if (partes.length == 4) {
        if (partes[4].matches("[0-9]")) {
          tipo = "repetido";
          numVeces = partes[4];
        }
      }
    }
    return tipo;
  }

  public static String comando(String orden) throws ComandoExcepcion {
    String param = "";
    boolean bOrdenOK = true;
    if (orden != null && !orden.isEmpty()) {
      String[] palabras = orden.split(" ");
      if (orden.matches(".*(coger).*")) {
        if (palabras.length == 2) {
          param = orden.replaceAll(".*coger[ ]+", "");
        } else {
          bOrdenOK = false;
        }
      } else if (orden.matches(".*(mover).*")) {
        if (palabras.length == 2) {
          param = orden.replaceAll(".*mover[ ]+", "");
        } else {
          bOrdenOK = false;
        }
      } else if (orden.matches(".*(desequipar).*")) {
        if (palabras.length == 2) {
          param = orden.replaceAll(".*desequipar[ ]+", "");
        } else {
          bOrdenOK = false;
        }
      } else if (orden.matches(".*(equipar).*")) {
        if (palabras.length == 2) {
          param = orden.replaceAll(".*equipar[ ]+", "");
        } else {
          bOrdenOK = false;
        }
      } else if (orden.matches(".*(usar).*")) {
        if (palabras.length == 2) {
          param = orden.replaceAll(".*usar[ ]+", "");
        } else {
          bOrdenOK = false;
        }
      } else if (orden.matches(".*(mirar).*")) {
        if (palabras.length >= 1 && palabras.length <= 3) {
          param = orden.replaceAll(".*mirar[ ]*", "");
        } else {
          bOrdenOK = false;
        }
      } else if (orden.matches(".*(tirar).*")) {
        if (palabras.length >= 1 && palabras.length <= 3) {
          param = orden.replaceAll(".*tirar[ ]*", "");
        } else {
          bOrdenOK = false;
        }
      } else if (orden.matches(".*(atacar).*")) {
        if (palabras.length >= 1 && palabras.length <= 4) {
          param = orden.replaceAll(".*atacar[ ]+", "");
        } else {
          bOrdenOK = false;
        }
      } else {
        bOrdenOK = false;
      }
    } else {
      bOrdenOK = false;
    }
    if (!bOrdenOK) {
      throw new ComandoExcepcion("orden incorrecta: " + orden);
    }
    return param;
  }
}
