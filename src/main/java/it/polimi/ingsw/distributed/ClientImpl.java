package it.polimi.ingsw.distributed;

import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.distributed.socket.middleware.ServerStub;
import it.polimi.ingsw.exception.PreGameException;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class is the implementation of the {@link Client} interface.
 * Each ClientImpl is the client-side counterpart of a {@link ServerImpl}.
 * It is used to handle all the requests coming from the server.
 *
 * @see Client
 */
public class ClientImpl extends UnicastRemoteObject implements Client, Runnable, StartUIListener, GameUIListener {
    private final Server server;
    private final StartUI startUI;
    private final GameUI gameUI;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    /**
     * This variable is used to check if the server is still alive.
     * It is set true when the server sends a ping to the client.
     * Every {@link Constants#connectionLostTimeout} milliseconds, the client checks if this variable is still true.
     * If it is not, the connection is considered lost and the client should be closed.
     * @see #run()
     */
    private boolean pingReceived;

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
            default -> throw new RuntimeException("UI type not supported");
        }

        initialize();
    }

    public void initialize() throws RemoteException {
        this.server.register(this);
        startUI.addListener(this);
    }

    @Override
    public void run() {
        scheduler.scheduleWithFixedDelay(() -> {
            if(!pingReceived){
                System.err.println("\nConnection lost, exiting...");
                exit();
            }

            pingReceived = false;
        }, Constants.pingpongTimeout, Constants.connectionLostTimeout, TimeUnit.MILLISECONDS);

        System.out.println("Starting StartUI...");
        startUI.run();
    }

    @Override
    public void refreshStartUI() {
        try {
            server.getGamesList();
        } catch (RemoteException e) {
            System.err.println("Network error while requesting games list.");
        }
    }

    @Override
    public void createGame(int numberOfPlayers, int numberOfCommonGoalCards, String username) {
        try {
            this.server.create(numberOfPlayers, numberOfCommonGoalCards, username);
        } catch (PreGameException e) {
            try {
                this.showError(e.getMessage());
            } catch (RemoteException ignored) {}
        } catch (RemoteException e) {
            System.err.println("Network error while creating game.");
        }
    }

    @Override
    public void joinGame(int gameID, String username) {
        try {
            this.server.addPlayerToGame(gameID, username);
        } catch (PreGameException e) {
            try {
                this.showError(e.getMessage());
            } catch (RemoteException ignored) {}
        } catch (RemoteException e) {
            System.err.println("Network error while joining game.");
        }
    }

    @Override
    public void exit() {
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
    public void performTurn(int column, Point...points) {
        try {
            this.server.performTurn(column, points);
        } catch (RemoteException e) {
            System.err.println("Network error while performing turn.");
        }
    }

    /**
     * This method closes the connection with the server if the connection is SOCKET.
     */
    private void closeConnection() {
        if(this.server instanceof ServerStub) {
            try {
                ((ServerStub) this.server).close();
            } catch (RemoteException e) {
                throw new RuntimeException("Error while closing connection.", e);
            }
        }
    }

    @Override
    public void ping() throws RemoteException {
        pingReceived = true;

        this.server.pong();
    }
}
