package it.polimi.ingsw.distributed.socket.middleware;

import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.viewmodel.GameDetailsView;
import it.polimi.ingsw.viewmodel.GameView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.List;

/**
 * This class is the stub of the server seen by the client.
 * It is used to send SOCKET messages to the server.
 *
 * @see Server
 */
public class ServerStub implements Server {
    private final String ip;
    private final int port;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Socket socket;

    /**
     * This enum represents all the methods that can be called on the server.
     */
    public enum Methods {
        JOIN,
        CREATE,
        GET_GAMES_LIST,
        PERFORM_TURN,
        PONG
    }

    public ServerStub(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    /**
     * Open a socket connection to the server and create the input and output streams.
     * @param client the client to register, in this method is not used
     */
    @Override
    public void register(Client client) throws RemoteException {
        try {
            this.socket = new Socket(ip, port);
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
        } catch (IOException e) {
            throw new RemoteException("Unable to connect to the server", e);
        }
    }

    @Override
    public synchronized void addPlayerToGame(int gameID, String username) throws RemoteException {
        try {
            oos.reset();
            oos.writeObject(Methods.JOIN);
            oos.writeObject(gameID);
            oos.writeObject(username);
            oos.flush();
        } catch (IOException e) {
            throw new RemoteException("Cannot send event", e);
        }
    }

    @Override
    public synchronized void create(int numberOfPlayers, int numberOfCommonGoalCards, String username) throws RemoteException {
        try {
            oos.reset();
            oos.writeObject(Methods.CREATE);
            oos.writeObject(numberOfPlayers);
            oos.writeObject(numberOfCommonGoalCards);
            oos.writeObject(username);
            oos.flush();
        } catch (IOException e) {
            throw new RemoteException("Cannot send event", e);
        }
    }

    @Override
    public synchronized void getGamesList() throws RemoteException {
        try {
            oos.reset();
            oos.writeObject(Methods.GET_GAMES_LIST);
            oos.flush();
        } catch (IOException e) {
            throw new RemoteException("Cannot receive event", e);
        }
    }

    @Override
    public synchronized void performTurn(int column, Point... points) throws RemoteException {
        try {
            oos.reset();
            oos.writeObject(Methods.PERFORM_TURN);
            oos.writeObject(column);
            oos.writeObject(points);
            oos.flush();
        } catch (IOException e) {
            throw new RemoteException("Cannot send event", e);
        }
    }

    @Override
    public synchronized void pong() throws RemoteException {
        try {
            oos.reset();
            oos.writeObject(Methods.PONG);
            oos.flush();
        } catch (IOException e) {
            throw new RemoteException("Cannot send event", e);
        }
    }

    /**
     * This method is used to receive SOCKET messages from the server.
     */
    @SuppressWarnings("unchecked")
    public void receive(Client client) throws RemoteException {
        try {
            ClientSkeleton.Methods method = (ClientSkeleton.Methods) ois.readObject();

            switch (method) {
                case UPDATE_GAMES_LIST -> client.updateGamesList((List<GameDetailsView>) ois.readObject());
                case UPDATE_PLAYERS_LIST -> client.updatePlayersList((List<String>) ois.readObject());
                case SHOW_ERROR -> client.showError((String) ois.readObject());
                case GAME_HAS_STARTED -> client.gameHasStarted();
                case MODEL_CHANGED -> client.modelChanged((GameView) ois.readObject());
                case GAME_ENDED -> client.gameEnded((GameView) ois.readObject());
                case PING -> client.ping();
            }
        } catch (SocketException e) {
            System.exit(0);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot deserialize object", e);
        } catch (IOException e) {
            throw new RemoteException("Connection error", e);
        }
    }

    /**
     * Closes the socket connection.
     */
    public void close() throws RemoteException {
        try {
            System.out.println("Closing connection...");
            socket.close();
        } catch (IOException e) {
            throw new RemoteException("Cannot close socket", e);
        }
    }
}
