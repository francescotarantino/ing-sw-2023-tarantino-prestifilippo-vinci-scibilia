package it.polimi.ingsw.model;

import javafx.scene.input.DataFormat;

import java.io.Serial;
import java.io.Serializable;

public class Point implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final int x;
    private final int y;
    public static final DataFormat pointFormat = new DataFormat("it.polimi.ingsw.model.Point");

    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Point point)) {
            return false;
        }
        return point.getX() == this.x && point.getY() == this.y;
    }

    @Override
    public String toString() {
        return ("[" + x + "," + y + "]");
    }
}
