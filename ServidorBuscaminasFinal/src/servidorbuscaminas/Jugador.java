package servidorbuscaminas;

import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author DARKCEUS
 */
public class Jugador {

    public static final boolean ESTADO_JUGANDO = true;
    public static final boolean ESTADO_ESPECTADOR = false;
    private final ArrayList<Campo> listaBanderas;
    private int id;
    private boolean estado;
    private boolean clic;
    private Sala sala;
    private String nombre;
    private PrintWriter pw;
    private int puntos;
    

    public Jugador(String nombre, PrintWriter pw) {
        this.nombre = nombre;
        this.pw = pw;
        this.estado = Jugador.ESTADO_JUGANDO;
        this.clic = false;
        this.puntos = 0;
        this.listaBanderas = new ArrayList<>();
    }
    
    public boolean sinBanderas(){
        return this.listaBanderas.isEmpty();
    }
    
    public boolean agregarBandera(Campo campo){
        return this.listaBanderas.add(campo);
    }
    
    public boolean quitarBandera(Campo campo) {
        return this.listaBanderas.remove(campo);
    }
    
    public void quitarBanderas() {
        this.listaBanderas.forEach((campo) -> {
            campo.setEstado(Campo.ESTADO_INICIAL);
            campo.quitarAdmin();
            sala.enviarInfo("QUITARBANDERA " + campo.getX() + "," + campo.getY() + "," + this.getID());
        });
        this.listaBanderas.clear();
    }

    public void ponerBanderas() {
        this.listaBanderas.forEach((campo) -> {
            sala.enviarInfo("PONERBANDERA " + campo.getX() + "," + campo.getY() + "," + this.getID());
        });
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public Sala getSala() {
        return this.sala;
    }
    
    public void reiniciarPuntos(){
        this.puntos = 0;
    }
    
    public void aumentarPuntos(){
        this.puntos++;
    }
    
    public void quitarPuntos(){
        this.puntos--;
    }
    
    public int getPuntos(){
        return this.puntos;
    }
    
    public void reiniciarClic(){
        this.clic = false;
    }
    
    public boolean checarClic(){
        return this.clic;
    }
    
    public void darClic(){
        this.clic = true;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String id) {
        this.nombre = id;
    }

    public PrintWriter getPW() {
        return pw;
    }

    public void setPW(PrintWriter pw) {
        this.pw = pw;
    }

    public boolean getEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
