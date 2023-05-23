package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TileTest {
    @Test
    void checkType(){
        for(int i=0;i<Constants.TileType.values().length;i++){
            Tile myTile = new Tile(Constants.TileType.values()[i], 1);
            assertEquals(myTile.getType(), Constants.TileType.values()[i]);
            assertEquals(myTile.getVariant(), 1);
            Tile mySecondTile = new Tile(myTile);
            assertEquals(mySecondTile.getType(), Constants.TileType.values()[i]);
            assertEquals(mySecondTile.getVariant(), 1);
        }
    }

    @Test
    void checkEquals(){
        Tile myTile = new Tile(Constants.TileType.values()[0], 1);
        Tile myTile2 = new Tile(Constants.TileType.values()[0], 1);
        assertEquals(myTile, myTile2);
        assertEquals(myTile, myTile);

        Tile myTile3 = new Tile(Constants.TileType.values()[0], 2);
        assertNotEquals(myTile, myTile3);
        assertNotEquals(myTile, new Object());
    }

    @Test
    void checkGetImagePath(){
        Tile myTile = new Tile(Constants.TileType.PLACEHOLDER);
        String path = myTile.getImagePath();
        assertNull(path);
        Tile mySecondTile = new Tile(Constants.TileType.CATS);
        String secondPath = mySecondTile.getImagePath();
    }

    @Test
    void checkToString(){
        Tile myTile = new Tile(Constants.TileType.PLACEHOLDER);
        Tile mySecondTile = new Tile(Constants.TileType.CATS);
        assertEquals(myTile.toString()," ");
        assertEquals(mySecondTile.toString(),Constants.TileType.CATS.toString());

    }
}
