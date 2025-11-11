package music.sound;

import java.util.concurrent.CompletableFuture;

public interface AudioManager {
    CompletableFuture<Void> playMusic(String resourcePath, boolean loop);

    void stopMusic();

    boolean isMusicPlaying();
}
