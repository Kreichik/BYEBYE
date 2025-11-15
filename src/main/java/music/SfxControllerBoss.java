package music;

import core.GameState;
import model.characters.GameCharacter;
import patterns.observer.IObserver;

import java.util.*;

public class SfxControllerBoss extends SfxController implements IObserver {
    private boolean isPhaseTwoAnnounced = false;

    private long lastKnownAttackTime = 0;

    @Override
    public void update(Object state) {
        if (!(state instanceof GameState)) {
            return;
        }

        GameState gameState = (GameState) state;
        GameCharacter boss = gameState.getCharacterById(0);


        if (boss != null) {
            handlePhaseChangeEvent(boss);
            handleBossAttackEvent(boss);
            handleWalkingEvent(boss);
            handleDeathEvent(boss);
        }

    }

    private void handlePhaseChangeEvent(GameCharacter boss) {
        double healthPercent = (double) boss.getHealth() / boss.getMaxHealth();
        if (healthPercent <= 0.5 && !isPhaseTwoAnnounced) {
            oneShotSfxPlayer.playOnce("src/main/resources/music/boss_2_phase_opening.mp3");
            isPhaseTwoAnnounced = true;
        }
    }

    private void handleBossAttackEvent(GameCharacter boss) {
        long currentAttackTime = boss.getLastAttackTime();
        if (currentAttackTime > this.lastKnownAttackTime) {
            oneShotSfxPlayer.playOnce("src/main/resources/music/fire_attack.mp3");
            this.lastKnownAttackTime = currentAttackTime;
        }
    }
}