package model.characters;


import net.PlayerAction.*;
import patterns.factory.CharacterFactory;
import ui.Animation;

public class Hero extends GameCharacter {
    public Hero(int id, double x, double y, String name, int damage, double attackRange, long attackCooldown, CharacterFactory.HeroType type) {
        super(id, x, y, 70, 80, createHeroAnimation(type), name, 100, damage, attackRange, attackCooldown);
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


}