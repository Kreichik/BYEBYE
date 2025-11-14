package music;

import core.GameState;
import model.GameObject;
import model.characters.GameCharacter;
import patterns.observer.IObserver;

import java.util.*;

public class SfxControllerClient extends SfxController implements IObserver {

    private long lastKnownAttackTime = 0;
    private final Map<Integer, Integer> heroHealthMap = new HashMap<>();

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
            handleHeroTakeDamageEvent(hero1);
        }
        if (hero2 != null) {
            handleHeroAttackEvent(hero2);
            handleWalkingEvent(hero2);
            handleDeathEvent(hero2);
            handleHeroTakeDamageEvent(hero2);
        }
    }

    private void handleHeroTakeDamageEvent(GameCharacter hero) {
        int heroId = hero.getId();
        int currentHealth = hero.getHealth();
        int lastKnownHealth = heroHealthMap.getOrDefault(heroId, hero.getMaxHealth());
        if (hero.isActive() && currentHealth < lastKnownHealth ) {
            oneShotSfxPlayer.playOnce("src/main/resources/music/hero_take_damage.mp3");
        }
        heroHealthMap.put(heroId, currentHealth);
    }

    private void handleHeroAttackEvent(GameCharacter hero) {
        long currentAttackTime = hero.getLastAttackTime();
        if (currentAttackTime > this.lastKnownAttackTime) {
            oneShotSfxPlayer.playOnce("src/main/resources/music/fire_attack.mp3");
            this.lastKnownAttackTime = currentAttackTime;
        }
    }
}