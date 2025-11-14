package music;

import core.GameState;
import model.characters.GameCharacter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class SfxController {
    protected final AsyncMusicPlayer oneShotSfxPlayer = new AsyncMusicPlayer();
    protected final AsyncMusicPlayer loopingSfxPlayer = new AsyncMusicPlayer();

    private final Map<Integer, CharacterWalkState> walkingStates = new HashMap<>();
    protected final Set<Integer> deathSoundPlayedFor = new HashSet<>();


    protected void handleWalkingEvent(GameCharacter character) {
        int charId = character.getId();
        double currentX = character.getX();

        CharacterWalkState walkState = walkingStates.computeIfAbsent(charId, id -> new CharacterWalkState(currentX));

        boolean isCurrentlyMoving = (Math.abs(currentX - walkState.lastX) > 0.1);

        if (isCurrentlyMoving && !walkState.isSoundPlaying) {
            if (!loopingSfxPlayer.isPlaying()) {
                loopingSfxPlayer.playLoop("src/main/resources/music/walk.mp3");
                walkState.isSoundPlaying = true;
            }
        } else if (!isCurrentlyMoving && walkState.isSoundPlaying) {
            loopingSfxPlayer.stop();
            walkState.isSoundPlaying = false;
        }

        walkState.lastX = currentX;
    }

    protected void handleDeathEvent(GameCharacter character) {
        int charId = character.getId();
        if (!character.isActive() && !deathSoundPlayedFor.contains(charId)) {
            if (charId == 0) {
                oneShotSfxPlayer.playOnce("src/main/resources/music/dead.mp3");
                oneShotSfxPlayer.playOnce("src/main/resources/music/victory.mp3");
            } else {
                oneShotSfxPlayer.playOnce("src/main/resources/music/dead.mp3");
            }
            deathSoundPlayedFor.add(charId);
        }
    }

    private static class CharacterWalkState {
        double lastX;
        boolean isSoundPlaying;

        CharacterWalkState(double startX) {
            this.lastX = startX;
            this.isSoundPlaying = false;
        }
    }
}
