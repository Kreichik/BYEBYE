package model.characters;

import core.GameState;
import model.GameObject;
import patterns.strategy.IAttackStrategy;

public abstract class GameCharacter extends GameObject {
    protected int health;
    protected String name;
    protected IAttackStrategy attackStrategy;

    public GameCharacter(int id, double x, double y, int width, int height, String skinPath, String name, int health) {
        super(id, x, y, width, height, skinPath);
        this.name = name;
        this.health = health;
    }

    public void performAttack(GameState gameState) {
        if (attackStrategy != null) {
            attackStrategy.execute(this, gameState);
        }
    }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
    public String getName() { return name; }
    public void setAttackStrategy(IAttackStrategy attackStrategy) { this.attackStrategy = attackStrategy; }
}