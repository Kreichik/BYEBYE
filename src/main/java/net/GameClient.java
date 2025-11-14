package net;

import core.GameState;
import patterns.observer.IObserver;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameClient implements Runnable {
    private final String serverIp;
    private final int port;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in; // Добавляем поле для in
    private IObserver gameStateObserver;
    private volatile boolean running = true;

    public GameClient(String serverIp, int port, IObserver gameStateObserver) {
        this.serverIp = serverIp;
        this.port = port;
        this.gameStateObserver = gameStateObserver;
        System.out.println("GameClient: Initialized for server " + serverIp + ":" + port);
    }

    public void sendAction(PlayerAction action) {
        try {
            if (out != null) {
                System.out.println("GameClient: Sending action: " + action.getType());
                out.writeObject(action);
                out.flush();
                System.out.println("GameClient: Action " + action.getType() + " sent successfully.");
            } else {
                System.out.println("GameClient: Cannot send action, ObjectOutputStream is null.");
            }
        } catch (Exception e) {
            System.err.println("GameClient: Error sending action " + action.getType() + ": " + e.getMessage());
            e.printStackTrace();
            stop();
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("GameClient: Attempting to connect to " + serverIp + ":" + port);
            socket = new Socket(serverIp, port);
            System.out.println("GameClient: Connected to server.");

            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush(); // Важно, чтобы заголовки ObjectOutputStream были отправлены
            System.out.println("GameClient: ObjectOutputStream initialized and flushed.");

            in = new ObjectInputStream(socket.getInputStream()); // Инициализируем поле in
            System.out.println("GameClient: ObjectInputStream initialized.");

            // Клиент ждет начального GameState от сервера
            System.out.println("GameClient: Waiting for initial GameState from server...");
            GameState initialGameState = (GameState) in.readObject();
            System.out.println("GameClient: Received initial GameState.");

            if (gameStateObserver != null) {
                gameStateObserver.update(initialGameState); // Обновляем UI/логику клиента начальным состоянием
            }

            // Теперь входим в основной цикл получения обновлений
            while (running) {
                System.out.println("GameClient: Waiting for GameState update from server...");
                GameState gameState = (GameState) in.readObject();
                System.out.println("GameClient: Received GameState update.");
                if (gameStateObserver != null) {
                    gameStateObserver.update(gameState);
                }
            }
        } catch (Exception e) {
            System.err.println("GameClient: Connection to server lost or error occurred: " + e.getMessage());
            e.printStackTrace(); // Выводим стектрейс клиента, чтобы понять, что именно произошло
        } finally {
            System.out.println("GameClient: Stopping and closing resources.");
            stop();
        }
    }

    public void stop() {
        running = false;
        try {
            if (socket != null) {
                socket.close();
                System.out.println("GameClient: Socket closed.");
            }
        } catch (Exception e) {
            System.err.println("GameClient: Error closing socket: " + e.getMessage());
            e.printStackTrace();
        }
    }

}