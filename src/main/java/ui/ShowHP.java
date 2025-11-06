package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.image.BufferedImage;

public class ShowHP {

    protected static final int BAR_HEIGHT = 10; // Высота полосы здоровья
    protected static final int BAR_WIDTH_MAX = 50; // Максимальная ширина полосы здоровья
    protected static final int TEXT_OFFSET_Y = -15; // Смещение текста над полосой здоровья
    protected static final Color HEALTH_COLOR = Color.RED; // Цвет полосы здоровья
    protected static final Color BACKGROUND_COLOR = Color.GRAY; // Цвет фона полосы здоровья
    protected static final Color BORDER_COLOR = Color.WHITE; // Цвет границы полосы здоровья
    protected static final int BORDER_THICKNESS = 1; // Толщина границы

    public static void drawHealthBar(Graphics g, int currentHealth, int maxHealth, int x, int y, String name) {
        // Убедимся, что здоровье не отрицательное
        if (currentHealth < 0) {
            currentHealth = 0;
        }

        // Вычисляем текущую ширину полосы здоровья
        int barWidth = (int) ((double) currentHealth / maxHealth * BAR_WIDTH_MAX);

        // Рисуем фон (серый)
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(x, y, BAR_WIDTH_MAX, BAR_HEIGHT);

        // Рисуем текущее здоровье (красный)
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

    public static void drawImageHealthBar(Graphics g, int currentHealth, int maxHealth, int x, int y, String name, BufferedImage hpRowImage) {
        if (hpRowImage == null) {
            drawHealthBar(g, currentHealth, maxHealth, x, y, name); // Откат к обычному бару, если изображение не загружено
            return;
        }

        // Убедимся, что здоровье не отрицательное
        if (currentHealth < 0) {
            currentHealth = 0;
        }

        // Вычисляем текущую ширину полосы здоровья на основе BAR_WIDTH_MAX
        int barFillWidth = (int) ((double) currentHealth / maxHealth * BAR_WIDTH_MAX);

        // --- Изменения начинаются здесь ---

        // 1. Сначала рисуем фоновый цвет полосы (пустую часть)
        // Если hpRowImage выступает как рамка, то фон будет виден в пустой части.
        // Используем исходные размеры изображения для отрисовки фоновой полосы,
        // но с учетом BAR_WIDTH_MAX и BAR_HEIGHT для масштабирования.
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(x, y, BAR_WIDTH_MAX, BAR_HEIGHT);


        // 2. Затем рисуем заполненную часть полосы здоровья (красную)
        g.setColor(HEALTH_COLOR);
        g.fillRect(x, y, barFillWidth, BAR_HEIGHT);


        // 3. После этого рисуем изображение hpRowImage поверх всего,
        // чтобы оно было рамкой, покрывающей полосу здоровья.
        // Масштабируем изображение до размеров BAR_WIDTH_MAX и BAR_HEIGHT.
        g.drawImage(hpRowImage, x, y, BAR_WIDTH_MAX, BAR_HEIGHT, null);

        // --- Изменения заканчиваются здесь ---


        // Рисуем имя персонажа и его здоровье (остается без изменений)
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.setColor(Color.WHITE);
        String healthText = name + ": " + currentHealth + "/" + maxHealth;
        int textX = x + (BAR_WIDTH_MAX - g.getFontMetrics().stringWidth(healthText)) / 2;
        g.drawString(healthText, textX, y + TEXT_OFFSET_Y);
    }
}