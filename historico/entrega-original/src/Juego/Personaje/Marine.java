package Juego.Personaje;

import Utilidades.CONST;
import Utilidades.MiConsola;
import excepciones.ComandoExcepcion;
import excepciones.ExcepcionMover;
import java.util.ArrayList;
import java.util.List;

public final class Marine extends Jugador {

  public Marine(String nombre) {
    super(nombre);
  }

  public Marine(String tipo, String nombre, int energia, Mochila mochila) throws ComandoExcepcion {
    super(tipo, nombre, energia, mochila);
  }

  @Override
  public int calculaGastoEnergetico() {
    int totalGasto = CONST.CANT_ENERG_POR_CELDA + 2;
    double multiplicador = 1;
    int cont = 0;
    for (Objeto obj : this.getEquipacion()) {
      if (obj instanceof Arma) {
        if (((Arma) obj).getManos() == 2) {
          cont++;
        }
      }
    }
    if (cont == 2) {
      multiplicador = 1.5;
    }
    if (this.getMochila() != null) {
      totalGasto += (int) ((this.getMochila().getPeso() / 4) * multiplicador);
    }

    return totalGasto;
  }

  /**
   * Si tiene 2 armas de 2 manos equipadas gasta 1.5 veces más
   *
   * @throws ExcepcionMover
   */
  @Override
  public void gastoEnergetico() throws ComandoExcepcion {
    this.setEnergia(this.getEnergia() - calculaGastoEnergetico());
  }

  /**
   * Los marines hacen el doble de daño a corta distancia al tener mala puntería, sólo harán un 5%
   * de daño cuando ataquen con un arma a más de 2 casillas de distancia
   *
   * @param enemigo
   * @param armas
   * @param persAtacables
   * @return
   */
  @Override
  protected int danhoCausado(Personaje enemigo, List<Arma> armas, int persAtacables) {
    int danho = super.danhoCausado(enemigo, armas, persAtacables);
    int numCeldas = this.celdasSeparacion(enemigo);
    if (numCeldas <= 1) {
      danho = danho * 2;
    } else if (numCeldas > 2) {
      danho = danho - (int) (danho * 0.5);
    }
    MiConsola.printAmarillo("danho causado por Marine: " + danho);
    return danho;
  }

  /**
   * El marine tiene la habilidad especial de poder equipar dos armas a dos manos simultáneamente
   *
   * @param objeto
   */
  @Override
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
          List<Objeto> armasEquipadas = new ArrayList<>();
          // si es un arma miramos que cumpla 2 de 1 mano o 1 de 2 manos
          for (Objeto obj : this.getEquipacion()) {
            if (obj instanceof Arma) {
              if (((Arma) obj).getManos() == 1) {
                unaMano.add(obj);
              } else {
                dosManos.add(obj);
              }
              armasEquipadas.add(obj);
            }
          }

          // el marine permite 2 armas a 2 manos a la vez
          // si tiene 2 le quitamos una, preferentemente de 1 mano para equipar la nueva
          if (armasEquipadas.size() == 2) {
            if (!unaMano.isEmpty()) {
              objetosDesequipar.add(unaMano.get(0));
            } else if (!dosManos.isEmpty()) {
              objetosDesequipar.add(dosManos.get(0));
            }
          }
        }
        this.equipa(objeto, objetosDesequipar);
        MiConsola.printVerde("Marine equipado= " + objeto.getNombre());
      }
    } else {
      MiConsola.printRojo("no existe este objeto en la mochila= " + objeto.getNombre());
    }
  }
}
