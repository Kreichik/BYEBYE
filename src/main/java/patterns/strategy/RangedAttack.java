package patterns.strategy;

import core.GameState;
import model.Projectile;
import model.characters.GameCharacter;
import java.util.concurrent.atomic.AtomicInteger;
import static core.Main.WORLD_WIDTH;

public class RangedAttack implements IAttackStrategy {
    private static final AtomicInteger projectileIdCounter = new AtomicInteger(2000);

    @Override
    public void execute(GameCharacter attacker, GameState gameState) {
        double startX = attacker.getX() + attacker.getWidth() / 2.0;
        double startY = attacker.getY() + attacker.getHeight() / 2.0;
        double velX = attacker.getX() < WORLD_WIDTH / 2.0 ? 10 : -10;

        Projectile projectile = new Projectile(
                projectileIdCounter.getAndIncrement(),
                startX, startY, velX, 0, "skins/archer_attack.png",
                attacker.getId(),
                attacker.getDamage()
        );
        gameState.addGameObject(projectile);
    }
}