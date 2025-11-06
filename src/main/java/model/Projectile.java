package model;

import static core.Main.WORLD_WIDTH;

public class Projectile extends GameObject {
    private final int ownerId;
    private final int damage;

    public Projectile(int id, double x, double y, double velX, String skinPath, int ownerId, int damage) {
        super(id, x, y, 30, 30, skinPath);
        this.velX = velX;
        this.ownerId = ownerId;
        this.damage = damage;
    }

    @Override
    public void tick() {
        super.tick();
        if (x < -width || x > WORLD_WIDTH) {
            active = false;
        }
    }

    public int getOwnerId() { return ownerId; }
    public int getDamage() { return damage; }
}