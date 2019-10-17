package clientebuscaminas;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author DARKCEUS
 */
public class PrincipalClienteJugador extends JFrame {

    String direccion;
    int puerto;
    Scanner in;
    PrintWriter out;
    private TableroJuego juego;
    JFrame frame = new JFrame("Chat");
    JTextField textField = new JTextField(50);
    JTextArea messageArea = new JTextArea(16, 50);
    private final JLabel tiempo;
    private final JLabel texto;
    private ArrayList<Integer> numerosJugadores;

    public PrincipalClienteJugador() {
        validarDireccion(getDireccion());
        validarPuerto(getPuerto());
        this.setTitle("Buscaminas");
        texto = new JLabel("Banderas Restantes: 0");
        this.add(texto, BorderLayout.NORTH);
        tiempo = new JLabel("Tiempo: 00:00");
        this.add(tiempo, BorderLayout.SOUTH);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.pack();
        juego = new TableroJuego();
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        textField.addActionListener((ActionEvent e) -> {
            out.println(textField.getText());
            textField.setText("");
        });
    }
    
    private String getDireccion(){
        return JOptionPane.showInputDialog(this, "Dirección", "Ingresa una Dirección: ", JOptionPane.PLAIN_MESSAGE);
    }
    
    private String getPuerto(){
        return JOptionPane.showInputDialog(this, "Puerto", "Ingresa un puerto: ", JOptionPane.PLAIN_MESSAGE);
    }
    
