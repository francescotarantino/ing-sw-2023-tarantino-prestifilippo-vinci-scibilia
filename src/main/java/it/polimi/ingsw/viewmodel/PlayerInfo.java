package it.polimi.ingsw.viewmodel;


import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.model.Tile;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record PlayerInfo(String username, int points, List<Integer> tokens,
                         Point[] lastMovePoints, Tile[] lastMoveTiles,
                         boolean isConnected, boolean isLast) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return username + ": " + points;
    }
    public String getTokensString(){
        String temp = "";
        for(int token : tokens){
            temp = temp.concat("[" + token + "]");
        }
        return temp;
    }
}
