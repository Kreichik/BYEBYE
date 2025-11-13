package music.sound;

import javazoom.jl.player.Player;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public class JLayerAudioManager implements AudioManager {

    private volatile boolean isPlaying = false;
    private volatile Thread playbackThread = null;

    @Override
    public CompletableFuture<Void> playMusic(String resourcePath, boolean loop) {
        stopMusic();
        isPlaying = true;

        Thread thread = new Thread(() -> {
            while (isPlaying) {
                try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
                    if (is == null) {
                        System.err.println("Fie not found: " + resourcePath);
                        break;
                    }
                    Player player = new Player(is);
                    player.play();
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                if (!loop) break;
            }
            isPlaying = false;
        }, "JLayer-Music");

        thread.setDaemon(true);
        thread.start();
        playbackThread = thread;

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void stopMusic() {
        isPlaying = false;
        if (playbackThread != null) {
            playbackThread.interrupt();
        }
    }

    @Override
    public boolean isMusicPlaying() {
        return isPlaying;
    }
}