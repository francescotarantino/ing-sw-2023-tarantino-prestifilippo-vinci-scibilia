package it.polimi.ingsw.model;

import it.polimi.ingsw.utils.Constants;
import javafx.scene.input.DataFormat;

import java.io.Serial;
import java.io.Serializable;

public class Tile implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Constants.TileType type;
    private final int variant;
    public static final DataFormat tileFormat = new DataFormat("it.polimi.ingsw.model.Tile");

    /**
     * Creates a new tile with the specified type. The variant is set to 0 (useful for placeholders).
     * @param type the type of the new tile
     */
    public Tile (Constants.TileType type){
        this.type = type;
        this.variant = 0;
    }

    /**
     * Creates a new tile with the specified type and variant.
     * @param type the type of the new tile
     * @param variant the variant of the new tile
     */
    public Tile (Constants.TileType type, int variant){
        if(variant < 0 || variant > Constants.tileVariants){
            throw new IllegalArgumentException("Tile variant invalid");
        }
        this.type = type;
        this.variant = variant;
    }

    /**
     * Creates a new tile with the same type and variant of the specified tile.
     * @param oldTile the tile to be copied
     */
    public Tile (Tile oldTile){
        this.type = oldTile.getType();
        this.variant = oldTile.getVariant();
    }

    /**
     * This method returns the type of the tile.
     */
    public Constants.TileType getType(){
        return this.type;
    }

    /**
     * This method returns the variant of the tile.
     */
    public int getVariant() {
        return this.variant;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Tile tile)) {
            return false;
        }
        return tile.getType() == this.type && tile.getVariant() == this.variant;
    }

    /**
     * This method confronts the current tile type with another one passed as argument
     * @param t tile to confront with
     */
    public boolean sameType(Tile t) {
        return t.type.equals(this.type);
    }

    /**
     * @return true if the tile is a PLACEHOLDER, false otherwise
     */
    public boolean isPlaceholder() {
        return this.type == Constants.TileType.PLACEHOLDER;
    }


    /**
     * @return the path of the tile image, based on its type and variant
     */
    public String getImagePath() {
        if(!isPlaceholder()) {
            return "/images/tiles/" + this.type.toString().toLowerCase() + "_" + (this.variant + 1) + ".png";
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        if(this.type == Constants.TileType.PLACEHOLDER)
            return " ";
        else
            return type.toString();
    }
}
