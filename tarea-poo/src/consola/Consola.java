package consola;

import juegos.Jugable;
import juegos.Puntuacion;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Menú principal. Permite lanzar cualquier juego Jugable y ver las estadísticas.
 * No conoce los detalles de ningún juego: solo llama a start() y registra la Puntuacion.
 */
public class Consola {

    /** Cuántas puntuaciones se guardan por juego. Se puede cambiar a 5, 10, etc. */
    private static final int TAMANO_TOP = 3;

    private static final String BUSCAMINAS = "Buscaminas";
    private static final String SOLITARIO = "Solitario";

    private final Scanner scanner;
    private final Map<String, Jugable> juegos = new LinkedHashMap<>();
    private final Estadisticas estadisticas = new Estadisticas(TAMANO_TOP);

    public Consola(Scanner scanner) {
        this.scanner = scanner;
    }

    public void registrarJuego(String nombre, Jugable juego) {
        juegos.put(nombre, juego);
        estadisticas.registrarJuego(nombre);
    }

    /* ---------- Menú ---------- */

    public void ejecutar() {
        boolean salir = false;
        while (!salir) {
            System.out.println();
            System.out.println("==============================");
            System.out.println("        SALA DE JUEGOS");
            System.out.println("==============================");
            System.out.println("  1. Lanzar juego");
            System.out.println("  2. Ver estadísticas");
            System.out.println("  3. Salir");
            System.out.print("Elige una opción: ");

            if (!scanner.hasNextLine()) break;
            switch (scanner.nextLine().trim()) {
                case "1" -> lanzarJuego();
                case "2" -> estadisticas.mostrar();
                case "3" -> salir = true;
                default -> System.out.println("Opción no válida. Escribe 1, 2 o 3.");
            }
        }
        System.out.println("¡Hasta luego!");
    }

    private void lanzarJuego() {
        List<String> nombres = new ArrayList<>(juegos.keySet());

        System.out.println();
        System.out.println("¿A qué quieres jugar?");
        for (int i = 0; i < nombres.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + nombres.get(i));
        }
        System.out.println("  0. Volver");
        System.out.print("Elige un juego: ");

        if (!scanner.hasNextLine()) return;
        int eleccion;
        try {
            eleccion = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Opción no válida.");
            return;
        }
        if (eleccion == 0) return;
        if (eleccion < 1 || eleccion > nombres.size()) {
            System.out.println("Opción no válida.");
            return;
        }

        String nombreJuego = nombres.get(eleccion - 1);
        System.out.print("Tu nombre: ");
        String jugador = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
        if (jugador.isEmpty()) jugador = "Jugador";

        // El mismo código sirve para cualquier juego: polimorfismo con Jugable.
        Puntuacion puntuacion = juegos.get(nombreJuego).start().conJugador(jugador);

        // 1) Al terminar el juego se registra la estadística.
        // 2) Al registrar, se compara contra el top actual.
        boolean entroAlTop = estadisticas.registrar(nombreJuego, puntuacion);

        System.out.println();
        System.out.println("Terminaste " + nombreJuego + " con " + puntuacion.getPuntos() + " puntos.");
        System.out.println(entroAlTop
                ? "¡Entraste al Top " + TAMANO_TOP + " de " + nombreJuego + "!"
                : "No alcanzaste el Top " + TAMANO_TOP + " de " + nombreJuego + ".");
    }

    /* ---------- Escenario ficticio ---------- */

    /** Datos inventados para que "Ver estadísticas" tenga algo que mostrar desde el inicio. */
    private void cargarEscenarioFicticio() {
        estadisticas.registrar(BUSCAMINAS, new Puntuacion("Ana", 1180, "Ganó en 95 s"));
        estadisticas.registrar(BUSCAMINAS, new Puntuacion("Luis", 1010, "Ganó en 190 s"));
        estadisticas.registrar(BUSCAMINAS, new Puntuacion("Sofía", 870, "Perdió con 87 casillas abiertas"));
        estadisticas.registrar(BUSCAMINAS, new Puntuacion("Pedro", 340, "Perdió con 34 casillas abiertas"));

        estadisticas.registrar(SOLITARIO, new Puntuacion("Marta", 720, "Ganó el solitario"));
        estadisticas.registrar(SOLITARIO, new Puntuacion("Diego", 380, "38 cartas en fundaciones"));
        estadisticas.registrar(SOLITARIO, new Puntuacion("Ana", 210, "21 cartas en fundaciones"));
        estadisticas.registrar(SOLITARIO, new Puntuacion("Luis", 90, "9 cartas en fundaciones"));
    }

    /* ---------- Punto de entrada ---------- */

    public static void main(String[] args) {
        // Un solo Scanner compartido: si cada juego creara el suyo, se perderían líneas de entrada.
        Scanner scanner = new Scanner(System.in);
        Consola consola = new Consola(scanner);

        // Las dos clases se llaman Juego, así que se usa el nombre completo (están en paquetes distintos).
        consola.registrarJuego(BUSCAMINAS, new juegos.buscaminas.Juego(scanner));
        consola.registrarJuego(SOLITARIO, new juegos.solitario.Juego(scanner));

        consola.cargarEscenarioFicticio();   // quitar esta línea para empezar con las estadísticas vacías
        consola.ejecutar();
        scanner.close();
    }
}