    private void getMensaje(String info){
        JOptionPane.showMessageDialog(this, info, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private String getNombre() {
        return JOptionPane.showInputDialog(frame, "Nombre", "Ingresa un Nombre: ", JOptionPane.PLAIN_MESSAGE);
    }
    
    private void mensaje(String line) {
        String info = line.substring(12);
        getMensaje(info);
    }

    private void run() throws IOException {
        try {
            Socket socket = new Socket(direccion, puerto);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            String[] tam;
            while (in.hasNextLine()) {                
                String linea = in.nextLine();
                if(linea.startsWith("NOMBREDEENVIO")){
                    out.println(getNombre());
                } else if (linea.startsWith("INFOMESSAGE")) {
                    mensaje(linea);
                } else if (linea.startsWith("PONERBANDERA ")) {
                    tam = linea.substring(13).split(",");
                    if (tam.length == 3) {
                        juego.ponerBandera(convertirInt(tam[0]), convertirInt(tam[1]), convertirInt(tam[2]));
                    } else {
                        getMensaje("Error al poner bandera");
                    }
                } else if (linea.startsWith("QUITARBANDERA ")) {
                    tam = linea.substring(14).split(",");
                    if (tam.length == 3) {
                        juego.quitarBandera(convertirInt(tam[0]), convertirInt(tam[1]), convertirInt(tam[2]));
                    } else {
                        getMensaje("Error al quitar bandera");
                    }
                } else if (linea.startsWith("ACTUALIZAR ")) {
                    tam = linea.substring(11).split(",");
                    if (tam.length == 3) {
                        System.out.println(Arrays.toString(tam));
                        getMensaje("Eres el jugador " + tam[0]);
                        juego.actualizarDatos(convertirInt(tam[0]), convertirInt(tam[1]), convertirBol(tam[2]));
                    } else {
                        getMensaje("Error al actualizar datos");
                    }
                } else if (linea.startsWith("DATOS ")) {
                    tam = linea.substring(6).split(",");
                    if (tam.length == 7) {
                        System.out.println(Arrays.toString(tam));
                        juego = new TableroJuego(texto, tiempo, out, convertirInt(tam[0]), convertirInt(tam[1]), convertirInt(tam[2]), convertirInt(tam[3]), convertirInt(tam[4]), convertirInt(tam[5]), convertirInt(tam[6]));
                        add(juego, BorderLayout.CENTER);
                        pack();
                        this.setVisible(true);
                    } else {
                        getMensaje("Error al recibir datos");
                    }
                } else if (linea.startsWith("NOMINA ")) {
                    tam = linea.substring(7).split(",");
                    if (tam.length == 2) {
                        juego.noMina(convertirInt(tam[0]), convertirInt(tam[1]));
                    } else {
                        getMensaje("Error al poner mina");
                    }
                } else if (linea.startsWith("2HAYMINA ")) {
                    tam = linea.substring(9).split(",");
                    if (tam.length == 2) {
                        juego.hayMina2(convertirInt(tam[0]), convertirInt(tam[1]));
                    } else {
                        getMensaje("Error al poner mina");
                    }
                } else if (linea.startsWith("HAYMINA ")) {
                    tam = linea.substring(8).split(",");
                    if (tam.length == 3) {
                        juego.hayMina(convertirInt(tam[0]), convertirInt(tam[1]), convertirInt(tam[2]));
                    } else {
                        getMensaje("Error al poner mina");
                    }
                } else if (linea.startsWith("2DESCUBRIRCAMPO ")) {
                    tam = linea.substring(16).split(",");
                    if (tam.length == 2) {
                        juego.descubrirCampo2(convertirInt(tam[0]), convertirInt(tam[1]));
                    } else {
                        getMensaje("Error al descubrir campo");
                    }
                } else if (linea.startsWith("DESCUBRIRCAMPO ")) {
                    tam = linea.substring(15).split(",");
                    if (tam.length == 3) {
                        juego.descubrirCampo(convertirInt(tam[0]), convertirInt(tam[1]), convertirInt(tam[2]));
                    } else {
                        getMensaje("Error al descubrir campo");
                    }
                } else if (linea.startsWith("NAMEACCEPTED ")) {
                    tam = linea.substring(13).split(",");
                    if (tam.length == 2) {
                        String sala = tam[1];
                        String nombre = tam[0];
                        frame.setTitle("Chat Buscaminas - Sala: " + sala + " - Jugador: " + nombre);
                        this.setTitle("Buscaminas - Sala: " + sala + " - Jugador: " + nombre);
                        textField.setEditable(true);
                    } else {
                        getMensaje("Error al recibir nombre");
                    }
                } else if (linea.startsWith("PUNTOS ")) {
                    tam = linea.substring(7).split("\\.");
                    if (tam.length > 0 && tam.length < 5) {
                        String jugadores = "";
                        for (String tam1 : tam) {
                            jugadores += tam1 + "\n";
                        }
                        getMensaje(jugadores);
                        juego.setVisible(false);
                        juego.cerrarJuego();
                    } else {
                        getMensaje("Error al recibir datos");
                    }
                } else if (linea.startsWith("MESSAGE ")) {
                    messageArea.append(linea.substring(8) + "\n");
                }
            }
        } finally {
            System.exit(0);
        }
    }
    
    private int convertirInt(String num) {
        int num2 = -1;
        try {
            num2 = Integer.parseInt(num);
        } catch (NumberFormatException e) {
            System.err.println("Debes de poner datos válidos");
            System.exit(0);
        }
        return num2;
    }
    
    private boolean convertirBol(String num) {
        boolean num2 = false;
        try {
            num2 = Boolean.parseBoolean(num);
        } catch (NumberFormatException e) {
            System.err.println("Debes de poner datos válidos");
            System.exit(0);
        }
        return num2;
    }

    public void Abrir() throws IOException {
        run();
        this.setVisible(true);
        this.setUndecorated(true);
    }

    private void validarDireccion(String valor) {
        if(valor == null || valor.equals("") || valor.isEmpty()){
            System.err.println("Debes de poner una dirección válida");
            System.exit(0);
        }
        if (valor.equalsIgnoreCase("localhost")) {
            this.direccion = "127.0.0.1";
            return;
        }
        char[] algo = valor.toCharArray();
        int cont = 0;
        for (int i = 0; i < algo.length; i++) {
            if (algo[i] == '.') {
                cont++;
            }
        }
        String[] dir = valor.split("\\.");
        if (dir.length == 4) {
            boolean bol = (convertirInt(dir[0]) <= 255) && (convertirInt(dir[1]) <= 255) && (convertirInt(dir[2]) <= 255) && (convertirInt(dir[3]) <= 254);
            if ((cont == 3) && (bol)) {
                this.direccion = valor;
            } else {
                System.err.println("Debes de poner una dirección válida");
                System.exit(0);
            }
        } else {
            System.err.println("Debes de poner una dirección válida");
            System.exit(0);
        }
    }

    private void validarPuerto(String valor) {
        try {
            this.puerto = Integer.parseInt(valor);
            if (this.puerto < 0) {
                System.err.println("Debes de poner un puerto válido");
                System.exit(0);
            }
        } catch (NumberFormatException e) {
            System.err.println("Debes de poner un puerto válido");
            System.exit(0);
        }
    }

    public static void main(String[] args) throws IOException {
        PrincipalClienteJugador cliente = new PrincipalClienteJugador();
        cliente.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cliente.frame.setVisible(true);
        cliente.run();

    }

    /*public class TableroJuego extends JPanel { //ActionListener, MouseListener

        private final int FILAS;
        private final int COLUMNAS;
        private final int tamAlto;
        private final int tamAncho;
        private final int NUM_JUGADOR;
        private int X;
        private int Y;
        private final JButton[][] BOTONES;

        public TableroJuego(int filas, int columnas, int AltoCampo, int AnchoCampo, int NumJugador) {
            FILAS = filas;
            COLUMNAS = columnas;
            tamAlto = AltoCampo;
            tamAncho = AnchoCampo;
            NUM_JUGADOR = NumJugador;
            BOTONES = new JButton[FILAS][COLUMNAS];
            iniciarTablero();
        }
        
        private void iniciarTablero(){
            this.setLayout(new GridLayout(FILAS, COLUMNAS));
            for (int y = 0; y < COLUMNAS; y++) {
                for (int x = 0; x < FILAS; x++) {
                    BOTONES[x][y] = new JButton();
                    BOTONES[x][y].setPreferredSize(new Dimension(tamAncho, tamAlto));
                    BOTONES[x][y].addMouseListener(new EventoClic());
                    this.add(BOTONES[x][y]);
                    BOTONES[x][y].setEnabled(true);
                }
            }
        }

        private String clic(JButton boton) {
            JButton boton2;
            for (int y = 0; y < COLUMNAS; y++) {
                for (int x = 0; x < FILAS; x++) {
                    boton2 = BOTONES[x][y];
                    if (boton2 == boton) {
                        X = x;
                        Y = y;
                        out.println(X + "," + Y);
                        return X + "," + Y;
                    }
                }
            }
            return "";
        }

        private class EventoClic extends MouseAdapter {
            
            public EventoClic(){
                
            }

            @Override
            public void mousePressed(MouseEvent e) {
                JButton boton = (JButton) e.getSource();
                String clic = "CLIC";
                if (e.getButton() == MouseEvent.BUTTON1) {
                    clic += "IZQUIERDO ";
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    clic += "DERECHO ";
                }
                clic += clic(boton);
                out.print(clic);
            }
        }
    }*/
}
