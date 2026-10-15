package consola;

import juegos.Puntuacion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Guarda, por cada juego, las mejores puntuaciones (un "Top N").
 * El tamaño del top se decide al crear el objeto: no tiene que ser 3.
 */
public class Estadisticas {

    private final int tamanoTop;
    private final Map<String, List<Puntuacion>> topPorJuego = new LinkedHashMap<>();

    public Estadisticas(int tamanoTop) {
        this.tamanoTop = tamanoTop;
    }

    public void registrarJuego(String nombreJuego) {
        topPorJuego.putIfAbsent(nombreJuego, new ArrayList<>());
    }

    /**
     * Compara la puntuación contra el top actual del juego y la guarda si entra.
     *
     * @return true si la puntuación quedó dentro del top
     */
    public boolean registrar(String nombreJuego, Puntuacion puntuacion) {
        List<Puntuacion> top = topPorJuego.get(nombreJuego);
        top.add(puntuacion);
        top.sort(Comparator.reverseOrder());          // usa Puntuacion.compareTo: mayor primero
        if (top.size() > tamanoTop) {
            top.subList(tamanoTop, top.size()).clear(); // se descarta lo que quedó fuera del top
        }
        return top.contains(puntuacion);
    }

    /** Muestra el top actual de cada juego. */
    public void mostrar() {
        System.out.println();
        System.out.println("===== ESTADÍSTICAS (Top " + tamanoTop + " por juego) =====");
        for (Map.Entry<String, List<Puntuacion>> entrada : topPorJuego.entrySet()) {
            System.out.println();
            System.out.println("-- " + entrada.getKey() + " --");
            List<Puntuacion> top = entrada.getValue();
            if (top.isEmpty()) {
                System.out.println("   (sin registros)");
            }
            for (int i = 0; i < top.size(); i++) {
                System.out.println("   " + (i + 1) + ". " + top.get(i));
            }
        }
    }
}
