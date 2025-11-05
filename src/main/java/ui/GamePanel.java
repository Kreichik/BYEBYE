package ui;

import core.GameState;
import model.GameObject;
import net.NetworkFacade;
import net.PlayerAction;
import patterns.observer.IObserver;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
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

    public GamePanel(RoleSelectionDialog.Role role, NetworkFacade networkFacade) {
        this.role = role;
        this.networkFacade = networkFacade;
        this.gameState = new GameState();

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
                if (pressedKeys.contains(keyCode)) return;

                pressedKeys.add(keyCode);
                handleKeyPress(keyCode);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int keyCode = e.getKeyCode();
                pressedKeys.remove(keyCode);
                handleKeyRelease(keyCode);
            }
        });
    }

    private void handleKeyPress(int keyCode) {
        PlayerAction.ActionType type = null;
        if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
            type = PlayerAction.ActionType.MOVE_LEFT;
        } else if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) {
            type = PlayerAction.ActionType.MOVE_RIGHT;
        } else if (keyCode == KeyEvent.VK_SPACE) {
            type = PlayerAction.ActionType.ATTACK;
        }
        if (type != null) {
            networkFacade.sendActionToServer(new PlayerAction(type));
        }
    }

    private void handleKeyRelease(int keyCode) {
        PlayerAction.ActionType type = null;
        if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
            type = PlayerAction.ActionType.STOP_MOVE_LEFT;
        } else if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) {
            type = PlayerAction.ActionType.STOP_MOVE_RIGHT;
        }
        if (type != null) {
            networkFacade.sendActionToServer(new PlayerAction(type));
        }
    }

    @Override
    public void update(Object state) {
        if (state instanceof GameState) {
            this.gameState = (GameState) state;
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameState != null) {
            for (GameObject obj : gameState.getGameObjects()) {
                obj.render(g, screenOffset);
            }
        }
    }

    public GameState getInitialGameState() {
        return gameState;
    }
}