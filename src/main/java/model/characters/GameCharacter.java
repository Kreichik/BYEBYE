package model.characters;

import core.GameState;
import model.GameObject;
import patterns.strategy.IAttackStrategy;
import ui.Animation;

public abstract class GameCharacter extends GameObject {
    protected int health;
    protected final int MAX_HEALTH;
    protected String name;
    protected IAttackStrategy attackStrategy;
    protected final int factionId;

    protected int damage;
    protected double attackRange;
    protected long attackCooldown;
    protected long lastAttackTime;
    protected double moveSpeed = 5;

    public GameCharacter(int id, double x, double y, int width, int height, Animation animation, String name, int health, int damage, double attackRange, long attackCooldown, int factionId) {
        super(id, x, y, width, height, animation);
        this.name = name;
        this.health = health;
        this.MAX_HEALTH = health;
        this.damage = damage;
        this.attackRange = attackRange;
        this.attackCooldown = attackCooldown;
        this.lastAttackTime = 0;
        this.factionId = factionId;
    }

    public void takeDamage(int amount) {
        this.health -= amount;
        if (this.health <= 0) {
            this.active = false;
            System.out.println(name + " has been defeated!");
        }
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
    public int getDamage() { return damage; }
    public double getAttackRange() { return attackRange; }
    public long getAttackCooldown() { return attackCooldown; }
    public long getLastAttackTime() { return lastAttackTime; }
    public void setLastAttackTime(long time) { this.lastAttackTime = time; }
    public int getMaxHealth() { return MAX_HEALTH; }
    public double getMoveSpeed() { return moveSpeed; }
    public int getFactionId() { return factionId; }
    public IAttackStrategy getAttackStrategy(){
        return attackStrategy;
    }
}