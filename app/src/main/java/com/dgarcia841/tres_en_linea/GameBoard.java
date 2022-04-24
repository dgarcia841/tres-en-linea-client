package com.dgarcia841.tres_en_linea;

/**
 * Representación en el cliente del tablero. La lógica de juego no está acá, sino en el servidor
 */
public class GameBoard {
    private STATUS[] board;

    public enum STATUS {
        EMPTY,
        PLAYER,
        RIVAL
    }
    public GameBoard() {
        board = new STATUS[9];
        for(int i = 0; i < 9; i++) {
            board[i] = STATUS.EMPTY;
        }
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
}
