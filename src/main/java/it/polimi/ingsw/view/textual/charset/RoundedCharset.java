package it.polimi.ingsw.view.textual.charset;

public class RoundedCharset extends Charset {
    public RoundedCharset() {
        this.wall = "│";
        this.fiveCeilings = "─────";
        this.cornerTopLeft = "╭";
        this.cornerTopRight = "╮";
        this.cornerBottomLeft = "╰";
        this.cornerBottomLeftAlternative = "┴";
        this.cornerBottomRight = "╯";
        this.cornerBottomRightAlternative = "┴";
        this.edgeTop = "┬";
        this.edgeBottom = "┴";
        this.edgeLeft = "├";
        this.edgeRight = "┤";
        this.cross = "┼";
    }
}