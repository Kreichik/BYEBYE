package model.characters;

import net.PlayerAction;
import patterns.visitor.GameObjectVisitor;
import ui.Animation;

public class NPC extends GameCharacter {
    public static final int FACTION_ID = 0;

    public NPC(int id, double x, double y) {
        super(id, x, y, 70, 70, createNpcAnimation(), "tower", 150, 10, 1000, 2000, FACTION_ID);
        this.setAttackStrategy(new patterns.strategy.CircularAttack(8, 4));
    }

    private static Animation createNpcAnimation() {
        return new Animation("skins/tower.png");
    }

    @Override
    public double getMoveSpeed() {
        return 0;
    }

    @Override
    public void accept(GameObjectVisitor visitor) {
        visitor.visitNpc(this);
    }
}