package it.polimi.ingsw.model;

import it.polimi.ingsw.listeners.GameListListener;
import it.polimi.ingsw.viewmodel.GameDetailsView;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.listeners.Listener.notifyListeners;

/**
 * This class is used to store the list of games on the server.
 */
public class GameList {

    private final List<GameListListener> lst = new ArrayList<>();

    private static final List<Game> games = new ArrayList<>();

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
        notifyListeners(lst, GameListListener::newGame);
    }

    /**
     * This method is used to remove a game from the list of games on the server. It is not currently used.
     * @param game the reference to the Game class to remove
     */
    public void removeGame(Game game) {
        games.remove(game);
        notifyListeners(lst, GameListListener::removedGame);
    }

    /**
     * This method is used to get the list of games on the server.
     * @return the list of games on the server
     */
    public List<Game> getGames() {
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
    @Deprecated
    public String[] getGamesString() throws RemoteException {
        if(games.size() != 0)
            return games
                    .stream()
                    .map(Game::toString)
                    .toArray(String[]::new);
        else
            return null;
    }

    /**
     * This method is used to get a list of {@link GameDetailsView} objects representing the games on the server.
     * @see GameDetailsView
     * @return the list of {@link GameDetailsView} objects
     */
    public List<GameDetailsView> getGamesDetails() {
        return games
                .stream()
                .map(x -> new GameDetailsView(x.getGameID(), x.playersList(), x.getTotalPlayersNumber(), x.getCommonGoalCards().length, x.isStarted(), x.isFull()))
                .toList();
    }

    public void addListener(GameListListener o){
        if(!lst.contains(o)){
            lst.add(o);
        }
    }

    public void removeListener(GameListListener o){
        lst.remove(o);
    }

    public synchronized void notifyPlayerJoinedGame(int gameID) {
        notifyListeners(lst, gameListListener -> gameListListener.playerJoinedGame(gameID));
    }

    public synchronized void notifyGameFull(int gameID) {
        notifyListeners(lst, gameListListener -> gameListListener.gameIsFull(gameID));
    }
}
