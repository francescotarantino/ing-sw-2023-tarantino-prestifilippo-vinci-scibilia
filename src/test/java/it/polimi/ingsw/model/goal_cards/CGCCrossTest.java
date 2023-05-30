package it.polimi.ingsw.model.goal_cards;

import static it.polimi.ingsw.utils.Constants.TileType.*;
import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class CGCCrossTest {
    static Tile[][] matrix_with_cross, matrix_without_cross, matrix_null, matrix_empty;

    @BeforeAll
    static void init() {
        matrix_with_cross = new Tile[][]{{
                new Tile(TROPHIES), new Tile(TROPHIES), new Tile(TROPHIES), new Tile(FRAMES), new Tile(BOOKS), new Tile(PLANTS)
        }, {
                new Tile(TROPHIES), new Tile(TROPHIES), new Tile(GAMES), new Tile(BOOKS), new Tile(PLANTS), new Tile(PLANTS)
        }, {
                new Tile(TROPHIES), new Tile(CATS), new Tile(TROPHIES), new Tile(FRAMES), new Tile(PLANTS), new Tile(PLANTS)
        }, {
                new Tile(CATS), new Tile(CATS), new Tile(GAMES), new Tile(BOOKS), new Tile(CATS), null
        }, {
                new Tile(CATS), new Tile(CATS), null, null, null, null
        }};

        matrix_without_cross = new Tile[][]{{
                new Tile(TROPHIES), new Tile(CATS), new Tile(PLANTS), new Tile(FRAMES), new Tile(BOOKS), new Tile(GAMES)
        }, {
                new Tile(TROPHIES), new Tile(TROPHIES), new Tile(TROPHIES), new Tile(FRAMES), new Tile(BOOKS), new Tile(BOOKS)
        }, {
                new Tile(TROPHIES), new Tile(TROPHIES), new Tile(TROPHIES), new Tile(FRAMES), new Tile(BOOKS), new Tile(BOOKS)
        }, {
                new Tile(CATS), new Tile(CATS), new Tile(GAMES), new Tile(BOOKS), new Tile(CATS), null
        }, {
                new Tile(GAMES), new Tile(CATS), new Tile(PLANTS), new Tile(FRAMES), new Tile(BOOKS), new Tile(TROPHIES)
        }};
        matrix_null = null;
        matrix_empty = new Tile[][]{
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
        };
    }

    @Test
    void checkCGC11() {
        CommonGoalCard cgc = CommonGoalCard.create(Constants.playersLowerBound, 11);

        assertTrue(cgc.check(matrix_with_cross));
        assertFalse(cgc.check(matrix_without_cross));
        assertThrows(NullPointerException.class, () -> cgc.check(matrix_null));
        assertFalse(cgc.check(matrix_empty));
    }
}
