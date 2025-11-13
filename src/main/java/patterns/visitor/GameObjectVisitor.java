package patterns.visitor;

import model.Projectile;
import model.characters.Boss;
import model.characters.Hero;

public interface GameObjectVisitor {
    void visitHero(Hero hero);
    void visitBoss(Boss boss);
    void visitProjectile(Projectile projectile);
}

