package net;

import core.GameEngine;
import core.GameState;
import music.SfxControllerBoss;
import music.SfxControllerClient;
import patterns.factory.CharacterFactory;
import ui.GamePanel;
import ui.RoleSelectionDialog;
import music.MusicController;
import music.sound.AudioManager;
import music.sound.JLayerAudioManager;

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

            AudioManager audioManager = new JLayerAudioManager();
            MusicController musicController = new MusicController(audioManager);
            engine.addObserver(musicController);

            SfxControllerBoss sfxController = new SfxControllerBoss();
            engine.addObserver(sfxController);

            server = new GameServer(9999, engine);
            new Thread(server).start();
            new Thread(engine).start();

        } else {
            // SfxControllerClient sfxController = new SfxControllerClient(); // Эту логику нужно будет реализовать иначе
            // engine.addObserver(sfxController); // <<< ЭТА СТРОКА ВЫЗЫВАЛА ОШИБКУ И БЫЛА УДАЛЕНА
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
    public void pauseGame() {
        if (role == RoleSelectionDialog.Role.BOSS && engine != null) {
            engine.addPlayerAction(withClientId(new PlayerAction(PlayerAction.ActionType.PAUSE), 0));
        } else if (client != null) {
            client.sendAction(new PlayerAction(PlayerAction.ActionType.PAUSE));
        }
    }

    public void resumeGame() {
        if (role == RoleSelectionDialog.Role.BOSS && engine != null) {
            engine.addPlayerAction(withClientId(new PlayerAction(PlayerAction.ActionType.RESUME), 0));
        } else if (client != null) {
            client.sendAction(new PlayerAction(PlayerAction.ActionType.RESUME));
        }
    }

    public void setStrategyMelee() {
        dispatchStrategy(PlayerAction.ActionType.STRATEGY_MELEE);
    }

    public void setStrategyRanged() {
        dispatchStrategy(PlayerAction.ActionType.STRATEGY_RANGED);
    }

    public void setStrategyMagic() {
        dispatchStrategy(PlayerAction.ActionType.STRATEGY_MAGIC);
    }

    private void dispatchStrategy(PlayerAction.ActionType type) {
        if (role == RoleSelectionDialog.Role.BOSS && engine != null) {
            engine.addPlayerAction(withClientId(new PlayerAction(type), 0));
        } else if (client != null) {
            client.sendAction(new PlayerAction(type));
        }
    }

    private PlayerAction withClientId(PlayerAction action, int id) {
        action.setClientId(id);
        return action;
    }
}