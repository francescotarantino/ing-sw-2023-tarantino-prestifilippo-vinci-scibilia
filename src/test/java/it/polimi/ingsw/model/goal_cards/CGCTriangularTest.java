package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static it.polimi.ingsw.utils.Constants.TileType.*;
import static org.junit.jupiter.api.Assertions.*;

public class CGCTriangularTest {
    static Tile[][] triangular_matrix_ascending, triangular_matrix_descending, non_triangular_matrix, matrix_null, matrix_empty;

    @BeforeAll
    static void init() {
        triangular_matrix_ascending = new Tile[][]{{
                new Tile(CATS), null, null, null, null, null,
        }, {
                new Tile(CATS), new Tile(CATS), null, null, null, null
        }, {
                new Tile(PLANTS), new Tile(PLANTS), new Tile(PLANTS), null, null, null
        }, {
                new Tile(FRAMES), new Tile(FRAMES), new Tile(CATS), new Tile(CATS), null, null
        }, {
                new Tile(CATS), new Tile(BOOKS), new Tile(CATS), new Tile(CATS), new Tile(PLANTS), null
        }};

        triangular_matrix_descending = new Tile[][]{{
                new Tile(CATS), new Tile(BOOKS), new Tile(CATS), new Tile(CATS), new Tile(PLANTS), null
        }, {
                new Tile(CATS), new Tile(CATS), new Tile(FRAMES), new Tile(FRAMES), null, null
        }, {
                new Tile(PLANTS), new Tile(PLANTS), new Tile(PLANTS), null, null, null
        }, {
                new Tile(FRAMES), new Tile(FRAMES), null, null, null, null
        }, {
                new Tile(CATS), null, null, null, null, null
        }};

        non_triangular_matrix = new Tile[][]{{
                new Tile(CATS), new Tile(BOOKS), new Tile(CATS), new Tile(CATS), new Tile(PLANTS), new Tile(BOOKS)
        }, {
                new Tile(CATS), null, null, null, null, null
        }, {
                new Tile(PLANTS), new Tile(PLANTS), new Tile(PLANTS), new Tile(PLANTS), null, null
        }, {
                new Tile(FRAMES), new Tile(FRAMES), null, null, null, null
        }, {
                new Tile(CATS), new Tile(CATS), new Tile(FRAMES), new Tile(FRAMES), new Tile(CATS), null
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
    void checkCGC12() {
        CommonGoalCard cgc = CommonGoalCard.create(Constants.playersLowerBound, 12);

        assertTrue(cgc.check(triangular_matrix_ascending));
        assertTrue(cgc.check(triangular_matrix_descending));
        assertFalse(cgc.check(non_triangular_matrix));
        assertThrows(NullPointerException.class, () -> cgc.check(matrix_null));
        assertFalse(cgc.check(matrix_empty));
    }
}
