package it.polimi.ingsw.model;

public class Tile {
    private TileType type;

    public Tile (TileType Ttipe){
        this.type = Ttipe;
    }
    public TileType getType( Tile t){
        return t.type;
    }
}
