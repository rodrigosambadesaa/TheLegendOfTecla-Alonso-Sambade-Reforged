/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Juego.Personaje;

import excepciones.ComandoExcepcion;

/**
 * @author Rodrigo Sambade Saá y Miguel Alonso Castro
 */
public class Objeto {

  private String nombre;
  private double peso;
  private String tipo;
  private String descripcion;

  public Objeto() {}

  public Objeto(String tipo, String nombre, String descripcion) {
    this.nombre = nombre;
    this.tipo = tipo;
    this.descripcion = descripcion;
  }

  public Objeto(String tipo, String nombre, String descripcion, double peso) {
    this.nombre = nombre;
    this.peso = peso;
    this.tipo = tipo;
    this.descripcion = descripcion;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    if (nombre.length() < 100) {
      this.nombre = nombre;
    } else {
      System.out.println("Nombre demasiado largo");
    }
  }

  public double getPeso() {
    return peso;
  }

  public void setPeso(double peso) {
    if (peso < 300 && peso >= 0) {
      this.peso = peso;
    } else {
      System.out.println("El peso debe estar entre 0 y 300 kg");
    }
  }

  public String getTipo() {
    return tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  /**
   * utiliza el objeto, devuelve true si lo ha utilizado y false si no
   *
   * @param pers
   * @return
   * @throws excepciones.ComandoExcepcion
   */
  public boolean usar(Personaje pers) throws ComandoExcepcion {
    if (true) {
      throw new ComandoExcepcion("no usabilidad");
    }
    return false;
  }

  @Override
  public String toString() {
    return "Objeto{"
        + "nombre="
        + nombre
        + ", peso="
        + peso
        + ", tipo="
        + tipo
        + ", descripcion="
        + descripcion
        + '}';
  }
}
