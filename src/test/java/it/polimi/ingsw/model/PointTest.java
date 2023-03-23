package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class PointTest {

    @Test
    void checkPoint(){
        // 9 x 9 max dimension of LivingRoomBoard
        for(int i = 0; i < 9 ; i++){
            for( int j = 0; j < 9; j++){
                Point point = new Point(i,j);
                assertEquals(point.getX(), i);
                assertEquals(point.getY(),j);
            }

        }
    }


}
