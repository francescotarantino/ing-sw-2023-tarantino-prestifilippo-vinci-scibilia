package it.polimi.ingsw.distributed;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.distributed.socket.middleware.ServerStub;
import it.polimi.ingsw.exception.InvalidChoiceException;
import it.polimi.ingsw.listeners.GameUIListener;
import it.polimi.ingsw.listeners.StartUIListener;
import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.view.GameUI;
import it.polimi.ingsw.view.StartUI;
import it.polimi.ingsw.view.graphical.GraphicalGameUI;
import it.polimi.ingsw.view.graphical.GraphicalStartUI;
import it.polimi.ingsw.viewmodel.GameView;
import it.polimi.ingsw.view.textual.TextualGameUI;
import it.polimi.ingsw.view.textual.TextualStartUI;
import it.polimi.ingsw.viewmodel.GameDetailsView;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ClientImpl extends UnicastRemoteObject implements Client, Runnable, StartUIListener, GameUIListener {
    private final Server server;
    private final StartUI startUI;
    private final GameUI gameUI;

    public ClientImpl(Server server, Constants.UIType uiType) throws RemoteException {
        this.server = server;

        switch (uiType){
            case CLI -> {
                this.startUI = new TextualStartUI();
                this.gameUI = new TextualGameUI();
            }
            case GUI -> {
                this.startUI = new GraphicalStartUI();
                this.gameUI = new GraphicalGameUI();
            }
            default -> {
                this.startUI = null;
                this.gameUI = null;
                System.out.println("UI type not supported");
                exit();
            }
        }

        initialize();
    }

    public void initialize() throws RemoteException {
        this.server.register(this);
        startUI.addListener(this);
    }

    @Override
    public void run() {
        System.out.println("Starting StartUI...");
        startUI.run();
    }

    @Override
    public void refreshStartUI() throws RemoteException {
        server.getGamesList();
    }

    @Override
    public void createGame(int numberOfPlayers, int numberOfCommonGoalCards, String username) throws RemoteException {
        try {
            this.server.create(numberOfPlayers, numberOfCommonGoalCards, username);
        } catch (InvalidChoiceException e) {
            this.showError(e.getMessage());
        }
    }

    @Override
    public void joinGame(int gameID, String username) throws RemoteException {
        try {
            this.server.addPlayerToGame(gameID, username);
        } catch (InvalidChoiceException e) {
            this.showError(e.getMessage());
        }
    }

    @Override
    public void exit() throws RemoteException {
        closeConnection();

        System.exit(0);
    }

    @Override
    public void updateGamesList(List<GameDetailsView> o) throws RemoteException {
        startUI.showGamesList(o);
    }

    @Override
    public void showError(String err) throws RemoteException {
        startUI.showError(err);
    }

    @Override
    public void updatePlayersList(List<String> o) throws RemoteException {
        startUI.showPlayersList(o);
    }

    @Override
    public void gameHasStarted() throws RemoteException {
        System.out.println("Closing StartUI...");
        startUI.close();

        System.out.println("Starting GameUI...");
        new Thread(gameUI).start();
        gameUI.addListener(this);
    }

    @Override
    public void modelChanged(GameView gameView) throws RemoteException {
        gameUI.update(gameView);
    }

    @Override
    public void gameEnded(GameView gameView) throws RemoteException {
        gameUI.gameEnded(gameView);
    }

    @Override
    public void performTurn(int column, Point...points) throws RemoteException {
        this.server.performTurn(column, points);
    }

    /**
     * This method closes the connection with the server if the connection is SOCKET.
     */
    private void closeConnection() throws RemoteException {
        if(this.server instanceof ServerStub) {
            ((ServerStub) this.server).close();
        }
    }

    @Override
    public void ping() throws RemoteException {
        this.server.pong();
    }
}
