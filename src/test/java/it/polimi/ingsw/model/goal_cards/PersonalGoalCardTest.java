package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.utils.GameUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PersonalGoalCardTest {

    /**
     * Checks if PGC file is read correctly: every element is a valid TileType, and there are no PLACEHOLDERs.
     */
    @Test
    void checkReadFromFile(){
        assertDoesNotThrow(GameUtils::getPersonalGoalCards, "Error while reading PGC file");

        for(int i = 0; i < GameUtils.getPersonalGoalCards().size(); i++){
            Map<String, ?> data = GameUtils.getPersonalGoalCards().get(i);

            // Check if the image path is present
            assertNotNull(data.get("image_path"));

            // Check if the matrix is valid
            for (int j = 0; j < GameUtils.getPersonalGoalCards().get(i).size(); j++){
                List<?> rawList = (List<?>) data.get("matrix");
                List<String> matrix = new ArrayList<>();
                for (Object obj : rawList) {
                    if (obj instanceof String || obj == null) {
                        matrix.add((String) obj);
                    } else {
                        throw new RuntimeException("Invalid JSON file.");
                    }
                }

                int finalJ = j;
                assertDoesNotThrow(() -> {
                    if(matrix.get(finalJ) != null)
                        Constants.TileType.valueOf(matrix.get(finalJ));
                }, "Error while reading PGC #" + i + " at position " + j);

                assertNotEquals("PLACEHOLDER", matrix.get(j));
            }
        }
    }

    @Test
    void pgcTest(){
        Constants.TileType[][] matrix = {
                {Constants.TileType.PLANTS, null, Constants.TileType.TROPHIES, null, null, null},
                {Constants.TileType.FRAMES, null, null, null, null, null},
                {null, null, null, null, null, null},
                {Constants.TileType.CATS, null, null, null, null, Constants.TileType.GAMES},
                {null, null, null, null, null, Constants.TileType.TROPHIES},
        };
        PersonalGoalCard pgc = new PersonalGoalCard(matrix);

        assertEquals(matrix, pgc.getMatrix());
        assertNull(pgc.getImagePath());

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

        assertEquals(GameUtils.getPersonalGoalCardPoints(5), pgc.checkValidity(bookshelf1));

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

        assertEquals(GameUtils.getPersonalGoalCardPoints(3), pgc.checkValidity(bookshelf2));

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
