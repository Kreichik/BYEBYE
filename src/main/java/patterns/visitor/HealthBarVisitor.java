package patterns.visitor;

import java.awt.Graphics;
import model.Projectile;
import model.characters.Boss;
import model.characters.Hero;
import ui.ShowHP;

public class HealthBarVisitor implements GameObjectVisitor {
    private final Graphics g;
    private final int screenOffset;

    public HealthBarVisitor(Graphics g, int screenOffset) {
        this.g = g;
        this.screenOffset = screenOffset;
    }

    @Override
    public void visitHero(Hero hero) {
        int healthBarX = (int) hero.getX() + screenOffset;
        int healthBarY = (int) hero.getY() - ShowHP.BAR_HEIGHT - ShowHP.TEXT_OFFSET_Y - 5;
        ShowHP.drawHealthBar(g, hero.getHealth(), hero.getMaxHealth(), healthBarX, healthBarY, hero.getName());
    }

    @Override
    public void visitBoss(Boss boss) {
        int healthBarX = (int) boss.getX() + screenOffset;
        int healthBarY = (int) boss.getY() - ShowHP.BAR_HEIGHT - ShowHP.TEXT_OFFSET_Y - 5;
        ShowHP.drawHealthBar(g, boss.getHealth(), boss.getMaxHealth(), healthBarX, healthBarY, boss.getName());
    }

    @Override
    public void visitProjectile(Projectile projectile) {
    }
}

