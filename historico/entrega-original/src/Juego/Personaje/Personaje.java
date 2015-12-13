/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Juego.Personaje;

import Juego.Mapa.Celda;
import Juego.Mapa.Mapa;
import Utilidades.CONST;
import Utilidades.ConsolaNormal;
import Utilidades.MiConsola;
import Utilidades.Utiles;
import excepciones.ComandoExcepcion;
import excepciones.ExcepcionFatal;
import excepciones.ExcepcionMover;
import excepciones.ExcepcionPers;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Rodrigo Sambade Saá y Miguel Alonso Castro
 */
public class Personaje {

  protected String nombre;
  private String tipo;
  private int salud;
  private int energia;
  ArrayList<Point> posicion;
  Point posicionMapa;
  protected Mochila mochila;
  private List<Objeto> equipacion = new ArrayList<Objeto>();
  private Armadura armadura;
  private Arma arma;
  protected int alcance = 1;
  ConsolaNormal c = new ConsolaNormal();

  // Constructores
  public Personaje(String nombre) {
    this.nombre = nombre;
    posicion = new ArrayList<>();
  }

  public Personaje(String tipo, String nombre, int energia, Mochila mochila)
      throws ComandoExcepcion {
    this.tipo = tipo;
    this.setNombre(nombre);
    this.setEnergia(energia);
    this.setMochila(mochila);
    posicion = new ArrayList<>();
  }

  public String getNombre() {
    return nombre;
  }

  public void setPosicion(ArrayList<Point> posicion) {
    this.posicion = posicion;
  }

  public Point getPosicionMapa() {
    return posicionMapa;
  }

  public void setPosicionMapa(Point posicionMapa) {
    this.posicionMapa = posicionMapa;
  }

