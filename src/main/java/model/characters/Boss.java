package model.characters;

public class Boss extends GameCharacter {
    public Boss(int id, double x, double y, String skinPath, String name, int damage, double attackRange, long attackCooldown) {
        super(id, x, y, 150, 200, skinPath, name, 1000, damage, attackRange, attackCooldown);
    }
}