package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TileTest {
    @Test
    void checkType(){
        for(int i=0;i<Constants.TileType.values().length;i++){
            Tile myTile = new Tile(Constants.TileType.values()[i]);
            assertEquals(myTile.getType(), Constants.TileType.values()[i]);
        }
    }
}
