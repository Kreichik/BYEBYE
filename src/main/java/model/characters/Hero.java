package model.characters;

public class Hero extends GameCharacter {
    public Hero(int id, double x, double y, String skinPath, String name, int damage, double attackRange, long attackCooldown) {
        super(id, x, y, 70, 80, skinPath, name, 100, damage, attackRange, attackCooldown);
    }
}