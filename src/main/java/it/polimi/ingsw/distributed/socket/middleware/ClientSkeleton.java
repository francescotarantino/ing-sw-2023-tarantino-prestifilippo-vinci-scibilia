package it.polimi.ingsw.distributed.socket.middleware;

import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.exception.InvalidChoiceException;
import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.viewmodel.GameView;
import it.polimi.ingsw.viewmodel.GameDetailsView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.List;

public class ClientSkeleton implements Client {
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;

    public enum Methods {
        UPDATE_GAMES_LIST,
        UPDATE_PLAYERS_LIST,
        SHOW_ERROR,
        GAME_HAS_STARTED,
        MODEL_CHANGED,
        GAME_FINISHED,
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
    public synchronized void gameFinished(GameView gameView) throws RemoteException {
        try {
            oos.reset();
            oos.writeObject(Methods.GAME_FINISHED);
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
        } catch (IOException | ClassNotFoundException e) {
            throw new RemoteException("Connection seems to be closed", e);
        } catch (InvalidChoiceException e) {
            this.showError(e.getMessage());
        }
    }
}
