/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Juego.Personaje;

import Juego.Mapa.Celda;
import Utilidades.MiConsola;
import excepciones.ComandoExcepcion;
import excepciones.ExcepcionMover;
import java.util.ArrayList;

/**
 * @author Rodrigo Sambade Saá y Miguel Alonso Castro
 */
public class Mochila extends Objeto {

  // private static final int CAPACIDAD_MOCHILA = 5;    private static final int
  // PESO_SOPORTADO_MOCHILA = 25;
  private ArrayList<Objeto> objetos = new ArrayList<Objeto>();
  private int capacidad;
  private double pesoMax;

  // Constructores
  public Mochila() { // Vacío
  }

  public Mochila(String tipo, String nombre, String descripcion, int capacidad, double pesoMax) {
    super(tipo, nombre, descripcion);
    this.setPesoMax(pesoMax);
    this.setCapacidad(capacidad);
  }

  public ArrayList<Objeto> getObjetos() {
    return objetos;
  }

  /**
   * si encuentra ese objeto en la mochila lo devuelve, el primero que encuentre si tiene varios de
   * ese tipo
   *
   * @param tipo tipo de objeto buscado
   * @return
   */
  public Objeto getObjetoTipo(String tipo) {
    Objeto objeto = null;
    for (Objeto obj : this.getObjetos()) {
      if (tipo.equalsIgnoreCase(obj.getTipo())) {
        objeto = obj;
        break;
      }
    }
    return objeto;
  }

  public boolean tieneObjeto(String tipo) {
    return getObjetoTipo(tipo) != null;
  }

  public void setObjetos(ArrayList<Objeto> objetos) {
    this.objetos = objetos;
  }

  public void setObjeto(Objeto objeto) throws ComandoExcepcion {
    if (!puedeAgregarObjeto(objeto)) {
      MiConsola.printRojo(
          "setObjeto: no puede agregar el objeto a la mochila, ya tiene el maximo de este tipo de"
              + " objeto");
    } else {
      this.objetos.add(objeto);
      this.capacidad++;
      this.setPeso(this.getPeso() + objeto.getPeso());
    }
  }

  public void eliminarObjetoDeMochila(Objeto objeto) throws ComandoExcepcion {
    this.objetos.remove(objeto);
  }

  public void cogerObjeto(Celda celda, String nombreObj, Personaje personaje)
      throws ExcepcionMover, ComandoExcepcion {
    Objeto objEncontrado = null;
    for (Objeto objeto : celda.getObjetos()) {
      if (nombreObj.equalsIgnoreCase(objeto.getNombre())) {
        objEncontrado = objeto;
      }
    }
    // si hay objeto lo quitamos de la celda y lo metemos en la mochila
    if (objEncontrado != null) {
      if (!puedeAgregarObjeto(objEncontrado)) {
        MiConsola.printRojo(
            "no puede agregar el objeto a la mochila, ya tiene el maximo de este tipo de objeto");
      } else {
        celda.getObjetos().remove(objEncontrado);
        setObjeto(objEncontrado);
        MiConsola.printVerde("cogido objeto y agregado a mochila: " + nombreObj);

        // la armadura puede tener energia y salud, por lo tanto la agregamos
        if (objEncontrado.getTipo().equalsIgnoreCase("armadura")) {
          Armadura armadura = (Armadura) objEncontrado;
          personaje.setEnergia(personaje.getEnergia() + armadura.getEnergia());
          personaje.incrementaSalud(armadura.getSalud());
        }
      }
    } else {
      MiConsola.printRojo("no existe objeto en la celda=" + nombreObj);
    }
  }

  public void tirarObjeto(Celda celda, String nombreObj) throws ComandoExcepcion {
    Objeto objetoAElim = null;
    for (Objeto obj : this.objetos) {
      if (obj.getNombre().equalsIgnoreCase(nombreObj)) {
        objetoAElim = obj;
        break;
      }
    }
    // borramos el objeto de la mochila y lo ponemos en la celda que nos pasan
    if (objetoAElim != null) {
      this.objetos.remove(objetoAElim);
      celda.setObjeto(objetoAElim);
      System.out.println("borrando de la mochila el objeto: " + objetoAElim.getNombre());
    } else {
      System.out.println("No tiene ese objeto en la mochila");
    }
  }

