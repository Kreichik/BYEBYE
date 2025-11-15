package model;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import ui.Animation;
import patterns.visitor.GameObjectVisitor;
import ui.ImageLoader;

public abstract class GameObject implements Serializable, Cloneable {
    protected int id;
    protected double x, y;
    protected double velX, velY;
    protected int width, height;
    protected Animation animation;
    protected boolean active = true;

    public GameObject(int id, double x, double y, int width, int height, Animation animation) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.animation = animation;
    }

    public void tick() {
        if (!active) {
            velX = 0;
            velY = 0;
            return;
        }
        x += velX;
        y += velY;
    }

    public void render(Graphics g, int screenOffset) {
        if (!active) return;

        String currentSkin = animation.getCurrentSkinPath();
        BufferedImage image = ImageLoader.loadImage(currentSkin);
        if (image != null) {
            g.drawImage(image, (int) x + screenOffset, (int) y, width, height, null);
        }
    }

    public void updateAnimationState(net.PlayerAction.ActionType action) {
        if (animation != null) {
            animation.changeState(action);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }
    public Animation getAnimation() {
        return animation;
    }
    public int getId() { return id; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public double getVelX() { return velX; }
    public double getVelY() { return velY; }
    public boolean isActive() { return active; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setVelX(double velX) { this.velX = velX; }
    public void setVelY(double velY) { this.velY = velY; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public GameObject clone() {
        try {
            return (GameObject) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public abstract void accept(GameObjectVisitor visitor);
}