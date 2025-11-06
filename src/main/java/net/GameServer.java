package net;

import core.GameEngine;
import patterns.factory.CharacterFactory;
import ui.RoleSelectionDialog;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer implements Runnable {
    private final int port;
    private final GameEngine gameEngine;
    private ServerSocket serverSocket;
    private int nextClientId = 1;
    private volatile boolean running = true;

    public GameServer(int port, GameEngine gameEngine) {
        this.port = port;
        this.gameEngine = gameEngine;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port: " + port);

            while (running && nextClientId <= 2) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress() + " with ID: " + nextClientId);

                if (nextClientId == 1) {
                    CharacterFactory.getFactory().createHero(CharacterFactory.HeroType.WARRIOR_LEFT, nextClientId);
                } else if (nextClientId == 2) {
                    CharacterFactory.getFactory().createHero(CharacterFactory.HeroType.ARCHER_RIGHT, nextClientId);
                }

                ClientHandler clientHandler = new ClientHandler(clientSocket, nextClientId, gameEngine);
                gameEngine.addObserver(clientHandler);
                new Thread(clientHandler).start();

                nextClientId++;
            }
        } catch (Exception e) {
            if (running) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable, patterns.observer.IObserver {
    private final Socket socket;
    private final int clientId;
    private final GameEngine gameEngine;
    private ObjectOutputStream out;
    private volatile boolean running = true;

    public ClientHandler(Socket socket, int clientId, GameEngine gameEngine) {
        this.socket = socket;
        this.clientId = clientId;
        this.gameEngine = gameEngine;
    }

    @Override
    public void run() {
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.out.flush(); // <<< ДОБАВЬТЕ ЭТУ СТРОКУ
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            update(gameEngine.getCurrentGameState().deepCopy());

            while (running) {
                PlayerAction action = (PlayerAction) in.readObject();
                action.setClientId(clientId);
                gameEngine.addPlayerAction(action);
            }

        } catch (Exception e) {
            System.out.println("Client " + clientId + " disconnected.");
            e.printStackTrace(); // Добавим вывод стека для отладки
        } finally {
            gameEngine.removeObserver(this);
            close();
        }
    }

    @Override
    public void update(Object state) {
        try {
            if (out != null) {
                out.writeObject(state);
                out.reset();
            }
        } catch (Exception e) {
            System.out.println("Failed to send state to client " + clientId);
            close();
        }
    }

    private void close() {
        running = false;
        try {
            if (socket != null) socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}