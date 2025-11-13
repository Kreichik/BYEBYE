package model.characters;

import net.PlayerAction;
import patterns.factory.CharacterFactory;
import ui.Animation;
import patterns.visitor.GameObjectVisitor;

public class Boss extends GameCharacter {
    public Boss(int id, double x, double y, String name, int damage, double attackRange, long attackCooldown, CharacterFactory.BossType type) {
        super(id, x, y, 150, 200, createBossAnimation(type), name, 1000, damage, attackRange, attackCooldown);
    }

    private static Animation createBossAnimation(CharacterFactory.BossType type) {
        Animation bossAnimation;

        switch (type) {
            case FIRE_MAGE:
            default:
                bossAnimation = new Animation("skins/boss_skin1.png");
                bossAnimation.addFrame(PlayerAction.ActionType.MOVE_RIGHT, "skins/boss_skin2_right.png");
                bossAnimation.addFrame(PlayerAction.ActionType.MOVE_LEFT, "skins/boss_skin2_left.png");
                bossAnimation.addFrame(PlayerAction.ActionType.ATTACK, "skins/boss_skin3.png");
                bossAnimation.addFrame(PlayerAction.ActionType.STOP_MOVE_RIGHT, "skins/boss_skin1.png");
                bossAnimation.addFrame(PlayerAction.ActionType.STOP_MOVE_LEFT, "skins/boss_skin1.png");
                break;
        }

        return bossAnimation;
    }

    @Override
    public void accept(GameObjectVisitor visitor) {
        visitor.visitBoss(this);
    }

    @Override
    public double getMoveSpeed() {
        return 3.0;
    }
}
