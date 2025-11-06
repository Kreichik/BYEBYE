package net;

import java.io.Serializable;

public class PlayerAction implements Serializable {
    public enum ActionType {
        MOVE_LEFT, MOVE_RIGHT ,MOVE_UP ,MOVE_DOWN ,STOP_MOVE_LEFT,STOP_MOVE_RIGHT ,STOP_MOVE_UP ,STOP_MOVE_DOWN , ATTACK,
        PAUSE, RESUME,
        STRATEGY_MELEE, STRATEGY_RANGED, STRATEGY_MAGIC
    }

    private int clientId;
    private final ActionType type;

    public PlayerAction(ActionType type) {
        this.type = type;
    }

    public int getClientId() { return clientId; }
    public ActionType getType() { return type; }
    public void setClientId(int clientId) { this.clientId = clientId; }
}