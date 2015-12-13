/* * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Juego.Mapa;

import Juego.Personaje.Objeto;
import Juego.Personaje.Personaje;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rodrigo Sambade Saá y Miguel Alonso Castro
 */
public class Celda {
  private Point posicionMapa;
  private String Descripcion;
  private ArrayList<Objeto> Objetos = new ArrayList<>();
  private List<Personaje> personajes = new ArrayList<>();
  private boolean Transitable;

  // Constructores
  public Celda() { // Vacío
  }

  public Celda(String descripción, ArrayList<Objeto> objetos, boolean transitable) {

    this.Descripcion = descripción;
    this.Objetos = objetos;
    this.Transitable = transitable;
  }

  // Setters

  public void setDescripcion(String descripción) {
    this.Descripcion = descripción;
  }

  public void setObjetos(ArrayList<Objeto> objetos) {
    this.Objetos = objetos;
  }

  public void setObjeto(Objeto objeto) {
    this.Objetos.add(objeto);
  }

  public void setTransitable(boolean transitable) {
    this.Transitable = transitable;
  }

  public void ponerObjetos(Objeto objeto) {
    this.Objetos.add(objeto);
  }

  public void ponerObjetos(ArrayList<Objeto> objetos) {
    for (int i = 0; i < objetos.size(); i++) this.Objetos.add(objetos.get(i));
  }

  public Objeto buscarObjetoPorNombre(String nombreObjeto) {
    Objeto objeto = null;
    for (Objeto obj : this.getObjetos()) {
      if (nombreObjeto.equalsIgnoreCase(obj.getNombre())) {
        objeto = obj;
        break;
      }
    }
    return objeto;
  }

  public List<Personaje> getPersonajes() {
    return personajes;
  }

  public Personaje getPersonaje(String nombre) {
    Personaje personaje = null;
    for (Personaje pers : personajes) {
      if (nombre.equalsIgnoreCase(pers.getNombre())) {
        personaje = pers;
      }
    }
    return personaje;
  }

  public void setPersonaje(Personaje personaje) {
    this.personajes.add(personaje);
  }

  // Getters

  public String getDescripcion() {
    return Descripcion;
  }

  public ArrayList<Objeto> getObjetos() {
    return Objetos;
  }

  public boolean isTransitable() {
    return Transitable;
  }

  public Point getPosicionMapa() {
    return posicionMapa;
  }

  public void setPosicionMapa(Point posicionMapa) {
    this.posicionMapa = posicionMapa;
  }

  @Override
  public String toString() {
    String retorno = "";
    retorno = this.Descripcion;
    if (this.Objetos != null) {
      retorno += ", " + this.Objetos.toString();
    }
    retorno += ", " + this.Transitable;
    retorno += ", " + this.getPersonajes().toString();
    return retorno;
  }
}
