package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static it.polimi.ingsw.Constants.TileType.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CGCTriangularTest {
    static Tile[][] triangular_matrix, triangular_matrix_reverse, non_triangular_matrix;

    @BeforeAll
    static void init() {
        triangular_matrix = new Tile[][]{{
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

        triangular_matrix_reverse = new Tile[][]{{
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
    }

    @Test
    void checkCGC12() {
        CommonGoalCard cgc = CommonGoalCard.create(Constants.playersLowerBound, 12);

        assert cgc != null;
        assertTrue(cgc.check(triangular_matrix_reverse));
        assertTrue(cgc.check(triangular_matrix));
        assertFalse(cgc.check(non_triangular_matrix));
    }
}