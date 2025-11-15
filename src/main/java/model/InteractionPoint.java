package model;

import core.GameState;
import model.bridge.InteractionEffect;
import patterns.visitor.GameObjectVisitor;
import ui.Animation;
import java.awt.Color;
import java.awt.Graphics;

public class InteractionPoint extends GameObject {
    private final InteractionEffect effect;
    private long interactionStartTime = 0;
    private final long requiredInteractionTime;
    private double progress = 0.0;

    public InteractionPoint(int id, double x, double y, long requiredTime, InteractionEffect effect) {
        super(id, x, y, 100, 100, new Animation("skins/revive_point.png"));
        this.requiredInteractionTime = requiredTime;
        this.effect = effect;
    }

    public void updateInteraction(boolean heroIsNear, GameState gameState) {
        if (heroIsNear) {
            if (interactionStartTime == 0) {
                interactionStartTime = System.currentTimeMillis();
            }
            long elapsedTime = System.currentTimeMillis() - interactionStartTime;
            this.progress = (double) elapsedTime / requiredInteractionTime;

            if (elapsedTime >= requiredInteractionTime) {
                effect.applyEffect(gameState, this.x, this.y);
                this.setActive(false);
            }
        } else {
            interactionStartTime = 0;
            this.progress = 0;
        }
    }

    @Override
    public void render(Graphics g, int screenOffset) {
        super.render(g, screenOffset);
        if (progress > 0 && progress < 1.0) {
            g.setColor(Color.YELLOW);
            int barWidth = (int) (this.width * progress);
            g.fillRect((int) this.x + screenOffset, (int) this.y + this.height, barWidth, 5);
        }
    }

    @Override
    public void accept(GameObjectVisitor visitor) {
        visitor.visitInteractionPoint(this);
    }
}