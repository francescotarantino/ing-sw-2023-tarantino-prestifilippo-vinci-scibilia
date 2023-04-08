package it.polimi.ingsw.distributed;

import it.polimi.ingsw.exception.InvalidChoiceException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
    void join(int gameID, String username) throws RemoteException, InvalidChoiceException;

    void create(int numberOfPlayers, int numberOfCommonGoalCards, String username) throws RemoteException, InvalidChoiceException;

    void register(Client client) throws RemoteException;

    void getGamesList() throws RemoteException;
}