  public String getTipo() {
    return tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public int getSalud() {
    return salud;
  }

  public int getEnergia() {
    return energia;
  }

  public Mochila getMochila() {
    return mochila;
  }

  public void cogerObjeto(Celda celda, String objetoACoger)
      throws ComandoExcepcion, ExcepcionMover {
    // si es la mochila la agregamos al jugador
    if ("mochila".equalsIgnoreCase(objetoACoger)) {
      Mochila mochila = (Mochila) celda.buscarObjetoPorNombre(objetoACoger);
      this.setMochila(mochila);
    } else if (this.mochila != null) {
      // si tiene mochila buscamos el objeto para agregarlo
      this.mochila.cogerObjeto(celda, objetoACoger, this);
    } else {
      MiConsola.printRojo("el jugador no tiene mochila");
    }
  }

  /**
   * los personajes enemigo cogen sus armas y armaduras que estan en la celdas de situacion inicial
   * del personaje
   *
   * @param celda
   * @throws ComandoExcepcion
   */
  public void cogerObjetoAutomaticoEnemigo(Celda celda) throws ComandoExcepcion {
    List<Objeto> objetos = new ArrayList<>();
    for (Objeto obj : celda.getObjetos()) {
      // si no contiene este objeto
      if (!this.mochila.getObjetos().contains(obj)) {
        if (obj.getTipo().equalsIgnoreCase("arma")) {
          if (((Arma) obj).getPortador().equalsIgnoreCase(this.getNombre())) {
            // los metemos en la mochila
            this.getMochila().setObjeto((Arma) obj);
            // lo equipamos
            this.setEquipacion((Arma) obj);
            objetos.add(obj);
          }
        }
        if (obj.getTipo().equalsIgnoreCase("armadura")) {
          if (((Armadura) obj).getPortador().equalsIgnoreCase(this.getNombre())) {
            // lo metemos en la mochila
            this.getMochila().setObjeto((Armadura) obj);
            this.setEquipacion((Armadura) obj);
            objetos.add(obj);
          }
        }
      }
    }

    for (Objeto obj : objetos) {
      celda.getObjetos().remove(obj);
    }
  }

  //    public Armadura getArmadura() {
  //        return armadura;
  //    }
  //
  //    public void setArmadura(Armadura armadura) {
  //        this.armadura = armadura;
  //    }
  //
  //    public Arma getArma() {
  //        return arma;
  //    }
  //
  //    public void setArma(Arma arma) {
  //        this.arma = arma;
  //    }
  public List<Objeto> getEquipacion() {
    return equipacion;
  }

  public void setEquipacion(List<Objeto> equipacion) {
    this.equipacion = equipacion;
  }

  public List<Arma> armasEquipadas() {
    List<Arma> equipadas = new ArrayList<>();
    for (Objeto obj : this.getEquipacion()) {
      if (obj instanceof Arma) {
        equipadas.add((Arma) obj);
      }
    }
    return equipadas;
  }

  public Armadura armaduraEquipada() {
    Armadura armaduraEquipada = null;
    for (Objeto obj : this.getEquipacion()) {
      if (obj instanceof Armadura) {
        armaduraEquipada = (Armadura) obj;
        break;
      }
    }
    return armaduraEquipada;
  }

  /**
   * Para equiparlo buscamos el objeto de la mochila y si se encuentra lo quitamos y lo equipamos
   *
   * @param objeto
   */
  public void setEquipacion(Objeto objeto) {
    // comprobamos si lo tiene en la mochila
    int pos = this.mochila.getObjetos().indexOf(objeto);
    if (pos > -1) {
      if (this.getEquipacion().contains(objeto)) {
        MiConsola.printRojo("ese objeto ya esta equipado: " + objeto.getNombre());
      } else {
        List<Objeto> objetosDesequipar = new ArrayList<>();
        // esta en la mochila y no está equipado
        if (objeto instanceof Armadura) {
          // buscamos si hay una armadura para quitar
          for (Objeto equipado : this.getEquipacion()) {
            if (equipado instanceof Armadura) {
              objetosDesequipar.add(equipado);
            }
          }
        } else if (objeto instanceof Arma) {
          Arma armaAEquipar = (Arma) objeto;
          List<Objeto> unaMano = new ArrayList<>();
          List<Objeto> dosManos = new ArrayList<>();
          // si es un arma miramos que cumpla 2 de 1 mano o 1 de 2 manos
          for (Objeto obj : this.getEquipacion()) {
            if (obj instanceof Arma) {
              if (((Arma) obj).getManos() == 1) {
                unaMano.add(obj);
              } else {
                dosManos.add(obj);
              }
            }
          }

          if (armaAEquipar.getManos() == 2) {
            // si la nueva es de 2 manos quitaremos todas las que tenga ocupadas
            objetosDesequipar.addAll(unaMano);
            objetosDesequipar.addAll(dosManos);
          } else {
            // si es de 1 mano si tiene de 2 manos la quitamos y si tiene 2 de 1 quitamos 1
            if (!dosManos.isEmpty()) {
              objetosDesequipar.addAll(dosManos);
            } else {
              if (unaMano.size() > 1) {
                // le quitamos 1 de las 2 de 1 mano que tiene, la primera
                objetosDesequipar.add(unaMano.get(0));
              }
            }
          }
        }
        this.equipa(objeto, objetosDesequipar);
        MiConsola.printVerde(this.nombre + " equipando= " + objeto.getNombre());
      }
    } else {
      MiConsola.printRojo("no existe este objeto en la mochila= " + objeto.getNombre());
    }
  }

  /**
   * si no lo tiene equipado lo equipa si tiene otro equipado lo metemos en la mochila y equipamos
   * este @Objeto objeto
   *
   * @param objetoAEquipar
   * @param objetosDesequipar
   */
  public void equipa(Objeto objetoAEquipar, List<Objeto> objetosDesequipar) {
    // los objetos  a desequipar los desequipamos y los agregamos a la mochila
    for (Objeto objDesequipar : objetosDesequipar) {
      this.getEquipacion().remove(objDesequipar);
      this.mochila.getObjetos().add(objDesequipar);
    }

    // el objeto a equipar: se equipa y se quita de la mochila
    this.getEquipacion().add(objetoAEquipar);
    this.mochila.getObjetos().remove(objetoAEquipar);
  }

  public void desequipa(String nombreObjeto) {
    Objeto objDesequipar = null;
    for (Objeto objeto : this.getEquipacion()) {
      if (objeto.getNombre().equalsIgnoreCase(nombreObjeto)) {
        objDesequipar = objeto;
        break;
      }
    }
    if (objDesequipar != null) {
      this.getEquipacion().remove(objDesequipar);
      this.mochila.getObjetos().add(objDesequipar);
      MiConsola.printAmarillo("desequipado: " + nombreObjeto);
    } else {
      MiConsola.printRojo("no hay equipado ese objeto= " + nombreObjeto);
    }
  }

  /**
   * Equipa un objeto si lo tiene en la mochila
   *
   * @param nombreObjeto
   */
  public void setEquipacion(String nombreObjeto) {
    Objeto quitarMochila = null;
    for (Objeto obj : this.getMochila().getObjetos()) {
      if (obj.getNombre().equalsIgnoreCase(nombreObjeto)) {
        quitarMochila = obj;
      }
    }
    if (quitarMochila != null) {
      this.setEquipacion(quitarMochila);
      this.mochila.getObjetos().remove(quitarMochila);
      MiConsola.printAmarillo("equipado: " + nombreObjeto);
    } else {
      MiConsola.printRojo("no hay este objeto en la mochila para equipar= " + nombreObjeto);
    }
  }

  //    public Point getPosicionMapa() {
  //        if (posicion.isEmpty()) {
  //            return new Point(0, 0);
  //        } else {
  //            return posicion.get(posicion.size() - 1);
  //        }
  //    }

  public String getPosicionActualString() {
    String cadena;
    int x = this.getPosicionMapa().x;
    int y = this.getPosicionMapa().y;

    cadena = "(" + x + "," + y + ")";
    return cadena;
  }

  public ArrayList<Point> getPosicion() {
    return this.posicion;
  }

  // Setters
  public void setSalud(int salud) {
    if (salud > 0 && salud < 101) {
      this.salud = salud;
    } else {
      System.out.println("La salud debe estar entre 0 y 100");
    }
  }

  public boolean incrementaSalud(int saludIncremento) {
    boolean bIncrementa = false;
    if (this.getSalud() != 100) {
      int total = this.getSalud() + saludIncremento;
      // nunca superaremos 100
      if (total > 100) {
        total = 100;
      }
      this.setSalud(total);
      bIncrementa = true;
    }
    return bIncrementa;
  }

  public boolean incrementaEnergia(int energiaIncremento) throws ComandoExcepcion {
    boolean bIncrementa = false;
    if (this.getEnergia() != 100) {
      int total = this.getEnergia() + energiaIncremento;
      // nunca superaremos 100
      if (total > 100) {
        total = 100;
      }
      this.setEnergia(total);
      bIncrementa = true;
    }
    return bIncrementa;
  }

  public void setNombre(String nombre) {
    if (nombre.length() < 100) {
      this.nombre = nombre;
    } else {
      System.out.println("El nombre es demasiado largo");
    }
  }

  public void setEnergia(int energ) throws ComandoExcepcion {
    if (energ > 0) {
      if (energ > 100) {
        energia = 100;
      } else {
        this.energia = energ;
      }
    } else {
      System.out.println("La energia debe tener un valor entre 0 y 100");
      if (energia <= 0) {
        throw new ComandoExcepcion("muerte del personaje por agotamiento de energia");
      }
    }
  }

  public void setPosicion(Point posicion) {
    this.posicion.add(posicion);
  }

  public void setMochila(Mochila mochila) {
    this.mochila = mochila;
  }

  public int calculaGastoEnergetico() {
    int totalGasto = CONST.CANT_ENERG_POR_CELDA;
    if (this.getMochila() != null) {
      totalGasto += (int) this.getMochila().getPeso() / 5;
    }
    return totalGasto;
  }

  public void gastoEnergetico() throws ComandoExcepcion {
    this.setEnergia(this.getEnergia() - calculaGastoEnergetico());
  }

  // metodos
  /**
   * retona un String con la posicion inicial del personaje en el mapa, exemplos 0,0
   *
   * @param mapa
   * @return
   */
  public Point empezar(Mapa mapa) throws ExcepcionPers {
    int x = Utiles.aleatorio(0, mapa.getMapaTamVertical());
    int y = Utiles.aleatorio(0, mapa.getMapaTamHorizonal());
    Point p = new Point(x, y);
    while (true) {
      if (mapa.getCelda(p) != null && mapa.getCelda(p).isTransitable()) {
        this.setPosicion(p);
        break;
      }
    }
    // asignamos un arma por defecto
    // Arma armaInicial = new Arma(6, "1_manos", "arma_inicial", 5, "arma", 10);
    // this.getMochila().setObjeto(armaInicial);
    return this.getPosicionMapa();
  }

  public Point empezar(Mapa mapa, Point posicion) {
    this.setPosicion(posicion);
    return posicion;
  }

  /**
   * mover un personaje en el mapa
   *
   * @param mapa
   * @param direccion
   * @return
   * @throws ExcepcionMover
   */
  public Point mover(Mapa mapa, String direccion) throws ExcepcionMover, ComandoExcepcion {
    boolean bContinuar = true;
    int gastoEner = calculaGastoEnergetico();
    if ((this.getEnergia() - gastoEner) <= 0) {
      if (this instanceof Jugador) {
        this.setEnergia(100);
        c.imprimir(
            "sin energia para moverte, tienes: "
                + this.getEnergia()
                + ", necesitas: "
                + gastoEner
                + ", en el proximo movimiento ya la recuperas (100)");
      }
      c.imprimir(
          "sin energia para moverte, tienes: "
              + this.getEnergia()
              + ", necesitas: "
              + gastoEner
              + ", "
              + this.getNombre());
      bContinuar = false;
    }
    Point puntoDestino = null;
    int i = (int) this.getPosicionMapa().getX();
    int j = (int) this.getPosicionMapa().getY();
    Point pPosInicial = this.getPosicionMapa();
    puntoDestino = pPosInicial;
    if (bContinuar) {
      if (i == 0 && "norte".equalsIgnoreCase(direccion)) {
        MiConsola.printRojo(this.getNombre() + " sales del mapa por el norte");
      } else if (j == 0 && "oeste".equalsIgnoreCase(direccion)) {
        MiConsola.printRojo(this.getNombre() + " sales del mapa por el oeste");
      } else if (j == mapa.getMapaTamHorizonal() - 1 && "este".equalsIgnoreCase(direccion)) {
        MiConsola.printRojo(this.getNombre() + " sales del mapa por el este");
      } else if (i == mapa.getMapaTamVertical() - 1 && "sur".equalsIgnoreCase(direccion)) {
        MiConsola.printRojo(this.getNombre() + " sales del mapa por el sur");
      } else {
        if ("norte".equalsIgnoreCase(direccion)) {
          i--;
        } else if ("sur".equalsIgnoreCase(direccion)) {
          i++;
        } else if ("oeste".equalsIgnoreCase(direccion)) {
          j--;
        } else if ("este".equalsIgnoreCase(direccion)) {
          j++;
        }
        Point p = new Point(i, j);
        Celda celda = mapa.getCelda(p);
        if (celda.isTransitable()) {
          this.gastoEnergetico();
          this.posicion.add(new Point(i, j));
          if (!pPosInicial.equals(p)) {
            mapa.getCelda(p).setPersonaje(this);
            mapa.getCelda(pPosInicial).getPersonajes().remove(this);
          }
          this.setPosicionMapa(p);
          puntoDestino = p;
        } else {
          MiConsola.printRojo("celda no transitable:" + i + "," + j);
        }
      }
    }
    return puntoDestino;
  }

  public void imprimePosicion() {
    for (int i = 0; i < posicion.size(); i++) {
      String cadena;
      int x = posicion.get(i).x;
      int y = posicion.get(i).y;

      cadena = "(" + x + "," + y + ")";
      System.out.println(cadena);
    }
  }

  public boolean tieneObjeto(String tipo) {
    boolean tiene = false;
    // si tiene Mochila miramos en ella
    if (this.mochila != null) {
      tiene = this.mochila.tieneObjeto(tipo);
    }
    return tiene;
  }

  public int rangoVision() {
    return alcance;
  }

  /**
   * mirar y describe los objetos de la celda en la que está el personje Si no hay objetos o piden
   * un objeto que no existe en la celda lanzamos una excepcion
   *
   * @param mapa
   * @param objMirado
   * @throws ComandoExcepcion
   */
  public void mirar(Mapa mapa, String objMirado) throws ComandoExcepcion {
    Celda celda = mapa.getCelda(this.getPosicionMapa());
    String infoObjeto = "";
    if (celda.getObjetos().isEmpty()) {
      throw new ComandoExcepcion("no hay objetos en la celda");
    } else {
      if ("".equalsIgnoreCase(objMirado)) {
        for (Objeto objeto : celda.getObjetos()) {
          infoObjeto += "\n" + objeto.getNombre() + ",";
        }
      } else {
        // si viene el nombre del objeto lo detallamos
        for (Objeto objeto : celda.getObjetos()) {
          if (objeto.getNombre().equalsIgnoreCase(objMirado)) {
            infoObjeto += "\nobjeto detallado=" + objeto.toString();
          }
        }
      }
      if (!"".equals(infoObjeto)) {
        c.imprimir(infoObjeto.replaceAll(",$", ""));
      } else {
        throw new ComandoExcepcion("no hay ese objeto en la celda: " + objMirado);
      }
    }
  }

  /**
   * permite mirar en una celda remota accesible los personajes si damos nombre de personaje nos da
   * sólo sus datos
   *
   * @param mapa
   * @param pers
   * @param cmd
   */
  public void mirarExt(Mapa mapa, Personaje pers, String cmd) {
    String cmdAux = cmd;
    cmdAux = cmdAux.replaceAll("mirar ", "");
    Pattern pCmd = Pattern.compile("([0-9]+)(.*)");
    Matcher mCmd = pCmd.matcher(cmdAux);
    int numCeldas = -1;
    String direc = "";
    String enemigo = "";
    while (mCmd.find()) {
      numCeldas = Integer.parseInt(mCmd.group(1));
      direc = mCmd.group(2);
      if (direc.contains(" ")) {
        String[] partes = direc.split(" ");
        direc = partes[0];
        enemigo = partes[1];
      }
    }

    if (!direc.matches("(norte|sur|este|oeste)")) {
      MiConsola.printRojo("Comando invalido: " + cmd);
    } else {
      if (numCeldas > pers.rangoVision()) {
        MiConsola.printRojo("intentas ver fuera de alcance: " + pers.rangoVision());
      } else {
        int x = pers.getPosicionMapa().x;
        int y = pers.getPosicionMapa().y;
        // Point puntoBuscado = pers.getPosicionMapa();
        if (direc.equals("norte")) {
          x -= numCeldas;
        } else if (direc.equals("sur")) {
          x += numCeldas;
        } else if (direc.equals("oeste")) {
          y -= numCeldas;
        } else if (direc.equals("este")) {
          y += numCeldas;
        }
        Point puntoBuscado = new Point(x, y);
        // buscamos la celda
        // y que esté dentro del mapa
        if (hayObstaculos(mapa, pers, direc)) {
          MiConsola.printRojo("ataque impedido por obstaculos");
        } else {
          if ("".equals(enemigo)) {
            String personejesCelda = "";
            // nos piden uno mostramos los que pueda haber en la celda
            for (Personaje per : mapa.getCelda(puntoBuscado).getPersonajes()) {
              personejesCelda += per.getNombre() + ",";
            }
            if (!"".equals(personejesCelda)) {
              MiConsola.printVerde(
                  "personajes en celda "
                      + puntoBuscado
                      + ": "
                      + personejesCelda.replaceAll(",$", ""));
            } else {
              MiConsola.printRojo("ningun personaje en la celda: " + puntoBuscado);
            }

          } else {
            Personaje persBuscado = mapa.getCelda(puntoBuscado).getPersonaje(enemigo);
            if (persBuscado != null) {
              MiConsola.printVerde("detalle: " + persBuscado.toString());
            } else {
              MiConsola.printRojo(
                  "no existe: " + persBuscado.toString() + ", en la celda: " + puntoBuscado);
            }
          }
        }
      }
    }
  }

  /**
   * usar un objeto si lo tiene en la mochila, si lo llega a utilizar lo eliminamos de la mochila
   *
   * @param comandousar
   * @throws ExcepcionFatal
   * @throws ComandoExcepcion
   */
  public void usar(String comandousar) throws ExcepcionFatal, ComandoExcepcion {
    // miramos si lo tiene en la mochila
    boolean bEncontrado = false;
    for (Objeto objeto : this.getMochila().getObjetos()) {
      if (objeto instanceof Botiquin && objeto.getNombre().equalsIgnoreCase(comandousar)) {
        if (((Botiquin) objeto).usar(this)) {
          // una vez usado lo eliminamos de la mochila
          this.getMochila().eliminarObjetoDeMochila(objeto);
          c.imprimir(
              "usado botiquin y eliminado de mochila: "
                  + objeto.getNombre()
                  + ", salud despues de usarlo: "
                  + this.salud);
        }
        bEncontrado = true;
        break;
      } else if (objeto instanceof ToritoRojo && objeto.getNombre().equalsIgnoreCase(comandousar)) {
        if (((ToritoRojo) objeto).usar(this)) {
          // una vez usado lo eliminamos de la mochila
          this.getMochila().eliminarObjetoDeMochila(objeto);
          c.imprimir(
              "usado toritoRojo y eliminado de mochila: "
                  + objeto.getNombre()
                  + ", energia despues de usarlo: "
                  + this.energia);
        }
        bEncontrado = true;
        break;
      } else if (objeto instanceof Binoculares
          && objeto.getNombre().equalsIgnoreCase(comandousar)) {
        ((Binoculares) objeto).usar(this);
        bEncontrado = true;
        break;
      } else if (objeto instanceof Arma && objeto.getNombre().equalsIgnoreCase(comandousar)) {
        ((Arma) objeto).usar(this);
        bEncontrado = true;
        break;
      } else if (objeto instanceof Armadura && objeto.getNombre().equalsIgnoreCase(comandousar)) {
        ((Armadura) objeto).usar(this);
        bEncontrado = true;
        break;
      }
    }

    if (!bEncontrado) {
      throw new ComandoExcepcion("no tienes el objeto para usar: " + comandousar);
    }

    //            if ("binoculares".equalsIgnoreCase(objetoAUsar)) {
    //                int distancia = 1;
    //                if (jugador.getMochila().tieneObjeto("binoculares")) {
    //                    distancia = CONST.CELDAS_DISTANCIA_BINOCULARES;
    //                } else {
    //                    MiConsola.printRojo("no tienes binoculares");
    //                }
    //                String mapaParcial =
    // mapa.pintarMapaParcialDistancia(mapa.getCelda(jugador.getPosicionMapa()), distancia);
    //                System.out.println(mapaParcial);
    //                jugador.mirar(mapa, "");
    //            }
  }

  protected boolean bHayObstaculos2(Mapa mapa, Point p1, Point p2, String direc)
      throws ComandoExcepcion {
    boolean bHay = true;

    int xMenor = p1.x;
    int xMayor = p2.x;
    int yMenor = p1.y;
    int yMayor = p2.y;
    if (xMenor > xMayor) {
      xMenor = p2.x;
      xMayor = p1.x;
    }
    if (yMenor > yMayor) {
      yMenor = p2.y;
      yMayor = p1.y;
    }

    if (p1.x == p2.x) {
      Point puntoCompr = new Point(p1.x, p1.y);
      for (int i = yMenor; i < yMayor; i++) {
        if (direc.equals("oeste")) {
          puntoCompr.translate(0, -1);
        } else {
          puntoCompr.translate(0, 1);
        }

        if (!mapa.getCelda(puntoCompr).isTransitable()) {
          throw new ComandoExcepcion("celda con obstaculo en:" + puntoCompr);
        }
      }
    } else {
      Point puntoCompr = new Point(p1.x, p1.y);
      for (int i = xMenor; i < xMayor; i++) {
        // si direccion es norte se resta
        if (direc.equals("norte")) {
          puntoCompr.translate(-1, 0);
        } else {
          puntoCompr.translate(1, 0);
        }

        if (!mapa.getCelda(puntoCompr).isTransitable()) {
          throw new ComandoExcepcion("celda con obstaculo en:" + puntoCompr);
        }
      }
    }
    return bHay;
  }

  public void atacar2(String comandoAtaque, Mapa mapa)
      throws ExcepcionFatal, ComandoExcepcion, ExcepcionMover {
    // miramos si es comando con 2 direcciones
    if (this.armasEquipadas().isEmpty()) {
      throw new ComandoExcepcion("no se puede atacar sin armas=" + this.nombre);
    }
    String movimientos = "";
    String nombreEnemigo = "";
    String numAtaques = "";
    Point p2 = null;
    String[] pa = comandoAtaque.split(" ");
    // si hay 2 tiene que ser el nombre ejemplos: 1este sectoid1, 1norte2este sectoid1 2
    movimientos = pa[0];
    if (pa.length == 2) {
      nombreEnemigo = pa[1];
    } else if (pa.length == 3) {
      // el tercero tiene que ser un numero
      if (!pa[3].matches("[0-9]+")) {
        throw new ComandoExcepcion(
            "si hay tercer parametro sólo puede ser un número (número de ataques): " + pa[3]);
      } else {
        numAtaques = pa[3];
      }
    }
    if (movimientos.matches(".*(norte|sur|este|oeste).+(norte|sur|este|oeste).*")) {
      Pattern p = Pattern.compile("([0-9]+)(norte|sur|este|oeste)([0-9]+)(norte|sur|este|oeste)");
      Matcher m = p.matcher(movimientos);
      String mov1 = "";
      String direc1 = "";
      String mov2 = "";
      String direc2 = "";
      if (m.find()) {
        mov1 = m.group(1);
        direc1 = m.group(2);
        mov2 = m.group(3);
        direc2 = m.group(4);

        if (Integer.parseInt(mov1) > this.rangoVision()
            || Integer.parseInt(mov2) > this.rangoVision()) {
          throw new ComandoExcepcion("fuera de rango de alcance=" + this.rangoVision());
        }
        p2 = new Point(this.getPosicionMapa().x, this.getPosicionMapa().y);
        Point pVirtual = (Point) this.getPosicionMapa().clone();
        if (direc1.matches("norte")) {
          p2.move(p2.x - Integer.parseInt(mov1), p2.y);
        } else if (direc1.matches("sur")) {
          p2.move(p2.x + Integer.parseInt(mov1), p2.y);
        } else if (direc1.matches("oeste")) {
          p2.move(p2.x, p2.y - Integer.parseInt(mov1));
        } else if (direc1.matches("este")) {
          p2.move(p2.x, p2.y + Integer.parseInt(mov1));
        }
        pVirtual = (Point) p2.clone();
        if (mapa.getCelda(pVirtual) == null) {
          throw new ComandoExcepcion("celda inexistente en mapa=" + pVirtual);
        }
        bHayObstaculos2(mapa, pVirtual, p2, direc1);

        if (direc2.matches("norte")) {
          p2.move(p2.x - Integer.parseInt(mov2), p2.y);
        } else if (direc2.matches("sur")) {
          p2.move(p2.x + Integer.parseInt(mov2), p2.y);
        } else if (direc2.matches("oeste")) {
          p2.move(p2.x, p2.y - Integer.parseInt(mov2));
        } else if (direc2.matches("este")) {
          p2.move(p2.x, p2.y + Integer.parseInt(mov2));
        }
        if (mapa.getCelda(pVirtual) == null) {
          throw new ComandoExcepcion("celda inexistente en mapa=" + pVirtual);
        }
        bHayObstaculos2(mapa, pVirtual, p2, direc2);
      }
    } else if (movimientos.matches(".*(norte|sur|este|oeste)")) {
      Pattern p = Pattern.compile("([0-9]+)(norte|sur|este|oeste)");
      Matcher m = p.matcher(movimientos);
      String mov1 = "";
      String direc1 = "";

      if (m.find()) {
        mov1 = m.group(1);
        direc1 = m.group(2);

        if (Integer.parseInt(mov1) > this.rangoVision()) {
          throw new ComandoExcepcion("fuera de rango de alcance=" + this.rangoVision());
        }
        p2 = new Point(this.getPosicionMapa().x, this.getPosicionMapa().y);
        if (direc1.matches("norte")) {
          p2.move(p2.x - Integer.parseInt(mov1), p2.y);
        } else if (direc1.matches("sur")) {
          p2.move(p2.x + Integer.parseInt(mov1), p2.y);
        } else if (direc1.matches("oeste")) {
          p2.move(p2.x, p2.y - Integer.parseInt(mov1));
        } else if (direc1.matches("este")) {
          p2.move(p2.x, p2.y + Integer.parseInt(mov1));
        }
        if (mapa.getCelda(p2) == null) {
          throw new ComandoExcepcion("celda inexistente en mapa=" + p2);
        }
        bHayObstaculos2(mapa, this.getPosicionMapa(), p2, direc1);
      }
    }

    if (mapa.getCelda(p2) == null) {
      throw new ComandoExcepcion("celda inexistente no hay personajes en la celda: " + p2);
    } else if (mapa.getCelda(p2).getPersonajes().isEmpty()) {
      throw new ComandoExcepcion("no hay personajes en la celda: " + p2);
    } else {
      List<Personaje> persAtacables = new ArrayList<>();
      // si es ataque a un enemigo en la celda y nos dan su nombre atacamos solo a ese
      if (!"".equals(nombreEnemigo)) {
        Personaje enemigo = mapa.getCelda(p2).getPersonaje(nombreEnemigo);
        persAtacables.add(enemigo);
      } else {
        persAtacables.addAll(mapa.getCelda(p2).getPersonajes());
      }
      List<Personaje> cadaveres = new ArrayList<>();
      for (Personaje enemigo : persAtacables) {
        // solo pueden atacarse entre jugador y enemigo, entre enemigos no
        if ((this instanceof Jugador && enemigo instanceof Enemigo)
            || (this instanceof Enemigo && enemigo instanceof Jugador)) {
          int danho =
              danhoCausado(
                  enemigo, this.armasEquipadas(), mapa.getCelda(p2).getPersonajes().size());
          enemigo.restaSalud(danho);
          if (enemigo.getSalud() < 1) {
            MiConsola.printVerde("enemigo eliminado");
            if (enemigo instanceof Jugador) {
              MiConsola.printRojo("juego acabado por muerte del jugador");
              throw new ExcepcionFatal("final por muerte del jugador");
            }
            cadaveres.add(enemigo);
          }
        } else {
          c.imprimir("no se pueden atacar entre enemigos");
        }
      }
      for (Personaje cadaver : cadaveres) {
        gestionaCadaver(cadaver, mapa);
      }
    }
  }

  //    public void atacar(String comandoAtaque, Mapa mapa) throws ExcepcionFatal, ComandoExcepcion,
  // ExcepcionMover {
  //        String cmd;
  //        //atacar2(comandoAtaque, mapa);
  //        String nombreEnemigo = "";
  //        String[] partesCmd = comandoAtaque.split(" ");
  //        cmd = partesCmd[0];
  //        if (partesCmd.length == 2) {
  //            nombreEnemigo = partesCmd[1];
  //        }
  //        Pattern pNums = Pattern.compile("^([0-9]+)(.*)$");
  //        Matcher mNums = pNums.matcher(cmd);
  //        int num = 0;
  //        String direccion = "";
  //        while (mNums.find()) {
  //            num = Integer.parseInt(mNums.group(1));
  //            direccion = mNums.group(2);
  //        }
  //
  //        //atacamos a distancia en la direccion indicada
  //        int x = 0;
  //        int y = 0;
  //        if ("oeste".equalsIgnoreCase(direccion) || "este".equalsIgnoreCase(direccion)) {
  //            x = this.getPosicionMapa().x;
  //            if ("este".equalsIgnoreCase(direccion)) {
  //                y = this.getPosicionMapa().y + num;
  //            } else {
  //                y = this.getPosicionMapa().y - num;
  //            }
  //        } else if ("norte".equalsIgnoreCase(direccion) || "sur".equalsIgnoreCase(direccion)) {
  //            y = this.getPosicionMapa().y;
  //            if ("sur".equalsIgnoreCase(direccion)) {
  //                x = this.getPosicionMapa().x + num;
  //            } else {
  //                x = this.getPosicionMapa().x - num;
  //            }
  //        }
  //        if (x < 0 || x > mapa.getMapaTamHorizonal() || y < 0 || y > mapa.getMapaTamHorizonal())
  // {
  //            MiConsola.printRojo("ataque fuera de los límites del mapa");
  //        } else {
  //            Point posEnemigo = new Point(x, y);
  //            List<Personaje> persAtacables = new ArrayList<>();
  //            //si es ataque a un enemigo en la celda y nos dan su nombre atacamos solo a ese
  //            if (!"".equals(nombreEnemigo)) {
  //                Personaje enemigo = mapa.getCelda(posEnemigo).getPersonaje(nombreEnemigo);
  //                persAtacables.add(enemigo);
  //            } else {
  //                persAtacables.addAll(mapa.getCelda(posEnemigo).getPersonajes());
  //            }
  //
  //            if (persAtacables.isEmpty()) {
  //                MiConsola.printRojo("no hay enemigos en la celda= " + posEnemigo);
  //            } else {
  //                for (Personaje enemigo : persAtacables) {
  //                    //necesitamos que haya enemigo y estar equipado con armas
  //                    List<Arma> armas = armasEquipadas();
  //                    boolean bObstaculos = hayObstaculos(mapa, enemigo, direccion);
  //                    if (enemigo != null && !armas.isEmpty() && !bObstaculos) {
  ////                        //int danho = CONST.DANHO;
  ////                        int danho = 0;
  ////                        //se suma el efecto de las armas que tenga
  ////                        for (Arma armita : armas) {
  ////                            danho += armita.getDanho();
  ////                        }
  ////
  ////                        //si hay más de un personaje en la celda y se ataca sin nombre se
  // divide el danho
  ////                        if ("".equals(nombreEnemigo) && persAtacables.size() > 1) {
  ////                            danho = danho / 2;
  ////                        }
  ////
  ////                        int critico = Utiles.aleatorio(1, 4);
  ////                        //si el aleatorio es 4 el daño será crítico
  ////                        if (critico == 4) {
  ////                            danho *= 2;
  ////                            MiConsola.printVerde("ataque con daño critico");
  ////                        }
  ////
  ////                        // si tiene armadura le hace menos daño dependiendo de la cantidad de
  // defensa de la armadura
  ////                        if (enemigo.armaduraEquipada() != null) {
  ////                            danho = danho - (danho * (enemigo.armaduraEquipada().getDefensa()
  // / 100));
  ////                        }
  //                        int danho = danhoCausado(enemigo, armas, persAtacables.size());
  //                        enemigo.restaSalud(danho);
  //                        if (enemigo.getSalud() < 1) {
  //                            MiConsola.printVerde("enemigo eliminado");
  //                            if (enemigo instanceof Jugador) {
  //                                MiConsola.printRojo("juego acabado por muerte del jugador");
  //                                throw new ExcepcionFatal("final por muerte del jugador");
  //                            }
  //                            gestionaCadaver(enemigo, mapa);
  //                        } else {
  //                            MiConsola.printVerde("enemigo atacado " + enemigo.getNombre() + ",
  // le quitamos salud: " + danho + ", le queda= " + enemigo.getSalud());
  //                        }
  //                    } else {
  //                        if (armas.isEmpty()) {
  //                            MiConsola.printRojo("no tenemos armas para atacar");
  //                        }
  //                    }
  //                }
  //            }
  //        }
  //    }
  /**
   * numero de celdas de distancia entre el personaje y un enemigo
   *
   * @param enemigo
   * @return
   */
  protected int celdasSeparacion(Personaje enemigo) {
    int numCeldas = 0;
    if (this.posicionMapa.x == enemigo.getPosicionMapa().x) {
      if (this.posicionMapa.x > enemigo.getPosicionMapa().x) {
        numCeldas = this.posicionMapa.x - enemigo.getPosicionMapa().x;
      } else {
        numCeldas = enemigo.getPosicionMapa().x - this.posicionMapa.x;
      }
    } else {
      if (this.posicionMapa.y > enemigo.getPosicionMapa().y) {
        numCeldas = this.posicionMapa.y - enemigo.getPosicionMapa().y;
      } else {
        numCeldas = enemigo.getPosicionMapa().y - this.posicionMapa.y;
      }
    }
    return numCeldas;
  }

  protected int danhoCausado(Personaje enemigo, List<Arma> armas, int persAtacables) {

    int danho = 0;
    // se suma el efecto de las armas que tenga
    for (Arma armita : armas) {
      danho += armita.getDanho();
    }

    // si hay más de un personaje en la celda y se ataca sin nombre se divide el danho
    if ("".equals(enemigo.getNombre()) && persAtacables > 1) {
      danho = danho / 2;
    }

    int critico = Utiles.aleatorio(1, 5);
    // si el aleatorio es 4 el daño será crítico
    if (critico == 4) {
      danho *= 2;
      MiConsola.printVerde("ataque con daño critico");
    }

    // si tiene armadura le hace menos daño dependiendo de la cantidad de defensa de la armadura
    if (enemigo.armaduraEquipada() != null) {
      danho = danho - (danho * (enemigo.armaduraEquipada().getDefensa() / 100));
    }
    return danho;
  }

  private boolean hayObstaculos(Mapa mapa, Personaje enemigo, String direccion) {
    boolean bObstaculos = false;
    int celdaIni;
    int celdaFin;
    int x = this.getPosicionMapa().x;
    int y = this.getPosicionMapa().y;
    Point p = null;
    if (direccion.equals("norte") || direccion.equals("sur")) {
      if (this.getPosicionMapa().x < enemigo.getPosicionMapa().x) {
        celdaIni = this.getPosicionMapa().x;
        celdaFin = enemigo.getPosicionMapa().x;
      } else {
        celdaFin = this.getPosicionMapa().x;
        celdaIni = enemigo.getPosicionMapa().x;
      }
      for (int i = celdaIni; i < celdaFin; i++) {
        p = new Point(i, y);
        if (!mapa.getCelda(p).isTransitable()) {
          bObstaculos = true;
        }
      }
    } else {
      if (this.getPosicionMapa().y < enemigo.getPosicionMapa().y) {
        celdaIni = this.getPosicionMapa().y;
        celdaFin = enemigo.getPosicionMapa().y;
      } else {
        celdaFin = this.getPosicionMapa().y;
        celdaIni = enemigo.getPosicionMapa().y;
      }
      for (int i = celdaIni; i < celdaFin; i++) {
        p = new Point(x, i);
        if (!mapa.getCelda(p).isTransitable()) {
          bObstaculos = true;
          break;
        }
      }
    }

    if (bObstaculos) {
      MiConsola.printRojo("no se puede atacar por obstaculos en celda intermedia: " + p);
    }

    return bObstaculos;
  }

  // dejamos los objetos que pueda tener el enemigo en la celda en la que estaba
  private void gestionaCadaver(Personaje enemigo, Mapa mapa) {
    for (Objeto obj : enemigo.getMochila().getObjetos()) {
      mapa.getCelda(enemigo.getPosicionMapa()).setObjeto(obj);
      MiConsola.printAmarillo(
          "objeto de mochila de "
              + enemigo.getNombre()
              + " abandonados en la celda que lo matamos= "
              + enemigo.getPosicionActualString()
              + ", nombre objeto= "
              + obj.getNombre());
    }
    // también dejamos los objetos con los que vaya equipado
    for (Objeto obj : enemigo.getEquipacion()) {
      mapa.getCelda(enemigo.getPosicionMapa()).setObjeto(obj);
      MiConsola.printAmarillo(
          "objeto equipado de "
              + enemigo.getNombre()
              + " abandonados en la celda que lo matamos= "
              + enemigo.getPosicionActualString()
              + ", nombre objeto= "
              + obj.getNombre());
    }
    // lo eliminamos de la celda
    mapa.getCelda(enemigo.getPosicionMapa()).getPersonajes().remove(enemigo);
    // lo eliminamos del array de objetos personajes del mapa
    mapa.getPersonajes().remove(enemigo);
  }

  public String restaSalud(int danho) {
    String estado = "vivo";
    this.salud = this.getSalud() - danho;
    if (this.salud <= 0) {
      // MiConsola.printRojo("Personaje muerto por ataque= " + this.getNombre() + ", en celda= " +
      // this.getPosicionMapa());
      estado = "muerto";
    } else {
      // MiConsola.printAmarillo("personaje atacado= " + this.getNombre() + ", en celda= " +
      // this.getPosicionActualString() + ", le queda salud: " + this.salud);
    }
    return estado;
  }

  public void inventario() {
    if (this.getMochila() != null) {
      MiConsola.printVerde(this.getMochila().inventario());
    } else {
      MiConsola.printRojo("no tiene mochila");
    }
  }

  @Override
  public String toString() {
    return "Personaje{"
        + "nombre="
        + nombre
        + ", tipo="
        + tipo
        + ", salud="
        + salud
        + ", energia="
        + energia
        + ", posicionMapa="
        + posicionMapa
        + ", mochila="
        + mochila
        + ", equipacion="
        + equipacion
        + ", armadura="
        + armadura
        + ", arma="
        + arma
        + '}';
  }
}
