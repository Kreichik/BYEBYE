package patterns.visitor;

import core.GameState;
import model.Projectile;
import model.characters.Boss;
import model.characters.Hero;

public class CollisionWithProjectileVisitor implements GameObjectVisitor {
    private final Projectile projectile;
    private final GameState gameState;

    public CollisionWithProjectileVisitor(Projectile projectile, GameState gameState) {
        this.projectile = projectile;
        this.gameState = gameState;
    }

    @Override
    public void visitHero(Hero hero) {
        if (!projectile.isActive()) return;
        if (!hero.isActive()) return;
        if (projectile.getOwnerId() == hero.getId()) return;
        if (projectile.getBounds().intersects(hero.getBounds())) {
            hero.takeDamage(projectile.getDamage());
            projectile.setActive(false);
            System.out.printf("%s damaged by projectile from %d. Current HP: %d%n", hero.getName(), projectile.getOwnerId(), hero.getHealth());
        }
    }

    @Override
    public void visitBoss(Boss boss) {
        if (!projectile.isActive()) return;
        if (!boss.isActive()) return;
        if (projectile.getOwnerId() == boss.getId()) return;
        if (projectile.getBounds().intersects(boss.getBounds())) {
            boss.takeDamage(projectile.getDamage());
            projectile.setActive(false);
            System.out.printf("%s damaged by projectile from %d. Current HP: %d%n", boss.getName(), projectile.getOwnerId(), boss.getHealth());
        }
    }

    @Override
    public void visitProjectile(Projectile otherProjectile) {
    }
}

