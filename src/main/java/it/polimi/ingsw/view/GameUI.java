package it.polimi.ingsw.view;

import it.polimi.ingsw.listeners.GameUIListener;
import it.polimi.ingsw.viewmodel.GameView;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for the GameUI.
 */
abstract public class GameUI implements Runnable {
    protected final List<GameUIListener> lst = new ArrayList<>();

    public abstract void update(GameView gameView);

    public abstract void gameEnded(GameView gameView);

    public synchronized void addListener(GameUIListener o){
        if(!lst.contains(o)){
            lst.add(o);
        }
    }

    public synchronized void removeListener(GameUIListener o){
        lst.remove(o);
    }
}
