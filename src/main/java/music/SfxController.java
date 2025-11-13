package music;

import core.GameState;
import model.GameObject;
import model.characters.GameCharacter;
import patterns.observer.IObserver;

import java.util.*;

public class SfxController implements IObserver {

    private final AsyncMusicPlayer oneShotSfxPlayer = new AsyncMusicPlayer();
    private final AsyncMusicPlayer loopingSfxPlayer = new AsyncMusicPlayer();

    // --- Переменные для отслеживания состояния ---
    private boolean isPhaseTwoAnnounced = false;

    private long lastKnownAttackTime = 0;

    private double lastKnownBossX = -1;
    private boolean isWalkingSoundPlaying = false;

    private final Map<Integer, CharacterWalkState> walkingStates = new HashMap<>();

    private final Set<Integer> deathSoundPlayedFor = new HashSet<>();

    @Override
    public void update(Object state) {
        if (!(state instanceof GameState)) {
            return;
        }

        GameState gameState = (GameState) state;
        GameCharacter boss = findBoss(gameState);
        GameCharacter hero1 = findHero1(gameState);
        GameCharacter hero2 = findHero2(gameState);


        if (boss != null) {
            handlePhaseChangeEvent(boss);
            handleBossAttackEvent(boss);
            handleWalkingEvent(boss);
            handleDeathEvent(boss);
        }
        if (hero1 != null) {
            handleWalkingEvent(hero1);
            handleDeathEvent(hero1);
        }
        if (hero2 != null) {
            handleWalkingEvent(hero2);
            handleDeathEvent(hero2);
        }
    }

    private void handleDeathEvent(GameCharacter character) {
        int charId = character.getId();
        if (!character.isActive() && !deathSoundPlayedFor.contains(charId)) {
            if (charId == 0) {
                oneShotSfxPlayer.playOnce("src/main/resources/music/dead.mp3");
                oneShotSfxPlayer.playOnce("src/main/resources/music/victory.mp3");
            } else {
                oneShotSfxPlayer.playOnce("src/main/resources/music/dead.mp3");
            }
            deathSoundPlayedFor.add(charId);
        }
    }
    /**
     * Проверяет, перешел ли босс во вторую фазу, и проигрывает звук.
     */
    private void handlePhaseChangeEvent(GameCharacter boss) {
        double healthPercent = (double) boss.getHealth() / boss.getMaxHealth();
        if (healthPercent <= 0.5 && !isPhaseTwoAnnounced) {
            oneShotSfxPlayer.playOnce("src/main/resources/music/boss_2_phase_opening.mp3");
            isPhaseTwoAnnounced = true;
        }
    }

    /**
     * Проверяет, атаковал ли босс с момента последнего кадра.
     */
    private void handleBossAttackEvent(GameCharacter boss) {
        long currentAttackTime = boss.getLastAttackTime();
        if (currentAttackTime > this.lastKnownAttackTime) {
            oneShotSfxPlayer.playOnce("src/main/resources/music/fire_attack.mp3");
            this.lastKnownAttackTime = currentAttackTime;
        }
    }

    /**
     * Проверяет, двигается ли босс, и включает/выключает звук ходьбы.
     */
    private void handleWalkingEvent(GameCharacter character) {
        int charId = character.getId();
        double currentX = character.getX();

        CharacterWalkState walkState = walkingStates.computeIfAbsent(charId, id -> new CharacterWalkState(currentX));

        boolean isCurrentlyMoving = (Math.abs(currentX - walkState.lastX) > 0.1);

        if (isCurrentlyMoving && !walkState.isSoundPlaying) {
            // Важно: пока у нас только один плеер для ходьбы.
            // Если нужно, чтобы несколько персонажей одновременно издавали звук,
            // потребуется более сложная система. Пока звук будет только один.
            if (!loopingSfxPlayer.isPlaying()) {
                loopingSfxPlayer.playLoop("src/main/resources/music/walk.mp3");
                walkState.isSoundPlaying = true;
            }
        } else if (!isCurrentlyMoving && walkState.isSoundPlaying) {
            loopingSfxPlayer.stop();
            walkState.isSoundPlaying = false;
        }

        walkState.lastX = currentX;
    }

    private GameCharacter findBoss(GameState gameState) {
        return gameState.getCharacterById(0);
    }
    private GameCharacter findHero1(GameState gameState) {
        return gameState.getCharacterById(1);
    }
    private GameCharacter findHero2(GameState gameState) {
        return gameState.getCharacterById(2);
    }



    private static class CharacterWalkState {
        double lastX;
        boolean isSoundPlaying;

        CharacterWalkState(double startX) {
            this.lastX = startX;
            this.isSoundPlaying = false;
        }
    }
}