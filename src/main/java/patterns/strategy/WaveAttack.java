package patterns.strategy;

import core.GameState;
import model.Projectile;
import model.characters.GameCharacter;
import java.util.concurrent.atomic.AtomicInteger;

public class WaveAttack implements IAttackStrategy {
    private static final AtomicInteger projectileIdCounter = new AtomicInteger(1000);
    private final double velX;
    private final double velY;

    public WaveAttack(double velX, double velY) {
        this.velX = velX;
        this.velY = velY;
    }

    @Override
    public void execute(GameCharacter attacker, GameState gameState) {
        double startX = attacker.getX() + attacker.getWidth() / 2.0;
        double startY = attacker.getY() + attacker.getHeight() / 2.0;

        Projectile wave = new Projectile(
                projectileIdCounter.getAndIncrement(),
                startX, startY,
                this.velX, this.velY,
                "skins/wave.png",
                attacker.getId(),
                attacker.getDamage()
        );

        gameState.addGameObject(wave);
    }
}