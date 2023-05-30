package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlayerTest {
    Player player = new Player("TestUsername");

    @Test
    void checkPlayer(){
        assertEquals(player.getUsername(), "TestUsername");
        assertEquals(player.getPoints(), 0);
    }

    @Test
    void checkAddPoints(){
        player.addPoints(5);
        assertEquals(player.getPoints(), 5);
    }

    @Test
    void checkAddScoringToken(){
        assertThrows(IllegalArgumentException.class, () -> player.addScoringToken(10, 4));
        assertEquals(player.getScoringTokens().size(), 0);

        player.addScoringToken(2, 4);
        assertEquals(player.getScoringTokens().get(0), 2);
    }
    @Test
    void checkToStringOverride(){
        assertEquals(player.toString(), "@TestUsername");
        player.setConnected(false);
        assertEquals(player.toString(), "@TestUsername (disconnected)");
    }
}
