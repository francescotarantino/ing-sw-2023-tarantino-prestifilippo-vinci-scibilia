package it.polimi.ingsw.distributed;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.exception.InvalidChoiceException;
import it.polimi.ingsw.listeners.GameListener;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameList;
import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.listeners.GameListListener;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

public class ServerImpl extends UnicastRemoteObject implements Server, GameListListener, GameListener {
    private Game model;
    private Controller controller;
    private Client client;
    private int playerIndex;


    public ServerImpl() throws RemoteException {
        super();
    }

    public ServerImpl(int port) throws RemoteException {
        super(port);
    }

    public ServerImpl(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }

    @Override
    public void register(Client client) throws RemoteException {
        this.client = client;
        GameList.getInstance().addListener(this);

        System.out.println("A client is registering to the server...");
    }

    @Override
    public void addPlayerToGame(int gameID, String username) throws RemoteException, InvalidChoiceException {
        System.out.println("A client is joining game " + gameID + " with username " + username + "...");

        this.model = GameList.getInstance().getGame(gameID);
        if(this.model == null){
            throw new InvalidChoiceException("Game not found.");
        }

        try {
            this.playerIndex = this.model.addBookshelf(new Player(username));
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new InvalidChoiceException(e.getMessage());
        }

        GameList.getInstance().notifyPlayerJoinedGame(gameID);

        this.controller = new Controller(this.model, this.client);

        this.model.addListener(this);

        if(this.model.isFull()){
            GameList.getInstance().notifyGameFull(gameID);

            this.controller.start();
        }
    }

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

        this.controller = new Controller(this.model, this.client);

        this.model.addListener(this);
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
        if(this.model == null){
            this.client.updateGamesList(GameList.getInstance().getGamesDetails());
        }
    }

    @Override
    public void removedGame() throws RemoteException {
        if(this.model == null){
            this.client.updateGamesList(GameList.getInstance().getGamesDetails());
        }
    }

    @Override
    public void playerJoinedGame(int gameID) throws RemoteException {
        if(this.model != null && this.model.getGameID() == gameID){
            this.client.updatePlayersList(this.model.playersList());
        }
    }

    @Override
    public void gameIsFull(int gameID) throws RemoteException {
        if(this.model != null && this.model.getGameID() == gameID){
            this.client.gameHasStarted();

            GameList.getInstance().removeListener(this);
        }
    }

    @Override
    public void modelChanged() throws RemoteException {
        this.client.modelChanged(new GameView(this.model, this.playerIndex));
    }
}
