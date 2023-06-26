package it.polimi.ingsw.viewmodel;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * This class is used to send the details of a specific game to the client.
 */
public record GameDetailsView(int gameID,
                              List<PlayerInfo> playersInfo,
                              int numberOfPlayers,
                              int numberOfCommonGoalCards,
                              boolean isFull,
                              boolean isStarted) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}