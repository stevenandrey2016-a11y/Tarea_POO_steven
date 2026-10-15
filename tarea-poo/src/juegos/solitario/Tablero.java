package juegos.solitario;


import java.util.ArrayList;
import java.util.List;

/**
 * Contiene TODA la lógica y las reglas del solitario (Klondike):
 *  - mazo: cartas boca abajo para robar
 *  - descarte: cartas robadas, boca arriba (la última es la jugable)
 *  - fundaciones: 4 pilas, una por palo, de As a K
 *  - columnas: 7 columnas donde se apilan cartas alternando color en orden descendente
 *
 * No imprime nada ni lee del teclado: eso lo hacen VistaConsola y Juego.
 */
public class Tablero {
    public static final int NUM_COLUMNAS = 7;

    private final List<Carta> mazo = new ArrayList<>();
    private final List<Carta> descarte = new ArrayList<>();
    private final List<List<Carta>> fundaciones = new ArrayList<>();
    private final List<List<Carta>> columnas = new ArrayList<>();

    public Tablero() {
        for (int i = 0; i < Palo.values().length; i++) {
            fundaciones.add(new ArrayList<>());
        }
        for (int i = 0; i < NUM_COLUMNAS; i++) {
            columnas.add(new ArrayList<>());
        }
        repartir();
    }

    // ---------------------------------------------------------------
    // Preparación de la partida
    // ---------------------------------------------------------------

    /** La columna 1 recibe 1 carta, la 2 recibe 2, ... la 7 recibe 7. El resto va al mazo. */
    private void repartir() {
        Baraja baraja = new Baraja();
        for (int col = 0; col < NUM_COLUMNAS; col++) {
            for (int i = 0; i <= col; i++) {
                Carta carta = baraja.robar();
                carta.setBocaArriba(i == col); // solo la última queda boca arriba
                columnas.get(col).add(carta);
            }
        }
        while (!baraja.estaVacia()) {
            mazo.add(baraja.robar());
        }
    }

    // ---------------------------------------------------------------
    // Consultas (las usa la vista para dibujar)
    // ---------------------------------------------------------------

    public int cantidadEnMazo() {
        return mazo.size();
    }

    public Carta topeDescarte() {
        return descarte.isEmpty() ? null : descarte.get(descarte.size() - 1);
    }

    public Carta topeFundacion(Palo palo) {
        List<Carta> pila = fundaciones.get(palo.ordinal());
        return pila.isEmpty() ? null : pila.get(pila.size() - 1);
    }

    public List<Carta> getColumna(int indice) {
        return columnas.get(indice);
    }

    /** Total de cartas que ya están en las cuatro fundaciones (0 a 52). */
    public int cartasEnFundaciones() {
        int total = 0;
        for (List<Carta> pila : fundaciones) {
            total += pila.size();
        }
        return total;
    }

    public boolean haGanado() {
        for (List<Carta> pila : fundaciones) {
            if (pila.size() != 13) {
                return false;
            }
        }
        return true;
    }

    // ---------------------------------------------------------------
    // Movimientos
    // ---------------------------------------------------------------

    /** Pasa una carta del mazo al descarte. Si el mazo está vacío, recicla el descarte. */
    public void robarDelMazo() throws MovimientoInvalidoException {
        if (!mazo.isEmpty()) {
            Carta carta = quitarUltima(mazo);
            carta.setBocaArriba(true);
            descarte.add(carta);
        } else if (!descarte.isEmpty()) {
            while (!descarte.isEmpty()) {
                Carta carta = quitarUltima(descarte);
                carta.setBocaArriba(false);
                mazo.add(carta);
            }
        } else {
            throw new MovimientoInvalidoException("No quedan cartas para robar.");
        }
    }

    public void descarteAColumna(int destino) throws MovimientoInvalidoException {
        validarColumna(destino);
        Carta carta = topeDescarte();
        if (carta == null) {
            throw new MovimientoInvalidoException("El descarte está vacío.");
        }
        if (!puedeColocarEnColumna(carta, columnas.get(destino))) {
            throw new MovimientoInvalidoException(
                    "No puedes poner " + carta.getTexto() + " en la columna " + (destino + 1) + ".");
        }
        columnas.get(destino).add(quitarUltima(descarte));
    }

