package it.polimi.ingsw.distributed;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.exception.InvalidChoiceException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameList;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.listeners.GameListListener;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

public class ServerImpl extends UnicastRemoteObject implements Server, GameListListener {
    private Game model;
    private Controller controller;
    private Client client;


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
            this.model.addBookshelf(new Player(username));
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new InvalidChoiceException(e.getMessage());
        }

        GameList.getInstance().notifyPlayerJoinedGame(gameID);

        if(this.model.isFull()){
            GameList.getInstance().notifyGameFull(gameID);
        }

        this.controller = new Controller(this.model, this.client);
    }

    @Override
    public void create(int numberOfPlayers, int numberOfCommonGoalCards, String username) throws RemoteException, InvalidChoiceException {
        int gameID = GameList.getInstance().getGames().stream() // TODO: do we need to synchronize?
                .mapToInt(Game::getGameID)
                .max()
                .orElse(0) + 1;

        System.out.println("A client is creating game " + gameID + " with username " + username + "...");

        try {
            this.model = new Game(gameID, numberOfPlayers, new Player(username), numberOfCommonGoalCards);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new InvalidChoiceException(e.getMessage());
        }

        GameList.getInstance().addGame(this.model);

        this.controller = new Controller(this.model, this.client);
    }

    /**
     * When the client asks for the list of games, the server will trigger on the current ServerImpl a fake update of
     * the GameList class.
     */
    @Override
    public void getGamesList() throws RemoteException {
        this.client.updateGamesList(GameList.getInstance().getGamesString());
    }

    @Override
    public void newGame() throws RemoteException {
        if(this.model == null){
            this.client.updateGamesList(GameList.getInstance().getGamesString());
        }
    }

    @Override
    public void removedGame() throws RemoteException {
        if(this.model == null){
            this.client.updateGamesList(GameList.getInstance().getGamesString());
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
}
