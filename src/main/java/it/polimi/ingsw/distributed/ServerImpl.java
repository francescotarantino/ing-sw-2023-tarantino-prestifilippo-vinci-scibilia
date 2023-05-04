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
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

public class ServerImpl extends UnicastRemoteObject implements Server, GameListListener, GameListener {
    private Game model;
    private Controller controller;
    private Client client;
    private int playerIndex;
    /**
     * This boolean is used in the ping-pong mechanism to check if the client is still connected to the server.
     * It is set to false before the ping is sent. If the server receives a pong, it is set to true.
     */
    boolean pong;

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

        new Thread(this::pingpong).start();
    }

    @Override
    public void addPlayerToGame(int gameID, String username) throws RemoteException, InvalidChoiceException {
        System.out.println("A client is joining game " + gameID + " with username " + username + "...");

        this.model = GameList.getInstance().getGame(gameID);
        if(this.model == null){
            throw new InvalidChoiceException("Game not found.");
        }

        this.controller = new Controller(this.model);

        if(this.controller.isPlayerTryingToReconnect(username)){
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
        if(this.model == null){
            this.client.updateGamesList(GameList.getInstance().getGamesDetails());
        }
    }

    @Override
    public void updatedGame() throws RemoteException {
        if(this.model == null){
            this.client.updateGamesList(GameList.getInstance().getGamesDetails());
        }
    }

    @Override
    public void removedGame() throws RemoteException {
        if(this.model == null){
            this.client.updateGamesList(GameList.getInstance().getGamesDetails());
        } else if(GameList.getInstance().getGame(this.model.getGameID()) == null){
            GameList.getInstance().removeListener(this);
            this.model.removeListener(this);
            this.model = null;

            this.client.showError("The game you were waiting for has been removed.", true);
        }
    }

    @Override
    public void playerJoinedGame() throws RemoteException {
        this.client.updatePlayersList(this.model.playersList());
    }

    @Override
    public void gameIsFull() throws RemoteException {
        GameList.getInstance().removeListener(this);

        this.client.gameHasStarted();
    }

    @Override
    public void modelChanged() throws RemoteException {
        this.client.modelChanged(new GameView(this.model, this.playerIndex));
    }

    @Override
    public void gameFinished() throws RemoteException {
        GameView gameView = new GameView(this.model, this.playerIndex);

        // In this way, only one ServerImpl instance will print the game result on the server console and remove the game from the GameList
        if(this.playerIndex == 0){
            System.out.println("Game " + this.model.getGameID() + " has finished.");
            System.out.println("The winner is " + gameView.getPlayerInfo().get(0).username() + " with " + gameView.getPlayerInfo().get(0).points() + " points.");
            GameList.getInstance().removeGame(this.model);
        }

        this.model.removeListener(this);
        this.model = null;
        try {
            this.client.gameFinished(gameView);
        } catch (RemoteException ignored) {}
    }

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
        pong = true;
    }

    /**
     * This method is used to check if the client is still connected to the server.
     */
    private void pingpong() {
        while (true) {
            pong = false;
            try {
                this.client.ping();
            } catch (RemoteException e) {
                break;
            }

            try {
                Thread.sleep(Constants.pingpongTimeout);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (!pong) {
                break;
            }
        }

        if (this.model != null) {
            // Client is linked to a game
            System.out.println("Player " + this.model.getPlayer(this.playerIndex).getUsername() + " disconnected in game " + this.model.getGameID() + ".");

            // If the game is not started or there is only one player left, the game is removed from the server
            if(!this.model.isStarted() || this.model.getConnectedPlayersNumber() == 1) {
                System.out.println("The game is removed.");

                this.model.removeListener(this);
                GameList.getInstance().removeListener(this);
                GameList.getInstance().removeGame(this.model);
            } else {
                System.out.println("The game will continue.");
                this.model.removeListener(this);

                this.controller.handlePlayerDisconnection(this.playerIndex);
            }
        } else {
            // Client is not linked to a game
            System.out.println("A client disconnected.");

            GameList.getInstance().removeListener(this);
        }
    }
}
