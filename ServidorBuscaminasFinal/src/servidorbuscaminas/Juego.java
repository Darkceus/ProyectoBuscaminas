package servidorbuscaminas;

import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JOptionPane;

/**
 *
 * @author DARKCEUS
 */
public class Juego {

    public static final int FILAS = 30;
    public static final int COLUMNAS = 30;
    public static final int TAM_ALTO = 20;
    public static final int TAM_ANCHO = 20;
    public static final int NUMERO_MINAS = 200;
    private final Campo[][] TABLERO;
    private final ArrayList<Campo> listaMinas;
    private boolean inicio;
    private final int[] NUM_X = {-1, 0, 1, -1, 1, -1, 0, 1};
    private final int[] NUM_Y = {-1, -1, -1, 0, 0, 1, 1, 1};
    private final int[] CRUZ_X = {0, -1, 1, 0};
    private final int[] CRUZ_Y = {-1, 0, 0, 1};
    private final Sala sala;
    
    public Juego() {
        this.TABLERO = null;
        this.listaMinas = null;
        this.sala = null;
    }

    public Juego(Sala sala) {
        this.sala = sala;
        this.TABLERO = new Campo[FILAS][COLUMNAS];
        listaMinas = new ArrayList<>();
        crearTablero();
        colocarMinas();
        checarPerimetro();
        imprimirInfo();
    }

    public void setInicio(boolean inicio) {
        this.inicio = inicio;
    }

    public boolean getInicio() {
        return this.inicio;
    }

    private void imprimirInfo() {
        for (int y = 0; y < COLUMNAS; y++) {
            for (int x = 0; x < FILAS; x++) {
                System.out.print(TABLERO[x][y].getValor());
            }
            System.out.println("");
        }
    }

    private void crearTablero() {
        for (int y = 0; y < COLUMNAS; y++) {
            for (int x = 0; x < FILAS; x++) {
                TABLERO[x][y] = new Campo(x, y);
            }
        }
    }

    private void colocarMinas() {
        boolean minasC;
        int fila;
        int columna;
        Campo campo;
        int contarMinas = 0;
        do {
            minasC = false;
            for (int i = 0; i < NUMERO_MINAS; i++) {
                fila = (int) (Math.random() * FILAS);
                columna = (int) (Math.random() * COLUMNAS);
                campo = TABLERO[fila][columna];
                if (campo.getValor() == Campo.VALOR_VACIO) {
                    campo.setValor(Campo.VALOR_MINA);
                    listaMinas.add(campo);
                    contarMinas++;
                } else {
                    i--;
                }
            }
            if (contarMinas == NUMERO_MINAS) {
                minasC = true;
            }
        } while (minasC == false);
        System.out.println(contarMinas);
    }
    
    private void checarPerimetro() {
        int x2;
        int y2;
        Campo campo;
        int valor;
        for (Campo minas : this.listaMinas) {
            for (int i = 0; i < 8; i++) {
                x2 = NUM_X[i] + minas.getX();
                y2 = NUM_Y[i] + minas.getY();
                if ((x2 > NUM_X[0] && x2 < Juego.FILAS) && (y2 > NUM_Y[0] && y2 < Juego.COLUMNAS)) {
                    campo = TABLERO[x2][y2];
                    valor = campo.getValor();
                    if (valor != Campo.VALOR_MINA) {
                        campo.setValor(valor + 1);
                    }
                }
            }
        }
    }    
    
    private boolean checarClic(Jugador jugador, Campo campo) {
        if (jugador.checarClic()) {
            return true;
        }
        boolean esquina = checarEsquina(jugador, campo);
        if (esquina) {
            jugador.darClic();
            return true;
        }
        if (jugador.getID() == 1 && !esquina) {
            jugador.getPW().println("INFOMESSAGE Debes iniciar por la Izquierda");
        } else if (jugador.getID() == 2 && !esquina) {
            jugador.getPW().println("INFOMESSAGE Debes iniciar por la Derecha");
        } else if (jugador.getID() == 3 && !esquina) {
            jugador.getPW().println("INFOMESSAGE Debes iniciar por la Arriba");
        } else if (jugador.getID() == 4 && !esquina) {
            jugador.getPW().println("INFOMESSAGE Debes iniciar por la Abajo");
        }
        return false;
    }
    
