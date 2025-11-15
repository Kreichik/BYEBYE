package patterns.factory;

import core.GameState;
import model.characters.Boss;
import model.characters.GameCharacter;
import model.characters.Hero;
import model.characters.NPC;
import patterns.strategy.CircularAttack;
import patterns.strategy.MeleeAttackStrategy;
import patterns.strategy.RangedAttack;
import java.util.concurrent.atomic.AtomicInteger;

import static core.Main.SCREEN_WIDTH;
import static core.Main.SCREEN_HEIGHT;
import static core.Main.WORLD_WIDTH;

public class CharacterFactory {
    private static CharacterFactory instance;
    private final GameState gameState;
    private final AtomicInteger npcIdCounter = new AtomicInteger(100);

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

    public enum HeroType { WARRIOR_LEFT, ARCHER_RIGHT }
    public enum BossType { FIRE_MAGE }

    public GameCharacter createHero(HeroType type, int id) {
        Hero hero = null;
        switch (type) {
            case WARRIOR_LEFT:
                hero = new Hero(id, WORLD_WIDTH / 2.0, SCREEN_HEIGHT / 2.0, "Warrior", 30, 800, 1000, type);
                hero.setAttackStrategy(new MeleeAttackStrategy());
                break;
            case ARCHER_RIGHT:
                hero = new Hero(id, WORLD_WIDTH / 2.0, SCREEN_HEIGHT / 2.0, "Archer", 20, 1200, 700, type);
                hero.setAttackStrategy(new RangedAttack());
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
                boss = new Boss(id, WORLD_WIDTH / 2.0, SCREEN_HEIGHT / 2.0, "Fire Mage", 25, 2000, 5000, type);
                boss.setAttackStrategy(new CircularAttack(12, 5));
                break;
        }
        if (boss != null) {
            gameState.addGameObject(boss);
        }
        return boss;
    }

    public GameCharacter createNpc(double x, double y) {
        NPC npc = new NPC(npcIdCounter.getAndIncrement(), x, y);
        gameState.addGameObject(npc);
        return npc;
    }
}
