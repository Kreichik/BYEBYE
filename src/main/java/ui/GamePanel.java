package ui;

import core.GameState;
import model.GameObject;
import model.characters.GameCharacter;
import net.NetworkFacade;
import net.PlayerAction;
import patterns.observer.IObserver;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import java.awt.Window;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
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
    private BufferedImage hpRowImage;
    private volatile boolean gameEnded = false;

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

        hpRowImage = ImageLoader.loadImage("row_points/hp_row.jpg");

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
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
                        case MELEE:
                            networkFacade.setStrategyMelee();
                            break;
                        case RANGED:
                            networkFacade.setStrategyRanged();
                            break;
                        case MAGIC:
                            networkFacade.setStrategyMagic();
                            break;
                    }
                }
                networkFacade.resumeGame();
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
        } else if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) {
            type = PlayerAction.ActionType.MOVE_UP;
        } else if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) {
            type = PlayerAction.ActionType.MOVE_DOWN;
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
        } else if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) {
            type = PlayerAction.ActionType.STOP_MOVE_UP;
        } else if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) {
            type = PlayerAction.ActionType.STOP_MOVE_DOWN;
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
            checkWinConditions();
        }
    }

    //    @Override
//    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        if (gameState != null) {
//            for (GameObject obj : gameState.getGameObjects()) {
//                obj.render(g, screenOffset);
//            }
//        }
//    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Image background = new ImageIcon("src/main/resources/skins/background.png").getImage();
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

        if (gameState != null) {
            for (GameObject obj : gameState.getGameObjects()) {
                obj.render(g, screenOffset);

                if (obj instanceof GameCharacter) {
                    GameCharacter character = (GameCharacter) obj;
                    int healthBarX = (int) character.getX() + screenOffset;
                    int healthBarY = (int) character.getY() - ShowHP.BAR_HEIGHT - ShowHP.TEXT_OFFSET_Y - 5;
                    if (hpRowImage != null) {
                        ShowHP.drawHealthBar(g, character.getHealth(), character.getMaxHealth(),
                                healthBarX, healthBarY, character.getName());

                    } else {
                        ShowHP.drawHealthBar(g, character.getHealth(), character.getMaxHealth(),
                                healthBarX, healthBarY, character.getName());
                    }
                }
            }
        }

    }


    public GameState getInitialGameState() {
        return gameState;
    }

    private void checkWinConditions() {
        if (gameEnded || gameState == null) return;

        boolean bossPresent = false;
        boolean bossAlive = false;
        boolean clientsPresent = false;
        boolean anyClientAlive = false;

        for (GameObject obj : gameState.getGameObjects()) {
            if (obj instanceof GameCharacter) {
                GameCharacter ch = (GameCharacter) obj;
                if (ch.getId() == 0) {
                    bossPresent = true;
                    bossAlive = obj.isActive();
                } else {
                    clientsPresent = true;
                    if (obj.isActive()) anyClientAlive = true;
                }
            }
        }

        String winner = null;
        if (bossPresent && !bossAlive) {
            winner = "Clients";
        } else if (clientsPresent && !anyClientAlive) {
            winner = "Boss";
        }

        if (winner != null) {
            gameEnded = true;
            final String message = winner + " win";
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
                try {
                    networkFacade.stop();
                } catch (Exception ignored) {
                }
                Window w = SwingUtilities.getWindowAncestor(this);
                if (w != null) {
                    w.dispose();
                }
                System.exit(0);
            });
        }

    }
}