package it.polimi.ingsw.view.graphical.fx;

import it.polimi.ingsw.Utils;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ImageCache {
    private final Map<String, Image> cache = new HashMap<>();
    private static ImageCache instance;

    public static void loadImages() {
        if (getInstance().cache.size() == 0) {
            Utils.iterateInResourceDirectory("images", (path) -> {
                getInstance().cache.put(path, new Image(Objects.requireNonNull(getInstance().getClass().getResourceAsStream(path))));
            });
        }
    }

    public static Image getImage(String path) {
        if(!getInstance().cache.containsKey(path)) {
            getInstance().cache.put(path, new Image(Objects.requireNonNull(getInstance().getClass().getResourceAsStream(path))));
        }

        return getInstance().cache.get(path);
    }

    private static ImageCache getInstance() {
        if (instance == null) {
            instance = new ImageCache();
        }

        return instance;
    }
}
