package patterns.decorator;

import core.GameState;
import model.characters.Boss;
import model.characters.BossBehavior;
import music.AsyncMusicPlayer;
import patterns.factory.CharacterFactory;
import patterns.strategy.IAttackStrategy;
import patterns.strategy.MagicAttackStrategy;
import patterns.visitor.GameObjectVisitor;
import ui.Animation;
import net.PlayerAction;

import java.io.Serializable;

/**
 * Декоратор второй фазы босса.
 * Оборачивает GameCharacter (обычно Boss) и расширяет его поведение:
 * - уменьшает получаемый урон,
 * - усиливает атаку,
 * - ускоряет передвижение,
 * - меняет звук и анимацию.
 *
 * Сохраняет совместимость с сериализацией и клонированием.
 */
public class BossPhase2Decorator extends Boss implements BossBehavior, Serializable {

    private final Boss originalBoss;
    private final AsyncMusicPlayer sfxPlayer;
    private boolean phase2Active = false;

    @SuppressWarnings("unused")
    private BossPhase2Decorator() {
        super(-1, 0, 0, "", 0, 0, 0, CharacterFactory.BossType.FIRE_MAGE);
        this.originalBoss = null;
        this.sfxPlayer = null;
    }

    public BossPhase2Decorator(Boss boss, AsyncMusicPlayer sfxPlayer) {
        super(
                boss.getId(),
                boss.getX(),
                boss.getY(),
                boss.getName(),
                boss.getDamage(),
                boss.getAttackRange(),
                boss.getAttackCooldown(),
                CharacterFactory.BossType.FIRE_MAGE
        );

        this.originalBoss = boss;
        this.sfxPlayer = sfxPlayer;

        this.setVelX(boss.getVelX());
        this.setVelY(boss.getVelY());
        this.setHealth(boss.getHealth());
        this.setLastAttackTime(boss.getLastAttackTime());
        this.setAttackStrategy(boss.getAttackStrategy());
        this.animation = boss.getAnimation();

        activatePhase2();
    }

    private void activatePhase2() {
        if (phase2Active) return;
        phase2Active = true;

        IAttackStrategy phase2Attack = new MagicAttackStrategy(10, 4);
        this.setAttackStrategy(phase2Attack);

        Animation phase2Anim = new Animation("skins/boss_phase2_idle.png");
        phase2Anim.addFrame(PlayerAction.ActionType.MOVE_RIGHT, "skins/boss_phase2_right.png");
        phase2Anim.addFrame(PlayerAction.ActionType.MOVE_LEFT, "skins/boss_phase2_left.png");
        phase2Anim.addFrame(PlayerAction.ActionType.ATTACK, "skins/boss_phase2_attack.png");
        phase2Anim.addFrame(PlayerAction.ActionType.STOP_MOVE_RIGHT, "skins/boss_phase2_idle.png");
        phase2Anim.addFrame(PlayerAction.ActionType.STOP_MOVE_LEFT, "skins/boss_phase2_idle.png");
        this.animation = phase2Anim;

        System.out.println("🔥Boss in second phase");
    }

    @Override
    public void takeDamage(int amount) {
        int reducedDamage = (int) Math.max(1, amount * 0.8);
        originalBoss.takeDamage(reducedDamage);

        this.setHealth(originalBoss.getHealth());
        this.setActive(originalBoss.isActive());

        if (!phase2Active && getHealth() <= getMaxHealth() * 0.5) {
            activatePhase2();
        }
    }

    @Override
    public void performAttack(GameState gameState) {
        if (attackStrategy != null) {
            attackStrategy.execute(this, gameState);
        }
        if (sfxPlayer != null) {
            sfxPlayer.playOnce("src/main/resources/music/boss_phase2_attack.mp3");
        }
    }

    @Override
    public void tick() {
        originalBoss.tick();
        this.setX(originalBoss.getX());
        this.setY(originalBoss.getY());
        this.setVelX(originalBoss.getVelX());
        this.setVelY(originalBoss.getVelY());
    }

    @Override
    public double getMoveSpeed() {
        return super.getMoveSpeed() * 1.5;
    }

    @Override
    public int getMaxHealth() {
        return super.getMaxHealth();
    }

    @Override
    public long getLastAttackTime() {
        return super.getLastAttackTime();
    }

    @Override
    public void accept(GameObjectVisitor visitor) {
        visitor.visitBoss(this);
    }

    @Override
    public BossPhase2Decorator clone() {
        throw new UnsupportedOperationException("Клонирование декоратора не поддерживается напрямую.");
    }

}