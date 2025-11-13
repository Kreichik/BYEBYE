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

    public MusicController(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    @Override
    public void update(Object state) {
        // Не делаем ничего, если игра закончилась или состояние некорректно
        if (gameEnded || !(state instanceof GameState)) {
            return;
        }

        GameState gameState = (GameState) state;
        GameCharacter boss = findBoss(gameState);

        // Определяем, какой трек должен играть сейчас
        String newTrack = determineBackgroundTrack(boss, gameState);

        // Если трек должен смениться, меняем его
        if (newTrack != null && !newTrack.equals(currentTrack)) {
            switchTrack(newTrack);
            currentTrack = newTrack;
        }
    }

    /**
     * Находит объект босса в состоянии игры.
     */
    private GameCharacter findBoss(GameState gameState) {
        // Предполагаем, что ID босса всегда 0
        return gameState.getCharacterById(0);
    }

    private String determineBackgroundTrack(GameCharacter boss, GameState gameState) {
        if (boss == null || !boss.isActive()) {
            gameEnded = true;
            return "first_bg";
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
            return "first_bg";
        }

        double healthPercent = (double) boss.getHealth() / boss.getMaxHealth();

        if (healthPercent <= 0.99) {
            return "terraria_boss_1";
        }else{
            return "first_bg";
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