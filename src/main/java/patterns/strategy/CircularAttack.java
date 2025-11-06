package patterns.strategy;

import core.GameState;
import model.Projectile;
import model.characters.GameCharacter;
import java.util.concurrent.atomic.AtomicInteger;

public class CircularAttack implements IAttackStrategy {
    private static final AtomicInteger projectileIdCounter = new AtomicInteger(3000);
    private final int projectileCount;
    private final double speed;

    public CircularAttack(int projectileCount, double speed) {
        this.projectileCount = projectileCount;
        this.speed = speed;
    }

    @Override
    public void execute(GameCharacter attacker, GameState gameState) {
        double startX = attacker.getX() + attacker.getWidth() / 2.0;
        double startY = attacker.getY() + attacker.getHeight() / 2.0;

        for (int i = 0; i < projectileCount; i++) {
            double angle = 2 * Math.PI * i / projectileCount;
            double velX = Math.cos(angle) * speed;
            double velY = Math.sin(angle) * speed;

            Projectile projectile = new Projectile(
                    projectileIdCounter.getAndIncrement(),
                    startX, startY, velX, velY, "skins/wave.png",
                    attacker.getId(),
                    attacker.getDamage()
            );
            gameState.addGameObject(projectile);
        }
    }
}