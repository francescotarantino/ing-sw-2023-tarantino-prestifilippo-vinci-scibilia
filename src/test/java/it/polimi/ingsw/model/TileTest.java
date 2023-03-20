package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TileTest {
    @Test
    void checkType(){
        for(int i=0;i<TileType.values().length;i++){
            Tile myTile = new Tile(TileType.values()[i]);
            assertEquals(myTile.getType(),TileType.values()[i]);
        }
    }
}
