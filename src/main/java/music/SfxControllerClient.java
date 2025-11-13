package music;

import core.GameState;
import model.GameObject;
import model.characters.GameCharacter;
import patterns.observer.IObserver;

import java.util.*;

public class SfxControllerClient extends SfxController implements IObserver {

    private long lastKnownAttackTime = 0;

    @Override
    public void update(Object state) {
        if (!(state instanceof GameState)) {
            return;
        }

        GameState gameState = (GameState) state;
        GameCharacter hero1 = gameState.getCharacterById(1);
        GameCharacter hero2 = gameState.getCharacterById(2);


        if (hero1 != null) {
            handleWalkingEvent(hero1);
            handleDeathEvent(hero1);
            handleWalkingEvent(hero1);
        }
        if (hero2 != null) {
            handleHeroAttackEvent(hero2);
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

    private void handleHeroAttackEvent(GameCharacter hero) {
        long currentAttackTime = hero.getLastAttackTime();
        if (currentAttackTime > this.lastKnownAttackTime) {
            oneShotSfxPlayer.playOnce("src/main/resources/music/fire_attack.mp3");
            this.lastKnownAttackTime = currentAttackTime;
        }
    }
}