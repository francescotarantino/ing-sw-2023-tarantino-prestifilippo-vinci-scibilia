package it.polimi.ingsw.model;

public class Point {
    private final int x;
    private final int y;

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
        return "x=" + x + ", y=" + y;
    }
}
