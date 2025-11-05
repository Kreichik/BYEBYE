package patterns.factory;

import core.GameState;
import model.characters.Boss;
import model.characters.GameCharacter;
import model.characters.Hero;
import patterns.strategy.RangedAttack;
import patterns.strategy.WaveAttack;

import static core.Main.SCREEN_WIDTH;

public class CharacterFactory {
    private static CharacterFactory instance;
    private final GameState gameState;

    private CharacterFactory(GameState gameState) {
        this.gameState = gameState;
    }

    public static void init(GameState state) {
        if (instance == null) {
            instance = new CharacterFactory(state);
        }
    }

    public static CharacterFactory getFactory() {
        if (instance == null) {
            throw new IllegalStateException("CharacterFactory not initialized");
        }
        return instance;
    }

    public enum HeroType { WARRIOR_LEFT, ARCHER_RIGHT, MAGE_LEFT }
    public enum BossType { FIRE_MAGE, ICE_MAGE, DRAGON }

    public GameCharacter createHero(HeroType type, int id) {
        Hero hero = null;
        switch (type) {
            case WARRIOR_LEFT:
                hero = new Hero(id, 200, 450, "skins/knight_stay.png", "Warrior");
                break;
            case ARCHER_RIGHT:
                hero = new Hero(id, SCREEN_WIDTH * 2 + 200, 450, "skins/archer_stay.png", "Archer");
                break;
        }
        if (hero != null) {
            gameState.addGameObject(hero);
        }
        return hero;
    }

    public GameCharacter createBoss(BossType type, int id) {
        Boss boss = null;
        switch (type) {
            case FIRE_MAGE:
                boss = new Boss(id, SCREEN_WIDTH + (SCREEN_WIDTH / 2.0) - 75, 350, "skins/boss_skin1.png", "Fire Mage");
                boss.setAttackStrategy(new WaveAttack());
                break;
        }
        if (boss != null) {
            gameState.addGameObject(boss);
        }
        return boss;
    }
}