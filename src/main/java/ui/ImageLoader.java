package ui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageLoader {
    private static final Map<String, BufferedImage> cache = new HashMap<>();

    public static BufferedImage loadImage(String path) {
        if (cache.containsKey(path)) {
            return cache.get(path);
        }
        try (InputStream is = ImageLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("Could not find resource: " + path);
                return null;
            }
            BufferedImage image = ImageIO.read(is);
            cache.put(path, image);
            return image;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}