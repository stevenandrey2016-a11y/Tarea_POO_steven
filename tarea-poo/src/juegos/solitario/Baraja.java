package juegos.solitario;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Una baraja de 52 cartas, ya mezclada, de la que se pueden ir sacando cartas.
 */
public class Baraja {
    private final List<Carta> cartas = new ArrayList<>();

    public Baraja() {
        for (Palo palo : Palo.values()) {
            for (int valor = 1; valor <= 13; valor++) {
                cartas.add(new Carta(palo, valor));
            }
        }
        Collections.shuffle(cartas);
    }

    public Carta robar() {
        return cartas.remove(cartas.size() - 1);
    }

    public boolean estaVacia() {
        return cartas.isEmpty();
    }
}
