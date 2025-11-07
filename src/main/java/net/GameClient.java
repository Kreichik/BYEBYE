package net;

import core.GameState;
import patterns.observer.IObserver;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GameClient implements Runnable {
    private final String serverIp;
    private final int port;
    private Socket socket;
    private ObjectOutputStream out;
    private IObserver gameStateObserver;
    private volatile boolean running = true;

    public GameClient(String serverIp, int port, IObserver gameStateObserver) {
        this.serverIp = serverIp;
        this.port = port;
        this.gameStateObserver = gameStateObserver;
    }

    public void sendAction(PlayerAction action) {
        try {
            if (out != null) {
                out.writeObject(action);
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            stop();
        }
    }

    @Override
    public void run() {
        try {
            socket = new Socket(serverIp, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush(); // честно хз здесь была проблема
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            while (running) {
                GameState gameState = (GameState) in.readObject();
                if (gameStateObserver != null) {
                    gameStateObserver.update(gameState);
                }
            }
        } catch (Exception e) {
            System.err.println("Connection to server lost.");
        } finally {
            stop();
        }
    }
    public void stop() {
        running = false;
        try {
            if (socket != null) socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}