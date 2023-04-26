package it.polimi.ingsw.listeners;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public interface Listener {
    /**
     * This interface is useful to define a lambda expression that can throw a RemoteException.
     * The exception will be caught inside the {@link #notifyListeners(List, ConsumerWithRemoteException)} method.
     * @param <T> type of the listener
     */
    @FunctionalInterface
    interface ConsumerWithRemoteException<T> {
        void accept(T t) throws RemoteException;
    }

    /**
     * This method is used to notify all the listeners using a lambda expression passed as argument.
     * If a listener throws a RemoteException, it is removed from the list of listeners.
     * @param listeners ArrayList of listeners to notify
     * @param lambda lambda expression that should call a method on the listener
     * @param <L> type of the listener
     */
    static <L extends Listener> void notifyListeners(List<L> listeners, ConsumerWithRemoteException<L> lambda){
        List<L> tmp = new ArrayList<>(listeners);

        tmp.forEach(listener -> {
            try {
                lambda.accept(listener);
            } catch (RemoteException e) {
                listeners.remove(listener);
                System.err.println("Removing listener " + listener + " because of a RemoteException.");
                e.printStackTrace();
            }
        });
    }
}
