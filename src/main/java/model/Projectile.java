package model;

import static core.Main.WORLD_WIDTH;

public class Projectile extends GameObject {
    public Projectile(int id, double x, double y, double velX, String skinPath) {
        super(id, x, y, 30, 30, skinPath);
        this.velX = velX;
    }

    @Override
    public void tick() {
        super.tick();
        if (x < 0 || x > WORLD_WIDTH) {
            active = false;
        }
    }
}