package ui.video;

public interface VideoPlayer {
    void play(String path, long fallbackDurationMillis, Runnable onComplete, Runnable onError);
}

