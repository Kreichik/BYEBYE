import core.GameState;
import junit.framework.TestCase;
import model.characters.Boss;
import model.characters.GameCharacter;
import patterns.factory.CharacterFactory;

public class BossAttributesTest extends TestCase {
    private GameState state;

    @Override
    protected void setUp() throws Exception {
        state = new GameState();
        CharacterFactory.init(state);
    }

    public void testBossCooldownIsFiveSeconds() {
        GameCharacter boss = CharacterFactory.getFactory().createBoss(CharacterFactory.BossType.FIRE_MAGE, 0);
        assertEquals(5000L, boss.getAttackCooldown());
    }

    public void testBossMovementSpeedReduced() {
        GameCharacter boss = CharacterFactory.getFactory().createBoss(CharacterFactory.BossType.FIRE_MAGE, 0);
        assertEquals(3.0, boss.getMoveSpeed());
    }

    public void testHeroesUnaffectedMovementSpeed() {
        GameCharacter leftHero = CharacterFactory.getFactory().createHero(CharacterFactory.HeroType.WARRIOR_LEFT, 1);
        GameCharacter rightHero = CharacterFactory.getFactory().createHero(CharacterFactory.HeroType.ARCHER_RIGHT, 2);
        assertEquals(5.0, leftHero.getMoveSpeed());
        assertEquals(5.0, rightHero.getMoveSpeed());
    }

    public void testCooldownGateLogicExample() {
        GameCharacter boss = CharacterFactory.getFactory().createBoss(CharacterFactory.BossType.FIRE_MAGE, 0);
        long now = System.currentTimeMillis();
        boss.setLastAttackTime(now);
        boolean canAttackNow = now - boss.getLastAttackTime() > boss.getAttackCooldown();
        assertFalse(canAttackNow);
        boolean canAttackAfterFiveSeconds = (now + 5001) - boss.getLastAttackTime() > boss.getAttackCooldown();
        assertTrue(canAttackAfterFiveSeconds);
    }
}
