package juegos.solitario;


/**
 * Los cuatro palos de la baraja francesa.
 * Cada palo sabe cuál es su símbolo y si es rojo o negro.
 */
public enum Palo {
    CORAZONES("♥", true),
    DIAMANTES("♦", true),
    TREBOLES("♣", false),
    PICAS("♠", false);

    private final String simbolo;
    private final boolean rojo;

    Palo(String simbolo, boolean rojo) {
        this.simbolo = simbolo;
        this.rojo = rojo;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public boolean esRojo() {
        return rojo;
    }
}
