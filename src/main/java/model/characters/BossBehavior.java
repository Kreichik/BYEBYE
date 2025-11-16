package model.characters;

import core.GameState;

public interface BossBehavior {
    void takeDamage(int amount);
    void performAttack(GameState gameState);
    void tick();
    boolean isActive();
    int getHealth();
    int getMaxHealth();
    double getMoveSpeed();
}
