package it.polimi.ingsw.view.textual.charset;

public class DoubleLineCharset extends Charset {
    public DoubleLineCharset() {
        this.wall = "║";
        this.fiveCeilings = "═════";
        this.cornerTopLeft = "╔";
        this.cornerTopRight = "╗";
        this.cornerBottomLeft = "╚";
        this.cornerBottomLeftAlternative = "╩";
        this.cornerBottomRight = "╝";
        this.cornerBottomRightAlternative = "╩";
        this.edgeTop = "╦";
        this.edgeBottom = "╩";
        this.edgeLeft = "╠";
        this.edgeRight = "╣";
        this.cross = "╬";
    }
}
