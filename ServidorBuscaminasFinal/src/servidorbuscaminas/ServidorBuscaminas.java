package servidorbuscaminas;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JOptionPane;

/**
 *
 * @author DARKCEUS
 */
public class ServidorBuscaminas {

    public static final Map<Integer, Sala> SALAS = new TreeMap<>();
    private int puerto;
    
    public static void main(String[] args) {
        ServidorBuscaminas sb = new ServidorBuscaminas();
        sb.iniciarServidor();
    }
    
    private void iniciarServidor() {
        if (!validaciones()) {
            System.exit(0);
        }
        validarPuerto(getPuerto());
        System.out.println("El Servidor de Buscaminas está en línea...");
        ExecutorService pool = Executors.newFixedThreadPool(500);
        try (ServerSocket listener = new ServerSocket(this.puerto)) {
            while (true) {
                pool.execute(new Handler(listener.accept()));
            }
        } catch (IOException e) {
            System.out.println("Error al Abrir el Servidor");
        }
    }
    
    private boolean validaciones(){
        if (Juego.FILAS != Juego.COLUMNAS) {
            System.out.println("El número de Filas y Columnas deben ser iguales.");
            JOptionPane.showMessageDialog(null, "El número de Filas y Columnas deben ser iguales.");
            return false;
        }
        if ((Juego.FILAS > 30 || Juego.FILAS < 10) || (Juego.COLUMNAS > 30 || Juego.COLUMNAS < 10)) {
            System.out.println("El número de Filas y Columnas deben ser menores o iguales a 30 y mayores a 10.");
            JOptionPane.showMessageDialog(null, "El número de Filas y Columnas deben ser menores o iguales a 30 y mayores a 10.");
            return false;
        }
        if (Juego.TAM_ALTO != Juego.TAM_ANCHO) {
            System.out.println("El tamaño de los campos deben ser iguales.");
            JOptionPane.showMessageDialog(null, "El tamaño de los campos deben ser iguales.");
            return false;
        }
        if ((Juego.TAM_ALTO > 20 || Juego.TAM_ALTO < 10) || (Juego.TAM_ANCHO > 20 || Juego.TAM_ANCHO < 10)) {
            System.out.println("El tamaño de los campos deben ser menores o iguales a 20 y mayores a 3.");
            JOptionPane.showMessageDialog(null, "El tamaño de los campos deben ser menores o iguales a 20 y mayores a 3.");
            return false;
        }
        /*if ((Juego.FILAS < Juego.TAM_ANCHO) || (Juego.COLUMNAS < Juego.TAM_ALTO)) {
            System.out.println("El tamaño de las filas o columnas debe ser mayor o igual al tamaño de los campos.");
            JOptionPane.showMessageDialog(null, "El tamaño de las filas o columnas debe ser mayor o igual al tamaño de los campos.");
            return false;
        }*/
        if (Juego.NUMERO_MINAS > (Juego.FILAS * Juego.COLUMNAS)) {
            System.out.println("El número de minas debe ser menor al tamaño total del tablero.");
            JOptionPane.showMessageDialog(null, "El número de minas debe ser menor al tamaño total del tablero.");
            return false;
        }
        return true;
    }
    
