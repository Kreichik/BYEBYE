package net;

import core.GameEngine;
import music.MusicController;
import music.sound.AudioManager;
import music.sound.JLayerAudioManager;
import patterns.factory.CharacterFactory;
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

            System.out.println("Initializing server-side audio...");
            AudioManager audioManager = new JLayerAudioManager();
            MusicController musicController = new MusicController(audioManager);

            gameEngine.addObserver(musicController);
            System.out.println("MusicController is now observing the game state.");

            Object gameStateLock = gameEngine.getGameStateLock();

            while (running && nextClientId <= 2) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress() + " with ID: " + nextClientId);

                synchronized (gameStateLock) {
                    if (nextClientId == 1) {
                        CharacterFactory.getFactory().createHero(CharacterFactory.HeroType.WARRIOR_LEFT, nextClientId);
                    } else if (nextClientId == 2) {
                        CharacterFactory.getFactory().createHero(CharacterFactory.HeroType.ARCHER_RIGHT, nextClientId);
                    }
                }

                ClientHandler clientHandler = new ClientHandler(clientSocket, nextClientId, gameEngine);
                gameEngine.addObserver(clientHandler);
                new Thread(clientHandler).start();

                nextClientId++;
            }

            System.out.println("All clients connected. Server is running...");

            while (running) {
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            if (running) {
                e.printStackTrace();
            }
        } finally {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (Exception e) {
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
    private ObjectInputStream in;
    private volatile boolean running = true;

    public ClientHandler(Socket socket, int clientId, GameEngine gameEngine) {
        this.socket = socket;
        this.clientId = clientId;
        this.gameEngine = gameEngine;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            send(gameEngine.getCurrentGameState());

            while (running) {
                PlayerAction action = (PlayerAction) in.readObject();
                action.setClientId(clientId);
                gameEngine.addPlayerAction(action);
            }

        } catch (Exception e) {
            System.out.println("Client " + clientId + " disconnected.");
            e.printStackTrace();
        } finally {
            gameEngine.removeObserver(this);
            close();
        }
    }

    private synchronized void send(Object state) {
        try {
            if (out != null && running) {
                out.reset();
                out.writeObject(state);
                out.flush();
            }
        } catch (Exception e) {
            System.out.println("Failed to send state to client " + clientId);
            e.printStackTrace();
            close();
        }
    }

    @Override
    public void update(Object state) {
        send(state);
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