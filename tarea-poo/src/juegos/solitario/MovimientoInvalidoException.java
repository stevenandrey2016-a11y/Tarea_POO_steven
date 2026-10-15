package juegos.solitario;

/**
 * Se lanza cuando el jugador intenta hacer un movimiento que rompe las reglas.
 * El mensaje explica el motivo para mostrárselo al jugador.
 */
public class MovimientoInvalidoException extends Exception {
    public MovimientoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
