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
        int j = 0;
        assertEquals(this.bag.getRemainingTilesQuantity(), bound);
        for (int i = 0; i < bound; i++) {
            if(j == Constants.tilesPerType) j = 0;

            Tile tempTile = this.bag.popTile(0);

            assertEquals(this.bag.getRemainingTilesQuantity(), bound - i - 1);

            assertEquals(tempTile.getVariant(), j % Constants.tileVariants);
            assertEquals(tempTile.getType(), this.array[i / Constants.tilesPerType]);

            j++;
        }
    }
    @Test
    void checkConditions() {
        assertThrows(IndexOutOfBoundsException.class, () -> this.bag.popTile(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> this.bag.popTile(this.bound));
        for(int i = 0; i < bound; i++) {
            this.bag.popTile(0);
        }
        assertThrows(IllegalStateException.class, () -> this.bag.getRandomTile());
    }
}
