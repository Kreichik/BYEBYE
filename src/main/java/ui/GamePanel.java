package ui;

import core.GameState;
import model.GameObject;
import model.characters.Boss;
import model.characters.GameCharacter;
import music.SfxControllerClient; // <<< ДОБАВЬТЕ ЭТОТ ИМПОРТ
import net.NetworkFacade;
import net.PlayerAction;
import patterns.observer.IObserver;
import javax.swing.*;
import java.awt.*;
import ui.ImageLoader;
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
    private int cameraOffsetX = 0;
    private int cameraOffsetY = 0;
    private final Set<Integer> pressedKeys = new HashSet<>();
    private volatile boolean gameEnded = false;
    private volatile boolean inputLocked = false;
    private final SfxControllerClient sfxController; // <<< ДОБАВЬТЕ ЭТО ПОЛЕ

    public GamePanel(RoleSelectionDialog.Role role, NetworkFacade networkFacade) {
        this.role = role;
        this.networkFacade = networkFacade;
        this.gameState = new GameState();

        // <<< ДОБАВЬТЕ ЭТОТ БЛОК
        if (role != RoleSelectionDialog.Role.BOSS) {
            this.sfxController = new SfxControllerClient();
        } else {
            this.sfxController = null;
        }

        // camera offsets are computed dynamically in update/paint

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
            GameState newGameState = (GameState) state;

            // <<< ДОБАВЬТЕ ЭТОТ БЛОК
            if (sfxController != null) {
                sfxController.update(newGameState); // Передаем новое состояние в SfxController
            }

            this.gameState = newGameState;
            repaint();
            checkWinConditions();

        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        java.awt.image.BufferedImage background = ImageLoader.loadImage("skins/background.png");
        if (background != null) {
            int bgW = background.getWidth();
            int bgH = background.getHeight();
            int startX = -Math.floorMod(cameraOffsetX, bgW);
            int startY = -Math.floorMod(cameraOffsetY, bgH);
            for (int x = startX; x < getWidth(); x += bgW) {
                for (int y = startY; y < getHeight(); y += bgH) {
                    g.drawImage(background, x, y, bgW, bgH, this);
                }
            }
        }

        if (gameState != null) {
            updateCameraOffsets();
            for (GameObject obj : gameState.getGameObjects()) {
                obj.render(g, cameraOffsetX, cameraOffsetY);
                obj.accept(new patterns.visitor.HealthBarVisitor(g, cameraOffsetX, cameraOffsetY));
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

    private void updateCameraOffsets() {
        int myId = role == RoleSelectionDialog.Role.BOSS ? 0 : (role == RoleSelectionDialog.Role.LEFT_HERO ? 1 : 2);
        GameCharacter me = gameState.getCharacterById(myId);
        if (me != null) {
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            cameraOffsetX = centerX - (int) me.getX();
            cameraOffsetY = centerY - (int) me.getY();
        }
    }
}
