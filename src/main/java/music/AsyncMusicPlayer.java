package music;

import javazoom.jl.player.Player;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class AsyncMusicPlayer {

    private volatile boolean isPlaying = false;
    private volatile Thread playbackThread = null;

    public CompletableFuture<Void> playOnce(String filePath) {
        stop();
        isPlaying = true;

        CompletableFuture<Void> future = new CompletableFuture<>();

        playbackThread = new Thread(() -> {
            try (FileInputStream fis = new FileInputStream(filePath)) {
                Player player = new Player(fis);
                player.play();
                if (!future.isDone()) future.complete(null);
            } catch (IOException | javazoom.jl.decoder.JavaLayerException e) {
                future.completeExceptionally(e);
            } finally {
                isPlaying = false;
            }
        }, "MusicPlayer-Thread");

        playbackThread.setDaemon(true);
        playbackThread.start();

        return future;
    }

    public CompletableFuture<Void> playLoop(String filePath) {
        stop();
        isPlaying = true;

        Thread loopThread = new Thread(() -> {
            while (isPlaying) {
                try (FileInputStream fis = new FileInputStream(filePath)) {
                    Player player = new Player(fis);
                    player.play();
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }, "MusicPlayer-Loop");

        loopThread.setDaemon(true);
        loopThread.start();
        playbackThread = loopThread;

        return CompletableFuture.runAsync(() -> {}, CompletableFuture.delayedExecutor(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS));
    }

    public void stop() {
        isPlaying = false;
        if (playbackThread != null && playbackThread.isAlive()) {
            playbackThread.interrupt();
            playbackThread = null;
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}