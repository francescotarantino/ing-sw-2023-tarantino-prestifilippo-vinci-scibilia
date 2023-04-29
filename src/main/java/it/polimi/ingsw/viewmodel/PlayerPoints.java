package it.polimi.ingsw.viewmodel;

import java.io.Serial;
import java.io.Serializable;

public record PlayerPoints(String username, int points) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return username + ": " + points;
    }
}
