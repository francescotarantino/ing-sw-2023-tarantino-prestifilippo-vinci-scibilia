package it.polimi.ingsw.model;

public class Tile {
    private TileType type;

    public Tile (TileType Ttipe){
        this.type = Ttipe;
    }
    public Tile (Tile oldTile){
        this.type = oldTile.getType();
    }
    public TileType getType(){
        return this.type;
    }
}
