package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

public class BagTest {
    private Bag bag;
    private Constants.TileType[] array;
    private int bound;
    @BeforeEach
    void init(){
        this.bag = new Bag();
        this.array = (Arrays.stream(Constants.TileType.values())
                .filter(x -> x != Constants.TileType.PLACEHOLDER)
                .toArray(Constants.TileType[]::new));
        this.bound = this.array.length * Constants.tilesPerType;
    }
    @Test
    void checkCreationAndRemoval() {
        Tile tempTile;
        Constants.TileType check;
        assertEquals(this.bag.getRemainingTilesQuantity(), bound);
        for (int i = 0; i < bound; i++) {
            tempTile = this.bag.popTile(0);
            assertEquals(tempTile.getVariant(), i % Constants.tileVariants);
            assertEquals(this.bag.getRemainingTilesQuantity(), bound - i - 1);
            check = this.array[i % array.length];
            assertEquals(tempTile.getType(), check);
        }
    }
}
