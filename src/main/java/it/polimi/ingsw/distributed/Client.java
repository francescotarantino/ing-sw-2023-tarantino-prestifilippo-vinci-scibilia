package it.polimi.ingsw.distributed;

import it.polimi.ingsw.viewmodel.GameView;
import it.polimi.ingsw.viewmodel.GameDetailsView;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Client extends Remote {
    /**
     * This method is called by the server to notify the client of the list of games.
     * @param o the list of games
     */
    void updateGamesList(List<GameDetailsView> o) throws RemoteException;

    /**
     * This method is called by the server when there is an error to show.
     * @param err the error message
     * @param exit if true, the client is asked to exit
     */
    void showError(String err, boolean exit) throws RemoteException;

    /**
     * This method is called by the server when the list of players in the current game changes.
     * @param o the list of players
     */
    void updatePlayersList(List<String> o) throws RemoteException;

    /**
     * This method is called by the server to notify the client that the game it is in has started.
     * It should trigger a UI change.
     */
    void gameHasStarted() throws RemoteException;

    /**
     * This method is called by the server to notify the client that the model has changed.
     * @param gameView the new model-view object of the game
     */
    void modelChanged(GameView gameView) throws RemoteException;
}
