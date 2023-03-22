package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PersonalGoalCardTest {

    // Should we move this to another class?
    // Checks if PGC file is read correctly: every element is a valid TileType and there is no PLACEHOLDER
    @Test
    void checkReadFromFile(){
        assertDoesNotThrow(Constants::getPersonalGoalCards, "Error while reading PGC file");

        for(int i = 0; i < Constants.getPersonalGoalCards().size(); i++){
            for (int j = 0; j < Constants.getPersonalGoalCards().get(i).size(); j++){
                int i1 = i;
                int j1 = j;

                assertDoesNotThrow(() -> {
                    if(Constants.getPersonalGoalCards().get(i1).get(j1) != null)
                        Constants.TileType.valueOf(Constants.getPersonalGoalCards().get(i1).get(j1));
                }, "Error while reading PGC #" + i + " at position " + j);

                assertNotEquals("PLACEHOLDER", Constants.getPersonalGoalCards().get(i).get(j));
            }
        }
    }
}
