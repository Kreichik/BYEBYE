import junit.framework.TestCase;
import net.NetworkFacade;
import ui.ads.AdConfig;
import ui.ads.AdManager;
import ui.video.VideoPlayer;

public class AdManagerPauseResumeTest extends TestCase {
    static class StubVideo implements VideoPlayer {
        @Override
        public void play(String path, long fallbackDurationMillis, Runnable onComplete, Runnable onError) {
            onComplete.run();
        }
    }

    static class StubFacade extends NetworkFacade {
        public boolean paused;
        public boolean resumed;
        @Override public void pauseGame() { paused = true; }
        @Override public void resumeGame() { resumed = true; }
    }

    public void testPauseResumeTriggered() {
        StubFacade facade = new StubFacade();
        AdManager manager = new AdManager(facade, new StubVideo(), new AdConfig(1000, 100, ""));
        manager.setInputLockHandlers(() -> {}, () -> {});
        manager.accumulatePlayTime(500_000_000L);
        manager.accumulatePlayTime(600_000_000L);
        assertTrue(facade.paused);
        assertTrue(facade.resumed);
    }
}

