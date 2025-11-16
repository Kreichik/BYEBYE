package patterns.decorator;

import core.GameState;
import model.characters.BossBehavior;

public abstract class BossDecorator implements BossBehavior {
    protected final BossBehavior boss;

    public BossDecorator(BossBehavior boss) {
        this.boss = boss;
    }

    @Override
    public void takeDamage(int amount) {
        boss.takeDamage(amount);
    }

    @Override
    public void performAttack(GameState gameState) {
        boss.performAttack(gameState);
    }

    @Override
    public void tick() {
        boss.tick();
    }

    @Override
    public boolean isActive() {
        return boss.isActive();
    }

    @Override
    public int getHealth() {
        return boss.getHealth();
    }

    @Override
    public int getMaxHealth() {
        return boss.getMaxHealth();
    }

    @Override
    public double getMoveSpeed() {
        return boss.getMoveSpeed();
    }
}
