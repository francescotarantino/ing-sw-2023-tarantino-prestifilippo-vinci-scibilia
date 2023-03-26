package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommonGoalCardTest {
    CommonGoalCard cgc2, cgc3, cgc4;

    @BeforeEach
    void init() {
        cgc2 = new CommonGoalCard(2, -1){
            @Override
            public boolean check(Tile[][] matrix){
                return true;
            }
        };

        cgc3 = new CommonGoalCard(3, -1){
            @Override
            public boolean check(Tile[][] matrix){
                return true;
            }
        };

        cgc4 = new CommonGoalCard(4, -1){
            @Override
            public boolean check(Tile[][] matrix){
                return true;
            }
        };
    }

    @Test
    void getAvailableScoresTest() {
        assertEquals(2, cgc2.getAvailableScores().length);
        assertEquals(3, cgc3.getAvailableScores().length);
        assertEquals(4, cgc4.getAvailableScores().length);

        assertArrayEquals(Constants.getScoringTokens(2), cgc2.getAvailableScores());
        assertArrayEquals(Constants.getScoringTokens(3), cgc3.getAvailableScores());
        assertArrayEquals(Constants.getScoringTokens(4), cgc4.getAvailableScores());

        assertEquals(Constants.getScoringTokens(2)[1], cgc2.takeScore());
        assertEquals(Constants.getScoringTokens(2)[0], cgc2.takeScore());
        assertEquals(0, cgc2.getAvailableScores().length);

        assertEquals(Constants.getScoringTokens(3)[2], cgc3.takeScore());
        assertEquals(Constants.getScoringTokens(3)[1], cgc3.takeScore());
        assertEquals(Constants.getScoringTokens(3)[0], cgc3.takeScore());
        assertEquals(0, cgc3.getAvailableScores().length);

        assertEquals(Constants.getScoringTokens(4)[3], cgc4.takeScore());
        assertEquals(Constants.getScoringTokens(4)[2], cgc4.takeScore());
        assertEquals(Constants.getScoringTokens(4)[1], cgc4.takeScore());
        assertEquals(Constants.getScoringTokens(4)[0], cgc4.takeScore());
        assertEquals(0, cgc4.getAvailableScores().length);
    }
}
