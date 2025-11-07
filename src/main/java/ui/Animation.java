package ui;

import net.PlayerAction.ActionType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Animation implements Serializable {

    private final Map<ActionType, String> animationFrames;
    private final String defaultSkinPath;
    private String currentSkinPath;

    public Animation(String defaultSkinPath) {
        this.defaultSkinPath = defaultSkinPath;
        this.currentSkinPath = defaultSkinPath;
        this.animationFrames = new HashMap<>();
    }

    public void addFrame(ActionType action, String skinPath) {
        animationFrames.put(action, skinPath);
    }

    public void changeState(ActionType action) {
        currentSkinPath = animationFrames.getOrDefault(action, defaultSkinPath);
    }

    public String getCurrentSkinPath() {
        return currentSkinPath;
    }
}