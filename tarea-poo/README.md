# Sala de Juegos (Tarea POO)

Aplicación de consola en Java con un menú principal desde el que se puede **lanzar un juego** o **ver las estadísticas**. Incluye dos juegos:

| Juego | Autor | Paquete |
|-------|-------|---------|
| Buscaminas | _Steven Vega_ | `juegos.buscaminas` |
| Solitario (Klondike) | _Sebas Salas_ | `juegos.solitario` |

## Cómo ejecutarlo

Requiere **Java 14 o superior** (el código usa `switch` con flechas).

Desde la carpeta del proyecto:

```bash
# Compilar
mkdir out
javac -encoding UTF-8 -d out $(find src -name "*.java")

# Ejecutar
java -cp out consola.Consola
```

En Windows (PowerShell):

```powershell
mkdir out
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out consola.Consola
```

También se puede abrir la carpeta en IntelliJ / Eclipse / VS Code, marcar `src` como carpeta de fuentes y ejecutar `consola.Consola`.

## Estructura

```
src/
├── consola/
│   ├── Consola.java        Menú principal y punto de entrada (main)
│   └── Estadisticas.java   Top N de puntuaciones por juego
└── juegos/
    ├── Jugable.java        Interfaz que implementan todos los juegos
    ├── Puntuacion.java     Resultado de una partida (Comparable)
    ├── buscaminas/         Juego, Tablero
    └── solitario/          Juego, Tablero, Baraja, Carta, Palo, VistaConsola,
                            MovimientoInvalidoException
```

## Diseño

```
          «interface»
           Jugable
        + start(): Puntuacion
          ▲           ▲
          │           │
  buscaminas.Juego   solitario.Juego        ← cada juego implementa Jugable
          │           │
          └─── Puntuacion ───┐
                             ▼
  Consola ──usa──► Jugable.start()  ──► Estadisticas.registrar(juego, puntuacion)
```

- **`Jugable`** garantiza que cualquier juego tiene `start()`. La consola no sabe qué juego está lanzando; solo llama a `start()` (polimorfismo). Para agregar un juego nuevo basta con implementar `Jugable` y registrarlo en `Consola.main`.
- **`Puntuacion`** implementa `Comparable<Puntuacion>`. `compareTo` compara por puntos (menor → mayor), así `Estadisticas` ordena con `Comparator.reverseOrder()` y deja los mejores primero.
- **`Estadisticas`** guarda el top de cada juego. El tamaño se define al crearla (`TAMANO_TOP` en `Consola`, por defecto 3) y puede cambiarse a cualquier valor.
- Todos los juegos comparten **un único `Scanner`** (se lo pasa la consola por el constructor), porque varios `Scanner` sobre `System.in` pierden líneas de entrada.

## Estadísticas

1. Al terminar un juego (ganar, perder o salir) se registra la puntuación.
2. Al registrarla se compara contra el top actual de ese juego; si no alcanza, se descarta.
3. **Ver estadísticas** muestra el top actual de cada juego. Al iniciar se carga un **escenario ficticio** (`Consola.cargarEscenarioFicticio()`) para que haya datos de ejemplo. Para empezar con las estadísticas vacías, basta con quitar esa llamada en `main`.

### Cómo se calcula la puntuación

**Buscaminas**
- 10 puntos por cada casilla segura abierta.
- Si se gana: +500 y +1 punto por cada segundo que sobre de 300.
- Perder o abandonar conserva solo los puntos de las casillas abiertas.

**Solitario**
- 10 puntos por cada carta en las fundaciones.
- Si se gana: +200.
- Escribir `s` termina la partida con los puntos acumulados.

## Cómo se juega

### Buscaminas
Las casillas se nombran como en ajedrez (letra = columna, número = fila).

| Comando | Acción |
|---------|--------|
| `mostrar D5` o `m D5` | Descubre la casilla |
| `bandera D5` o `b D5` | Pone o quita una bandera |
| `reiniciar` | Empieza un tablero nuevo (sin registrar puntos) |
| `ayuda` | Muestra la ayuda |
| `salir` | Termina la partida |

La primera casilla que se descubre nunca es mina.

### Solitario

| Comando | Acción |
|---------|--------|
| `r` | Robar del mazo (si está vacío, recicla el descarte) |
| `d <col>` | Mover el descarte a una columna |
| `df` | Mover el descarte a su fundación |
| `m <origen> <destino> [n]` | Mover n cartas (1 por defecto) entre columnas |
| `f <col>` | Mover la última carta de una columna a su fundación |
| `n` | Nueva partida (sin registrar puntos) |
| `a` | Muestra la ayuda |
| `s` | Termina la partida |
