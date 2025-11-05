package patterns.strategy;

import core.GameState;
import model.characters.GameCharacter;
import java.io.Serializable;

public interface IAttackStrategy extends Serializable {
    void execute(GameCharacter attacker, GameState gameState);
}