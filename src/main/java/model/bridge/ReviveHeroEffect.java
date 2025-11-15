package model.bridge;

import core.GameState;
import model.characters.GameCharacter;
import model.characters.Hero;

public class ReviveHeroEffect implements InteractionEffect {
    private final int heroToReviveId;

    public ReviveHeroEffect(int heroToReviveId) {
        this.heroToReviveId = heroToReviveId;
    }

    @Override
    public void applyEffect(GameState gameState, double x, double y) {
        GameCharacter character = gameState.getCharacterById(heroToReviveId);
        if (character instanceof Hero) {
            ((Hero) character).revive(x, y);
        }
    }
}