    public void descarteAFundacion() throws MovimientoInvalidoException {
        Carta carta = topeDescarte();
        if (carta == null) {
            throw new MovimientoInvalidoException("El descarte está vacío.");
        }
        if (!puedeColocarEnFundacion(carta)) {
            throw new MovimientoInvalidoException(
                    carta.getTexto() + " no puede ir todavía a la fundación.");
        }
        fundaciones.get(carta.getPalo().ordinal()).add(quitarUltima(descarte));
    }

    /** Mueve 'cantidad' cartas (la secuencia del final) de una columna a otra. */
    public void columnaAColumna(int origen, int destino, int cantidad) throws MovimientoInvalidoException {
        validarColumna(origen);
        validarColumna(destino);
        if (origen == destino) {
            throw new MovimientoInvalidoException("El origen y el destino son la misma columna.");
        }
        List<Carta> colOrigen = columnas.get(origen);
        if (cantidad < 1 || cantidad > colOrigen.size()) {
            throw new MovimientoInvalidoException(
                    "La columna " + (origen + 1) + " no tiene " + cantidad + " carta(s).");
        }

        int inicio = colOrigen.size() - cantidad;
        Carta primera = colOrigen.get(inicio);
        if (!primera.isBocaArriba()) {
            throw new MovimientoInvalidoException("No puedes mover cartas que están boca abajo.");
        }
        if (!puedeColocarEnColumna(primera, columnas.get(destino))) {
            throw new MovimientoInvalidoException(
                    "No puedes poner " + primera.getTexto() + " en la columna " + (destino + 1) + ".");
        }

        List<Carta> grupo = colOrigen.subList(inicio, colOrigen.size());
        columnas.get(destino).addAll(grupo);
        grupo.clear();
        voltearUltima(colOrigen);
    }

    public void columnaAFundacion(int origen) throws MovimientoInvalidoException {
        validarColumna(origen);
        List<Carta> colOrigen = columnas.get(origen);
        if (colOrigen.isEmpty()) {
            throw new MovimientoInvalidoException("La columna " + (origen + 1) + " está vacía.");
        }
        Carta carta = colOrigen.get(colOrigen.size() - 1);
        if (!puedeColocarEnFundacion(carta)) {
            throw new MovimientoInvalidoException(
                    carta.getTexto() + " no puede ir todavía a la fundación.");
        }
        fundaciones.get(carta.getPalo().ordinal()).add(quitarUltima(colOrigen));
        voltearUltima(colOrigen);
    }

    // ---------------------------------------------------------------
    // Reglas y utilidades internas
    // ---------------------------------------------------------------

    /** Una columna vacía solo acepta K; si no, color alterno y valor una unidad menor. */
    private boolean puedeColocarEnColumna(Carta carta, List<Carta> columna) {
        if (columna.isEmpty()) {
            return carta.getValor() == 13;
        }
        Carta tope = columna.get(columna.size() - 1);
        return tope.isBocaArriba()
                && tope.colorDistintoDe(carta)
                && tope.getValor() == carta.getValor() + 1;
    }

    /** La fundación de cada palo se construye en orden: A, 2, 3, ... K. */
    private boolean puedeColocarEnFundacion(Carta carta) {
        List<Carta> pila = fundaciones.get(carta.getPalo().ordinal());
        return pila.size() == carta.getValor() - 1;
    }

    private void voltearUltima(List<Carta> columna) {
        if (!columna.isEmpty()) {
            columna.get(columna.size() - 1).setBocaArriba(true);
        }
    }

    private Carta quitarUltima(List<Carta> lista) {
        return lista.remove(lista.size() - 1);
    }

    private void validarColumna(int indice) throws MovimientoInvalidoException {
        if (indice < 0 || indice >= NUM_COLUMNAS) {
            throw new MovimientoInvalidoException(
                    "La columna debe estar entre 1 y " + NUM_COLUMNAS + ".");
        }
    }
}
