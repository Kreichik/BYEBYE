package model;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import ui.ImageLoader;

public class GameObject implements Serializable, Cloneable {
    protected int id;
    protected double x, y;
    protected double velX, velY;
    protected int width, height;
    protected String skinPath;
    protected boolean active = true;

    public GameObject(int id, double x, double y, int width, int height, String skinPath) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.skinPath = skinPath;
    }

    public void tick() {
        x += velX;
        y += velY;
    }

    public void render(Graphics g, int screenOffset) {
        BufferedImage image = ImageLoader.loadImage(skinPath);
        if (image != null) {
            g.drawImage(image, (int) x + screenOffset, (int) y, width, height, null);
        }
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
}