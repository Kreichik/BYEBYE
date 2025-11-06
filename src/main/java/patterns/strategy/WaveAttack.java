package patterns.strategy;

import core.GameState;
import model.Projectile;
import model.characters.GameCharacter;
import java.util.concurrent.atomic.AtomicInteger;

public class WaveAttack implements IAttackStrategy {
    private static final AtomicInteger projectileIdCounter = new AtomicInteger(1000);

    @Override
    public void execute(GameCharacter attacker, GameState gameState) {
        double startX = attacker.getX() + attacker.getWidth() / 2.0;
        double startY = attacker.getY() + attacker.getHeight() / 2.0;

        Projectile waveLeft = new Projectile(projectileIdCounter.getAndIncrement(), startX, startY, -7, "skins/wave.png", attacker.getId(), attacker.getDamage());
        Projectile waveRight = new Projectile(projectileIdCounter.getAndIncrement(), startX, startY, 7, "skins/wave.png", attacker.getId(), attacker.getDamage());

        gameState.addGameObject(waveLeft);
        gameState.addGameObject(waveRight);
    }
}