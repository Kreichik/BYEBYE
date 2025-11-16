package patterns.visitor;

import model.Projectile;
import model.characters.Boss;
import model.characters.Hero;
import model.characters.NPC;
import patterns.decorator.BossPhase2Decorator;

public interface GameObjectVisitor {
    void visitHero(Hero hero);
    void visitBoss(Boss boss);
    void visitProjectile(Projectile projectile);
    void visitNpc(NPC npc);
}