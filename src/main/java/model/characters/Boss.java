package model.characters;

import net.PlayerAction;
import patterns.factory.CharacterFactory;
import ui.Animation;

public class Boss extends GameCharacter {
    public Boss(int id, double x, double y, String name, int damage, double attackRange, long attackCooldown, CharacterFactory.BossType type) {
        super(id, x, y, 150, 200, createBossAnimation(type), name, 1000, damage, attackRange, attackCooldown);
    }

    private static Animation createBossAnimation(CharacterFactory.BossType type) {
        Animation bossAnimation;

        switch (type) {
            case FIRE_MAGE:
            default:
                // 1. Создаем анимацию со скином по умолчанию (состояние покоя)
                bossAnimation = new Animation("skins/boss_skin1.png");

                // 2. Добавляем "кадры" для каждого действия на основе ваших файлов
                bossAnimation.addFrame(PlayerAction.ActionType.MOVE_RIGHT, "skins/boss_skin2_right.png");
                bossAnimation.addFrame(PlayerAction.ActionType.MOVE_LEFT, "skins/boss_skin2_left.png");
                bossAnimation.addFrame(PlayerAction.ActionType.ATTACK, "skins/boss_skin3.png");

                // 3. Когда босс прекращает движение, он должен вернуться в состояние покоя
                bossAnimation.addFrame(PlayerAction.ActionType.STOP_MOVE_RIGHT, "skins/boss_skin1.png");
                bossAnimation.addFrame(PlayerAction.ActionType.STOP_MOVE_LEFT, "skins/boss_skin1.png");

                break;
        }

        return bossAnimation;
    }
}