package it.polimi.ingsw.model;

import javafx.scene.input.DataFormat;

import java.io.Serial;
import java.io.Serializable;

/**
 * This record represents a generic 2D point.
 * @param x x coordinate
 * @param y y coordinate
 */
public record Point(int x, int y) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public static final DataFormat pointFormat = new DataFormat("it.polimi.ingsw.model.Point");

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Point point)) {
            return false;
        }
        return point.x() == this.x && point.y() == this.y;
    }

    @Override
    public String toString() {
        return ("[" + x + "," + y + "]");
    }
}
