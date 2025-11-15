package model.characters;

import core.GameState;
import net.PlayerAction;
import patterns.factory.CharacterFactory;
import ui.Animation;
import patterns.visitor.GameObjectVisitor;
import java.util.ArrayList;
import java.util.List;

public class Boss extends GameCharacter {
    public static final int MAX_NPC_COUNT = 2;
    public static final long NPC_COOLDOWN_ON_DEATH = 5000;

    private final long[] npcCooldowns = new long[MAX_NPC_COUNT];
    private final List<Integer> activeNpcIds = new ArrayList<>();

    public Boss(int id, double x, double y, String name, int damage, double attackRange, long attackCooldown, CharacterFactory.BossType type) {
        super(id, x, y, 150, 200, createBossAnimation(type), name, 1000, damage, attackRange, attackCooldown, 0);
    }

    public boolean trySpawnNpc(GameState gameState) {
        if (activeNpcIds.size() >= MAX_NPC_COUNT) return false;

        for (int i = 0; i < MAX_NPC_COUNT; i++) {
            if (System.currentTimeMillis() >= npcCooldowns[i]) {
                GameCharacter npc = CharacterFactory.getFactory().createNpc(this.x, this.y);
                if (npc != null) {
                    activeNpcIds.add(npc.getId());
                    npcCooldowns[i] = Long.MAX_VALUE;
                    return true;
                }
            }
        }
        return false;
    }

    public void onNpcDied(int npcId) {
        activeNpcIds.remove(Integer.valueOf(npcId));
        for (int i = 0; i < MAX_NPC_COUNT; i++) {
            if (npcCooldowns[i] == Long.MAX_VALUE) {
                npcCooldowns[i] = System.currentTimeMillis() + NPC_COOLDOWN_ON_DEATH;
                return;
            }
        }
    }

    public long[] getNpcCooldowns() {
        return npcCooldowns;
    }

    private static Animation createBossAnimation(CharacterFactory.BossType type) {
        Animation bossAnimation = new Animation("skins/boss_skin1.png");
        bossAnimation.addFrame(PlayerAction.ActionType.MOVE_RIGHT, "skins/boss_skin2_right.png");
        bossAnimation.addFrame(PlayerAction.ActionType.MOVE_LEFT, "skins/boss_skin2_left.png");
        bossAnimation.addFrame(PlayerAction.ActionType.ATTACK, "skins/boss_skin3.png");
        bossAnimation.addFrame(PlayerAction.ActionType.STOP_MOVE_RIGHT, "skins/boss_skin1.png");
        bossAnimation.addFrame(PlayerAction.ActionType.STOP_MOVE_LEFT, "skins/boss_skin1.png");
        return bossAnimation;
    }

    @Override
    public void accept(GameObjectVisitor visitor) {
        visitor.visitBoss(this);
    }

    @Override
    public double getMoveSpeed() {
        return 3.0;
    }
}