package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameListTest {
    GameList gameList;

    @BeforeEach
    void init() {
        gameList = GameList.getInstance();
    }

    @Test
    public void testList() {
        Game game1 = new Game(1, 2, new Player("testPlayer"), 2);
        Game game2 = new Game(2, 4, new Player("testPlayer2"), 1);

        gameList.addGame(game1);

        assertEquals(gameList.getGame(1), game1);
        assertEquals(gameList.getGames().get(0), game1);

        gameList.addGame(game2);

        assertEquals(gameList.getGame(2), game2);

        gameList.removeGame(game2);

        assertEquals(gameList.getGames().size(), 1);
        assertNull(gameList.getGame(2));
    }
}
