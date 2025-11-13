package ui.video;

import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DesktopVideoPlayer implements VideoPlayer {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void play(Component parent, String path, long fallbackDurationMillis, Runnable onComplete, Runnable onError) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                onError.run();
                return;
            }
            if (!Desktop.isDesktopSupported()) {
                onError.run();
                return;
            }
            Desktop.getDesktop().open(file);
            scheduler.schedule(() -> {
                try { scheduler.shutdown(); } catch (Exception ignored) {}
                onComplete.run();
            }, fallbackDurationMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            onError.run();
        }
    }
}
