package core;

import model.GameObject;
import model.characters.Boss;
import model.characters.GameCharacter;
import net.PlayerAction;
import patterns.observer.IObserver;
import patterns.observer.ISubject;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameEngine implements ISubject, Runnable {

    private final List<IObserver> observers = new ArrayList<>();
    private final GameState gameState;
    private final Queue<PlayerAction> actionsQueue = new ConcurrentLinkedQueue<>();
    private volatile boolean running = true;

    public GameEngine(GameState gameState) {
        this.gameState = gameState;
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
                case MOVE_LEFT:
                    character.setVelX(-5);
                    break;
                case MOVE_RIGHT:
                    character.setVelX(5);
                    break;
                case STOP_MOVE_LEFT:
                    if (character.getVelX() < 0) character.setVelX(0);
                    break;
                case STOP_MOVE_RIGHT:
                    if (character.getVelX() > 0) character.setVelX(0);
                    break;
                case ATTACK:
                    character.performAttack(gameState);
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
        // Implement collision logic here if needed
    }

    public void stop() {
        running = false;
    }

    @Override
    public void addObserver(IObserver o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(IObserver o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        GameState stateCopy = gameState.deepCopy();
        for (IObserver o : observers) {
            o.update(stateCopy);
        }
    }
}