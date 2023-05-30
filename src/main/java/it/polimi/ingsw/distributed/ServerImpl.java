package it.polimi.ingsw.distributed;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.exception.GameException;
import it.polimi.ingsw.exception.InvalidChoiceException;
import it.polimi.ingsw.exception.PreGameException;
import it.polimi.ingsw.listeners.GameListener;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameList;
import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.viewmodel.GameView;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.listeners.GameListListener;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class is the implementation of the {@link Server} interface.
 * Each ServerImpl instance is associated to a specific client, and it is used to handle all the requests coming
 * from that client.
 * It is also a {@link GameList} and a {@link GameListListener}, so it is notified of all model changes and can notify the client
 * accordingly.
 *
 * @see Server
 * @see PingPongThread
 */
public class ServerImpl implements Server, GameListListener, GameListener {
    /**
     * The executor service used to run all the ping-pong threads.
     */
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * This scheduler is used to handle paused games.
     */
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    protected Game model;
    protected Controller controller;
    protected Client client;
    /**
     * Index of the player served by this ServerImpl.
     */
    protected int playerIndex;
    /**
     * The ping-pong thread reference used to check if the client is still connected.
     */
    private final PingPongThread pingpongThread = new PingPongThread(this);

    /**
     * This method saves the instance of the client that is registering to the server and starts the ping-pong thread.
     * It also adds this ServerImpl to the GameListListener list, so the client will be notified when the list of games changes.
     * @param client the client to register
     */
    @Override
    public void register(Client client) throws RemoteException {
        this.client = client;
        GameList.getInstance().addListener(this);

        System.out.println("A client is registering to the server...");

        executor.submit(this.pingpongThread);
    }

    /**
     * This method is called when a player wants to join a game.
     * It will also handle the case where a player is reconnecting to a game in which he was already playing.
     *
     * @see Server#addPlayerToGame(int, String)
     */
    @Override
    public void addPlayerToGame(int gameID, String username) throws RemoteException, PreGameException {
        System.out.println("A client is joining game " + gameID + " with username " + username + "...");

        this.model = GameList.getInstance().getGame(gameID);
        if(this.model == null){
            throw new InvalidChoiceException("Game not found.");
        }

        this.controller = new Controller(this.model);

        if(this.controller.hasPlayerDisconnected(username)){
            GameList.getInstance().removeListener(this);

            this.client.gameHasStarted();

            this.playerIndex = this.controller.reconnectPlayer(username);

            // Force the update of the client
            this.client.modelChanged(new GameView(this.model, this.playerIndex));
            this.model.addListener(this);
        } else {
            this.model.addListener(this);

            try {
                this.playerIndex = this.controller.addPlayer(username);
            } catch (PreGameException e) {
                this.model.removeListener(this);
                this.model = null;
                this.controller = null;
                throw e;
            }

            this.playerJoinedGame();

            this.controller.startIfFull();
        }
    }

    /**
     * This method is called when a client player wants to create a new game.
     * The gameID is obtained from the maximum gameID of the games list.
     * @see Server#create(int, int, String)
     */
    @Override
    public void create(int numberOfPlayers, int numberOfCommonGoalCards, String username) throws RemoteException, PreGameException {
        int gameID = GameList.getInstance().getGames().stream()
                .mapToInt(Game::getGameID)
                .max()
                .orElse(0) + 1;

        System.out.println("A client is creating game " + gameID + " with username " + username + "...");

        this.model = new Game(gameID, numberOfPlayers, new Player(username), numberOfCommonGoalCards);

        // The player that creates the game is the first player to join it
        this.playerIndex = 0;

        GameList.getInstance().addGame(this.model);

        this.controller = new Controller(this.model);

        this.model.addListener(this);

        // Triggers the update of the players list on the client
        this.playerJoinedGame();
    }

    /**
     * When the client asks for the list of games, the server will trigger on the current ServerImpl a fake update of
     * the GameList class.
     */
    @Override
    public void getGamesList() throws RemoteException {
        this.client.updateGamesList(GameList.getInstance().getGamesDetails());
    }

    @Override
    public void newGame() throws RemoteException {
        this.client.updateGamesList(GameList.getInstance().getGamesDetails());
    }

    @Override
    public void updatedGame() throws RemoteException {
        this.client.updateGamesList(GameList.getInstance().getGamesDetails());
    }

    @Override
    public void removedGame() throws RemoteException {
        this.client.updateGamesList(GameList.getInstance().getGamesDetails());

        if(GameList.getInstance().getGame(this.model.getGameID()) == null){
            //GameList.getInstance().removeListener(this); // should be removed?
            this.model.removeListener(this);
            this.model = null;

            this.client.showError("The game you were waiting for has been removed.");
        }
    }

    @Override
    public void playerJoinedGame() throws RemoteException {
        this.client.updatePlayersList(this.model.playersList());
    }

    /**
     * This method is called by the {@link Game} class when the game the player is waiting for starts.
     * It will trigger the {@link Client#gameHasStarted()} method on the client
     * and removes this ServerImpl from the GameListListener list.
     */
    @Override
    public void gameIsFull() throws RemoteException {
        GameList.getInstance().removeListener(this);

        this.client.gameHasStarted();
    }

    /**
     * This method sends the updated model to the client.
     * It is called by the {@link Game} class when the model changes.
     * It will also schedule a walkover if the game is paused for too long.
     */
    @Override
    public void modelChanged() throws RemoteException {
        this.client.modelChanged(new GameView(this.model, this.playerIndex));

        // If the game is paused, it will be automatically ended after a certain amount of time.
        if(this.model.isPaused()){
            scheduler.schedule(() -> {
                if(this.model.isPaused()){
                    System.out.println("Game " + this.model.getGameID() + " has been paused for too long. It will be ended.");

                    executor.submit(() -> this.controller.walkover());
                }
            }, Constants.walkoverTimeout, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * This method is called by the {@link Game} class when the game ends.
     * It will trigger the {@link Client#gameEnded(GameView)} method on the client and removes the Game from the
     * GameList.
     */
    @Override
    public void gameEnded() throws RemoteException {
        GameView gameView = new GameView(this.model, this.playerIndex);

        GameList.getInstance().removeGame(this.model);

        this.model.removeListener(this);
        this.model = null;
        try {
            this.client.gameEnded(gameView);
        } catch (RemoteException ignored) {}
    }

    /**
     * This method calls the controller in order to execute the turn.
     * If the player who's trying to perform the turn is not the current player, it will throw a RemoteException.
     *
     * @see Controller#performTurn(int, Point...)
     */
    @Override
    public void performTurn(int column, Point... points) throws RemoteException {
        if(this.playerIndex != this.model.getCurrentPlayerIndex()){
            this.controller.setError("Player " + this.model.getPlayer(this.playerIndex).getUsername() + " tried to perform a turn while it was not his turn.");
            return;
        }

        try {
            this.controller.performTurn(column, points);
        } catch (GameException e) {
            this.controller.setError(e.getMessage());
        }
    }

    @Override
    public void pong() throws RemoteException {
        pingpongThread.pongReceived();
    }
}
