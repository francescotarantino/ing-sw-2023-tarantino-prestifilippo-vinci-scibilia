package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static it.polimi.ingsw.Constants.TileType.*;
import static it.polimi.ingsw.Constants.TileType.CATS;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CGCDiagonalsTest {
    static Tile[][] boardFail, boardPass, boardPass2;
    @BeforeAll
    static void init() {
        boardFail = new Tile[][] {
                {new Tile(PLANTS), new Tile(BOOKS), new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES), new Tile(CATS)},
                {new Tile(PLANTS), new Tile(BOOKS), new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES), new Tile(CATS)},
                {new Tile(PLANTS), new Tile(BOOKS), new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES), new Tile(CATS)},
                {new Tile(PLANTS), new Tile(BOOKS), new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES), new Tile(CATS)},
                {new Tile(PLANTS), new Tile(BOOKS), new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES), new Tile(CATS)},
        };
        boardPass = new Tile[][] {
                {new Tile(CATS), new Tile(BOOKS), new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES), new Tile(CATS)},
                {new Tile(PLANTS), new Tile(CATS), new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES), new Tile(CATS)},
                {new Tile(PLANTS), new Tile(BOOKS), new Tile(CATS), new Tile(FRAMES), new Tile(TROPHIES), new Tile(CATS)},
                {new Tile(PLANTS), new Tile(BOOKS), new Tile(GAMES), new Tile(CATS), new Tile(TROPHIES), new Tile(CATS)},
                {new Tile(PLANTS), new Tile(BOOKS), new Tile(GAMES), new Tile(FRAMES), new Tile(CATS), new Tile(CATS)},
        };
        boardPass2 = new Tile[][] {
                {new Tile(PLANTS), new Tile(BOOKS), new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES), new Tile(CATS)},
                {new Tile(PLANTS), new Tile(BOOKS), new Tile(GAMES), new Tile(FRAMES), new Tile(CATS), new Tile(CATS)},
                {new Tile(PLANTS), new Tile(BOOKS), new Tile(GAMES), new Tile(CATS), new Tile(TROPHIES), new Tile(CATS)},
                {new Tile(PLANTS), new Tile(BOOKS), new Tile(CATS), new Tile(FRAMES), new Tile(TROPHIES), new Tile(CATS)},
                {new Tile(PLANTS), new Tile(CATS), new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES), new Tile(CATS)},
        };
    }
    @Test
    void testCGC7() {
        CommonGoalCard cgc = CommonGoalCard.create(Constants.playersLowerBound, 7);
        assert cgc != null;
        assertFalse(cgc.check(boardFail));
        assertTrue(cgc.check(boardPass));
        assertTrue(cgc.check(boardPass2));
    }
}
