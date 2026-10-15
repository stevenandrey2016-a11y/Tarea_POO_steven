package juegos.buscaminas;


import juegos.Jugable;
import juegos.Puntuacion;

import java.util.Scanner;

/**
 * Partida en consola. Lee comandos, se los pasa a Tablero y dibuja el resultado.
 * Las casillas se nombran como en ajedrez: letra = columna, número = fila (ej. D5).
 */
public class Juego implements Jugable {

    private static final int FILAS = 10;
    private static final int COLUMNAS = 10;   // A-J
    private static final int MINAS = 12;

    // Puntuación: 10 por casilla segura abierta; si ganas, bonus fijo + 1 por cada segundo que sobre de 300.
    private static final int PUNTOS_POR_CASILLA = 10;
    private static final int BONUS_VICTORIA = 500;
    private static final int SEGUNDOS_BONUS = 300;

    private final Tablero tablero = new Tablero(FILAS, COLUMNAS, MINAS);
    private final Scanner scanner;
    private long inicio;                       // momento del primer "mostrar"

    public Juego (Scanner scanner) {
        this.scanner = scanner;
    }

    /** Juega una partida: termina al ganar, perder o escribir "salir". */
    @Override
    public Puntuacion start() {
        nuevaPartida();
        return bucleDePartida();
    }

    private void nuevaPartida() {
        tablero.reiniciar();
        inicio = 0;
        System.out.println();
        mostrarAyuda();
    }

    /** Corre la partida y devuelve la puntuación con la que terminó. */
    private Puntuacion bucleDePartida() {
        while (true) {
            dibujarTablero();

            if (tablero.getEstado() != Tablero.Estado.JUGANDO) {
                long segs = segundos();
                anunciarResultado(segs);
                return calcularPuntuacion(segs);
            }

            System.out.print("> ");
            if (!scanner.hasNextLine()) return calcularPuntuacion(segundos());
            String[] partes = scanner.nextLine().trim().toLowerCase().split("\\s+");
            String comando = partes[0];

            switch (comando) {
                case "mostrar", "m" -> ejecutarMostrar(partes);
                case "bandera", "b" -> ejecutarBandera(partes);
                case "reiniciar" -> nuevaPartida();
                case "ayuda" -> mostrarAyuda();
                case "salir" -> { return calcularPuntuacion(segundos()); }
                case "" -> { }
                default -> System.out.println("Comando no reconocido. Escribe 'ayuda' para ver las opciones.");
            }
        }
    }

    /* ---------- Comandos ---------- */

    private void ejecutarMostrar(String[] partes) {
        int[] pos = leerCasilla(partes);
        if (pos == null) return;

        boolean eraPrimerClic = tablero.esPrimerClic();
        tablero.revelar(pos[0], pos[1]);

        if (eraPrimerClic && !tablero.esPrimerClic()) {
            inicio = System.currentTimeMillis();
        }
    }

    private void ejecutarBandera(String[] partes) {
        int[] pos = leerCasilla(partes);
        if (pos == null) return;

        if (tablero.estaRevelada(pos[0], pos[1])) {
            System.out.println("Esa casilla ya está descubierta.");
            return;
        }
        tablero.alternarBandera(pos[0], pos[1]);
    }

    /** Convierte "D5" en {fila, columna}. Devuelve null (y avisa) si no es válida. */
    private int[] leerCasilla(String[] partes) {
        if (partes.length < 2) {
            System.out.println("Falta la casilla. Ejemplo: " + partes[0] + " D5");
            return null;
        }

        String texto = partes[1];
        char letra = texto.charAt(0);
        int columna = letra - 'a';

        int fila;
        try {
            fila = Integer.parseInt(texto.substring(1)) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Casilla no válida: " + partes[1].toUpperCase());
            return null;
        }

        if (columna < 0 || columna >= COLUMNAS || fila < 0 || fila >= FILAS) {
            System.out.println("Fuera del tablero. Usa letras A-" + letraColumna(COLUMNAS - 1)
                    + " y números 1-" + FILAS + ".");
            return null;
        }
        return new int[]{fila, columna};
    }

