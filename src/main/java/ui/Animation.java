// src/main/java/ui/Animation.java

package ui;

import net.PlayerAction.ActionType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Animation implements Serializable {

    // Карта для хранения путей к скинам для каждого действия
    private final Map<ActionType, String> animationFrames;
    // Скин по умолчанию (для состояния покоя)
    private final String defaultSkinPath;
    // Текущий скин, который должен быть отрисован
    private String currentSkinPath;

    /**
     * Конструктор принимает путь к скину по умолчанию.
     * @param defaultSkinPath Путь к изображению для состояния покоя.
     */
    public Animation(String defaultSkinPath) {
        this.defaultSkinPath = defaultSkinPath;
        this.currentSkinPath = defaultSkinPath;
        this.animationFrames = new HashMap<>();
    }

    /**
     * Добавляет кадр анимации для определенного действия.
     * @param action Тип действия (например, MOVE_RIGHT).
     * @param skinPath Путь к соответствующему изображению.
     */
    public void addFrame(ActionType action, String skinPath) {
        animationFrames.put(action, skinPath);
    }

    /**
     * Изменяет текущий скин в зависимости от действия.
     * Если для действия нет скина, используется скин по умолчанию.
     * @param action Действие, которое выполняет персонаж.
     */
    public void changeState(ActionType action) {
        // Находим скин для действия. Если не найден, используем скин по умолчанию.
        currentSkinPath = animationFrames.getOrDefault(action, defaultSkinPath);
    }

    /**
     * Возвращает путь к текущему скину для отрисовки.
     * @return String путь к файлу изображения.
     */
    public String getCurrentSkinPath() {
        return currentSkinPath;
    }
}