package it.polimi.ingsw.model;

import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.utils.Utils;
import it.polimi.ingsw.exception.InvalidChoiceException;
import it.polimi.ingsw.exception.NoFreeBookshelfException;
import it.polimi.ingsw.exception.PreGameException;
import it.polimi.ingsw.exception.UsernameTakenException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class GameTest {
    private Game game;
    @Test
    void checkConstruction(){
        //Checks if the constructor correctly throws an exception in the following cases:
        //The program tries to create a Game with less than the minimum amount of Players
        assertThrows(InvalidChoiceException.class, () -> this.game = new Game(1,1,new Player("TestName1"),1));
        //The program tries to create a Game with more than the maximum amount of Players
        assertThrows(InvalidChoiceException.class, () -> this.game = new Game(1,5,new Player("TestName1"),1));
        //The program tries to create a Game with an ID smaller than the smallest accepted one
        assertThrows(IllegalArgumentException.class, () -> this.game = new Game(0,4,new Player("TestName1"),1));
        //The program tries to create a Game with less than the minimum amount of CommonGoalCards
        assertThrows(InvalidChoiceException.class, () -> this.game = new Game(1,3,new Player("TestName1"),0));
        //The program tries to create a Game with more than the maximum amount of CommonGoalCards
        assertThrows(InvalidChoiceException.class, () -> this.game = new Game(1,3,new Player("TestName1"),3));
    }

    @Test
    void checkAddBookshelf() throws PreGameException {
        //Checks if the addBookshelf works correctly in the following situations:
        //A valid game is created, and a number of non-null bookshelves is expected
        this.game = new Game(1,4,new Player("TestName1"),2);
        int c = 0;
        for(int i=0; i<this.game.getTotalPlayersNumber(); i++){
            if(this.game.getBookshelves()[i] != null) {
                c++;
            }
        }
        assertEquals(c,1);
        //Checks if an exception is thrown by trying to create a new Bookshelf with a duplicate username
        assertThrows(UsernameTakenException.class, () -> this.game.addBookshelf(new Player("TestName1")));
        //Checks if the amount of non-null bookshelves is consistent with the addition of new bookshelves
        assertDoesNotThrow(() -> this.game.addBookshelf(new Player("TestName2")));
        assertDoesNotThrow(() -> this.game.addBookshelf(new Player("TestName3")));
        assertDoesNotThrow(() -> this.game.addBookshelf(new Player("TestName4")));
        c = 0;
        for(int i=0; i<this.game.getTotalPlayersNumber(); i++){
            if(this.game.getBookshelves()[i] != null) {
                c++;
            }
        }
        assertEquals(c,4);
        //Checks if an exception is thrown when trying to add more bookshelves than the current Game supports
        assertThrows(NoFreeBookshelfException.class, () -> this.game.addBookshelf(new Player("TestName5")));
    }

    @Test
    void checkGetters() throws PreGameException {
        //Checks if all the getters return the expected results on a valid new Game
        this.game = new Game (1,4,new Player("TestName1"),2);
        assertNotNull(this.game.getBookshelves());
        assertNotNull(this.game.getBag());
        assertNotNull(this.game.getLivingRoomBoard());
        assertEquals(this.game.getBookshelves().length, this.game.getTotalPlayersNumber());
        assertTrue(this.game.getGameID() > Constants.IDLowerBound);
        assertNotNull(this.game.getCommonGoalCards());
        assertNull(this.game.getCurrentPlayer());
        assertFalse(this.game.getFirstPlayerIndex() > this.game.getTotalPlayersNumber()
                || this.game.getFirstPlayerIndex() < 0);
    }

    @Test
    void checkRandomGeneration(){
        /* Checks if the method "extractRandomIDsWithoutDuplicates" correctly returns an adequate number of random numbers
            in the specified boundary with no repetitions */
        int[] test;
        //the method is Static, so it is not necessary to create an instance of Game to test it
        test = Utils.extractRandomIDsWithoutDuplicates(4,10);
        boolean check = true;
        assertEquals(test.length,4);
        for(int i=0; i<4; i++){
            for(int j = i+1; j<4; j++){
                if(test[i] == test[j]){
                    check = false;
                    break;
                }
            }
        }
        assertTrue(check);
    }

    @Test
    void checkSetCurrentPlayerIndex() throws PreGameException {
        //Tests the "setCurrentPlayerIndex" method
        this.game = new Game (1,4,new Player("TestName1"),2);
        int t1 = this.game.getCurrentPlayerIndex();
        int t2;
        if(t1>0)
            t2 = 0;
        else
            t2 = 1;
        this.game.setCurrentPlayerIndex(t2);
        t2 = this.game.getCurrentPlayerIndex();
        assertNotEquals(t1,t2);
    }
}