    private boolean checar(Jugador jugador) {
        int tam;
        switch (jugador.getID()) {
            case 1: {
                tam = COLUMNAS;
                for (int i = 0; i < tam; i++) {
                    if (TABLERO[0][i].getEstado() == Campo.ESTADO_INICIAL) {
                        return true;
                    }
                }
                break;
            }
            case 2: {
                tam = COLUMNAS;
                int info = (Juego.FILAS - 1);
                for (int i = 0; i < tam; i++) {
                    if (TABLERO[info][i].getEstado() == Campo.ESTADO_INICIAL) {
                        return true;
                    }
                }
                break;
            }
            case 3: {
                tam = FILAS;
                for (int i = 0; i < tam; i++) {
                    if (TABLERO[i][0].getEstado() == Campo.ESTADO_INICIAL) {
                        return true;
                    }
                }
                break;
            }
            case 4: {
                tam = FILAS;
                int info = (Juego.COLUMNAS - 1);
                for (int i = 0; i < tam; i++) {
                    if (TABLERO[i][info].getEstado() == Campo.ESTADO_INICIAL) {
                        return true;
                    }
                }
                break;
            }
        }
        return false;
    }
    
    private boolean checarEsquina(Jugador jugador, Campo campo) {
        if (jugador.getID() == 1 && (campo.getX() == 0 || !checar(jugador))) {
            return true;
        } else if (jugador.getID() == 2 && (campo.getX() == (Juego.FILAS - 1) || !checar(jugador))) {
            return true;
        } else if (jugador.getID() == 3 && (campo.getY() == 0 || !checar(jugador))) {
            return true;
        } else if (jugador.getID() == 4 && (campo.getY() == (Juego.COLUMNAS - 1) || !checar(jugador))) {
            return true;
        }
        return false;
    }
    
    public void descubrirCampo(Jugador jugador, int x, int y) {
        if (sala.estaIniciado() && jugador.getEstado() == Jugador.ESTADO_JUGANDO) {
            Campo campo = TABLERO[x][y];
            if (checarClic(jugador, campo)) {
                if (campo.getEstado() == Campo.ESTADO_INICIAL/* || (!campo.getAdmin().equals(jugador) && campo.getEstado() == Campo.ESTADO_BANDERA)*/) {
                    campo.setEstado(Campo.ESTADO_APLASTADO);
                    campo.setAdmin(jugador);
                    sala.enviarInfo("DESCUBRIRCAMPO " + x + "," + y + "," + campo.getValor());
                    if (campo.getValor() == Campo.VALOR_MINA) {
                        jugador.setEstado(Jugador.ESTADO_ESPECTADOR);
                        campo.setEstado(Campo.ESTADO_APLASTADO);
                        sala.agregarPerdedor(jugador);
                        sala.enviarInfo("HAYMINA " + x + "," + y + "," + jugador.getID());
                        sala.enviarInfo("MESSAGE " + jugador.getNombre() + " ha perdido");
                    } else if (campo.getValor() == Campo.VALOR_VACIO) {
                        revelarPerimetro(jugador, campo);
                    }
                }
                if (inicio) {
                    checarMinas();
                }
            }
        } else {
            jugador.getPW().println("INFOMESSAGE Ya perdiste, no puedes jugar");
        }
    }

    private void revelarPerimetro(Jugador jugador, Campo campo) {
        int x2;
        int y2;
        Campo campo2;
        for (int i = 0; i < 8; i++) {
            x2 = NUM_X[i] + campo.getX();
            y2 = NUM_Y[i] + campo.getY();
            if ((x2 > NUM_X[0] && x2 < Juego.FILAS) && (y2 > NUM_Y[0] && y2 < Juego.COLUMNAS)) {
                campo2 = TABLERO[x2][y2];
                if (campo2.getEstado() == Campo.ESTADO_INICIAL) {
                    campo2.setAdmin(jugador);
                    campo2.setEstado(Campo.ESTADO_APLASTADO);
                    sala.enviarInfo("DESCUBRIRCAMPO " + x2 + "," + y2 + "," + campo2.getValor());
                    if (campo2.getValor() == Campo.VALOR_VACIO) {
                        revelarPerimetro(jugador, campo2);
                    }
                }
            }
        }
    }
    
