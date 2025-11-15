package core;

import model.GameObject;
import model.InteractionPoint;
import model.characters.Boss;
import model.characters.GameCharacter;
import model.characters.Hero;
import model.characters.NPC;
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
    private volatile boolean paused = false;
    private final List<Integer> deadNpcQueue = new ArrayList<>();

    public GameEngine(GameState gameState) {
        this.gameState = gameState;
    }

    public GameState getCurrentGameState() {
        synchronized (gameState) {
            return this.gameState.deepCopy();
        }
    }

    public Object getGameStateLock() {
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
        if (!paused) {
            handleAutonomousActions(); // <<< ПЕРЕМЕСТИЛ СЮДА
            checkCollisions();         // <<< ПЕРЕМЕСТИЛ СЮДА
            updateGameObjects();       // <<< ПЕРЕМЕСТИЛ СЮДА
        }
        notifyObservers();
    }

    private void processInput() {
        while (!actionsQueue.isEmpty()) {
            PlayerAction action = actionsQueue.poll();
            GameCharacter character = gameState.getCharacterById(action.getClientId());
            if (character == null) continue;

            character.updateAnimationState(action.getType());

            switch (action.getType()) {
                case MOVE_LEFT: character.setVelX(-character.getMoveSpeed()); break;
                case MOVE_RIGHT: character.setVelX(character.getMoveSpeed()); break;
                case MOVE_UP: character.setVelY(-character.getMoveSpeed()); break;
                case MOVE_DOWN: character.setVelY(character.getMoveSpeed()); break;
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
                case SPAWN_NPC:
                    if (character instanceof Boss) {
                        ((Boss) character).trySpawnNpc(gameState);
                    }
                    break;
                case PAUSE: paused = true; break;
                case RESUME: paused = false; break;
                case STRATEGY_MELEE:
                    character.setAttackStrategy(new patterns.strategy.MeleeAttackStrategy());
                    adjustCharacterStats(character, +5, -200);
                    break;
                case STRATEGY_RANGED:
                    character.setAttackStrategy(new patterns.strategy.RangedAttack());
                    adjustCharacterStats(character, 0, +300);
                    break;
                case STRATEGY_MAGIC:
                    character.setAttackStrategy(new patterns.strategy.MagicAttackStrategy(8, 4));
                    adjustCharacterStats(character, +2, +150);
                    break;
            }
        }
    }

    private void adjustCharacterStats(GameCharacter character, int damageDelta, double rangeDelta) {
        try {
            java.lang.reflect.Field damageField = GameCharacter.class.getDeclaredField("damage");
            damageField.setAccessible(true);
            damageField.set(character, Math.max(1, ((int) damageField.get(character)) + damageDelta));

            java.lang.reflect.Field rangeField = GameCharacter.class.getDeclaredField("attackRange");
            rangeField.setAccessible(true);
            rangeField.set(character, Math.max(0, ((double) rangeField.get(character)) + rangeDelta));
        } catch (Exception ignore) {}
    }

    private void updateGameObjects() {
        synchronized (gameState) {
            for (GameObject obj : gameState.getGameObjects()) {
                obj.tick();
                if (obj instanceof NPC && !obj.isActive()) {
                    deadNpcQueue.add(obj.getId());
                }
            }
            // Теперь удаляем объекты после всех логических шагов
            gameState.getGameObjects().removeIf(obj -> !obj.isActive());
        }
    }

    private void handleAutonomousActions() {
        Boss boss = null;
        List<InteractionPoint> interactionPoints = new ArrayList<>();
        List<Hero> livingHeroes = new ArrayList<>();

        synchronized (gameState) {
            for (GameObject obj : gameState.getGameObjects()) {
                if (obj instanceof Boss) boss = (Boss) obj;
                if (obj instanceof InteractionPoint && obj.isActive()) interactionPoints.add((InteractionPoint) obj);
                if (obj instanceof Hero && obj.isActive()) livingHeroes.add((Hero) obj);
                // Если герой мертв, но еще не удален, он не попадет в livingHeroes
                // но его InteractionPoint может быть обработан, если другой герой рядом

                if (obj instanceof NPC) {
                    NPC npc = (NPC) obj;
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - npc.getLastAttackTime() > npc.getAttackCooldown()) {
                        npc.performAttack(gameState);
                        npc.setLastAttackTime(currentTime);
                    }
                }
            }
        }

        // Обрабатываем точки возрождения
        for (InteractionPoint point : interactionPoints) {
            boolean heroIsNear = false;
            for (Hero hero : livingHeroes) {
                if (point.getBounds().intersects(hero.getBounds())) {
                    heroIsNear = true;
                    break;
                }
            }
            point.updateInteraction(heroIsNear, gameState);
        }

        if (boss != null && !deadNpcQueue.isEmpty()) {
            for (Integer npcId : deadNpcQueue) {
                boss.onNpcDied(npcId);
            }
            deadNpcQueue.clear();
        }
    }

    private void checkCollisions() {
        List<GameObject> objects = gameState.getGameObjects();
        for (GameObject objA : objects) {
            if (!objA.isActive()) continue;
            for (GameObject objB : objects) {
                if (objA == objB || !objB.isActive()) continue;
                objA.accept(new patterns.visitor.CollisionVisitor(objB, gameState));
            }
        }
    }

    public void stop() { running = false; }

    @Override
    public void addObserver(IObserver o) { observers.add(o); }

    @Override
    public void removeObserver(IObserver o) { observers.remove(o); }

    @Override
    public void notifyObservers() {
        GameState stateCopy;
        synchronized (gameState) {
            stateCopy = gameState.deepCopy();
        }
        for (IObserver o : observers) {
            o.update(stateCopy);
        }
    }
}