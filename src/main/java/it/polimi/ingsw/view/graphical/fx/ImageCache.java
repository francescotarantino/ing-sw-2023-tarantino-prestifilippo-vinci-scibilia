package it.polimi.ingsw.view.graphical.fx;

import it.polimi.ingsw.utils.Utils;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class is used to cache JavaFX images for better performance.
 * @see #loadImages()
 */
public class ImageCache {
    /**
     * This map is used to store the cached images.
     * The path of the image is used as key, while the {@link Image} object is used as value.
     */
    private final Map<String, Image> cache = new HashMap<>();
    private static ImageCache instance;

    /**
     * This method loads all the images in memory as {@link Image} objects.
     * @see #getImage(String)
     */
    public static void loadImages() {
        if (getInstance().cache.size() == 0) {
            Utils.iterateInResourceDirectory("images", ImageCache::loadImage);
        }
    }

    /**
     * This method is used to get the {@link Image} object corresponding to the image at the given path.
     * If the image was not previously loaded, it is also loaded and cached.
     * @param path the path of the image
     * @return the corresponding {@link Image}
     */
    public static Image getImage(String path) {
        if(!getInstance().cache.containsKey(path)) {
            loadImage(path);
        }

        return getInstance().cache.get(path);
    }

    /**
     * This method is used to load an image and cache it.
     * @param path the path of the image
     */
    public static void loadImage(String path) {
        getInstance().cache.put(path, new Image(Objects.requireNonNull(getInstance().getClass().getResourceAsStream(path))));
    }

    /**
     * Singleton getter.
     * @return the class instance
     */
    private static ImageCache getInstance() {
        if (instance == null) {
            instance = new ImageCache();
        }

        return instance;
    }
}