    private String getPuerto() {
        return JOptionPane.showInputDialog(null, "Puerto", "Ingresa un puerto: ", JOptionPane.PLAIN_MESSAGE);
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

    public class Handler implements Runnable {

        public Jugador jugador;
        public String nombre;
        public Sala sala;
        public Socket socket;
        public PrintWriter Escritor;
        public int numerosala;
        public Scanner Entrada;
        private boolean prueba = false;
        private final String SIN_REPETICIONES = "[a-zA-Z0-9]{1}";
        private final String ALFANUMERICO = "[a-zA-Z0-9]+";
        private final String PATRON_NOMBRE = ALFANUMERICO;

        public Handler(Socket socket) {
            this.socket = socket;
        }
        
        public int convertirInt(String num) {
            int num2 = -1;
            try {
                num2 = Integer.parseInt(num);
            } catch (NumberFormatException e) {}
            return num2;
        }

        @Override
        public void run() {
            try {
                Entrada = new Scanner(socket.getInputStream());
                Escritor = new PrintWriter(socket.getOutputStream(), true);
                while (true) {
                    Escritor.println("NOMBREDEENVIO");
                    nombre = Entrada.nextLine();
                    if (nombre == null || nombre.isEmpty() || nombre.equals("") || !nombre.matches(PATRON_NOMBRE) || nombre.indexOf(' ') >= 0 || nombre.startsWith("/") || nombre.length() > 15) {
                        continue;
                    }
                    if (nombre.equals("null")) {
                        prueba = true;
                        return;
                    }
                    synchronized (SALAS) {
                        boolean entro = false;
                        this.jugador = new Jugador(nombre, Escritor);
                        for (Sala sala2 : SALAS.values()) {
                            if (sala2.checarDisponibilidad()) {
                                if (!sala2.checarJugador(this.jugador)) {
                                    this.sala = sala2;
                                    this.sala.agregarJugador(this.jugador);
                                    this.jugador.getPW().println("INFOMESSAGE Bienvenido " + nombre);
                                    entro = true;
                                    break;
                                }
                            }
                        }
                        if (!entro) {
                            int id = SALAS.size() + 1;
                            this.sala = new Sala(id, this.jugador);
                            SALAS.put(id, this.sala);
                            this.jugador.getPW().println("INFOMESSAGE Bienvenido " + nombre + ", eres el primero en entrar");
                        }
                        break;
                    }
                }
                Escritor.println("NAMEACCEPTED " + nombre + "," + sala.getID());
                sala.enviarInfo("MESSAGE [Servidor] " + jugador.getNombre() + " ha entrado");
                this.jugador.getPW().println("INFOMESSAGE Eres el jugador número " + jugador.getID());
                while (true) {
                    String input;
                    try {input = Entrada.nextLine();} catch (Exception e) {return;}
                    if (input != null && !input.equals("") && !input.isEmpty()) {
                        int espacio = input.indexOf(' ');
                        boolean prueba2 = espacio >= 0 && espacio < input.length();
                        if (input.toLowerCase().startsWith("/iniciarjuego")) {
                            sala.iniciarJuego(jugador);
                        } else if (input.startsWith("CLICIZQUIERDO ")) {
                            if (prueba2) {
                                String[] coordenadas = input.substring(espacio + 1).split(",");
                                if (coordenadas.length == 3) {
                                    sala.getJuego().descubrirCampo(jugador, convertirInt(coordenadas[0]), convertirInt(coordenadas[1]));
                                }
                            }
                        } else if (input.startsWith("CLICDERECHO ")) {
                            if (prueba2) {
                                String[] coordenadas = input.substring(espacio + 1).split(",");
                                if (coordenadas.length == 3) {
                                    sala.getJuego().gestionarBandera(jugador, convertirInt(coordenadas[0]), convertirInt(coordenadas[1]), convertirInt(coordenadas[2]));
                                }
                            }
                        } else {
                            sala.enviarInfo("MESSAGE " + jugador.getNombre() + ": " + input);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                if (Escritor != null || sala != null || jugador != null) {
                    if (!prueba) {
                        if (sala.estaIniciado()) {
                            jugador.quitarBanderas();
                        }
                        sala.eliminarJugador(jugador);
                        if (sala.getTam() == 1 && sala.estaIniciado()) {
                            sala.enviarInfo("INFOMESSAGE Eres el único que queda, el juego va a terminar");
                            sala.getJuego().mostrarPuntos();
                        }
                        if (!sala.estaVacia()) {
                            sala.enviarInfo("MESSAGE [Servidor] " + jugador.getNombre() + " ha salido");
                            if (sala.estaIniciado()) {
                                sala.enviarInfo("INFOMESSAGE " + jugador.getNombre() + " salió, se van a actualizar los Datos.");
                            }
                            Jugador j = sala.getPrimerJugador();
                            if (j != sala.getAdmin()) {
                                sala.setAdmin(j);
                                sala.enviarInfo("MESSAGE [Servidor] " + j.getNombre() + " es el nuevo Admin");
                                j.getPW().println("INFOMESSAGE Eres el nuevo Admin");
                                sala.actualizarDatos();
                            }
                        } else {
                            SALAS.remove(sala.getID());
                        }
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Error al cerrar el Socket, " + e);
                }
            }
        }
    }
}
