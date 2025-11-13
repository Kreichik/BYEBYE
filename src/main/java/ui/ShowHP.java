package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.image.BufferedImage;

public class ShowHP {

    public static final int BAR_HEIGHT = 10;
    public static final int BAR_WIDTH_MAX = 70;
    public static final int TEXT_OFFSET_Y = -15;
    public static final Color HEALTH_COLOR = Color.GREEN;
    public static final Color BACKGROUND_COLOR = Color.RED;
    public static final Color BORDER_COLOR = Color.WHITE;
    public static final int BORDER_THICKNESS = 1;

    public static void drawHealthBar(Graphics g, int currentHealth, int maxHealth, int x, int y, String name) {
        if (currentHealth < 0) {
            currentHealth = 0;
        }

        int barWidth = (int) ((double) currentHealth / maxHealth * BAR_WIDTH_MAX);

        g.setColor(BACKGROUND_COLOR);
        g.fillRect(x, y, BAR_WIDTH_MAX, BAR_HEIGHT);

        g.setColor(HEALTH_COLOR);
        g.fillRect(x, y, barWidth, BAR_HEIGHT);

        g.setColor(BORDER_COLOR);
        g.drawRect(x, y, BAR_WIDTH_MAX, BAR_HEIGHT);

        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.setColor(Color.WHITE);
        String healthText = name + ": " + currentHealth + "/" + maxHealth;
        int textX = x + (BAR_WIDTH_MAX - g.getFontMetrics().stringWidth(healthText)) / 2;
        g.drawString(healthText, textX, y + TEXT_OFFSET_Y);
    }
}