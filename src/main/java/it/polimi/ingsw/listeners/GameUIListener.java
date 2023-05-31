package it.polimi.ingsw.listeners;

import it.polimi.ingsw.model.Point;

/**
 * This interface is used to implement the communication between the {@link it.polimi.ingsw.view.GameUI}
 * and the {@link it.polimi.ingsw.distributed.ClientImpl} classes.
 */
public interface GameUIListener extends Listener {
    /**
     * This method is called when the GameUI wants to perform a turn.
     * @param column the column where to put the tiles
     * @param points the points of the tiles from the living room board
     */
    void performTurn(int column, Point...points);

    /**
     * This method is invoked when gameUI wants to exit.
     */
    void exit();
}
