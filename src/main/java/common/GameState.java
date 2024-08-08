package common;

import java.io.Serializable;

public class GameState implements Serializable {
    private char[][] board;
    private char currentPlayer;
    private boolean gameWon;
    private char winner;

    public GameState() {
        board = new char[3][3];
        currentPlayer = 'X';
        gameWon = false;
        winner = ' ';
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
    }

    public char[][] getBoard() {
        return board;
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public char getWinner() {
        return winner;
    }

    public boolean makeMove(int row, int col) {
        if (board[row][col] == ' ' && !gameWon) {
            board[row][col] = currentPlayer;
            if (checkWin()) {
                gameWon = true;
                winner = currentPlayer;
            }
            currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
            return true;
        }
        return false;
    }

    private boolean checkWin() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == currentPlayer && board[i][1] == currentPlayer && board[i][2] == currentPlayer)
                return true;
            if (board[0][i] == currentPlayer && board[1][i] == currentPlayer && board[2][i] == currentPlayer)
                return true;
        }
        if (board[0][0] == currentPlayer && board[1][1] == currentPlayer && board[2][2] == currentPlayer)
            return true;
        if (board[0][2] == currentPlayer && board[1][1] == currentPlayer && board[2][0] == currentPlayer)
            return true;
        return false;
    }
}
