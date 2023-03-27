package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TileTest {
    @Test
    void checkType(){
        for(int i=0;i<Constants.TileType.values().length;i++){
            Tile myTile = new Tile(Constants.TileType.values()[i], 1);
            assertEquals(myTile.getType(), Constants.TileType.values()[i]);
            assertEquals(myTile.getVariant(), 1);
        }
    }

    @Test
    void checkEquals(){
        Tile myTile = new Tile(Constants.TileType.values()[0], 1);
        Tile myTile2 = new Tile(Constants.TileType.values()[0], 1);
        assertEquals(myTile, myTile2);

        Tile myTile3 = new Tile(Constants.TileType.values()[0], 2);
        assertNotEquals(myTile, myTile3);
    }
}
