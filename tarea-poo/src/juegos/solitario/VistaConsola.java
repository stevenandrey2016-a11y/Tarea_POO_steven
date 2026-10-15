package juegos.solitario;


import java.util.List;

/**
 * Se encarga SOLO de dibujar el estado del tablero en la terminal.
 * Usa colores ANSI (rojo para ♥ ♦) y deja cada carta con ancho fijo para alinear columnas.
 */
public class VistaConsola {
    private static final String ROJO = "\u001B[31m";
    private static final String RESET = "\u001B[0m";
    private static final int ANCHO_CELDA = 7; // espacio por columna

    public void mostrar(Tablero tablero) {
        System.out.println();
        System.out.println("==================== SOLITARIO ====================");

        // Fila superior: mazo, descarte y fundaciones
        System.out.print("Mazo: " + (tablero.cantidadEnMazo() > 0
                ? "[###] (" + tablero.cantidadEnMazo() + ")" : "[   ] (0)"));
        System.out.print("   Descarte: " + celda(tablero.topeDescarte()));
        System.out.print("   Fundaciones: ");
        for (Palo palo : Palo.values()) {
            Carta tope = tablero.topeFundacion(palo);
            System.out.print(tope == null ? "[ " + colorear(palo.getSimbolo(), palo) + " ] " : celda(tope) + " ");
        }
        System.out.println();
        System.out.println();

        // Números de columna
        for (int i = 1; i <= Tablero.NUM_COLUMNAS; i++) {
            System.out.print(rellenar("  " + i, ANCHO_CELDA));
        }
        System.out.println();

        // Columnas, fila por fila
        int filas = 1;
        for (int c = 0; c < Tablero.NUM_COLUMNAS; c++) {
            filas = Math.max(filas, tablero.getColumna(c).size());
        }
        for (int fila = 0; fila < filas; fila++) {
            for (int c = 0; c < Tablero.NUM_COLUMNAS; c++) {
                List<Carta> columna = tablero.getColumna(c);
                String texto;
                if (fila < columna.size()) {
                    texto = celda(columna.get(fila));
                } else if (fila == 0) {
                    texto = " ... "; // columna vacía
                } else {
                    texto = "";
                }
                System.out.print(rellenar(texto, ANCHO_CELDA));
            }
            System.out.println();
        }
        System.out.println("===================================================");
    }

    public void mostrarAyuda() {
        System.out.println();
        System.out.println("Comandos:");
        System.out.println("  r            Robar del mazo (si está vacío, recicla el descarte)");
        System.out.println("  d <col>      Mover el descarte a una columna        ej: d 3");
        System.out.println("  df           Mover el descarte a su fundación");
        System.out.println("  m <o> <d> [n] Mover n cartas (1 por defecto) de la columna o a d   ej: m 2 5 3");
        System.out.println("  f <col>      Mover la última carta de una columna a su fundación   ej: f 4");
        System.out.println("  n            Nueva partida");
        System.out.println("  a            Mostrar esta ayuda");
        System.out.println("  s            Salir");
    }

    // ---------------------------------------------------------------
    // Ayudas de formato
    // ---------------------------------------------------------------

    /** Devuelve la carta como "[10♥]" (o "[###]" si está boca abajo, "[   ]" si no hay carta). */
    private String celda(Carta carta) {
        if (carta == null) {
            return "[   ]";
        }
        if (!carta.isBocaArriba()) {
            return "[###]";
        }
        String texto = String.format("%-3s", carta.getTexto());
        return "[" + colorear(texto, carta.getPalo()) + "]";
    }

    private String colorear(String texto, Palo palo) {
        return palo.esRojo() ? ROJO + texto + RESET : texto;
    }

    /** Rellena con espacios según el largo VISIBLE (ignora los códigos de color). */
    private String rellenar(String texto, int ancho) {
        int visible = texto.replaceAll("\u001B\\[[;\\d]*m", "").length();
        StringBuilder sb = new StringBuilder(texto);
        for (int i = visible; i < ancho; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }
}
