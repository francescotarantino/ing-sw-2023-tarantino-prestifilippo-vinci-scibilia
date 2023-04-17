package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Tile;
import static it.polimi.ingsw.Constants.TileType.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class CGCGroupsOfEqualsTest {
    static Tile[][]
            board1, //contains no groups
            board2, //contains 6 groups of two, but no groups of four, nor squares
            board3, //contains 7 groups, that are enough to have both six of two and four of four
            board4; //contains two squares, and nothing else

    @BeforeAll
    static void init() {
        board1 = new Tile[][] {
            {new Tile(CATS), new Tile(BOOKS), new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES), new Tile(PLANTS)},
            {new Tile(PLANTS), new Tile(CATS), new Tile(BOOKS), new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES)},
            {new Tile(TROPHIES), new Tile(PLANTS), new Tile(CATS), new Tile(BOOKS), new Tile(GAMES), new Tile(FRAMES)},
            {new Tile(FRAMES), new Tile(TROPHIES), new Tile(PLANTS), new Tile(CATS), new Tile(BOOKS), new Tile(GAMES)},
            {new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES), new Tile(PLANTS), new Tile(CATS), new Tile(BOOKS)},
        };

        board2 = new Tile[][] {
            {new Tile(CATS), new Tile(BOOKS), new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES), new Tile(PLANTS)},
            {new Tile(PLANTS), new Tile(CATS), new Tile(BOOKS), new Tile(GAMES), new Tile(FRAMES), new Tile(PLANTS)},
            {new Tile(FRAMES), new Tile(PLANTS), new Tile(CATS), new Tile(GAMES), new Tile(GAMES), new Tile(FRAMES)},
            {new Tile(FRAMES), new Tile(TROPHIES), new Tile(PLANTS), new Tile(CATS), new Tile(BOOKS), new Tile(GAMES)},
            {new Tile(GAMES), new Tile(FRAMES), new Tile(FRAMES), new Tile(PLANTS), new Tile(BOOKS), new Tile(GAMES)},
        };

        board3 = new Tile[][] {
            {new Tile(CATS), new Tile(BOOKS), new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES), new Tile(PLANTS)},
            {new Tile(CATS), new Tile(PLANTS), new Tile(CATS), new Tile(BOOKS), new Tile(BOOKS), new Tile(PLANTS)},
            {new Tile(CATS), new Tile(PLANTS), new Tile(FRAMES), new Tile(BOOKS), new Tile(BOOKS), new Tile(FRAMES)},
            {new Tile(CATS), new Tile(PLANTS), new Tile(FRAMES), new Tile(CATS), new Tile(CATS), new Tile(GAMES)},
            {new Tile(GAMES), new Tile(PLANTS), new Tile(TROPHIES), new Tile(TROPHIES), new Tile(TROPHIES), new Tile(TROPHIES)},
        };

        board4 = new Tile[][] {
            {new Tile(CATS), new Tile(BOOKS), new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES), new Tile(PLANTS)},
            {new Tile(PLANTS), new Tile(PLANTS), new Tile(CATS), new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES)},
            {new Tile(PLANTS), new Tile(PLANTS), new Tile(CATS), new Tile(PLANTS), new Tile(PLANTS), new Tile(FRAMES)},
            {new Tile(FRAMES), new Tile(TROPHIES), new Tile(TROPHIES), new Tile(PLANTS), new Tile(PLANTS), new Tile(GAMES)},
            {new Tile(GAMES), new Tile(FRAMES), new Tile(TROPHIES), new Tile(BOOKS), new Tile(CATS), new Tile(CATS)},
        };
    }
    @Test
    void testCGC1() {
        CommonGoalCard cgc = CommonGoalCard.create(Constants.playersLowerBound, 1);
        assertFalse(cgc.check(board1));
        assertTrue(cgc.check(board2));
        assertTrue(cgc.check(board3));
        assertFalse(cgc.check(board4));
    }
    @Test
    void testCGC3() {
        CommonGoalCard cgc = CommonGoalCard.create(Constants.playersLowerBound, 3);
        assertFalse(cgc.check(board1));
        assertFalse(cgc.check(board2));
        assertTrue(cgc.check(board3));
        assertFalse(cgc.check(board4));
    }
    @Test
    void testCGC4() {
        CommonGoalCard cgc = CommonGoalCard.create(Constants.playersLowerBound, 4);
        assertFalse(cgc.check(board1));
        assertFalse(cgc.check(board2));
        assertFalse(cgc.check(board3));
        assertTrue(cgc.check(board4));
    }
}

