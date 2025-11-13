package ui.video;

import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimulatedVideoPlayer implements VideoPlayer {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void play(Component parent, String path, long fallbackDurationMillis, Runnable onComplete, Runnable onError) {
        try {
            JDialog dialog = new JDialog(javax.swing.SwingUtilities.getWindowAncestor(parent));
            dialog.setModal(true);
            dialog.setUndecorated(true);
            dialog.setSize(parent.getWidth(), parent.getHeight());
            java.awt.Point p = parent.getLocationOnScreen();
            dialog.setLocation(p);
            dialog.getContentPane().setBackground(Color.BLACK);
            JProgressBar bar = new JProgressBar(0, 100);
            bar.setForeground(Color.WHITE);
            dialog.setLayout(new BorderLayout());
            dialog.add(bar, BorderLayout.SOUTH);
            SwingUtilities.invokeLater(() -> dialog.setVisible(true));

            long duration = fallbackDurationMillis;
            long steps = 100;
            long stepMillis = Math.max(1, duration / steps);
            for (int i = 1; i <= steps; i++) {
                final int v = i;
                scheduler.schedule(() -> bar.setValue(v), i * stepMillis, TimeUnit.MILLISECONDS);
            }
            scheduler.schedule(() -> {
                try { dialog.dispose(); } catch (Exception ignored) {}
                onComplete.run();
                try { scheduler.shutdown(); } catch (Exception ignored) {}
            }, duration + 10, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            onError.run();
        }
    }
}
