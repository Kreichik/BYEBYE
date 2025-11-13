package ui.video;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.io.File;
import java.net.URL;

public class JavaFxVideoPlayer implements VideoPlayer {
    @Override
    public void play(Component parent, String path, long fallbackDurationMillis, Runnable onComplete, Runnable onError) {
        try {
            URL resourceUrl = parent.getClass().getClassLoader().getResource(path);
            String mediaUri;
            if (resourceUrl != null) {
                mediaUri = resourceUrl.toExternalForm();
            } else {
                File f = new File(path);
                if (!f.exists()) { onError.run(); return; }
                mediaUri = f.toURI().toString();
            }

            JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent));
            dialog.setModal(true);
            dialog.setUndecorated(true);
            dialog.setSize(parent.getWidth(), parent.getHeight());
            Point p = parent.getLocationOnScreen();
            dialog.setLocation(p);
            dialog.getContentPane().setBackground(Color.BLACK);
            dialog.setLayout(new BorderLayout());

            JFXPanel jfxPanel = new JFXPanel();
            dialog.add(jfxPanel, BorderLayout.CENTER);

            Platform.runLater(() -> {
                try {
                    Media media = new Media(mediaUri);
                    MediaPlayer player = new MediaPlayer(media);
                    MediaView mediaView = new MediaView(player);
                    mediaView.setPreserveRatio(true);
                    mediaView.setFitWidth(dialog.getWidth());
                    mediaView.setFitHeight(dialog.getHeight());
                    Group root = new Group(mediaView);
                    Scene scene = new Scene(root, dialog.getWidth(), dialog.getHeight());
                    jfxPanel.setScene(scene);
                    player.setOnEndOfMedia(() -> {
                        try { dialog.dispose(); } catch (Exception ignored) {}
                        onComplete.run();
                    });
                    player.play();
                } catch (Throwable t) {
                    try { dialog.dispose(); } catch (Exception ignored) {}
                    onError.run();
                }
            });

            SwingUtilities.invokeLater(() -> dialog.setVisible(true));
        } catch (Throwable t) {
            try {
                new SimulatedVideoPlayer().play(parent, path, fallbackDurationMillis, onComplete, onError);
            } catch (Throwable ignored) {
                onError.run();
            }
        }
    }
}
