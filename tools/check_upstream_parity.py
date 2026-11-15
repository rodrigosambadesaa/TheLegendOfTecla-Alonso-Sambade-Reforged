#!/usr/bin/env python3
"""Comprueba la paridad de codigo y recursos con TheLegendOfTecla.

Se ejecuta con el original descargado aparte. Las excepciones Mago estan
acotadas por nombre de fichero y representan adaptaciones de personajes/clases
que no deben sobrescribirse al sincronizar cambios del motor comun.
"""
from __future__ import annotations

import argparse
import sys
from pathlib import Path

MAGO_CHANGED = frozenset({
    "src/main/java/com/legendoftecla/Main.java",
    "src/main/java/com/legendoftecla/config/OpcionesInicio.java",
    "src/main/java/com/legendoftecla/engine/ConfiguracionPartida.java",
    "src/main/java/com/legendoftecla/gui/PanelConfiguracion.java",
    "src/main/java/com/legendoftecla/loader/CargadorJuegoBase.java",
    "src/main/java/com/legendoftecla/loader/CargadorJuegoDeFicheros.java",
    "src/main/java/com/legendoftecla/loader/CargadorJuegoGrandeConAliados.java",
    "src/main/java/com/legendoftecla/loader/CargadorJuegoJson.java",
    "src/main/java/com/legendoftecla/loader/CargadorJuegoPorDefecto.java",
    "src/main/java/com/legendoftecla/loader/CargadorJuegoProcedural.java",
    "src/main/java/com/legendoftecla/model/characters/Francotirador.java",
    "src/main/java/com/legendoftecla/model/characters/Marine.java",
    "src/main/java/com/legendoftecla/model/characters/Zapador.java",
    "src/main/java/com/legendoftecla/persistence/PersistenciaPartida.java",
})
MAGO_EXTRA = frozenset({
    "src/main/java/com/legendoftecla/model/characters/Alquimista.java",
    "src/main/java/com/legendoftecla/model/characters/Guerrero.java",
    "src/main/java/com/legendoftecla/model/characters/Mago.java",
})


def inventario(raiz: Path) -> dict[str, Path]:
    directorio = raiz / "src" / "main"
    if not directorio.is_dir():
        raise ValueError(f"No existe el arbol fuente: {directorio}")
    return {
        ruta.relative_to(raiz).as_posix(): ruta
        for ruta in directorio.rglob("*")
        if ruta.is_file()
    }


def verificar(local: Path, original: Path, variante: str) -> int:
    propios = inventario(local)
    oficiales = inventario(original)
    cambios_permitidos = MAGO_CHANGED if variante == "mago" else frozenset()
    extras_permitidos = MAGO_EXTRA if variante == "mago" else frozenset()

    faltantes = sorted(oficiales.keys() - propios.keys())
    nuevos = sorted((propios.keys() - oficiales.keys()) - extras_permitidos)
    divergentes = sorted(
        ruta for ruta in oficiales.keys() & propios.keys()
        if ruta not in cambios_permitidos
        and oficiales[ruta].read_bytes() != propios[ruta].read_bytes()
    )
    obsoletos = sorted(
        ruta for ruta in cambios_permitidos
        if ruta in oficiales and ruta in propios
        and oficiales[ruta].read_bytes() == propios[ruta].read_bytes()
    )

    for etiqueta, rutas in (
        ("FALTA", faltantes),
        ("EXTRA NO AUTORIZADO", nuevos),
        ("DIFERENCIA NO AUTORIZADA", divergentes),
    ):
        for ruta in rutas:
            print(f"{etiqueta}: {ruta}", file=sys.stderr)
    for ruta in obsoletos:
        print(f"NOTA: excepcion Mago ya identica, se puede retirar: {ruta}")
    print(
        f"Paridad {variante}: {len(oficiales)} archivos originales, "
        f"{len(cambios_permitidos)} adaptaciones permitidas y "
        f"{len(extras_permitidos)} clases propias; "
        f"{len(faltantes) + len(nuevos) + len(divergentes)} discrepancias."
    )
    return 1 if faltantes or nuevos or divergentes else 0


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--variant", required=True, choices=["alonso", "mago"])
    parser.add_argument("--upstream", default=".upstream-original")
    args = parser.parse_args()
    try:
        return verificar(Path.cwd(), Path(args.upstream).resolve(), args.variant)
    except (OSError, ValueError) as error:
        print(f"No se puede verificar la paridad: {error}", file=sys.stderr)
        return 2


if __name__ == "__main__":
    raise SystemExit(main())
