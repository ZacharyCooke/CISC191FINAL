package client;

import common.GameState;
import common.Move;
import common.Player;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter your name: ");
            String name = scanner.nextLine();
            Player player = new Player(name);
            out.writeObject(player);

            new Thread(() -> {
                try {
                    while (true) {
                        Object response = in.readObject();
                        if (response instanceof GameState) {
                            GameState gameState = (GameState) response;
                            printBoard(gameState.getBoard());
                        } else if (response instanceof List) {
                            List<Player> leaderboard = (List<Player>) response;
                            printLeaderboard(leaderboard);
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }).start();

            while (true) {
                System.out.print("Enter row and column (e.g., 1 1): ");
                int row = scanner.nextInt();
                int col = scanner.nextInt();
                out.writeObject(new Move(row, col));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printBoard(char[][] board) {
        for (char[] row : board) {
            for (char cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    private static void printLeaderboard(List<Player> leaderboard) {
        System.out.println("Leaderboard:");
        for (Player player : leaderboard) {
            System.out.println(player.getName() + ": " + player.getScore());
        }
    }
}
