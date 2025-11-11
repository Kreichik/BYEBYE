package music;

import core.GameState;
import model.GameObject;
import model.characters.GameCharacter;
import music.sound.AudioManager;
import patterns.observer.IObserver;

public class MusicController implements IObserver {
    private final AudioManager audioManager;
    private String currentTrack = null;
    private boolean gameEnded = false;
    AsyncMusicPlayer music = new AsyncMusicPlayer();

    public MusicController(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    @Override
    public void update(Object state) {
        if (gameEnded || !(state instanceof GameState)) return;

        GameState gameState = (GameState) state;
        GameCharacter boss = findBoss(gameState);

        String newTrack = determineTrack(boss, gameState);
        if (newTrack != null && !newTrack.equals(currentTrack)) {
            switchTrack(newTrack);
            currentTrack = newTrack;
        }
    }

    private GameCharacter findBoss(GameState gameState) {
        return gameState.getCharacterById(0);
    }

    private String determineTrack(GameCharacter boss, GameState gameState) {
        if (boss == null || !boss.isActive()) {
            gameEnded = true;
            return "victory";
        }

        boolean anyHeroAlive = false;
        for (GameObject obj : gameState.getGameObjects()) {
            if (obj instanceof GameCharacter && obj.getId() != 0 && obj.isActive()) {
                anyHeroAlive = true;
                break;
            }
        }

        if (!anyHeroAlive) {
            gameEnded = true;
            return "boss_victory";
        }

        double healthPercent = (double) boss.getHealth() / boss.getMaxHealth();
        if (healthPercent <= 0.5) {
            music.playOnce("src/main/resources/music/boss_2_phase_opening.mp3");
            return "terraria_boss_1.mp3";
        } else {
            return "terraria_boss_1.mp3";
        }
    }

    private void switchTrack(String trackName) {
        audioManager.stopMusic();
        String path = "/music/" + trackName + ".mp3";
        audioManager.playMusic(path, true);
    }

    public void stop() {
        audioManager.stopMusic();
        gameEnded = true;
    }
}