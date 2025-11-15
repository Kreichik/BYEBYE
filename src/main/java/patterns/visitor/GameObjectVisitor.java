package patterns.visitor;

import model.InteractionPoint;
import model.Projectile;
import model.characters.Boss;
import model.characters.Hero;
import model.characters.NPC;

public interface GameObjectVisitor {
    void visitHero(Hero hero);
    void visitBoss(Boss boss);
    void visitProjectile(Projectile projectile);
    void visitNpc(NPC npc);
    void visitInteractionPoint(InteractionPoint point);
}