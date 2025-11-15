package patterns.visitor;

import core.GameState;
import model.InteractionPoint;
import model.Projectile;
import model.characters.Boss;
import model.characters.Hero;
import model.characters.NPC;
import patterns.factory.CharacterFactory;

public class CollisionWithProjectileVisitor implements GameObjectVisitor {
    private final Projectile projectile;
    private final GameState gameState;

    public CollisionWithProjectileVisitor(Projectile projectile, GameState gameState) {
        this.projectile = projectile;
        this.gameState = gameState;
    }

    @Override
    public void visitHero(Hero hero) {
        if (!projectile.isActive() || !hero.isActive()) return;
        if (projectile.getFactionId() == hero.getFactionId()) return;

        if (projectile.getBounds().intersects(hero.getBounds())) {
            boolean wasAlive = hero.isActive();
            hero.takeDamage(projectile.getDamage());
            projectile.setActive(false);

            if (wasAlive && !hero.isActive()) {
                CharacterFactory.getFactory().createRevivePoint(hero);
            }
        }
    }

    @Override
    public void visitBoss(Boss boss) {
        if (!projectile.isActive() || !boss.isActive()) return;
        if (projectile.getFactionId() == boss.getFactionId()) return;

        if (projectile.getBounds().intersects(boss.getBounds())) {
            boss.takeDamage(projectile.getDamage());
            projectile.setActive(false);
        }
    }

    @Override
    public void visitNpc(NPC npc) {
        if (!projectile.isActive() || !npc.isActive()) return;
        if (projectile.getFactionId() == npc.getFactionId()) return;

        if (projectile.getBounds().intersects(npc.getBounds())) {
            npc.takeDamage(projectile.getDamage());
            projectile.setActive(false);
        }
    }

    @Override
    public void visitProjectile(Projectile otherProjectile) {
    }

    @Override
    public void visitInteractionPoint(InteractionPoint point) {
    }
}