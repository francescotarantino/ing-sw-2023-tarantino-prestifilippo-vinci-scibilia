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

    @Test
    void checkValidityTest(){
        Constants.TileType[][] matrix = {
                {Constants.TileType.PLANTS, null, Constants.TileType.TROPHIES, null, null, null},
                {Constants.TileType.FRAMES, null, null, null, null, null},
                {null, null, null, null, null, null},
                {Constants.TileType.CATS, null, null, null, null, Constants.TileType.GAMES},
                {null, null, null, null, null, Constants.TileType.TROPHIES},
        };
        PersonalGoalCard pgc = new PersonalGoalCard(matrix);

        Tile[][] bookshelf1 = {{
                new Tile(Constants.TileType.PLANTS), new Tile(Constants.TileType.PLANTS), new Tile(Constants.TileType.TROPHIES), new Tile(Constants.TileType.TROPHIES), new Tile(Constants.TileType.TROPHIES), new Tile(Constants.TileType.BOOKS)
        }, {
                new Tile(Constants.TileType.FRAMES), new Tile(Constants.TileType.FRAMES), null, null, null, new Tile(Constants.TileType.BOOKS)
        }, {
                new Tile(Constants.TileType.TROPHIES), null, null, null, null, new Tile(Constants.TileType.BOOKS)
        }, {
                null, null, null, null, null, new Tile(Constants.TileType.GAMES)
        }, {
                null, null, null, null, null, new Tile(Constants.TileType.TROPHIES)
        }};

        assertEquals(Constants.getPersonalGoalCardPoints(5), pgc.checkValidity(bookshelf1));

        Tile[][] bookshelf2 = {{
                new Tile(Constants.TileType.PLANTS), new Tile(Constants.TileType.PLANTS), new Tile(Constants.TileType.TROPHIES), new Tile(Constants.TileType.TROPHIES), new Tile(Constants.TileType.TROPHIES), new Tile(Constants.TileType.TROPHIES)
        }, {
                new Tile(Constants.TileType.FRAMES), new Tile(Constants.TileType.FRAMES), null, null, null, null
        }, {
                new Tile(Constants.TileType.TROPHIES), null, null, null, null, null
        }, {
                null, null, null, null, null, null
        }, {
                null, null, null, null, null, null
        }};

        assertEquals(Constants.getPersonalGoalCardPoints(3), pgc.checkValidity(bookshelf2));

        Tile[][] bookshelf3 = {{
                null, null, null, null, null, null
        }, {
                null, null, null, null, null, null
        }, {
                null, null, null, null, null, null
        }, {
                null, null, null, null, null, null
        }, {
                null, null, null, null, null, null
        }};

        assertEquals(0, pgc.checkValidity(bookshelf3));
    }
}
