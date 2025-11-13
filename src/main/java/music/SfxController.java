package music;

import core.GameState;
import model.GameObject;
import model.characters.GameCharacter;
import patterns.observer.IObserver;

import java.util.*;

public class SfxController implements IObserver {

    // Используем два плеера: один для одноразовых звуков, другой для зацикленных (ходьба)
    private final AsyncMusicPlayer oneShotSfxPlayer = new AsyncMusicPlayer();
    private final AsyncMusicPlayer loopingSfxPlayer = new AsyncMusicPlayer();

    // --- Переменные для отслеживания состояния ---

    // Чтобы звук смены фазы проигрался только один раз
    private boolean isPhaseTwoAnnounced = false;

    // Чтобы отслеживать момент атаки
    private long lastKnownAttackTime = 0;

    // Чтобы отслеживать движение босса
    private double lastKnownBossX = -1;
    private boolean isWalkingSoundPlaying = false;

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
            handleWalkingEvent(boss); // Теперь безопасно
            handleBossDeathEvent(boss);   // Теперь безопасно
        }
        if (hero1 != null) {
            handleWalkingEvent(hero1);
            handleHeroDeathEvent(hero1);
        }
        if (hero2 != null) {
            handleWalkingEvent(hero2);
            handleHeroDeathEvent(hero2);
        }
    }

    private void handleBossDeathEvent(GameCharacter boss) {
        double healthPercent = (double) boss.getHealth() / boss.getMaxHealth();
        if (healthPercent <= 0 && isPhaseTwoAnnounced) {
            oneShotSfxPlayer.playOnce("src/main/resources/music/dead.mp3");
            oneShotSfxPlayer.playOnce("src/main/resources/music/victory.mp3");
        }
    }

    private void handleHeroDeathEvent(GameCharacter hero) {
        double healthPercent = (double) hero.getHealth() / hero.getMaxHealth();
        if (healthPercent <= 0) {
            oneShotSfxPlayer.playOnce("src/main/resources/dead.mp3");
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
        double currentX = character.getX();
        if (lastKnownBossX == -1) {
            lastKnownBossX = currentX;
            return;
        }

        boolean isCurrentlyMoving = (Math.abs(currentX - lastKnownBossX) > 0.1);

        if (isCurrentlyMoving && !isWalkingSoundPlaying) {
            loopingSfxPlayer.playLoop("src/main/resources/music/walk.mp3");
            isWalkingSoundPlaying = true;
        }
        else if (!isCurrentlyMoving && isWalkingSoundPlaying) {
            loopingSfxPlayer.stop();
            isWalkingSoundPlaying = false;
        }

        lastKnownBossX = currentX; // Обновляем позицию для следующего кадра
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

    /**
     * Останавливает все звуковые эффекты.
     */
    public void stopAllSfx() {
        oneShotSfxPlayer.stop();
        loopingSfxPlayer.stop();
        isWalkingSoundPlaying = false;
    }
}