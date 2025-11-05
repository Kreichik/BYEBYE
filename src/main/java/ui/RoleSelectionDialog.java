package ui;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

public class RoleSelectionDialog extends JDialog {
    public enum Role { BOSS, LEFT_HERO, RIGHT_HERO }
    private Role selectedRole = null;

    private RoleSelectionDialog() {
        setTitle("Choose Your Role");
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));

        JButton bossButton = new JButton("Boss (Server)");
        bossButton.addActionListener(this::onRoleSelected);
        panel.add(bossButton);

        JButton leftHeroButton = new JButton("Left Hero (Client)");
        leftHeroButton.addActionListener(this::onRoleSelected);
        panel.add(leftHeroButton);

        JButton rightHeroButton = new JButton("Right Hero (Client)");
        rightHeroButton.addActionListener(this::onRoleSelected);
        panel.add(rightHeroButton);

        add(panel);
        pack();
        setLocationRelativeTo(null);
    }

    private void onRoleSelected(ActionEvent e) {
        String command = ((JButton) e.getSource()).getText();
        if (command.startsWith("Boss")) {
            selectedRole = Role.BOSS;
        } else if (command.startsWith("Left")) {
            selectedRole = Role.LEFT_HERO;
        } else if (command.startsWith("Right")) {
            selectedRole = Role.RIGHT_HERO;
        }
        dispose();
    }

    public static Role showDialog() {
        RoleSelectionDialog dialog = new RoleSelectionDialog();
        dialog.setVisible(true);
        return dialog.selectedRole;
    }
}