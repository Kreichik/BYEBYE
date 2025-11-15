package model.bridge;

import core.GameState;
import java.io.Serializable;

public interface InteractionEffect extends Serializable {
    void applyEffect(GameState gameState);
}