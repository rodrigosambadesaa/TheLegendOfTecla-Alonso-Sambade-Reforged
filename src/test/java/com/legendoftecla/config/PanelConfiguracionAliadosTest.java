package com.legendoftecla.config;

import com.legendoftecla.constants.CondicionVictoria;
import com.legendoftecla.engine.ConfiguracionPartida;
import com.legendoftecla.gui.PanelConfiguracion;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PanelConfiguracionAliadosTest {
    @Test
    void laGuiPermiteElegirAliadosYCondicionDeVictoria() throws Exception {
        AtomicReference<ConfiguracionPartida> elegida = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            PanelConfiguracion panel = new PanelConfiguracion(elegida::set, () -> { });
            JCheckBox aliados = (JCheckBox) buscarPorNombre(panel, "aliados.activados");
            @SuppressWarnings("unchecked")
            JComboBox<CondicionVictoria> victoria = (JComboBox<CondicionVictoria>)
                    buscarPorNombre(panel, "victoria.condicion");
            assertNotNull(aliados);
            assertNotNull(victoria);

            aliados.doClick();
            victoria.setSelectedItem(CondicionVictoria.SOLO_JUGADOR);
            JButton iniciar = buscarBoton(panel, "Iniciar partida en GUI");
            assertNotNull(iniciar);
            iniciar.doClick();
        });

        assertNotNull(elegida.get());
        assertTrue(elegida.get().conAliados());
        assertEquals(CondicionVictoria.SOLO_JUGADOR, elegida.get().condicionVictoria());
    }

    private Component buscarPorNombre(Container contenedor, String nombre) {
        for (Component componente : contenedor.getComponents()) {
            if (nombre.equals(componente.getName())) {
                return componente;
            }
            if (componente instanceof Container hijo) {
                Component encontrado = buscarPorNombre(hijo, nombre);
                if (encontrado != null) {
                    return encontrado;
                }
            }
        }
        return null;
    }

    private JButton buscarBoton(Container contenedor, String texto) {
        for (Component componente : contenedor.getComponents()) {
            if (componente instanceof JButton boton && texto.equals(boton.getText())) {
                return boton;
            }
            if (componente instanceof Container hijo) {
                JButton encontrado = buscarBoton(hijo, texto);
                if (encontrado != null) {
                    return encontrado;
                }
            }
        }
        return null;
    }
}
