package juegos;

/**
 * Contrato que debe cumplir cualquier juego que se pueda lanzar desde la consola.
 * La consola no necesita saber qué juego es: solo llama a {@link #start()}.
 */
public interface Jugable {

    /**
     * Juega una partida completa (hasta ganar, perder o salir).
     *
     * @return la puntuación obtenida al terminar; nunca es null
     */
    Puntuacion start();
}
