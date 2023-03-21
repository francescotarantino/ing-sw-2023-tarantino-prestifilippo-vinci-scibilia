package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;

public class Tile {
    private Constants.TileType type;
    private int variant;

    // Default variant is 0 when not specified (useful for placeholders)
    public Tile (Constants.TileType type){
        this.type = type;
        this.variant = 0;
    }

    public Tile (Constants.TileType type, int variant){
        if(variant < 0 | variant > Constants.tileVariants){
            throw new IllegalArgumentException("Tile variant invalid");
        }
        this.type = type;
        this.variant = variant;
    }
    public Tile (Tile oldTile){
        this.type = oldTile.getType();
        this.variant = oldTile.getVariant();
    }
    public Constants.TileType getType(){
        return this.type;
    }
    public int getVariant() {
        return this.variant;
    }
}
