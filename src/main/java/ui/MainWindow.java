package ui;

import core.GameState;
import net.NetworkFacade;
import patterns.factory.CharacterFactory;
import javax.swing.JFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindow extends JFrame {
    private final NetworkFacade networkFacade;

    public MainWindow(RoleSelectionDialog.Role role, String serverIp) {
        this.networkFacade = new NetworkFacade();

        setTitle("Game - " + role.name());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        GamePanel gamePanel = new GamePanel(role, networkFacade);
        add(gamePanel);
        pack();
        setLocationRelativeTo(null);

        GameState initialGameState = gamePanel.getInitialGameState();
        CharacterFactory.init(initialGameState);

        if (role == RoleSelectionDialog.Role.BOSS) {
            CharacterFactory.getFactory().createBoss(CharacterFactory.BossType.FIRE_MAGE, 0);
        }

        networkFacade.start(role, serverIp, initialGameState, gamePanel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                networkFacade.stop();
            }
        });
    }
}