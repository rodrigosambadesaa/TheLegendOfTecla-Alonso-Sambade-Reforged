package Juego;

import Ayuda.Ayuda;
import Juego.Mapa.Mapa;
import Juego.Personaje.Enemigo;
import Juego.Personaje.Objeto;
import Juego.Personaje.Personaje;
import Utilidades.ConsolaNormal;
import Utilidades.Utiles;
import comandos.ComandoAtacar;
import comandos.ComandoCoger;
import comandos.ComandoCompuesto;
import comandos.ComandoDesequipar;
import comandos.ComandoEquipar;
import comandos.ComandoInventario;
import comandos.ComandoMirar;
import comandos.ComandoMover;
import comandos.ComandoTirar;
import comandos.ComandoUsar;
import excepciones.ComandoExcepcion;
import excepciones.ExcepcionFatal;
import excepciones.ExcepcionMover;
import excepciones.ExcepcionPers;
import java.util.ArrayList;

public class Juego {

  private Mapa mapa;
  private Personaje jugador;
  // private ArrayList<Personaje> personajes;
  private ArrayList<Objeto> objetos;
  private ConsolaNormal consola = new ConsolaNormal();

  public Juego() {}

  public Juego(Mapa mapa, Personaje jugador, ArrayList<Objeto> objetos) {
    this.mapa = mapa;
    this.jugador = jugador;
    // this.personajes = personajes;
    this.objetos = objetos;
  }

  public Mapa getMapa() {
    return mapa;
  }

  public void setMapa(Mapa mapa) {
    this.mapa = mapa;
  }

  public Personaje getJugador() {
    return jugador;
  }

  public void setJugador(Personaje jugador) {
    this.jugador = jugador;
  }

  //    public ArrayList<Personaje> getPersonajes() {
  //        return personajes;
  //    }
  //
  //    public void setPersonajes(ArrayList<Personaje> personajes) {
  //        this.personajes = personajes;
  //    }

  public ArrayList<Objeto> getObjetos() {
    return objetos;
  }

  public void setObjetos(ArrayList<Objeto> objetos) {
    this.objetos = objetos;
  }

  public void cargarMapaAleatorio() throws Exception {
    // en este constructor Mapa crea un mapa aleatorio
    this.mapa = new Mapa();
  }

  public void cargarMapaDeFichero(String ruta) throws Exception {
    String rutaAux = ruta;
    this.mapa = new Mapa(rutaAux);
    if (this.mapa.getJugador() != null) {
      this.jugador = this.mapa.getJugador();
    }
    consola.imprimir("Posicion actual do personaje: " + jugador.getPosicionMapa());
  }

  public void jugada(String orden)
      throws ExcepcionMover,
          ExcepcionPers,
          CloneNotSupportedException,
          ExcepcionFatal,
          ComandoExcepcion {
    String orde = orden;
    if ("h".equalsIgnoreCase(orde)) {
      consola.imprimir(Ayuda.Mostrar());
    } else if ("mirar completo".equalsIgnoreCase(orde)) {
      String mapaParcial = mapa.pintarMapaCompleto(mapa.getCelda(jugador.getPosicionMapa()));
      consola.imprimir(mapaParcial);
    } else if (orde.matches(".*( [0-9]{1,2}[, ].*| [0-9]{1,2}$)")) {
      new ComandoCompuesto(orde, this, true).ejecutar();
    } else if (orde.contains(",")) { // los compuestos vienen siempre con coma
      new ComandoCompuesto(orde, this, true).ejecutar();
    } else if (orde.matches(".*mirar.*(norte|sur|este|oeste).*")) {
      jugador.mirarExt(mapa, jugador, orde);
    } else if (orde.contains("mirar ")) {
      jugador.mirar(mapa, orde.replace("mirar ", ""));
    } else if ("mirar".equalsIgnoreCase(orde)) {
      new ComandoMirar(orde, this, true).ejecutar();
    } else if (orde.contains("usar")) {
      // String objetoAUsar = ProcesaOrden.comando(orde);
      // this.jugador.usar(objetoAUsar);
      new ComandoUsar(orde, this, true).ejecutar();
    } else if (orde.contains("atacar")) {
      new ComandoAtacar(orde, this, true).ejecutar();
    } else if ("mapa".equalsIgnoreCase(orde)) {
      mapa.imprimirMapa();
    } else if ("inventario".equalsIgnoreCase(orde)) {
      // jugador.inventario();
      new ComandoInventario("", this, true).ejecutar();
    } else if (orde.contains("coger")) {
      new ComandoCoger(orde, this, true).ejecutar();
    } else if (orde.contains("tirar")) {
      new ComandoTirar(orde, this, true).ejecutar();
    } else if (orde.contains("ver enemigos")) {
      mapa.verEnemigos();
    } else if (orde.contains("ver objetos")) {
      mapa.verObjetos();
    } else if (orde.contains("pasar turno")) {
      // no hacemos nada pero el resto se mueve y pueden atacarnos
      String[] direccion = {"norte", "sur", "este", "oeste"};
      for (Personaje pers : this.mapa.getPersonajes()) {
        if (pers instanceof Enemigo) {
          pers.mover(mapa, direccion[Utiles.aleatorio(0, 4)]);
          pers.atacar2(orde, mapa);
        }
      }
      // el jugador recupera energia
      jugador.setEnergia(100);
    } else if (orde.contains("desequipar")) {
      new ComandoDesequipar(orde, this, true).ejecutar();
      // jugador.desequipa(orde.replace("desequipar ", ""));
    } else if (orde.contains("equipar")) {
      new ComandoEquipar(orde, this, true).ejecutar();
      // jugador.setEquipacion(orde.replace("equipar ", ""));
    } else if (orde.matches("mover.*(norte|sur|este|oeste).*")) {
      // consola.imprimir("antes de moverse jugador posicion:" + jugador.getPosicionActualString());
      // jugador.mover(mapa, orde);
      new ComandoMover(orde, this, true).ejecutar();
      // consola.imprimir("despues de moverse jugador posicion:" +
      // jugador.getPosicionActualString());
      // pueden atacarnos los otros personajes
      for (Personaje pers : this.mapa.getPersonajes()) {
        if (pers instanceof Enemigo) {
          pers.atacar2(orde, mapa);
        }
      }
      // MiConsola.printVerde(mapa.pintarMapaParcialDistancia(mapa.getCelda(jugador.getPosicionMapa()), jugador.rangoVision()));
    } else {
      consola.imprimir("******orden inexistente:" + orde);
    }
    consola.imprimir("vida: " + jugador.getSalud());
    consola.imprimir("energia: " + jugador.getEnergia());
  }
  // *********************************************************************

}
