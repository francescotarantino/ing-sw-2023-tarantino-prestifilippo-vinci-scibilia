package it.polimi.ingsw.model;

import it.polimi.ingsw.distributed.ServerImpl;
import it.polimi.ingsw.exception.PreGameException;
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
    public void testList() throws PreGameException {
        Game game1 = new Game(1, 2, new Player("testPlayer"), 2);
        Game game2 = new Game(2, 4, new Player("testPlayer2"), 1);

        gameList.addGame(game1);

        assertEquals(gameList.getGame(1), game1);
        assertEquals(gameList.getGames().get(0), game1);

        gameList.addGame(game2);

        assertEquals(gameList.getGame(2), game2);
        assertEquals(2, gameList.getGamesDetails().size());

        gameList.removeGame(game2);

        assertEquals(gameList.getGames().size(), 1);
        assertNull(gameList.getGame(2));
    }

    @Test
    void checkListeners() {
        ServerImpl myServer = new ServerImpl();
        gameList.addListener(myServer);
        assertTrue(gameList.lst.contains(myServer));
        gameList.removeListener(myServer);
        assertFalse(gameList.lst.contains(myServer));
    }
    @Test
    void emptyMethodCalls() {
        //Just to cover the empty methods
        gameList.playerJoinedGame();
        gameList.gameEnded();
        gameList.modelChanged();
        gameList.gameIsFull();
    }
}
