package net;

import core.GameEngine;
import core.GameState;
import patterns.observer.IObserver;
import ui.GamePanel;
import ui.RoleSelectionDialog;

public class NetworkFacade {
    private GameServer server;
    private GameClient client;
    private GameEngine engine;

    public void start(RoleSelectionDialog.Role role, String ip, GameState gameState, GamePanel gamePanel) {
        if (role == RoleSelectionDialog.Role.BOSS) {
            engine = new GameEngine(gameState);
            engine.addObserver(gamePanel);

            server = new GameServer(9999, engine);

            new Thread(server).start();
            new Thread(engine).start();
        } else {
            client = new GameClient(ip, 9999, gamePanel);
            new Thread(client).start();
        }
    }

    public void sendActionToServer(PlayerAction action) {
        if (client != null) {
            client.sendAction(action);
        }
    }

    public void stop() {
        if (server != null) {
            server.stop();
        }
        if (client != null) {
            client.stop();
        }
        if (engine != null) {
            engine.stop();
        }
    }
}