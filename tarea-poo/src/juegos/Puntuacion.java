package juegos;

/**
 * Resultado de una partida. Es Comparable: el orden natural va de menor a
 * mayor cantidad de puntos (compareTo > 0 significa "esta puntuación es mejor").
 * Es inmutable; el juego la crea sin jugador y la consola le asigna el nombre
 * con {@link #conJugador(String)}.
 */
public class Puntuacion implements Comparable<Puntuacion> {

    private final String jugador;
    private final int puntos;
    private final String detalle;

    /** La usan los juegos al terminar: todavía no saben el nombre del jugador. */
    public Puntuacion(int puntos, String detalle) {
        this("", puntos, detalle);
    }

    public Puntuacion(String jugador, int puntos, String detalle) {
        this.jugador = jugador;
        this.puntos = puntos;
        this.detalle = detalle;
    }

    /** Copia de esta puntuación pero con el nombre del jugador. */
    public Puntuacion conJugador(String nombre) {
        return new Puntuacion(nombre, puntos, detalle);
    }

    public String getJugador() { return jugador; }
    public int getPuntos() { return puntos; }
    public String getDetalle() { return detalle; }

    /** Negativo si esta es peor, 0 si empatan, positivo si esta es mejor. */
    @Override
    public int compareTo(Puntuacion otra) {
        return Integer.compare(this.puntos, otra.puntos);
    }

    @Override
    public String toString() {
        return String.format("%-12s %5d pts  (%s)", jugador, puntos, detalle);
    }
}
