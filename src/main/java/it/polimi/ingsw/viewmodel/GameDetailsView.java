package it.polimi.ingsw.viewmodel;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * This class is used to send the details of a specific game on the server.
 */
public record GameDetailsView(int gameID,
                              List<PlayerInfo> playersInfo,
                              int numberOfPlayers,
                              int numberOfCommonGoalCards,
                              boolean isFull,
                              boolean isStarted) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append("Game ").append(gameID).append(" - ").append(playersInfo.size()).append("/").append(numberOfPlayers).append(" players\t");
        if(isStarted)
            s.append("STARTED\n");
        else
            s.append("NOT STARTED\n");
        s.append("Players: ");
        playersInfo.forEach(playerInfo -> s.append("\t").append(playerInfo.username()));
        return s.toString();
    }
}