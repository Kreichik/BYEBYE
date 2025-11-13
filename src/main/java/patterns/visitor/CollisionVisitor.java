package patterns.visitor;

import core.GameState;
import model.Projectile;
import model.characters.Boss;
import model.characters.Hero;
import model.GameObject;

public class CollisionVisitor implements GameObjectVisitor {
    private final GameObject other;
    private final GameState gameState;

    public CollisionVisitor(GameObject other, GameState gameState) {
        this.other = other;
        this.gameState = gameState;
    }

    @Override
    public void visitHero(Hero hero) {
    }

    @Override
    public void visitBoss(Boss boss) {
    }

    @Override
    public void visitProjectile(Projectile projectile) {
        other.accept(new CollisionWithProjectileVisitor(projectile, gameState));
    }
}

