package Server;

import common.GameState;
import common.Move;
import common.Player;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static Map<String, Player> leaderboard = new HashMap<>();
    private static ArrayList<Socket> clients = new ArrayList<>();
    private static GameState gameState = new GameState();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server started on port 12345");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clients.add(clientSocket);
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

            Player player = (Player) in.readObject();
            leaderboard.putIfAbsent(player.getName(), player);

            out.writeObject(gameState);

            while (true) {
                Move move = (Move) in.readObject();
                if (gameState.makeMove(move.getRow(), move.getCol())) {
                    broadcastGameState();
                    if (gameState.isGameWon()) {
                        leaderboard.get(player.getName()).incrementScore();
                        broadcastLeaderboard();
                        gameState = new GameState();
                    }
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void broadcastGameState() {
        for (Socket clientSocket : clients) {
            try {
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.writeObject(gameState);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void broadcastLeaderboard() {
        for (Socket clientSocket : clients) {
            try {
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.writeObject(new ArrayList<>(leaderboard.values()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
