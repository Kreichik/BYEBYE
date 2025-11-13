package ui.video;

import java.awt.Component;

public interface VideoPlayer {
    void play(Component parent, String path, long fallbackDurationMillis, Runnable onComplete, Runnable onError);
}
