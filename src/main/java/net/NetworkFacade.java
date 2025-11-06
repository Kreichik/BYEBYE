package net;

import core.GameEngine;
import core.GameState;
import patterns.factory.CharacterFactory;
import ui.GamePanel;
import ui.RoleSelectionDialog;

public class NetworkFacade {
    private GameServer server;
    private GameClient client;
    private GameEngine engine;
    private RoleSelectionDialog.Role role;

    public void start(RoleSelectionDialog.Role role, String ip, GameState gameState, GamePanel gamePanel) {
        this.role = role;
        if (role == RoleSelectionDialog.Role.BOSS) {
            synchronized (gameState) {
                CharacterFactory.init(gameState);
                CharacterFactory.getFactory().createBoss(CharacterFactory.BossType.FIRE_MAGE, 0);
            }

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
        if (role == RoleSelectionDialog.Role.BOSS && engine != null) {
            action.setClientId(0);
            engine.addPlayerAction(action);
        } else if (client != null) {
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