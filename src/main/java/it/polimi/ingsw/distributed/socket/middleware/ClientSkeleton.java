package it.polimi.ingsw.distributed.socket.middleware;

import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.exception.PreGameException;
import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.viewmodel.GameView;
import it.polimi.ingsw.viewmodel.GameDetailsView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.List;

/**
 * This class is the skeleton of the client seen by the server.
 * It is used to send SOCKET messages to the client.
 *
 * @see Client
 */
public class ClientSkeleton implements Client {
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;

    /**
     * This enum represents all the methods that can be called on the client.
     */
    public enum Methods {
        UPDATE_GAMES_LIST,
        UPDATE_PLAYERS_LIST,
        SHOW_ERROR,
        GAME_HAS_STARTED,
        MODEL_CHANGED,
        GAME_ENDED,
        PING
    }

    public ClientSkeleton(Socket socket) throws RemoteException {
        try {
            this.oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RemoteException("Cannot create output stream", e);
        }
        try {
            this.ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RemoteException("Cannot create input stream", e);
        }
    }

    @Override
    public synchronized void updateGamesList(List<GameDetailsView> o) throws RemoteException {
        try {
            oos.reset();
            oos.writeObject(Methods.UPDATE_GAMES_LIST);
            oos.writeObject(o);
            oos.flush();
        } catch (IOException e1) {
            throw new RemoteException("Cannot send object", e1);
        }
    }

    @Override
    public synchronized void showError(String err) throws RemoteException {
        try {
            oos.reset();
            oos.writeObject(Methods.SHOW_ERROR);
            oos.writeObject(err);
            oos.flush();
        } catch (IOException e) {
            throw new RemoteException("Cannot send error signal", e);
        }
    }

    @Override
    public synchronized void updatePlayersList(List<String> o) throws RemoteException {
        try {
            oos.reset();
            oos.writeObject(Methods.UPDATE_PLAYERS_LIST);
            oos.writeObject(o);
            oos.flush();
        } catch (IOException e1) {
            throw new RemoteException("Cannot send object", e1);
        }
    }

    @Override
    public synchronized void gameHasStarted() throws RemoteException {
        try {
            oos.reset();
            oos.writeObject(Methods.GAME_HAS_STARTED);
            oos.flush();
        } catch (IOException e1) {
            throw new RemoteException("Cannot send object", e1);
        }
    }

    @Override
    public synchronized void modelChanged(GameView gameView) throws RemoteException {
        try {
            oos.reset();
            oos.writeObject(Methods.MODEL_CHANGED);
            oos.writeObject(gameView);
            oos.flush();
        } catch (IOException e1) {
            throw new RemoteException("Cannot send object", e1);
        }
    }

    @Override
    public synchronized void gameEnded(GameView gameView) throws RemoteException {
        try {
            oos.reset();
            oos.writeObject(Methods.GAME_ENDED);
            oos.writeObject(gameView);
            oos.flush();
        } catch (IOException e1) {
            throw new RemoteException("Cannot send object", e1);
        }
    }

    @Override
    public synchronized void ping() throws RemoteException {
        try {
            oos.reset();
            oos.writeObject(Methods.PING);
            oos.flush();
        } catch (IOException e1) {
            throw new RemoteException("Cannot send object", e1);
        }
    }

    /**
     * This method is used to receive SOCKET messages from the client.
     */
    public void receive(Server server) throws RemoteException {
        try {
            ServerStub.Methods method = (ServerStub.Methods) ois.readObject();

            switch (method) {
                case JOIN -> server.addPlayerToGame((Integer) ois.readObject(), (String) ois.readObject());
                case CREATE -> server.create((Integer) ois.readObject(), (Integer) ois.readObject(), (String) ois.readObject());
                case GET_GAMES_LIST -> server.getGamesList();
                case PERFORM_TURN -> server.performTurn((Integer) ois.readObject(), (Point[]) ois.readObject());
                case PONG -> server.pong();
            }
        } catch (ClassNotFoundException e) {
            throw new RemoteException("Cannot deserialize object", e);
        } catch (IOException e) {
            throw new RemoteException("Connection seems to be closed", e);
        } catch (PreGameException e) {
            this.showError(e.getMessage());
        }
    }
}