    public void gestionarBandera(Jugador jugador, int x, int y, int minasRes) {
        if (sala.estaIniciado() && jugador.getEstado() == Jugador.ESTADO_JUGANDO) {
            Campo campo = TABLERO[x][y];
            if (campo.getEstado() == Campo.ESTADO_INICIAL && minasRes > 0) {
                if (checarClic(jugador, campo)) {
                    campo.setEstado(Campo.ESTADO_BANDERA);
                    campo.setAdmin(jugador);
                    jugador.agregarBandera(campo);
                    sala.enviarInfo("PONERBANDERA " + x + "," + y + "," + jugador.getID());
                    if (inicio) {
                        checarMinas();
                    }
                }
            } else if (campo.getEstado() == Campo.ESTADO_BANDERA && campo.getAdmin().equals(jugador)) {
                campo.setEstado(Campo.ESTADO_INICIAL);
                campo.setAdmin(jugador);
                jugador.quitarBandera(campo);
                sala.enviarInfo("QUITARBANDERA " + x + "," + y + "," + jugador.getID());
            }
        } else {
            jugador.getPW().println("INFOMESSAGE Ya perdiste, no puedes jugar");
        }
    }

    public void checarMinas() {
        int checar = 0;
        int checar2 = 0;
        Campo campo;
        for (int y = 0; y < COLUMNAS; y++) {
            for (int x = 0; x < FILAS; x++) {
                campo = TABLERO[x][y];
                if ((campo.getEstado() == Campo.ESTADO_BANDERA && campo.getValor() == Campo.VALOR_MINA) || (campo.getEstado() == Campo.ESTADO_APLASTADO && campo.getValor() == Campo.VALOR_MINA)) {
                    checar++;
                } else if (campo.getEstado() == Campo.ESTADO_APLASTADO) {
                    checar2++;
                }
            }
        }
        if (checar == NUMERO_MINAS && checar2 == ((FILAS * COLUMNAS) - NUMERO_MINAS)) {
            sala.enviarInfo("INFOMESSAGE Han puesto banderas sobre todas las minas y descubierto todos los campos.");
            this.mostrarPuntos();
        }
    }
    
    private void sacarPuntaje(){
       Campo campo;
        for (int y = 0; y < COLUMNAS; y++) {
            for (int x = 0; x < FILAS; x++) {
                campo = TABLERO[x][y];
                if (campo.getValor() != Campo.VALOR_MINA) {
                    if (campo.getEstado() == Campo.ESTADO_BANDERA) {
                        campo.setEstado(Campo.ESTADO_BANDERA_NO_MINA);
                        sala.enviarInfo("NOMINA " + x + "," + y);
                        for (Jugador jugador : sala.getLista()) {
                            if (campo.getAdmin().equals(jugador)) {
                                jugador.quitarPuntos();
                            }
                        }
                    } else if (campo.getEstado() == Campo.ESTADO_INICIAL || campo.getEstado() == Campo.ESTADO_APLASTADO) {
                        campo.setEstado(Campo.ESTADO_APLASTADO);
                        sala.enviarInfo("2DESCUBRIRCAMPO " + x + "," + y);
                    }
                } else {
                    if (campo.getEstado() == Campo.ESTADO_BANDERA) {
                        for (Jugador jugador : sala.getLista()) {
                            if (campo.getAdmin().equals(jugador)) {
                                jugador.aumentarPuntos();
                            }
                        }
                    } else if (campo.getEstado() == Campo.ESTADO_INICIAL) {
                        campo.setEstado(Campo.ESTADO_APLASTADO);
                        sala.enviarInfo("2HAYMINA " + x + "," + y);
                    }
                }
            }
        }
    }

    public void mostrarPuntos() {
        sacarPuntaje();
        inicio = false;
        //Collections.sort(sala.getLista(), (Jugador j1, Jugador j2) -> new Integer(j2.getPuntos()).compareTo(new Integer(j1.getPuntos())));
        sala.enviarInfo("MESSAGE [Servidor] El juego ha terminado");
        String algo = "";
        for (Jugador jugador : sala.getLista()) {
            algo += "Jugador " + jugador.getID() + ", Nombre: " + jugador.getNombre() + ", Puntos: " + jugador.getPuntos() + ".";
        }
        sala.enviarInfo("PUNTOS " + algo);
        sala.reiniciarDatos();
    }
}