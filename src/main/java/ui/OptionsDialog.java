package ui;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

public class OptionsDialog extends JDialog {

    public enum Strategy { MELEE, RANGED, MAGIC }

    private Strategy selected;

    public OptionsDialog() {
        setTitle("Options");
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.add(new JLabel("Choose Attack Strategy"), BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(1, 3, 10, 10));
        JButton melee = new JButton("Melee");
        JButton ranged = new JButton("Ranged");
        JButton magic = new JButton("Magic");

        melee.addActionListener(this::onMelee);
        ranged.addActionListener(this::onRanged);
        magic.addActionListener(this::onMagic);

        buttons.add(melee);
        buttons.add(ranged);
        buttons.add(magic);
        content.add(buttons, BorderLayout.CENTER);

        setContentPane(content);
        pack();
        setLocationRelativeTo(null);
    }

    private void onMelee(ActionEvent e) { selected = Strategy.MELEE; dispose(); }
    private void onRanged(ActionEvent e) { selected = Strategy.RANGED; dispose(); }
    private void onMagic(ActionEvent e) { selected = Strategy.MAGIC; dispose(); }

    public Strategy showDialog() {
        setVisible(true);
        return selected;
    }
}


