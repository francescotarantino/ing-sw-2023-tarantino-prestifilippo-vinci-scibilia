package it.polimi.ingsw.view;

import it.polimi.ingsw.listeners.StartUIListener;
import it.polimi.ingsw.viewmodel.GameDetailsView;

import java.util.ArrayList;
import java.util.List;

abstract public class StartUI implements Runnable {
    protected final List<StartUIListener> lst = new ArrayList<>();

    public abstract void showGamesList(List<GameDetailsView> o);

    public abstract void showError(String err);

    public abstract void showPlayersList(List<String> o);

    public synchronized void addListener(StartUIListener o){
        if(!lst.contains(o)){
            lst.add(o);
        }
    }

    public synchronized void removeListener(StartUIListener o){
        lst.remove(o);
    }
}
