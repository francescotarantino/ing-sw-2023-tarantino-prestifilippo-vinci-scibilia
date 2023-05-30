package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static it.polimi.ingsw.utils.Constants.TileType.*;
import static org.junit.jupiter.api.Assertions.*;

public class CGCCornersTest {
    static Tile[][] boardFail, boardPass, boardNull, boardEmpty;
    @BeforeAll
    static void init() {
        boardFail = new Tile[][] {
                {new Tile(CATS), new Tile(BOOKS), new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES), new Tile(CATS)},
                {new Tile(PLANTS), new Tile(CATS), new Tile(BOOKS), new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES)},
                {new Tile(TROPHIES), new Tile(PLANTS), new Tile(CATS), new Tile(BOOKS), new Tile(GAMES), new Tile(FRAMES)},
                {new Tile(FRAMES), new Tile(TROPHIES), new Tile(PLANTS), new Tile(CATS), new Tile(BOOKS), new Tile(GAMES)},
                {new Tile(CATS), new Tile(FRAMES), new Tile(TROPHIES), new Tile(PLANTS), new Tile(CATS), new Tile(BOOKS)},
        };
        boardPass = new Tile[][] {
                {new Tile(CATS), new Tile(BOOKS), new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES), new Tile(CATS)},
                {new Tile(PLANTS), new Tile(CATS), new Tile(BOOKS), new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES)},
                {new Tile(TROPHIES), new Tile(PLANTS), new Tile(CATS), new Tile(BOOKS), new Tile(GAMES), new Tile(FRAMES)},
                {new Tile(FRAMES), new Tile(TROPHIES), new Tile(PLANTS), new Tile(CATS), new Tile(BOOKS), new Tile(GAMES)},
                {new Tile(CATS), new Tile(FRAMES), new Tile(TROPHIES), new Tile(PLANTS), new Tile(CATS), new Tile(CATS)},
        };
        boardNull = null;
        boardEmpty = new Tile[][] {
                { null, null, null, null, null, null},
                { null, null, null, null, null, null},
                { null, null, null, null, null, null},
                { null, null, null, null, null, null},
                { null, null, null, null, null, null}
        };
    }
    @Test
    void testCGC1() {
        CommonGoalCard cgc = CommonGoalCard.create(Constants.playersLowerBound, 2);

        assertFalse(cgc.check(boardFail));
        assertTrue(cgc.check(boardPass));
        assertThrows(NullPointerException.class, () -> cgc.check(boardNull));
        assertFalse(cgc.check(boardEmpty));
    }
}
