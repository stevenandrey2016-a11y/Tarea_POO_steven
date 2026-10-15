package juegos.solitario;


import juegos.Jugable;
import juegos.Puntuacion;

import java.util.Scanner;

/**
 * Controla el ciclo del juego: lee comandos del teclado, los traduce a
 * movimientos sobre el Tablero y le pide a la VistaConsola que dibuje.
 */
public class Juego implements Jugable {
    // Puntuación: 10 por cada carta en las fundaciones, más 200 si se gana.
    private static final int PUNTOS_POR_CARTA = 10;
    private static final int BONUS_VICTORIA = 200;

    private Tablero tablero = new Tablero();
    private final VistaConsola vista = new VistaConsola();
    private final Scanner scanner;

    /** Recibe el Scanner de la consola para que todos compartan la misma entrada. */
    public Juego(Scanner scanner) {
        this.scanner = scanner;
    }

    /** Juega una partida: termina al ganar o al escribir "s". */
    @Override
    public Puntuacion start() {
        tablero = new Tablero();   // cada lanzamiento desde la consola empieza con baraja nueva
        System.out.println("¡Bienvenido al Solitario! Escribe 'a' para ver los comandos.");

        boolean jugando = true;
        while (jugando) {
            vista.mostrar(tablero);

            if (tablero.haGanado()) {
                System.out.println("🎉 ¡Felicidades, ganaste la partida!");
                break;
            }

            System.out.print("> ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String linea = scanner.nextLine().trim().toLowerCase();
            if (linea.isEmpty()) {
                continue;
            }

            try {
                jugando = procesarComando(linea.split("\\s+"));
            } catch (MovimientoInvalidoException e) {
                System.out.println("⚠ " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("⚠ Debes escribir números donde corresponda. Escribe 'a' para ayuda.");
            }
        }
        System.out.println("¡Hasta pronto!");
        return calcularPuntuacion();
    }

    /** Convierte el estado final del tablero en una Puntuacion. */
    private Puntuacion calcularPuntuacion() {
        int cartas = tablero.cartasEnFundaciones();
        int puntos = cartas * PUNTOS_POR_CARTA;
        String detalle;
        if (tablero.haGanado()) {
            puntos += BONUS_VICTORIA;
            detalle = "Ganó el solitario";
        } else {
            detalle = cartas + " cartas en fundaciones";
        }
        return new Puntuacion(puntos, detalle);
    }

    /** Devuelve false si el jugador quiere salir. Los números de columna van de 1 a 7. */
    private boolean procesarComando(String[] partes) throws MovimientoInvalidoException {
        switch (partes[0]) {
            case "r":
                tablero.robarDelMazo();
                break;
            case "d":
                tablero.descarteAColumna(numero(partes, 1) - 1);
                break;
            case "df":
                tablero.descarteAFundacion();
                break;
            case "m":
                int cantidad = partes.length > 3 ? numero(partes, 3) : 1;
                tablero.columnaAColumna(numero(partes, 1) - 1, numero(partes, 2) - 1, cantidad);
                break;
            case "f":
                tablero.columnaAFundacion(numero(partes, 1) - 1);
                break;
            case "n":
                tablero = new Tablero();
                System.out.println("Nueva partida.");
                break;
            case "a":
                vista.mostrarAyuda();
                break;
            case "s":
                return false;
            default:
                System.out.println("⚠ Comando desconocido. Escribe 'a' para ver la ayuda.");
        }
        return true;
    }

    /** Lee el número que está en la posición 'indice' del comando. */
    private int numero(String[] partes, int indice) throws MovimientoInvalidoException {
        if (indice >= partes.length) {
            throw new MovimientoInvalidoException("Faltan datos en el comando. Escribe 'a' para ver la ayuda.");
        }
        return Integer.parseInt(partes[indice]);
    }
}
