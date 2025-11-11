package core;


import ui.MainWindow;
import ui.RoleSelectionDialog;
import javax.swing.JOptionPane;

public class Main {
    public static final int SCREEN_WIDTH = 800 ;//бека сделай красиво
    public static final int SCREEN_HEIGHT = 600;
    public static final int WORLD_WIDTH = SCREEN_WIDTH * 3;

    public static void main(String[] args) {
        RoleSelectionDialog.Role selectedRole = RoleSelectionDialog.showDialog();
        if (selectedRole == null) {
            System.exit(0);
        }

        String serverIp = "127.0.0.1";
        if (selectedRole != RoleSelectionDialog.Role.BOSS) {
            serverIp = JOptionPane.showInputDialog("Enter Boss IP address:", "127.0.0.1");
            if (serverIp == null || serverIp.trim().isEmpty()) {
                System.exit(0);
            }
        }

        MainWindow window = new MainWindow(selectedRole, serverIp);
        window.setVisible(true);
    }
}