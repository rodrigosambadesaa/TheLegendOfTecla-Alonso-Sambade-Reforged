/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Juego.Mapa;

/**
 * @author Rodrigo Sambade Saá y Miguel Alonso Castro
 */
import Juego.Personaje.Arma;
import Juego.Personaje.Armadura;
import Juego.Personaje.Binoculares;
import Juego.Personaje.Botiquin;
import Juego.Personaje.Enemigo;
import Juego.Personaje.Floater;
import Juego.Personaje.Mochila;
// import Juego.Personaje.Enemigo;
import Juego.Personaje.Objeto;
import Juego.Personaje.Personaje;
import Juego.Personaje.Sectoid;
import Juego.Personaje.ToritoRojo;
import Utilidades.CONST;
import Utilidades.MiConsola;
import Utilidades.Utiles;
import excepciones.ComandoExcepcion;
import excepciones.ExcepcionMover;
import excepciones.ExcepcionPers;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Mapa {

  private int mapaTamHorizonal;
  private int mapaTamVertical;
  private String Nombre;
  private String Descripcion;
  private HashMap<Point, Celda> celdas = new HashMap<>();
  private Celda CeldadeComienzo;
  private Personaje jugador;
  private List<Personaje> personajes = new ArrayList<>();

  public Mapa(String rutaArchivos) {
    rellenaHashMap(rutaArchivos);
  }

  public Mapa(Personaje jugador) throws ExcepcionMover, ComandoExcepcion {
    this.setJugador(jugador);
    this.setPersonaje(jugador);
    rellenaHashMap();
  }

  public Mapa() throws ExcepcionMover, ComandoExcepcion {
    rellenaHashMap();
  }

  public String getNombre() {
    return Nombre;
  }

  public void setNombre(String Nombre) {
    this.Nombre = Nombre;
  }

  public int getMapaTamHorizonal() {
    return mapaTamHorizonal;
  }

  public void setMapaTamHorizonal(int mapaTamHorizonal) {
    this.mapaTamHorizonal = mapaTamHorizonal;
  }

  public int getMapaTamVertical() {
    return mapaTamVertical;
  }

  public Personaje getJugador() {
    return jugador;
  }

  public void setJugador(Personaje jugador) {
    this.jugador = jugador;
  }

  public List<Personaje> getPersonajes() {
    return personajes;
  }

  public void setPersonajes(List<Personaje> personajes) {
    this.personajes = personajes;
  }

  public void setPersonaje(Personaje personaje) {
    this.personajes.add(personaje);
  }

  public void setMapaTamVertical(int mapaTamVertical) {
    this.mapaTamVertical = mapaTamVertical;
  }

  public String getDescripcion() {
    return Descripcion;
  }

  public void setDescripcion(String Descripcion) {
    this.Descripcion = Descripcion;
  }

  public HashMap<Point, Celda> getCeldas() {
    return celdas;
  }

  public void setCeldas(HashMap<Point, Celda> celdas) {
    this.celdas = celdas;
  }

  public Celda getCeldadeComienzo() {
    return CeldadeComienzo;
  }

  public void setCeldadeComienzo(Celda CeldadeComienzo) {
    this.CeldadeComienzo = CeldadeComienzo;
  }

  public void rellenaHashMap() throws ExcepcionMover, ComandoExcepcion {
    String tipoCelda = "";
    this.setMapaTamHorizonal(this.getMapaTamHorizonal());
    this.setMapaTamVertical(this.getMapaTamVertical());
    if (this.getMapaTamHorizonal() == 0) {
      int aleatorio = Utiles.aleatorio(10, 15);
      this.setMapaTamHorizonal(aleatorio);
      this.setMapaTamVertical(aleatorio);
    }
    for (int i = 0; i < this.getMapaTamHorizonal(); i++) {
      for (int j = 0; j < this.getMapaTamVertical(); j++) {
        tipoCelda = "camino";
        if ((i + j) % 5 == 0) {
          tipoCelda = "camino dificil";
        }
        Celda celda = new Celda(tipoCelda, new ArrayList(), true);
        if ((i + j) % 9 == 0 && i % 2 == 0) {
          celda.setTransitable(false);
        }
        Point p = new Point(i, j);
        celda.setPosicionMapa(p);
        celdas.put(p, celda);
      }
    }
    colocaObjetosMapa();
    colocaEnemigosMapa();
  }

  public void rellenaHashMap(String rutaArchivos) {

    try {

      Utilidades.Utilidades.leerDatosMapa(rutaArchivos + "mapa.csv", this);
      Utilidades.Utilidades.leerDatosObjetos(rutaArchivos + "objetos.csv", this);
      Utilidades.Utilidades.leerDatosPersonajes(rutaArchivos + "npcs.csv", this);

    } catch (Exception ex) {
      MiConsola.printRojo("error: " + ex.toString());
    }
  }

  public void colocaObjetosMapa() {
    // colocamos objetos en el mapa de forma aleatoria
    int efecto = 10;
    int salud = 30;
    double peso = 1.0;

    int botiquin = 0;
    int binocular = 0;
    int torito = 0;
    int totalCeldas = this.getMapaTamHorizonal() * this.getMapaTamVertical();
    int celdasConObjetos = (int) (totalCeldas * 0.3);
    for (int k = 0; ; k++) {
      int posX = Utiles.aleatorio(0, this.getMapaTamHorizonal());
      int posY = Utiles.aleatorio(0, this.getMapaTamHorizonal());
      Point p2 = new Point(posX, posY);
      Objeto objeto = null;
      int num = Utiles.aleatorio(1, 4);
      if (num == 1) {
        if (botiquin < celdasConObjetos) {
          objeto =
              new Botiquin(
                  "botiquin",
                  "botiquin" + k,
                  "el botiquin de " + k,
                  Utiles.aleatorio(1, 3),
                  Utiles.aleatorio(3, 40));
        }
      } else if (num == 2) {
        if (binocular < celdasConObjetos) {
          objeto =
              new Binoculares(
                  "binoculares",
                  "binoculares" + k,
                  "los binoculares de " + k,
                  peso,
                  Utiles.aleatorio(10, 21));
        }
      } else if (num == 3) {
        if (torito < celdasConObjetos) {
          objeto = new ToritoRojo("torito", "torito" + k, "torito rojo " + k, 1, 0);
          ((ToritoRojo) objeto).setEnergia(40);
        }
      }
      Celda celdaAleatoria = this.getCelda(p2);
      // si es transitable ponemos el objeto
      if (celdaAleatoria.isTransitable()
          && celdaAleatoria.getObjetos().isEmpty()
          && objeto != null) {
        this.getCelda(p2).setObjeto(objeto);
        if (objeto instanceof Botiquin) {
          botiquin++;
        } else if (objeto instanceof Binoculares) {
          binocular++;
        } else if (objeto instanceof ToritoRojo) {
          torito++;
        }
      }
      if ((binocular + botiquin + torito) >= celdasConObjetos) {
        break;
      }
    }
    int armaduras = 0;
    for (int i = 0; ; i++) {
      int posX = Utiles.aleatorio(0, this.getMapaTamHorizonal());
      int posY = Utiles.aleatorio(0, this.getMapaTamHorizonal());
      Point p2 = new Point(posX, posY);
      Celda celdaAleatoria = this.getCelda(p2);
      if (celdaAleatoria.isTransitable() && this.getCelda(p2).getObjetos().isEmpty()) {
        Armadura armadura =
            new Armadura(
                "armadura", "armadura" + i, "la armadura" + i, peso, efecto, salud, efecto);
        this.getCelda(p2).setObjeto(armadura);
        armaduras++;
        if (armaduras == CONST.TOTAL_ARMADURAS) {
          break;
        }
      }
    }
    int armas = 0;
    int danho = 10;
    int armastotales = Utiles.aleatorio(4, 8);
    for (int i = 0; ; i++) {
      int posX = Utiles.aleatorio(0, this.getMapaTamHorizonal());
      int posY = Utiles.aleatorio(0, this.getMapaTamHorizonal());
      Point p2 = new Point(posX, posY);
      Celda celdaAleatoria = this.getCelda(p2);
      if (celdaAleatoria.isTransitable() && this.getCelda(p2).getObjetos().isEmpty()) {
        Arma arma =
            new Arma("arma", "arma" + i, "el arma" + i, danho, Utiles.aleatorio(1, 3), peso);
        this.getCelda(p2).setObjeto(arma);
        armas++;
        if (armas == armastotales) {
          break;
        }
      }
    }
  }

  public void colocaEnemigosMapa() throws ComandoExcepcion, ExcepcionMover {
    // colocamos enemigos en el mapa de forma aleatoria
    int efecto = 0;
    double peso = 10.0;
    int enemigos = 0;

    try {
      int celdasConEnemigos = Utiles.aleatorio(2, 5);
      for (int k = 0; ; k++) {
        int posX = Utiles.aleatorio(0, this.getMapaTamHorizonal());
        int posY = Utiles.aleatorio(0, this.getMapaTamHorizonal());
        Point p2 = new Point(posX, posY);
        Enemigo enemigo = null;
        Arma arma =
            new Arma(
                "arma",
                "arma" + k,
                "el arma" + k,
                Utiles.aleatorio(10, 30),
                Utiles.aleatorio(1, 2),
                peso);
        Mochila mochila = new Mochila("mochila", "mochila_" + "floater" + k, "desc", 5, 30);
        if (Utiles.aleatorio(1, 3) == 1) {
          mochila = new Mochila("mochila", "mochila_" + "floater" + k, "desc", 5, 30);
          mochila.setObjeto(arma);
          enemigo = new Floater("floater", "floater" + k, Utiles.aleatorio(10, 30), mochila);

        } else {
          mochila = new Mochila("mochila", "mochila_" + "floater" + k, "desc", 5, 30);
          mochila.setObjeto(arma);
          enemigo = new Sectoid("sectoid", "sectoid" + k, Utiles.aleatorio(10, 30), mochila);
        }
        enemigo.setEquipacion(arma);

        // ponemos armadura aleatoriamente
        if (Utiles.aleatorio(1, 3) == 3) {
          Armadura armadura =
              new Armadura(
                  "armadura",
                  "armadura" + k,
                  "la armadura" + k,
                  peso,
                  efecto,
                  Utiles.aleatorio(20, 40),
                  efecto);
          mochila.setObjeto(armadura);
          enemigo.setEquipacion(armadura);
        }

        Celda celdaAleatoria = this.getCelda(p2);
        if (celdaAleatoria.isTransitable()) {
          this.getCelda(p2).setPersonaje(enemigo);
          enemigo.setPosicionMapa(p2);
          enemigos++;
          this.setPersonaje(enemigo);
        }
        if (enemigos == celdasConEnemigos) {
          break;
        }
      }
    } catch (ComandoExcepcion ex) {
      MiConsola.printRojo("colocaEnemigosMapa: " + ex.toString());
    }
  }

  public Celda getCelda(Point coordenadas) {
    return this.celdas.get(coordenadas);
  }

  public void imprimirMapa() {
    MiConsola.printAmarillo(pintarMapaCompleto(this.getCelda(new Point(0, 0))));
    //        Point actual = new Point();
    //        for (int i = 0; i < this.getMapaTamVertical(); i++) {
    //            for (int j = 0; j < this.getMapaTamHorizonal(); j++) {
    //
    //                Point p = new Point(i, j);
    //                if (p.equals(actual)) {
    //                    System.out.print("\u263A ");
    //
    ////                } else if (p.equals(objetivo)) {
    ////                    System.out.print("+ ");
    //                } else {
    //                    if (celdas.get(p).isTransitable()) {
    //                        if (celdas.get(p).getDescripcion() != null &&
    // celdas.get(p).getDescripcion().equals("camino dificil")) {
    //                            System.out.print("\u2653 ");
    //                        } else {
    //                            System.out.print("\u26F6 ");
    //                        }
    //
    //                    } else {
    //                        System.out.print(CONST.CARACTER_NO_TRANSITABLE + " ");
    //                    }
    //                }
    //            }
    //            System.out.println("");
    //        }
    //
  }

  public String pintarMapaParcial(Celda celda) {
    String mapaParcial = "";
    int x = celda.getPosicionMapa().x;
    int y = celda.getPosicionMapa().y;
    mapaParcial = "posicion actual: " + celda.getPosicionMapa() + "\n";
    for (int i = x - 1; i <= x + 1; i++) {
      for (int j = y - 1; j <= y + 1; j++) {
        if (i >= 0 && j >= 0) {
          Celda celda_adyacente = celdas.get(new Point(i, j));
          if (x == i && y == j) {
            mapaParcial += CONST.CARACTER_POSICION_ACTUAL;
          } else {
            mapaParcial += pintaCaracterCelda(celda_adyacente);
          }
        }
      }
      mapaParcial += "\n";
    }
    return mapaParcial;
  }

  public String pintarMapaParcialDistancia(Celda celda, int distancia) {
    String mapaParcial = "";
    int x = celda.getPosicionMapa().x;
    int y = celda.getPosicionMapa().y;
    mapaParcial = "posicion actual: " + celda.getPosicionMapa() + "\n";
    for (int i = x - distancia; i <= x + distancia; i++) {
      for (int j = y - distancia; j <= y + distancia; j++) {
        if (i >= 0 && j >= 0) {
          Celda celda_adyacente = celdas.get(new Point(i, j));
          if (celda_adyacente != null) {
            if (x == i && y == j) {
              mapaParcial += CONST.CARACTER_POSICION_ACTUAL;
            } else {
              mapaParcial += pintaCaracterCelda(celda_adyacente);
            }
          }
        }
      }
      if (i >= 0 && i <= this.getMapaTamVertical()) {
        mapaParcial += "\n";
      }
    }
    return mapaParcial;
  }

  /**
   * muestra todo el mapa con las ubicaciones de objetos y guerreros
   *
   * @param celda
   * @return
   */
  public String pintarMapaCompleto(Celda celda) {
    String mapaParcial = "";
    int x = celda.getPosicionMapa().x;
    int y = celda.getPosicionMapa().y;
    mapaParcial = "posicion actual: " + celda.getPosicionMapa() + "\n";
    for (int i = 0; i < this.getMapaTamVertical(); i++) {
      for (int j = 0; j < this.getMapaTamHorizonal(); j++) {
        if (i >= 0 && j >= 0) {
          Celda celda_adyacente = celdas.get(new Point(i, j));
          if (celda_adyacente != null) {
            if (x == i && y == j) {
              mapaParcial += CONST.CARACTER_POSICION_ACTUAL;
            } else {
              mapaParcial += pintaCaracterCelda(celda_adyacente);
            }
          }
        }
      }
      mapaParcial += "\n";
    }
    return mapaParcial;
  }

  public String pintarMapaParcial2(Celda celda, ArrayList<Point> puntosDescubiertos) {
    String mapaParcial = "";

    int x = celda.getPosicionMapa().x;
    int y = celda.getPosicionMapa().y;
    mapaParcial = "posicion actual: " + celda.getPosicionMapa() + "\n";
    for (int i = 0; i < this.getMapaTamVertical(); i++) {
      for (int j = 0; j < this.getMapaTamHorizonal(); j++) {
        Celda celda_act = celdas.get(new Point(i, j));
        // si es una celda descubierta pintamos el caracter que le corresponda
        if (puntosDescubiertos.contains(celda_act.getPosicionMapa())) {
          if (x == i && y == j) {
            mapaParcial += CONST.CARACTER_POSICION_ACTUAL;
          } else {
            mapaParcial += pintaCaracterCelda(celda_act);
          }
        } else {
          // no es una celda descubierta -> pintamos un interrogante
          mapaParcial += CONST.CARACTER_INTERROGANTE;
        }
      }
      mapaParcial += "\n";
    }
    return mapaParcial;
  }

  private String pintaCaracterCelda(Celda celda) {
    String caracter = "";
    if (celda.getObjetos() != null && !celda.getObjetos().isEmpty()) {
      Objeto obj = celda.getObjetos().get(0);
      if (obj instanceof Armadura) {
        caracter = CONST.CARACTER_ARMADURA;
      } else if (obj instanceof Arma) {
        caracter = CONST.CARACTER_ARMA;
      } else {
        caracter = CONST.CARACTER_OBJETO;
      }
    } else if (celda.isTransitable()) {
      caracter = CONST.CARACTER_TRANSITABLE;
    } else if (!celda.isTransitable()) {
      caracter = CONST.CARACTER_NO_TRANSITABLE;
    }

    if (!celda.getPersonajes().isEmpty()) {
      caracter = CONST.CARACTER_ENEMIGO;
    }
    return caracter;
  }

  /**
   * movimiento de todos los personajes que no sean el jugador
   *
   * @throws ExcepcionPers
   */
  //    public void moverRestoPersonajes(int alcance, Personaje jugador) throws ExcepcionMover,
  // CloneNotSupportedException {
  //        List<Point> pOrigen = new ArrayList<>();
  //        List<Point> pDestino = new ArrayList<>();
  //        List<Personaje> personajesMovidos = new ArrayList<>();
  //        for (int i = 0; i < this.getMapaTamVertical(); i++) {
  //            for (int j = 0; j < this.getMapaTamHorizonal(); j++) {
  //                Point puntoOrigen = new Point(i, j);
  //                //Consola.printAmarillo("i=" + i  + ", j=" + j);
  //                for (Personaje pers : this.getCelda(puntoOrigen).getPersonajes()) {
  //                    //Consola.printVerde(pers.getNombre() + ": " + pers.getPosicionMapa());
  //                    //movemos personajes que no sean el jugador
  //                    if (pers != null && "enemigo".equalsIgnoreCase(pers.getTipo())) {
  //
  //                        String[] direccion = {"norte", "sur", "este", "oeste"};
  //                        int pos = Utiles.aleatorio(0, 3);
  //
  //                        Point destino = pers.mover(this, direccion[pos]);
  //
  //                        if (!puntoOrigen.equals(destino)) {
  //                            pOrigen.add(puntoOrigen);
  //                            pDestino.add(destino);
  //                            personajesMovidos.add(pers);
  //                        }
  //                        MiConsola.printVerde(pers.getNombre() + ": " + puntoOrigen + " -> " +
  // direccion[pos] + " -> " + pers.getPosicionMapa());
  //                        //miramos si está en rango el jugador para ser atacado
  //                        String cmdAtaqueAJugador = atacarJugador(pers, alcance, jugador);
  //                        //si hay comando ataca
  //                        if (!"".equals(cmdAtaqueAJugador)) {
  //                            pers.atacar(cmdAtaqueAJugador, this);
  //                        }
  //                    }
  //                }
  //            }
  //        }
  //        //borramos a los personajes de las celdas de las que se movieron
  //        int i = 0;
  //        for (Personaje pers : personajesMovidos) {
  //            //Consola.printRojo("borrando personaje duplicado: " + pers.toString());
  //            this.getCelda(pOrigen.get(i)).getPersonajes().remove(pers);
  //            this.getCelda(pDestino.get(i)).getPersonajes().add(pers);
  //            pers.setPosicion(pDestino.get(i));
  //            i++;
  //        }
  //    }
  //    public String atacarJugador(Personaje pers, int alcance, Personaje jugador) {
  //        String comandoAtaque = "";
  //        Point puntoPersonaje = pers.getPosicionMapa();
  //        int x = puntoPersonaje.x;
  //        int y = puntoPersonaje.y;
  //
  //        boolean bCumple = false;
  //        //hay jugador en el rango, miramos si esta en la direccion atacable (norte, sur, este,
  // oeste)
  //        String direccion = "";
  //        int num = -1;
  //
  //        if (jugador.getPosicionMapa().x == x) {
  //            if (jugador.getPosicionMapa().y >= y) {
  //                num = jugador.getPosicionMapa().y - y;
  //                direccion = num + "este";
  //            } else {
  //                num = y - jugador.getPosicionMapa().y;
  //                direccion = num + "oeste";
  //            }
  //        } else if (jugador.getPosicionMapa().y == y) {
  //            if (jugador.getPosicionMapa().x >= x) {
  //                num = jugador.getPosicionMapa().x - x;
  //                direccion = num + "sur";
  //            } else {
  //                num = x - jugador.getPosicionMapa().x;
  //                direccion = num + "norte";
  //            }
  //        }
  //
  //        //si el jugador está en alcance
  //        if (num != -1 && num <= alcance) {
  //            bCumple = true;
  //        }
  //
  //        if (bCumple) {
  //            // esta el jugador en perpendicular para ser atacado, miramos si no hay obstaculos
  // en medio
  //            if (!direccion.equalsIgnoreCase("")) {
  //                MiConsola.printAmarillo("posicion ataque de enemigo " + pers.getNombre() + ": "
  // + puntoPersonaje + " a jugador:" + jugador.getPosicionActualString());
  //                int pIni;
  //                int pFin;
  //                if (direccion.contains("norte") || direccion.contains("sur")) {
  //                    if (puntoPersonaje.x > jugador.getPosicionMapa().x) {
  //                        pIni = jugador.getPosicionMapa().x;
  //                        pFin = puntoPersonaje.x;
  //                    } else {
  //                        pIni = puntoPersonaje.x;
  //                        pFin = jugador.getPosicionMapa().x;
  //                    }
  //                    for (int i = pIni; i < pFin; i++) {
  //
  //                        if (!this.getCelda(new Point(i,
  // jugador.getPosicionMapa().y)).isTransitable()) {
  //                            bCumple = false;
  //                            break;
  //                        }
  //                    }
  //                } else {
  //                    if (puntoPersonaje.y > jugador.getPosicionMapa().y) {
  //                        pIni = jugador.getPosicionMapa().y;
  //                        pFin = puntoPersonaje.y;
  //                    } else {
  //                        pIni = puntoPersonaje.y;
  //                        pFin = jugador.getPosicionMapa().y;
  //                    }
  //                    for (int i = pIni; i < pFin; i++) {
  //                        if (!this.getCelda(new Point(jugador.getPosicionMapa().x,
  // i)).isTransitable()) {
  //                            bCumple = false;
  //                            MiConsola.printAmarillo("ataque imposible por obstaculos en medio en
  // celda: " + jugador.getPosicionMapa().x + "," + i);
  //                            break;
  //                        }
  //                    }
  //                }
  //            }
  //
  //            if (bCumple) {
  //                comandoAtaque = direccion;
  //            }
  //
  //        }
  //        return comandoAtaque;
  //    }
  public void verEnemigos() throws ExcepcionPers {
    for (int i = 0; i < this.getMapaTamVertical(); i++) {
      for (int j = 0; j < this.getMapaTamHorizonal(); j++) {
        for (Personaje pers : this.getCelda(new Point(i, j)).getPersonajes()) {
          if (pers != null && "enemigo".equalsIgnoreCase(pers.getTipo())) {
            MiConsola.printVerde(pers.toString());
          }
        }
      }
    }
  }

  public void verObjetos() throws ExcepcionPers {
    for (int i = 0; i < this.getMapaTamVertical(); i++) {
      for (int j = 0; j < this.getMapaTamHorizonal(); j++) {
        for (Objeto obj : this.getCelda(new Point(i, j)).getObjetos()) {
          if (obj != null) {
            MiConsola.printVerde("pos: " + i + "," + j + ", " + obj.toString());
          }
        }
      }
    }
  }

  @Override
  public String toString() {
    return "Mapa{"
        + "mapaTamHorizonal="
        + mapaTamHorizonal
        + ", mapaTamVertical="
        + mapaTamVertical
        + ", Nombre="
        + Nombre
        + ", Descripcion="
        + Descripcion
        + ", celdas="
        + celdas
        + ", CeldadeComienzo="
        + CeldadeComienzo
        + ", jugador="
        + jugador
        + ", personajes="
        + personajes.toString()
        + '}';
  }
}
