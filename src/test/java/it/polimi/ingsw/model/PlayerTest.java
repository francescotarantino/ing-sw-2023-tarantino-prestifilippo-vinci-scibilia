package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTest {
    @Test
    void checkPlayer(){
        Player player = new Player("TestUsername");
        assertEquals(player.getUsername(), "TestUsername");
        assertEquals(player.getPoints(), 0);
        player.addPoints(3);
        assertEquals(player.getPoints(), 3);
    }
}
