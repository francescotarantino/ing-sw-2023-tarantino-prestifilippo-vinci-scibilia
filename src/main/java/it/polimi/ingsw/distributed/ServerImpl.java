package it.polimi.ingsw.distributed;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.exception.InvalidChoiceException;
import it.polimi.ingsw.listeners.GameListener;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameList;
import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.viewmodel.GameView;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.listeners.GameListListener;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerImpl extends UnicastRemoteObject implements Server, GameListListener, GameListener {
    /**
     * The executor service used to run all the ping-pong threads.
     */
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    protected Game model;
    protected Controller controller;
    protected Client client;
    /**
     * Index of the player served by this ServerImpl.
     */
    protected int playerIndex;
    /**
     * The ping-pong thread used to check if the client is still connected.
     */
    private final PingPongThread pingpongThread = new PingPongThread(this);

    public ServerImpl() throws RemoteException {
        super();
    }

    /**
     * this method register a client as listener of the server
     * @param client the client to register
     * @throws RemoteException
     */
    @Override
    public void register(Client client) throws RemoteException {
        this.client = client;
        GameList.getInstance().addListener(this);

        System.out.println("A client is registering to the server...");

        executorService.submit(this.pingpongThread);
    }

    /**
     * this method actually add the Player to the game
     * @param gameID the ID of the game to join
     * @param username the username of the player
     * @throws RemoteException
     * @throws InvalidChoiceException
     */
    @Override
    public void addPlayerToGame(int gameID, String username) throws RemoteException, InvalidChoiceException {
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

            // Force update of the client
            this.client.modelChanged(new GameView(this.model, this.playerIndex));
            this.model.addListener(this);
        } else {
            this.model.addListener(this);

            try {
                this.playerIndex = this.controller.addPlayer(username);
            } catch (IllegalArgumentException | IllegalStateException e) {
                this.model.removeListener(this);
                this.model = null;
                this.controller = null;
                throw new InvalidChoiceException(e.getMessage());
            }

            this.playerJoinedGame();

            this.controller.startIfFull();
        }
    }

    /**
     * this method create an instance of a game, initializing the current player as the first one
     * @param numberOfPlayers the number of players in the game
     * @param numberOfCommonGoalCards the number of common goal cards to use in the game
     * @param username the username of the player
     * @throws RemoteException
     * @throws InvalidChoiceException
     */
    @Override
    public void create(int numberOfPlayers, int numberOfCommonGoalCards, String username) throws RemoteException, InvalidChoiceException {
        int gameID = GameList.getInstance().getGames().stream()
                .mapToInt(Game::getGameID)
                .max()
                .orElse(0) + 1;

        System.out.println("A client is creating game " + gameID + " with username " + username + "...");

        try {
            this.model = new Game(gameID, numberOfPlayers, new Player(username), numberOfCommonGoalCards);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new InvalidChoiceException(e.getMessage());
        }

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

    /**
     * this method update the Player list each time a player joins the game
     * @throws RemoteException
     */
    @Override
    public void playerJoinedGame() throws RemoteException {
        this.client.updatePlayersList(this.model.playersList());
    }

    /**
     * this method removes the selected game from the list of games to join and then start it
     * @throws RemoteException
     */
    @Override
    public void gameIsFull() throws RemoteException {
        GameList.getInstance().removeListener(this);

        this.client.gameHasStarted();
    }

    /**
     * this method update the game view after a change in the model.
     * the method also control if the game has been paused for a predetermined amount of time and if that's the case ends it
     * @throws RemoteException
     */
    @Override
    public void modelChanged() throws RemoteException {
        this.client.modelChanged(new GameView(this.model, this.playerIndex));

        // If the game is paused, it will be automatically ended after a certain amount of time.
        if(this.model.isPaused()){
            scheduler.schedule(() -> {
                if(this.model.isPaused()){
                    System.out.println("Game " + this.model.getGameID() + " has been paused for too long. It will be ended.");

                    this.controller.walkover();
                }
            }, Constants.walkoverTimeout, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * this method is called when the game ends, it erases the reference to the model and notify to the client the end
     * of the game
     * @throws RemoteException
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
     * this method calls the controller in order to execute the turn.
     * @param column the column where to put the tiles
     * @param points the points of the tiles from the living room board
     * @throws RemoteException
     */
    @Override
    public void performTurn(int column, Point... points) throws RemoteException {
        if(this.playerIndex == this.model.getCurrentPlayerIndex()){
            this.controller.performTurn(column, points);
        } else {
            throw new RemoteException("It's not your turn.");
        }
    }

    @Override
    public void pong() throws RemoteException {
        pingpongThread.pongReceived();
    }
}