  @Override
  public double getPeso() {
    double pes = 0.0;
    for (Objeto obj : this.objetos) {
      pes += obj.getPeso();
    }
    this.setPeso(pes);
    return pes;
  }

  @Override
  public void setPeso(double peso) {
    if (peso >= 0 && peso < this.getPesoMax()) {
      super.setPeso(peso);
    } else {
      System.out.println("el peso debe tomar un valor entre 0 y " + this.getPesoMax());
    }
  }

  public double getPesoMax() {
    return this.pesoMax;
  }

  public void setPesoMax(double pesoMax) {
    this.pesoMax = pesoMax;
  }

  public void setNombre(String nombre) {
    if (nombre.length() < 100) {
      this.setNombre(nombre);
    } else {
      System.out.println("El nombre es demasiado largo");
    }
  }

  public int getCapacidad() {

    return capacidad;
  }

  public void setCapacidad(int capacidad) {
    if (capacidad > 0 && capacidad <= this.getPesoMax()) {
      this.capacidad = capacidad;
    } else {
      System.out.println("La capacidad debe tomar un valor entre 0 y " + this.getPesoMax());
    }
  }

  /**
   * comprueba si puede agregarse el objeto enviado como parametro a la mochila por cumplir las
   * condiciones necesarias
   *
   * @param objeto
   * @return
   */
  public boolean puedeAgregarObjeto(Objeto objeto) {
    boolean puede = false;
    int binoculares = 0;
    int botiquin = 0;
    int torito = 0;
    int armas = 0;
    int armas1Mano = 0;
    int armas2Mano = 0;
    int armadura = 0;

    for (Objeto obj : this.getObjetos()) {
      if (obj instanceof Binoculares) {
        binoculares++;
      }
      if (obj instanceof Botiquin) {
        botiquin++;
      }
      if (obj instanceof ToritoRojo) {
        torito++;
      }
      if (obj instanceof Armadura) {
        armadura++;
      }
      if (obj instanceof Arma) {
        Arma arma = (Arma) obj;
        armas++;
        if (arma.getManos() == 1) {
          armas1Mano++;
        }
        if (arma.getManos() == 2) {
          armas2Mano++;
        }
      }
    }

    if (objeto instanceof Binoculares && binoculares < 2) {
      puede = true;
    }
    if (objeto instanceof Botiquin && botiquin < 1) {
      puede = true;
    }
    if (objeto instanceof ToritoRojo && torito < 1) {
      puede = true;
    }
    if (objeto instanceof Armadura && armadura < 1) {
      puede = true;
    }

    if (objeto instanceof Arma) {
      Arma arma = (Arma) objeto;
      if (arma.getManos() == 1 && armas1Mano < 2) {
        puede = true;
      }
      if (arma.getManos() == 2 && armas2Mano < 1) {
        puede = true;
      }
    }
    if (puede) {
      if (this.objetos.size() >= this.capacidad) {
        MiConsola.printRojo(
            "no se puede agregar objeto porque ya estamos en el limite: " + this.getPesoMax());
        puede = false;
      } else if ((this.getPeso() + objeto.getPeso()) > this.getPesoMax()) {
        MiConsola.printRojo(
            "no se puede agregar objeto porque superamos el peso limite: " + this.getPesoMax());
        puede = false;
      }
    } else {
      MiConsola.printRojo("no se puede coger objeto: " + objeto.getNombre());
    }
    return puede;
  }

  /**
   * muestra el inventario de objetos y armaduras que lleva en la mochila
   *
   * @return
   */
  public String inventario() {
    String retorno = "mochila: " + this.getNombre() + "\n";
    for (Objeto objeto : this.objetos) {
      retorno += objeto.toString() + "\n";
    }
    retorno += "Peso total mochila: " + this.getPeso();
    retorno += "\nobjetos en mochila: " + this.getObjetos().size();
    return retorno;
  }

  @Override
  public String toString() {
    return "Mochila{"
        + "objetos="
        + objetos.toString()
        + ", capacidad="
        + capacidad
        + ", pesoMax="
        + pesoMax
        + '}';
  }
}
