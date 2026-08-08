package com.legendoftecla.gui;

import com.legendoftecla.constants.Dificultad;
import com.legendoftecla.constants.CondicionVictoria;
import com.legendoftecla.engine.ConfiguracionPartida;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.file.Path;
import java.util.function.Consumer;

/** Asistente grafico para el juego clasico y los escenarios desde ficheros. */
public final class PanelConfiguracion extends JPanel {
    private record Opcion(String etiqueta, String valor) {
        @Override
        public String toString() {
            return etiqueta;
        }
    }

    /**
     * Ejecuta la operacion publica {@code JTextField}.
     */
    private final JTextField nombre = new JTextField("Tecla", 24);
    /**
     * Valor publico {@code clase} utilizado por el modelo del juego.
     */
    private final JComboBox<Opcion> clase = new JComboBox<>(new Opcion[]{
            new Opcion("Marine", "marine"),
            new Opcion("Francotirador", "francotirador"),
            new Opcion("Zapador", "zapador")
    });
    /**
     * Valor publico {@code modo} utilizado por el modelo del juego.
     */
    private final JComboBox<Opcion> modo = new JComboBox<>(new Opcion[]{
            new Opcion("Mapa predeterminado", "default"),
            new Opcion("Escenario desde ficheros / JSON", "ficheros")
    });
    /**
     * Ejecuta la operacion publica {@code JTextField}.
     */
    private final JTextField directorio = new JTextField(30);
    /**
     * Ejecuta la operacion publica {@code JButton}.
     */
    private final JButton examinar = new JButton("Examinar...");

    /**
     * Crea una instancia de {@code PanelConfiguracion}.
      * @param abrirEditor valor de {@code abrirEditor}
      * @param iniciar valor de {@code iniciar}
     */
    public PanelConfiguracion(Consumer<ConfiguracionPartida> iniciar, Runnable abrirEditor) {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel titulo = new JLabel("THE LEGEND OF TECLA", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 28f));
        JLabel subtitulo = new JLabel("Configuracion de partida", SwingConstants.CENTER);
        subtitulo.setFont(subtitulo.getFont().deriveFont(Font.PLAIN, 16f));
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.add(titulo, BorderLayout.CENTER);
        cabecera.add(subtitulo, BorderLayout.SOUTH);
        add(cabecera, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createEmptyBorder(30, 100, 20, 100));
        int fila = 0;
        agregarFila(formulario, fila++, "Nombre del personaje", nombre);
        agregarFila(formulario, fila++, "Clase", clase);
        agregarFila(formulario, fila++, "Modo", modo);
        JPanel selectorDirectorio = new JPanel(new BorderLayout(5, 0));
        selectorDirectorio.add(directorio, BorderLayout.CENTER);
        selectorDirectorio.add(examinar, BorderLayout.EAST);
        agregarFila(formulario, fila, "Directorio del escenario", selectorDirectorio);
        add(formulario, BorderLayout.CENTER);

        examinar.addActionListener(e -> seleccionarDirectorioConDialogo());
        modo.addActionListener(e -> actualizarModo());
        actualizarModo();

        JButton jugar = new JButton("Iniciar partida en GUI");
        jugar.setFont(jugar.getFont().deriveFont(Font.BOLD, 15f));
        jugar.addActionListener(e -> {
            try {
                iniciar.accept(crearConfiguracion());
            } catch (RuntimeException error) {
                JOptionPane.showMessageDialog(this, error.getMessage(),
                        "Configuracion no valida", JOptionPane.ERROR_MESSAGE);
            }
        });
        JButton editor = new JButton("Editor grafico de mapas");
        editor.addActionListener(e -> abrirEditor.run());
        JPanel botones = new JPanel();
        botones.add(jugar);
        botones.add(editor);
        add(botones, BorderLayout.SOUTH);
    }

    /**
     * Ejecuta la operacion publica {@code seleccionarDirectorio}.
      * @param ruta valor de {@code ruta}
     */
    public void seleccionarDirectorio(Path ruta) {
        seleccionarDirectorio(ruta, false);
    }

    /**
     * Selecciona un escenario y aplica su preferencia de aliados.
     *
     * @param ruta directorio del escenario
     * @param usarAliados indica si se activara la generacion automatica
     */
    public void seleccionarDirectorio(Path ruta, boolean usarAliados) {
        directorio.setText(ruta.toAbsolutePath().toString());
        modo.setSelectedIndex(1);
    }

    private ConfiguracionPartida crearConfiguracion() {
        Opcion claseElegida = (Opcion) clase.getSelectedItem();
        Opcion modoElegido = (Opcion) modo.getSelectedItem();
        Path ruta = directorio.getText().isBlank() ? null : Path.of(directorio.getText().trim());
        return new ConfiguracionPartida(
                nombre.getText().trim(),
                claseElegida.valor(),
                modoElegido.valor(),
                Dificultad.NORMAL,
                null,
                ruta,
                false,
                CondicionVictoria.SOLO_JUGADOR,
                1);
    }

    private void seleccionarDirectorioConDialogo() {
        JFileChooser selector = new JFileChooser();
        selector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        selector.setDialogTitle("Selecciona el directorio del escenario");
        if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            seleccionarDirectorio(selector.getSelectedFile().toPath());
        }
    }

    private void actualizarModo() {
        Opcion seleccion = (Opcion) modo.getSelectedItem();
        boolean usaFicheros = seleccion != null && "ficheros".equals(seleccion.valor());
        directorio.setEnabled(usaFicheros);
        examinar.setEnabled(usaFicheros);
    }

    private void agregarFila(JPanel panel, int fila, String etiqueta, Component componente) {
        GridBagConstraints izquierda = new GridBagConstraints();
        izquierda.gridx = 0;
        izquierda.gridy = fila;
        izquierda.anchor = GridBagConstraints.LINE_END;
        izquierda.insets = new Insets(7, 7, 7, 14);
        panel.add(new JLabel(etiqueta + ":"), izquierda);

        GridBagConstraints derecha = new GridBagConstraints();
        derecha.gridx = 1;
        derecha.gridy = fila;
        derecha.weightx = 1;
        derecha.fill = GridBagConstraints.HORIZONTAL;
        derecha.insets = new Insets(7, 7, 7, 7);
        panel.add(componente, derecha);
    }
}
