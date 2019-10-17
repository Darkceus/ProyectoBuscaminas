package servidorbuscaminas;

/**
 *
 * @author DARKCEUS
 */
public class Campo {
    public static final int VALOR_VACIO = 0;
    public static final int VALOR_MINA = 9;
    public static final int ESTADO_INICIAL = 0;
    public static final int ESTADO_APLASTADO = 1;
    public static final int ESTADO_BANDERA = 2;
    public static final int ESTADO_BANDERA_NO_MINA = 3;
    private Jugador Admin;
    private int X = 0;
    private int Y = 0;
    private int Valor = 0;
    private int Estado = 0;

    public Campo(int x, int y) {
        this.X = x;
        this.Y = y;
    }

    public int getX() {
        return this.X;
    }

    public void setX(int X) {
        this.X = X;
    }

    public int getY() {
        return this.Y;
    }

    public void setY(int y) {
        this.Y = y;
    }

    public int getValor() {
        return Valor;
    }

    public void setValor(int Valor) {
        this.Valor = Valor;
    }

    public int getEstado() {
        return Estado;
    }

    public void setEstado(int Estado) {
        this.Estado = Estado;
    }

    public Jugador getAdmin() {
        return Admin;
    }

    public void setAdmin(Jugador Admin) {
        this.Admin = Admin;
    }
    
    public void quitarAdmin(){
        this.Admin = null;
    }
}
