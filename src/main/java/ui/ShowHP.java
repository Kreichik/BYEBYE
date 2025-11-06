package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.image.BufferedImage;

public class ShowHP {

    protected static final int BAR_HEIGHT = 10; // Высота полосы здоровья
    protected static final int BAR_WIDTH_MAX = 70; // Максимальная ширина полосы здоровья
    protected static final int TEXT_OFFSET_Y = -15; // Смещение текста над полосой здоровья
    protected static final Color HEALTH_COLOR = Color.GREEN; // Цвет полосы здоровья
    protected static final Color BACKGROUND_COLOR = Color.RED; // Цвет фона полосы здоровья
    protected static final Color BORDER_COLOR = Color.WHITE; // Цвет границы полосы здоровья
    protected static final int BORDER_THICKNESS = 1; // Толщина границы

    public static void drawHealthBar(Graphics g, int currentHealth, int maxHealth, int x, int y, String name) {
        // Убедимся, что здоровье не отрицательное
        if (currentHealth < 0) {
            currentHealth = 0;
        }

        // Вычисляем текущую ширину полосы здоровья
        int barWidth = (int) ((double) currentHealth / maxHealth * BAR_WIDTH_MAX);

        // Рисуем фон (красный)
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(x, y, BAR_WIDTH_MAX, BAR_HEIGHT);

        // Рисуем текущее здоровье (зеленый)
        g.setColor(HEALTH_COLOR);
        g.fillRect(x, y, barWidth, BAR_HEIGHT);

        // Рисуем границу
        g.setColor(BORDER_COLOR);
        g.drawRect(x, y, BAR_WIDTH_MAX, BAR_HEIGHT);

        // Рисуем имя персонажа и его здоровье
        g.setFont(new Font("Arial", Font.BOLD, 10)); // Устанавливаем шрифт
        g.setColor(Color.WHITE); // Цвет текста
        String healthText = name + ": " + currentHealth + "/" + maxHealth;
        // Центрируем текст над полосой
        int textX = x + (BAR_WIDTH_MAX - g.getFontMetrics().stringWidth(healthText)) / 2;
        g.drawString(healthText, textX, y + TEXT_OFFSET_Y);
    }

    // Пример использования изображения для полосы здоровья (если вы хотите использовать hp_row.jpg)
    // Этот метод не используется напрямую в текущем решении, но показывает, как это можно сделать.
    public static void drawImageHealthBar(Graphics g, int currentHealth, int maxHealth, int x, int y, String name, BufferedImage hpRowImage) {
        if (hpRowImage == null) {
            drawHealthBar(g, currentHealth, maxHealth, x, y, name); // Откат к обычному бару, если изображение не загружено
            return;
        }

        int barWidth = (int) ((double) currentHealth / maxHealth * BAR_WIDTH_MAX);

        // Рисуем часть изображения, соответствующую текущему здоровью
        // Предполагаем, что hpRowImage - это полная полоса здоровья.
        // Вы можете обрезать изображение или использовать его как фон, а сверху накладывать цветную полосу.
        // Для простоты, здесь мы просто используем его как фон и рисуем поверх цветную полосу.

        // Можно нарисовать полную полосу как фон, а затем зеленую поверх
        g.drawImage(hpRowImage, x, y, BAR_WIDTH_MAX, BAR_HEIGHT, null);

        // Затем рисуем цветную полосу, как в drawHealthBar
        g.setColor(HEALTH_COLOR);
        g.fillRect(x, y, barWidth, BAR_HEIGHT);

        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.setColor(Color.WHITE);
        String healthText = name + ": " + currentHealth + "/" + maxHealth;
        int textX = x + (BAR_WIDTH_MAX - g.getFontMetrics().stringWidth(healthText)) / 2;
        g.drawString(healthText, textX, y + TEXT_OFFSET_Y);
    }
}