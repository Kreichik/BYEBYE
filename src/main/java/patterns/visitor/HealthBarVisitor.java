package patterns.visitor;

import java.awt.Graphics;
import model.Projectile;
import model.characters.Boss;
import model.characters.Hero;
import model.characters.NPC;
import ui.ShowHP;

public class HealthBarVisitor implements GameObjectVisitor {
    private final Graphics g;
    private final int offsetX;
    private final int offsetY;

    public HealthBarVisitor(Graphics g, int offsetX, int offsetY) {
        this.g = g;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    @Override
    public void visitHero(Hero hero) {
        int healthBarX = (int) hero.getX() + offsetX;
        int healthBarY = (int) hero.getY() + offsetY - ShowHP.BAR_HEIGHT - ShowHP.TEXT_OFFSET_Y - 5;
        ShowHP.drawHealthBar(g, hero.getHealth(), hero.getMaxHealth(), healthBarX, healthBarY, hero.getName());
    }

    @Override
    public void visitBoss(Boss boss) {
        int healthBarX = (int) boss.getX() + offsetX;
        int healthBarY = (int) boss.getY() + offsetY - ShowHP.BAR_HEIGHT - ShowHP.TEXT_OFFSET_Y - 5;
        ShowHP.drawHealthBar(g, boss.getHealth(), boss.getMaxHealth(), healthBarX, healthBarY, boss.getName());
    }

    @Override
    public void visitNpc(NPC npc) {
        int healthBarX = (int) npc.getX() + offsetX;
        int healthBarY = (int) npc.getY() + offsetY - ShowHP.BAR_HEIGHT - ShowHP.TEXT_OFFSET_Y - 5;
        ShowHP.drawHealthBar(g, npc.getHealth(), npc.getMaxHealth(), healthBarX, healthBarY, npc.getName());
    }

    @Override
    public void visitProjectile(Projectile projectile) {
    }
}
