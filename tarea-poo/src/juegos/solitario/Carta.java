package juegos.solitario;


/**
 * Una carta de la baraja: tiene palo, valor (1 = As ... 13 = K)
 * y un estado que indica si está boca arriba o boca abajo.
 */
public class Carta {
    private final Palo palo;
    private final int valor;
    private boolean bocaArriba;

    public Carta(Palo palo, int valor) {
        this.palo = palo;
        this.valor = valor;
        this.bocaArriba = false;
    }

    public Palo getPalo() {
        return palo;
    }

    public int getValor() {
        return valor;
    }

    public boolean isBocaArriba() {
        return bocaArriba;
    }

    public void setBocaArriba(boolean bocaArriba) {
        this.bocaArriba = bocaArriba;
    }

    /** Devuelve A, 2..10, J, Q o K según el valor. */
    public String getEtiqueta() {
        switch (valor) {
            case 1:  return "A";
            case 11: return "J";
            case 12: return "Q";
            case 13: return "K";
            default: return String.valueOf(valor);
        }
    }

    /** Texto plano de la carta, por ejemplo "10♥" o "A♠". */
    public String getTexto() {
        return getEtiqueta() + palo.getSimbolo();
    }

    /** Dos cartas son de colores distintos si una es roja y la otra negra. */
    public boolean colorDistintoDe(Carta otra) {
        return this.palo.esRojo() != otra.palo.esRojo();
    }
}
