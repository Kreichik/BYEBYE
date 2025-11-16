package core;

import model.GameObject;
import model.characters.GameCharacter;

import java.io.*;
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

    public void replaceGameObject(int id, GameObject newObject) {
        if (newObject == null) {
            throw new IllegalArgumentException("Нельзя заменить на null объект");
        }
        if (newObject.getId() != id) {
            throw new IllegalArgumentException("ID нового объекта не совпадает с целевым ID");
        }

        gameObjects.removeIf(obj -> obj.getId() == id);

        gameObjects.add(newObject);
    }

    public boolean replaceExistingGameObject(int id, GameObject newObject) {
        if (newObject == null || newObject.getId() != id) {
            throw new IllegalArgumentException("Некорректный новый объект");
        }

        boolean removed = gameObjects.removeIf(obj -> obj.getId() == id);
        if (removed) {
            gameObjects.add(newObject);
        }
        return removed;
    }

    public GameState deepCopy() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.flush();
            oos.close();

            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            GameState copiedState = (GameState) ois.readObject();
            ois.close();

            return copiedState;

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during GameState deep copy: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}