package it.polimi.ingsw.listeners;

/**
 * This interface is used to implement the communication between the {@link it.polimi.ingsw.view.StartUI}
 * and the {@link it.polimi.ingsw.distributed.ClientImpl} classes.
 */
public interface StartUIListener extends Listener {
    /**
     * This method is invoked when the startUI needs a refresh of the current game list.
     */
    void refreshStartUI();

    /**
     * This method is invoked when startUI wants to create a new game.
     */
    void createGame(int numberOfPlayers, int numberOfCommonGoalCards, String username);

    /**
     * This method is invoked when startUI wants to join a game.
     */
    void joinGame(int gameID, String username);

    /**
     * This method is invoked when startUI wants to exit.
     */
    void exit();
}
