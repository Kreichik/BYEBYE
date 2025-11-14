package music;

import core.GameState;
import patterns.observer.IObserver;


public class SfxControllerMenu{

    private static SfxControllerMenu instance;
    private final AsyncMusicPlayer oneShotSfxPlayer = new AsyncMusicPlayer();

    private SfxControllerMenu() {}

    public static SfxControllerMenu getInstance() {
        if (instance == null) {
            instance = new SfxControllerMenu();
        }
        return instance;
    }

    public void playMenuOpen(){
        oneShotSfxPlayer.playOnce("/sfx/menu_open.mp3");
    }

    public void playMenuClick(){
        oneShotSfxPlayer.playOnce("/sfx/menu_click.mp3");
    }

    public void playStrategySelect() {
        oneShotSfxPlayer.playOnce("/sfx/strategy_select.mp3");
    }
}
