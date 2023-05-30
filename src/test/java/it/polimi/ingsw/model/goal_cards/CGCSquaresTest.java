package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static it.polimi.ingsw.utils.Constants.TileType.*;
import static org.junit.jupiter.api.Assertions.*;


public class CGCSquaresTest {
    static Tile[][]
            board1, //contains no groups
            boardSquares, //contains two squares of PLANTS, neatly separated
            boardSquares2, //contains two squares of PLANTS, one of which has an extra PLANT next to it
            boardAdjacentSquares1, //contains two squares of PLANTS, adjacent to each other
            boardAdjacentSquares2, //contains two squares, one of PLANTS and one of CATS, adjacent to each other
            boardNull,
            boardEmpty;


    @BeforeAll
    static void init() {
        board1 = new Tile[][] {
                {new Tile(CATS), new Tile(BOOKS), new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES), new Tile(PLANTS)},
                {new Tile(PLANTS), new Tile(CATS), new Tile(BOOKS), new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES)},
                {new Tile(TROPHIES), new Tile(PLANTS), new Tile(CATS), new Tile(BOOKS), new Tile(GAMES), new Tile(FRAMES)},
                {new Tile(FRAMES), new Tile(TROPHIES), new Tile(PLANTS), new Tile(CATS), new Tile(BOOKS), new Tile(GAMES)},
                {new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES), new Tile(PLANTS), new Tile(CATS), new Tile(BOOKS)},
        };

        boardSquares = new Tile[][] {
                {new Tile(GAMES), null, null, null, null, null},
                {new Tile(PLANTS), new Tile(PLANTS), new Tile(CATS), new Tile(GAMES), new Tile(FRAMES), null},
                {new Tile(PLANTS), new Tile(PLANTS), new Tile(CATS), new Tile(PLANTS), new Tile(PLANTS), new Tile(FRAMES)},
                {new Tile(FRAMES), new Tile(TROPHIES), new Tile(TROPHIES), new Tile(PLANTS), new Tile(PLANTS), new Tile(GAMES)},
                {null, null, null, null, null, null},
        };
        boardSquares2 = new Tile[][] {
                {new Tile(CATS), new Tile(PLANTS), new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES), new Tile(PLANTS)},
                {new Tile(PLANTS), new Tile(PLANTS), new Tile(CATS), new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES)},
                {new Tile(PLANTS), new Tile(PLANTS), new Tile(CATS), new Tile(PLANTS), new Tile(PLANTS), new Tile(FRAMES)},
                {new Tile(FRAMES), new Tile(TROPHIES), new Tile(TROPHIES), new Tile(PLANTS), new Tile(PLANTS), new Tile(GAMES)},
                {new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES), new Tile(BOOKS), new Tile(CATS), new Tile(CATS)},
        };
        boardAdjacentSquares1 = new Tile[][] {
                {new Tile(CATS), new Tile(BOOKS), new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES), new Tile(PLANTS)},
                {new Tile(PLANTS), new Tile(PLANTS), new Tile(CATS), new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES)},
                {new Tile(PLANTS), new Tile(PLANTS), new Tile(PLANTS), new Tile(PLANTS), new Tile(GAMES), new Tile(FRAMES)},
                {new Tile(FRAMES), new Tile(TROPHIES), new Tile(PLANTS), new Tile(PLANTS), new Tile(FRAMES), new Tile(GAMES)},
                {new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES), new Tile(BOOKS), new Tile(CATS), new Tile(CATS)},
        };
        boardAdjacentSquares2 = new Tile[][] {
                {new Tile(CATS), new Tile(BOOKS), new Tile(PLANTS), new Tile(FRAMES), new Tile(TROPHIES), new Tile(PLANTS)},
                {new Tile(PLANTS), new Tile(PLANTS), new Tile(GAMES), null, null, null},
                {new Tile(PLANTS), new Tile(PLANTS), new Tile(CATS), new Tile(CATS), new Tile(GAMES), null},
                {new Tile(FRAMES), new Tile(TROPHIES), new Tile(CATS), new Tile(CATS), null, null},
                {null, null, null, null, null, null},
        };
        boardNull = null;
        boardEmpty = new Tile[][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
        };
    }
    @Test
    void testCGC4() {
        CommonGoalCard cgc = CommonGoalCard.create(Constants.playersLowerBound, 4);
        assertFalse(cgc.check(board1));
        assertTrue(cgc.check(boardSquares));
        assertFalse(cgc.check(boardSquares2));
        assertFalse(cgc.check(boardAdjacentSquares1));
        assertFalse(cgc.check(boardAdjacentSquares2));
        assertThrows(NullPointerException.class, () -> cgc.check(boardNull));
        assertFalse(cgc.check(boardEmpty));
    }
}