    /* ---------- Dibujo ---------- */

    private void dibujarTablero() {
        System.out.println();
        System.out.printf("Minas restantes: %d    Tiempo: %ds%n", tablero.minasRestantes(), segundos());
        System.out.println();

        // Encabezado de letras
        System.out.print("     ");
        for (int c = 0; c < COLUMNAS; c++) System.out.print(letraColumna(c) + " ");
        System.out.println();
        System.out.println("   +" + "-".repeat(COLUMNAS * 2 + 1) + "+");

        for (int f = 0; f < FILAS; f++) {
            System.out.printf("%2d | ", f + 1);
            for (int c = 0; c < COLUMNAS; c++) System.out.print(simbolo(f, c) + " ");
            System.out.printf("| %d%n", f + 1);
        }

        System.out.println("   +" + "-".repeat(COLUMNAS * 2 + 1) + "+");
        System.out.print("     ");
        for (int c = 0; c < COLUMNAS; c++) System.out.print(letraColumna(c) + " ");
        System.out.println();
        System.out.println();
    }

    /** Carácter que representa cada casilla según el estado del tablero. */
    private char simbolo(int f, int c) {
        Tablero.Estado estado = tablero.getEstado();

        if (tablero.estaRevelada(f, c)) {
            int n = tablero.minasVecinas(f, c);
            return n > 0 ? (char) ('0' + n) : '.';
        }

        boolean mina = tablero.tieneMina(f, c);
        boolean bandera = tablero.tieneBandera(f, c);

        if (estado == Tablero.Estado.PERDIDO) {
            if (f == tablero.getFilaExplotada() && c == tablero.getColumnaExplotada()) return '@';
            if (mina && !bandera) return '*';
            if (!mina && bandera) return 'X';
        }
        if (estado == Tablero.Estado.GANADO && mina) return 'F';
        return bandera ? 'F' : '#';
    }

    private char letraColumna(int c) {
        return (char) ('A' + c);
    }

    private long segundos() {
        return inicio == 0 ? 0 : (System.currentTimeMillis() - inicio) / 1000;
    }

    /* ---------- Mensajes ---------- */

    private void mostrarAyuda() {
        System.out.println("=== CÓMO JUGAR ===");
        System.out.println("  mostrar D5   -> descubre la casilla D5   (atajo: m D5)");
        System.out.println("  bandera D5   -> pone o quita una bandera (atajo: b D5)");
        System.out.println("  reiniciar    -> empieza una partida nueva");
        System.out.println("  ayuda        -> muestra este mensaje");
        System.out.println("  salir        -> vuelve al menú");
        System.out.println();
        System.out.println("Símbolos: # oculta | . vacía | 1-8 minas cercanas | F bandera");
        System.out.println("          * mina | X bandera mal puesta | @ mina que explotó");
        System.out.println("Hay " + MINAS + " minas. La primera casilla que descubras nunca es mina.");
    }

    private void anunciarResultado(long segs) {
        if (tablero.getEstado() == Tablero.Estado.PERDIDO) {
            System.out.println("¡BOOM! Pisaste una mina. Has perdido.");
        } else {
            System.out.println("¡Felicidades! Ganaste en " + segs + " segundos.");
        }
    }

    /** Convierte el estado final del tablero en una Puntuacion. */
    private Puntuacion calcularPuntuacion(long segs) {
        int abiertas = tablero.getReveladas();
        int puntos = abiertas * PUNTOS_POR_CASILLA;
        String detalle;

        switch (tablero.getEstado()) {
            case GANADO -> {
                puntos += BONUS_VICTORIA + (int) Math.max(0, SEGUNDOS_BONUS - segs);
                detalle = "Ganó en " + segs + " s";
            }
            case PERDIDO -> detalle = "Perdió con " + abiertas + " casillas abiertas";
            default -> detalle = "Abandonó con " + abiertas + " casillas abiertas";
        }
        return new Puntuacion(puntos, detalle);
    }
}
