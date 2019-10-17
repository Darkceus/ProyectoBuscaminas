package clientebuscaminas;

/**
 *
 * @author DARKCEUS
 */
import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.*;

public class TableroJuego extends JPanel {

    private final int BANDERA_J1 = 11;
    private final int BANDERA_J2 = 13;
    private final int BANDERA_J3 = 14;
    private final int BANDERA_J4 = 15;
    private final int BASE_1 = 0;
    private final int MINA = 9;
    private final int BASE_2 = 10;
    private final int NO_MINA = 12;
    private final int NUM_IMAGENES = 16;
    private final ImageIcon[] IMAGENES;
    private final int FILAS;
    private final int COLUMNAS;
    private final int TAM_ALTO;
    private final int TAM_ANCHO;
    private final int NUMERO_MINAS;
    private int minasRes;
    private int minutos = 0;
    private int segundos = 0;
    private String minutos2 = "";
    private String segundos2 = "";
    private Thread t;
    private final Boton[][] BOTONES;
    private final JLabel texto;
    private final JLabel tiempo;
    private boolean inicio;
    private int NUM_JUGADOR;
    private final PrintWriter out;
    private int ADMIN;

    public TableroJuego() {
        this.texto = null;
        this.tiempo = null;
        this.out = null;
        this.FILAS = 0;
        this.COLUMNAS = 0;
        this.TAM_ALTO = 0;
        this.TAM_ANCHO = 0;
        this.NUMERO_MINAS = 0;
        this.NUM_JUGADOR = 0;
        this.ADMIN = 0;
        this.BOTONES = null;
        this.IMAGENES = null;
    }

    public TableroJuego(JLabel texto, JLabel tiempo, PrintWriter out, int filas, int columnas, int AltoCampo, int AnchoCampo, int NumMinas, int NumJugador, int NumAdmin) {
        this.FILAS = filas;
        this.COLUMNAS = columnas;
        this.TAM_ALTO = AltoCampo;
        this.TAM_ANCHO = AnchoCampo;
        this.NUMERO_MINAS = NumMinas;
        this.NUM_JUGADOR = NumJugador;
        this.ADMIN = NumAdmin;
        this.minasRes = NUMERO_MINAS;
        this.out = out;
        this.texto = texto;
        this.texto.setText("Banderas Restantes: " + minasRes);
        this.tiempo = tiempo;
        this.setLayout(new GridLayout(FILAS, COLUMNAS));
        this.IMAGENES = new ImageIcon[NUM_IMAGENES];
        cargarImagenes();
        this.BOTONES = new Boton[FILAS][COLUMNAS];
        crearBotones();
        inicio = true;
        Tiempo();
    }

    private void cargarImagenes() {
        for (int i = 0; i < NUM_IMAGENES; i++) {
            String ruta = "/imagenes/" + i + ".png";
            IMAGENES[i] = new ImageIcon((new ImageIcon(this.getClass().getResource(ruta))).getImage().getScaledInstance(TAM_ANCHO, TAM_ALTO, java.awt.Image.SCALE_DEFAULT));
        }
    }

    public void ponerBandera(int x, int y, int numJugador) {
        BOTONES[x][y].setIcon(obtenerIcono(numJugador));
        //BOTONES[x][y].setIcon(iconoJugador);
        if (numJugador == NUM_JUGADOR) {
            minasRes--;
            texto.setText("Banderas Restantes: " + minasRes);
        }
    }

    public void actualizarDatos(int numJugador, int numAdmin, boolean perdio) {
        if (this.NUM_JUGADOR != numJugador) {
            this.NUM_JUGADOR = numJugador;
        }
        if (perdio && inicio) {
            this.inicio = false;
            t.stop();
        } else if (!perdio && !inicio) {
            this.inicio = true;
            t.start();
        }
        if (this.ADMIN != numAdmin) {
            this.ADMIN = numAdmin;
        }
    }

    public void quitarBandera(int x, int y, int numJugador) {
        BOTONES[x][y].setIcon(IMAGENES[BASE_2]);
        if (numJugador == NUM_JUGADOR) {
            minasRes++;
            texto.setText("Banderas Restantes: " + minasRes);
        }
    }

    public void hayMina(int x, int y, int numJugador) {
        BOTONES[x][y].setIcon(IMAGENES[MINA]);
        if (numJugador == NUM_JUGADOR) {
            inicio = false;
            t.stop();
            JOptionPane.showMessageDialog(this, "Perdiste");
        }
    }

    public void noMina(int x, int y) {
        BOTONES[x][y].setIcon(IMAGENES[NO_MINA]);
    }

    public void hayMina2(int x, int y) {
        BOTONES[x][y].setIcon(IMAGENES[MINA]);
    }

    public void descubrirCampo(int x, int y, int valorCampo) {
        BOTONES[x][y].setIcon(IMAGENES[valorCampo]);
    }

    public void descubrirCampo2(int x, int y) {
        BOTONES[x][y].setIcon(IMAGENES[BASE_1]);
    }

    private Icon obtenerIcono(int valor) {
        switch (valor) {
            case 1:
                return IMAGENES[BANDERA_J1];
            case 2:
                return IMAGENES[BANDERA_J2];
            case 3:
                return IMAGENES[BANDERA_J3];
            case 4:
                return IMAGENES[BANDERA_J4];
        }
        return null;
    }

    private void crearBotones() {
        for (int y = 0; y < COLUMNAS; y++) {
            for (int x = 0; x < FILAS; x++) {
                BOTONES[x][y] = new Boton(x, y);
                BOTONES[x][y].setIcon(IMAGENES[BASE_2]);
                BOTONES[x][y].setPreferredSize(new Dimension(TAM_ANCHO, TAM_ALTO));
                BOTONES[x][y].addMouseListener(new EventoClic());
                this.add(BOTONES[x][y]);
                BOTONES[x][y].setEnabled(true);
            }
        }
    }

    private void Tiempo() {
        minutos = 0;
        segundos = 0;
        tiempo.setText("Tiempo: 00:00");
        minutos2 = "";
        segundos2 = "";
        t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(1000);
                        segundos++;
                        if (segundos > 60) {
                            segundos = 0;
                            minutos++;
                        }
                        minutos2 = (minutos < 10) ? ("0" + minutos) : ("" + minutos);
                        segundos2 = (segundos < 10) ? ("0" + segundos) : ("" + segundos);
                        tiempo.setText("Tiempo: " + minutos2 + ":" + segundos2);
                    } catch (InterruptedException e) {
                    }
                }
            }
        };
        t.start();
    }

    public void cerrarJuego() {
        JFrame frame = (JFrame) this.getTopLevelAncestor();
        frame.dispose();
    }

    private class Boton extends JButton {

        private final int X;
        private final int Y;

        public Boton(int x, int y) {
            super();
            this.X = x;
            this.Y = y;
        }

        public int getX2() {
            return this.X;
        }

        public int getY2() {
            return this.Y;
        }
    }

    private class EventoClic extends MouseAdapter {

        public EventoClic() {
            super();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            //if (inicio) {
                Boton boton = (Boton) e.getSource();
                String clic = "CLIC";
                if (e.getButton() == MouseEvent.BUTTON1) {
                    clic += "IZQUIERDO ";
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    clic += "DERECHO ";
                }
                clic += boton.getX2() + "," + boton.getY2() + "," + minasRes;
                out.println(clic);
            //}
        }
    }
}