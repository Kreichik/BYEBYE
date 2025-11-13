package ui.ads;

import net.NetworkFacade;
import ui.video.VideoPlayer;

public class AdManager {
    private final NetworkFacade networkFacade;
    private final VideoPlayer player;
    private final AdConfig config;
    private long accumulatedPlayNanos = 0;
    private boolean adScheduled = false;
    private boolean adPlaying = false;
    private final PerformanceMetrics metrics = new PerformanceMetrics();
    private Runnable onLockInputs;
    private Runnable onUnlockInputs;

    public AdManager(NetworkFacade networkFacade, VideoPlayer player, AdConfig config) {
        this.networkFacade = networkFacade;
        this.player = player;
        this.config = config;
    }

    public void setInputLockHandlers(Runnable onLockInputs, Runnable onUnlockInputs) {
        this.onLockInputs = onLockInputs;
        this.onUnlockInputs = onUnlockInputs;
    }

    public void accumulatePlayTime(long deltaNanos) {
        if (adPlaying) return;
        accumulatedPlayNanos += deltaNanos;
        if (!adScheduled && accumulatedPlayNanos >= TimeUnitMillis.toNanos(config.getThresholdMillis())) {
            adScheduled = true;
            triggerAd();
        }
    }

    private void triggerAd() {
        adPlaying = true;
        metrics.markAdStart();
        try { if (onLockInputs != null) onLockInputs.run(); } catch (Exception ignored) {}
        try { networkFacade.pauseGame(); } catch (Exception ignored) {}
        player.play(config.getVideoPath(), config.getAdDurationMillis(), this::onAdComplete, this::onAdError);
    }

    private void onAdComplete() {
        metrics.markAdEnd();
        finishAd();
    }

    private void onAdError() {
        metrics.markAdError();
        finishAd();
    }

    private void finishAd() {
        try { networkFacade.resumeGame(); } catch (Exception ignored) {}
        try { if (onUnlockInputs != null) onUnlockInputs.run(); } catch (Exception ignored) {}
        adPlaying = false;
        accumulatedPlayNanos = 0;
    }

    public PerformanceMetrics getMetrics() { return metrics; }

    public static class TimeUnitMillis {
        public static long toNanos(long millis) { return millis * 1_000_000L; }
    }
}

