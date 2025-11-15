package model.characters;

import net.PlayerAction.ActionType;
import patterns.factory.CharacterFactory;
import ui.Animation;
import patterns.visitor.GameObjectVisitor;

public class Hero extends GameCharacter {
    public Hero(int id, double x, double y, String name, int damage, double attackRange, long attackCooldown, CharacterFactory.HeroType type) {
        super(id, x, y, 70, 80, createHeroAnimation(type), name, 250, damage, attackRange, attackCooldown, id);
    }

    public void revive(double spawnX, double spawnY) {
        this.setActive(true);
        this.setHealth(this.getMaxHealth() / 2);
        this.setVelX(0);
        this.setVelY(0);
        this.setX(spawnX + this.getWidth());
        this.setY(spawnY);
        System.out.printf("[DEBUG] Hero %s revived at coordinates X: %.2f, Y: %.2f%n", this.getName(), this.getX(), this.getY());
    }

    private static Animation createHeroAnimation(CharacterFactory.HeroType type) {
        Animation heroAnimation;
        switch (type) {
            case WARRIOR_LEFT:
                heroAnimation = new Animation("skins/knight_stay.png");
                heroAnimation.addFrame(ActionType.MOVE_RIGHT, "skins/warrior_right.png");
                heroAnimation.addFrame(ActionType.MOVE_LEFT, "skins/warrior_left.png");
                heroAnimation.addFrame(ActionType.ATTACK, "skins/warrior_attacking_right.png");
                heroAnimation.addFrame(ActionType.MOVE_UP, "skins/warrior_back.png");
                heroAnimation.addFrame(ActionType.MOVE_DOWN, "skins/knight_stay.png");
                break;
            case ARCHER_RIGHT:
            default:
                heroAnimation = new Animation("skins/archer_stay.png");
                heroAnimation.addFrame(ActionType.MOVE_RIGHT, "skins/archer_moving_right.png");
                heroAnimation.addFrame(ActionType.MOVE_LEFT, "skins/archer_moving_left.png");
                heroAnimation.addFrame(ActionType.ATTACK, "skins/archer_attacking_left.png");
                heroAnimation.addFrame(ActionType.MOVE_UP, "skins/archer_moving_back.png");
                heroAnimation.addFrame(ActionType.MOVE_DOWN, "skins/archer_stay.png");
                break;
        }
        return heroAnimation;
    }

    @Override
    public void accept(GameObjectVisitor visitor) {
        visitor.visitHero(this);
    }
}