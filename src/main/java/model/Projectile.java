package model;

import ui.Animation;
import patterns.visitor.GameObjectVisitor;

import static core.Main.SCREEN_HEIGHT;
import static core.Main.WORLD_WIDTH;

public class Projectile extends GameObject {
    private final int ownerId;
    private final int damage;

    public Projectile(int id, double x, double y, double velX, double velY, String skinPath, int ownerId, int damage) {
        super(id, x, y, 30, 30, new Animation(skinPath));
        this.velX = velX;
        this.velY = velY;
        this.ownerId = ownerId;
        this.damage = damage;
    }

    @Override
    public void tick() {
        super.tick();
        if (x < -width || x > WORLD_WIDTH || y < -height || y > SCREEN_HEIGHT) {
            active = false;
        }
    }

    public int getOwnerId() { return ownerId; }
    public int getDamage() { return damage; }

    @Override
    public void accept(GameObjectVisitor visitor) {
        visitor.visitProjectile(this);
    }
}
