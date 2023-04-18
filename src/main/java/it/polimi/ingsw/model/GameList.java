package it.polimi.ingsw.model;

import it.polimi.ingsw.util.Observable;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * This class is used to store the list of games on the server.
 */
public class GameList extends Observable<GameList.Event> {
    public enum Event {
        NEW_GAME,
        REMOVED_GAME,
        PLAYER_JOINED_GAME,
        GAME_IS_FULL,
    }

    private static final ArrayList<Game> games = new ArrayList<>();

    private static GameList instance = null;

    /**
     * GameList fake constructor, so it cannot be instantiated from outside.
     * To get the GameList instance, use the {@link GameList#getInstance()} method.
     */
    protected GameList() {}

    /**
     * GameList singleton instance getter.
     * @return the GameList instance
     */
    public static GameList getInstance() {
        if (instance == null) {
            instance = new GameList();
        }
        return instance;
    }

    /**
     * This method is used to insert a new game in the list of games on the server.
     * @param game the reference to the new Game class
     */
    public void addGame(Game game) {
        games.add(game);
        setChangedAndNotify(Event.NEW_GAME);
    }

    /**
     * This method is used to remove a game from the list of games on the server. It is not currently used.
     * @param game the reference to the Game class to remove
     */
    public void removeGame(Game game) {
        games.remove(game);
        setChangedAndNotify(Event.REMOVED_GAME);
    }

    /**
     * This method is used to get the list of games on the server.
     * @return the list of games on the server
     */
    public ArrayList<Game> getGames() {
        return games;
    }

    /**
     * This method is used to get a game from the list of games on the server.
     * @param gameID the ID of the game to get
     * @return the Game class of the game with the specified ID, null if the game doesn't exist
     */
    public Game getGame(int gameID) {
        return games
                .stream()
                .filter(g -> g.getGameID() == gameID)
                .findFirst()
                .orElse(null);
    }

    /**
     * This method is used to get a textual representation of the games on the server.
     * For each game, it is called the {@link Game#toString()} method.
     * @return an array of strings containing the list of games on the server, null if there are no games
     */
    public String[] getGamesString() throws RemoteException {
        if(games.size() != 0)
            return games
                    .stream()
                    .map(Game::toString)
                    .toArray(String[]::new);
        else
            return null;
    }
}