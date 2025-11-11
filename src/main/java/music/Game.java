package music;

public class Game {
    public static void main(String[] args) {
        AsyncMusicPlayer music = new AsyncMusicPlayer();

        // Запускаем фоновую музыку в цикле — НЕ БЛОКИРУЯ основной поток
        music.playLoop("src/main/resources/music/terraria_boss_1.mp3");

        // Сразу продолжаем выполнение — игра запускается мгновенно
        System.out.println("Игра запущена! Музыка играет в фоне...");

        // Имитация игрового цикла
        for (int i = 0; i < 600; i++) { // 10 секунд при 60 FPS
            // Обновление игры, рендер и т.д.
            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                break;
            }
        }

        // Завершение
        music.stop();
        System.out.println("Музыка остановлена. Выход.");
    }
}
