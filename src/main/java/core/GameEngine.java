package core;

import model.GameObject;
import model.Projectile;
import model.characters.GameCharacter;
import net.PlayerAction;
import patterns.observer.IObserver;
import patterns.observer.ISubject;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameEngine implements ISubject, Runnable {

    private final List<IObserver> observers = new CopyOnWriteArrayList<>();
    private final GameState gameState;
    private final Queue<PlayerAction> actionsQueue = new ConcurrentLinkedQueue<>();
    private volatile boolean running = true;

    public GameEngine(GameState gameState) {
        this.gameState = gameState;
    }

    public GameState getCurrentGameState() {
        return this.gameState;
    }

    public void addPlayerAction(PlayerAction action) {
        actionsQueue.add(action);
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                tick();
                delta--;
            }
        }
    }

    private void tick() {
        processInput();
        updateGameObjects();
        checkCollisions();
        notifyObservers();
    }

    private void processInput() {
        while (!actionsQueue.isEmpty()) {
            PlayerAction action = actionsQueue.poll();
            GameCharacter character = gameState.getCharacterById(action.getClientId());
            if (character == null) continue;

            switch (action.getType()) {
                case MOVE_LEFT: character.setVelX(-5); break;
                case MOVE_RIGHT: character.setVelX(5); break;
                case MOVE_UP: character.setVelY(-5); break;
                case MOVE_DOWN: character.setVelY(5); break;
                case STOP_MOVE_LEFT: if (character.getVelX() < 0) character.setVelX(0); break;
                case STOP_MOVE_RIGHT: if (character.getVelX() > 0) character.setVelX(0); break;
                case STOP_MOVE_UP: if (character.getVelY() < 0) character.setVelY(0); break;
                case STOP_MOVE_DOWN: if (character.getVelY() > 0) character.setVelY(0); break;
                case ATTACK:
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - character.getLastAttackTime() > character.getAttackCooldown()) {
                        character.performAttack(gameState);
                        character.setLastAttackTime(currentTime);
                    }
                    break;
            }
        }
    }

    private void updateGameObjects() {
        List<GameObject> objects = new ArrayList<>(gameState.getGameObjects());
        for (GameObject obj : objects) {
            obj.tick();
        }
        gameState.getGameObjects().removeIf(obj -> !obj.isActive());
    }

    private void checkCollisions() {
        List<GameObject> objects = gameState.getGameObjects();
        for (GameObject objA : objects) {
            if (!(objA instanceof Projectile)) {
                continue;
            }

            Projectile projectile = (Projectile) objA;
            if (!projectile.isActive()) continue;

            for (GameObject objB : objects) {
                if (!(objB instanceof GameCharacter)) {
                    continue;
                }

                GameCharacter character = (GameCharacter) objB;
                if (!character.isActive() || projectile.getOwnerId() == character.getId()) {
                    continue;
                }

                if (projectile.getBounds().intersects(character.getBounds())) {
                    character.takeDamage(projectile.getDamage());
                    projectile.setActive(false);
                    System.out.printf("%s damaged by projectile from %d. Current HP: %d%n", character.getName(), projectile.getOwnerId(), character.getHealth());
                    break;
                }
            }
        }
    }

    public void stop() {
        running = false;
    }

    @Override
    public void addObserver(IObserver o) { observers.add(o); }

    @Override
    public void removeObserver(IObserver o) { observers.remove(o); }

    @Override
    public void notifyObservers() {
        GameState stateCopy = gameState.deepCopy();
        for (IObserver o : observers) {
            o.update(stateCopy);
        }
    }
}