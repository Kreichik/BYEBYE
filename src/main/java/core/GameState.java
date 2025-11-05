package core;

import model.GameObject;
import model.characters.GameCharacter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameState implements Serializable {
    private final List<GameObject> gameObjects = new CopyOnWriteArrayList<>();

    public void addGameObject(GameObject object) {
        gameObjects.add(object);
    }

    public void removeGameObject(GameObject object) {
        gameObjects.remove(object);
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public GameCharacter getCharacterById(int id) {
        for (GameObject obj : gameObjects) {
            if (obj instanceof GameCharacter && obj.getId() == id) {
                return (GameCharacter) obj;
            }
        }
        return null;
    }

    public GameState deepCopy() {
        GameState newSate = new GameState();
        for (GameObject obj : this.gameObjects) {
            newSate.addGameObject(obj.clone());
        }
        return newSate;
    }
}