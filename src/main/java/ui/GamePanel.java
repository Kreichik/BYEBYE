package ui;

import core.GameState;
import model.GameObject;
import model.characters.Boss;
import model.characters.GameCharacter;
import net.NetworkFacade;
import ui.ads.AdManager;
import ui.ads.AdConfig;
import ui.video.JavaFxVideoPlayer;
import net.PlayerAction;
import patterns.observer.IObserver;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import static core.Main.SCREEN_HEIGHT;
import static core.Main.SCREEN_WIDTH;

public class GamePanel extends JPanel implements IObserver {

    private GameState gameState;
    private final RoleSelectionDialog.Role role;
    private final NetworkFacade networkFacade;
    private final int screenOffset;
    private final Set<Integer> pressedKeys = new HashSet<>();
    private volatile boolean gameEnded = false;
    private volatile boolean inputLocked = false;
    private final AdManager adManager;
    private long lastUpdateNano = 0;

    public GamePanel(RoleSelectionDialog.Role role, NetworkFacade networkFacade) {
        this.role = role;
        this.networkFacade = networkFacade;
        this.gameState = new GameState();
        this.adManager = new AdManager(networkFacade, this, new JavaFxVideoPlayer(), new AdConfig(60_000, 10_000, "ad/ad.mp4"));
        this.adManager.setInputLockHandlers(() -> inputLocked = true, () -> inputLocked = false);

        if (role == RoleSelectionDialog.Role.LEFT_HERO) {
            screenOffset = 0;
        } else if (role == RoleSelectionDialog.Role.BOSS) {
            screenOffset = -SCREEN_WIDTH;
        } else {
            screenOffset = -SCREEN_WIDTH * 2;
        }

        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (inputLocked) return;
                if (keyCode == KeyEvent.VK_ESCAPE) {
                    handlePauseAndOptions();
                    return;
                }
                if (pressedKeys.contains(keyCode)) return;

                pressedKeys.add(keyCode);
                handleKeyPress(keyCode);
            }

            private void handlePauseAndOptions() {
                networkFacade.pauseGame();
                OptionsDialog dialog = new OptionsDialog();
                OptionsDialog.Strategy choice = dialog.showDialog();
                if (choice != null) {
                    switch (choice) {
                        case MELEE: networkFacade.setStrategyMelee(); break;
                        case RANGED: networkFacade.setStrategyRanged(); break;
                        case MAGIC: networkFacade.setStrategyMagic(); break;
                    }
                }
                networkFacade.resumeGame();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int keyCode = e.getKeyCode();
                pressedKeys.remove(keyCode);
                if (inputLocked) return;
                handleKeyRelease(keyCode);
            }
        });
    }

    private void handleKeyPress(int keyCode) {
        PlayerAction.ActionType type = null;
        if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) type = PlayerAction.ActionType.MOVE_LEFT;
        else if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) type = PlayerAction.ActionType.MOVE_RIGHT;
        else if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) type = PlayerAction.ActionType.MOVE_UP;
        else if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) type = PlayerAction.ActionType.MOVE_DOWN;
        else if (keyCode == KeyEvent.VK_SPACE) type = PlayerAction.ActionType.ATTACK;
        else if (keyCode == KeyEvent.VK_Q && role == RoleSelectionDialog.Role.BOSS) type = PlayerAction.ActionType.SPAWN_NPC;

        if (type != null) {
            networkFacade.sendActionToServer(new PlayerAction(type));
        }
    }

    private void handleKeyRelease(int keyCode) {
        PlayerAction.ActionType type = null;
        if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) type = PlayerAction.ActionType.STOP_MOVE_LEFT;
        else if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) type = PlayerAction.ActionType.STOP_MOVE_RIGHT;
        else if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) type = PlayerAction.ActionType.STOP_MOVE_UP;
        else if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) type = PlayerAction.ActionType.STOP_MOVE_DOWN;

        if (type != null) {
            networkFacade.sendActionToServer(new PlayerAction(type));
        }
    }

    @Override
    public void update(Object state) {
        if (state instanceof GameState) {
            this.gameState = (GameState) state;
            repaint();
            checkWinConditions();
            long now = System.nanoTime();
            if (lastUpdateNano != 0) {
                adManager.accumulatePlayTime(now - lastUpdateNano);
            }
            lastUpdateNano = now;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Image background = new ImageIcon("src/main/resources/skins/background.png").getImage();
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

        if (gameState != null) {
            for (GameObject obj : gameState.getGameObjects()) {
                obj.render(g, screenOffset);
                obj.accept(new patterns.visitor.HealthBarVisitor(g, screenOffset));
            }
        }

        if (role == RoleSelectionDialog.Role.BOSS) {
            drawNpcCooldownUI(g);
        }
    }

    private void drawNpcCooldownUI(Graphics g) {
        if (gameState == null) return;
        Boss boss = null;
        for (GameObject obj : gameState.getGameObjects()) {
            if (obj instanceof Boss) {
                boss = (Boss) obj;
                break;
            }
        }

        if (boss == null) return;

        long[] cooldowns = boss.getNpcCooldowns();
        long currentTime = System.currentTimeMillis();
        int circleSize = 30;

        for (int i = 0; i < cooldowns.length; i++) {
            int x = 20 + (i * (circleSize + 10));
            int y = 20;

            if (currentTime >= cooldowns[i]) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.GRAY);
            }
            g.fillOval(x, y, circleSize, circleSize);
            g.setColor(Color.WHITE);
            g.drawOval(x, y, circleSize, circleSize);
        }
    }

    public GameState getInitialGameState() {
        return gameState;
    }

    private void checkWinConditions() {
        if (gameEnded || gameState == null) return;

        boolean bossPresent = false, bossAlive = false, clientsPresent = false, anyClientAlive = false;

        for (GameObject obj : gameState.getGameObjects()) {
            if (obj instanceof GameCharacter) {
                GameCharacter ch = (GameCharacter) obj;
                if (ch.getFactionId() == 0) {
                    bossPresent = true;
                    if (obj.isActive()) bossAlive = true;
                } else {
                    clientsPresent = true;
                    if (obj.isActive()) anyClientAlive = true;
                }
            }
        }

        String winner = null;
        if (bossPresent && !bossAlive) winner = "Clients";
        else if (clientsPresent && !anyClientAlive) winner = "Boss";

        if (winner != null) {
            gameEnded = true;
            final String message = winner + " win";
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
                try {
                    networkFacade.stop();
                } catch (Exception ignored) {}
                Window w = SwingUtilities.getWindowAncestor(this);
                if (w != null) w.dispose();
                System.exit(0);
            });
        }
    }
}