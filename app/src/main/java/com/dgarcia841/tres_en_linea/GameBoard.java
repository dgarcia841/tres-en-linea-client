package com.dgarcia841.tres_en_linea;

/**
 * Representación en el cliente del tablero. La lógica de juego no está acá, sino en el servidor
 */
public class GameBoard {
    private STATUS[] board;
    private boolean[] winner;
    public enum STATUS {
        EMPTY,
        PLAYER,
        RIVAL
    }
    public GameBoard() {
        board = new STATUS[9];
        winner = new boolean[9];
        clear();
    }

    public STATUS get(int i) {
        return board[i];
    }
    public STATUS get(int x, int y) {
        return board[3*y + x];
    }
    public void set(int i, STATUS value) {
        board[i] = value;
    }
    public void set(int x, int y, STATUS value) {
        board[3*y + x] = value;
    }

    /**
     * Limpia el tablero
     */
    public void clear() {
        for(int i = 0; i < 9; i++) {
            board[i] = STATUS.EMPTY;
            winner[i] = false;
        }
    }

    /**
     * Marcar una celda como parte de la jugada ganadora
     * @param i
     */
    public void setWinner(int i) {
        winner[i] = true;
    }
    /**
     * Marcar una celda como parte de la jugada ganadora
     * @param x
     * @param y
     */
    public void setWinner(int x, int y) {
        winner[3*y + x] = true;
    }

    /**
     * Comprueba si una celda es ganadora
     * @param i
     */
    public boolean isWinner(int i) {
        return winner[i];
    }

    /**
     * Comprueba si una celda es ganadora
     * @param x
     * @param y
     * @return
     */
    public boolean isWinner(int x, int y) {
        return winner[3*y + x];
    }
}
