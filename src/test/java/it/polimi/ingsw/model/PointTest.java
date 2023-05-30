package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PointTest {

    @Test
    void checkPoint(){
        // 9 x 9 max dimension of LivingRoomBoard
        for(int i = 0; i < Constants.livingRoomBoardX; i++){
            for(int j = 0; j < Constants.livingRoomBoardY; j++){
                Point point = new Point(i, j);
                assertEquals(point.x(), i);
                assertEquals(point.y(), j);
                assertEquals("[" + i + "," + j + "]", point.toString());
            }

        }
    }

    @Test
    void checkEquals(){
        Point point1 = new Point(0, 6);
        Point point2 = new Point(0, 6);
        LivingRoomBoard livingRoomBoard = new LivingRoomBoard(2);
        assertEquals(point1, point1);
        assertEquals(point1, point2);
        assertNotEquals(point1, livingRoomBoard);

        Point point3 = new Point(1, 6);
        assertNotEquals(point1, point3);
    }
}
