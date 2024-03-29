package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static it.polimi.ingsw.utils.Constants.TileType.*;
import static org.junit.jupiter.api.Assertions.*;

public class CGCLinesTest {
    static Tile[][] matrix_rulebook, matrix, aleMatrix, nullMatrix, emptyMatrix;

    @BeforeAll
    static void init(){
        matrix_rulebook = new Tile[][]{{
                new Tile(TROPHIES), new Tile(TROPHIES), new Tile(TROPHIES), new Tile(FRAMES), new Tile(BOOKS), new Tile(PLANTS)
        }, {
                new Tile(TROPHIES), new Tile(TROPHIES), new Tile(GAMES), new Tile(BOOKS), new Tile(PLANTS), new Tile(PLANTS)
        }, {
                new Tile(TROPHIES), new Tile(CATS), new Tile(TROPHIES), new Tile(FRAMES), new Tile(PLANTS), new Tile(PLANTS)
        }, {
                new Tile(CATS), new Tile(CATS), new Tile(GAMES), new Tile(BOOKS), new Tile(CATS), null
        }, {
                new  Tile(CATS), new Tile(CATS), null, null, null, null
        }};

        matrix = new Tile[][]{{
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
        aleMatrix = new Tile[][] {
                {new Tile(BOOKS), new Tile(BOOKS), new Tile(CATS), new Tile(CATS), null, null},
                {new Tile(FRAMES), new Tile(BOOKS), new Tile(CATS), new Tile(PLANTS), new Tile(GAMES), new Tile(TROPHIES)},
                {new Tile(GAMES), new Tile(BOOKS), new Tile(CATS), new Tile(PLANTS), new Tile(FRAMES), new Tile(TROPHIES)},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
        };
        nullMatrix = null;
        emptyMatrix = new Tile[][]{
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
        };
    }

    @Test
    void testCGC5(){
        CommonGoalCard cgc = CommonGoalCard.create(Constants.playersLowerBound, 5);

        assertFalse(cgc.check(matrix_rulebook));
        assertFalse(cgc.check(matrix));
        assertFalse(cgc.check(aleMatrix));
        assertThrows(NullPointerException.class, () -> cgc.check(nullMatrix));
        assertFalse(cgc.check(emptyMatrix));
    }

    @Test
    void testCGC8(){
        CommonGoalCard cgc = CommonGoalCard.create(Constants.playersLowerBound, 8);

        assertFalse(cgc.check(matrix_rulebook));
        assertFalse(cgc.check(matrix));
        assertFalse(cgc.check(aleMatrix));
        assertThrows(NullPointerException.class, () -> cgc.check(nullMatrix));
        assertFalse(cgc.check(emptyMatrix));
    }

    @Test
    void testCGC9(){
        CommonGoalCard cgc = CommonGoalCard.create(Constants.playersLowerBound, 9);

        assertFalse(cgc.check(matrix_rulebook));
        assertTrue(cgc.check(matrix));
        assertTrue(cgc.check(aleMatrix));
        assertThrows(NullPointerException.class, () -> cgc.check(nullMatrix));
        assertFalse(cgc.check(emptyMatrix));
    }

    @Test
    void testCGC10(){
        CommonGoalCard cgc = CommonGoalCard.create(Constants.playersLowerBound, 10);

        assertFalse(cgc.check(matrix_rulebook));
        assertFalse(cgc.check(matrix));
        assertFalse(cgc.check(aleMatrix));
        assertThrows(NullPointerException.class, () -> cgc.check(nullMatrix));
        assertFalse(cgc.check(emptyMatrix));
    }
}
