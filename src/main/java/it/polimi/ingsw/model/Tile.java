package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;

public class Tile {
    private Constants.TileType type;

    public Tile (Constants.TileType Ttipe){
        this.type = Ttipe;
    }
    public Tile (Tile oldTile){
        this.type = oldTile.getType();
    }
    public Constants.TileType getType(){
        return this.type;
    }
}
