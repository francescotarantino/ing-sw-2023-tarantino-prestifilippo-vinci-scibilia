package it.polimi.ingsw.view.textual;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.listeners.GameUIListener;
import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.model.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameUI implements Runnable {
    private final List<GameUIListener> lst = new ArrayList<>();

    private enum State {
        MY_TURN,
        NOT_MY_TURN
    }
    private State state = State.NOT_MY_TURN;
    private final Object lock = new Object();

    private State getState() {
        synchronized (lock) {
            return this.state;
        }
    }

    private void setState(State state) {
        synchronized (lock) {
            this.state = state;
            lock.notifyAll();
        }
    }

    public void run() {
        while (true) {
            while (getState() == State.NOT_MY_TURN) {
                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        System.out.println("Error while waiting for turn: " + e.getMessage());
                    }
                }
            }

            this.executeTurn();
        }
    }

    /**
     * This method should be called when there is a change in the model of the game.
     * @param gameView the new model-view of the game
     */
    public void update(GameView gameView){
        this.updateBoard(gameView);

        if(gameView.isMyTurn()) {
            System.out.println("It's now your turn!");

            setState(State.MY_TURN);
        } else {
            System.out.println("Now is player " + gameView.getPlayerUsernames()[gameView.getCurrentPlayerIndex()] + "'s turn.");

            setState(State.NOT_MY_TURN);
        }
    }

    /**
     * This method obtains the things that the player wants to do
     */
    private void executeTurn() {
        Scanner input = new Scanner(System.in);

        int howManyPick;
        do {
            System.out.print("How many tiles do you want to pick? ");
            howManyPick = input.nextInt();

            if(howManyPick < Constants.minPick || howManyPick > Constants.maxPick)
                System.out.print("You can pick from " + Constants.minPick + " to " + Constants.maxPick + " tiles.");
        } while (howManyPick < Constants.minPick || howManyPick > Constants.maxPick);


        Point[] points = new Point[howManyPick];
        System.out.println("Now it's time to pick the tiles in order.");
        for(int i = 0; i < howManyPick; i++){
            System.out.println("Tile #" + i);

            System.out.print("x: ");
            int x = input.nextInt();

            System.out.print("y: ");
            int y = input.nextInt();

            points[i] = new Point(x, y);
        }

        // TODO: check if the tiles are valid

        System.out.print("In which column do you want to put the tiles? ");
        int column = input.nextInt();

        // TODO: invoke the listener
    }

    /**
     * This method prints the actual living room board
     */
    private void updateBoard(GameView gameView){
        System.out.println("Current living room board:");

        for(int j = Constants.livingRoomBoardY - 1; j >= 0; j--){
            for(int i = 0; i < Constants.livingRoomBoardX; i++){
                System.out.print(gameView.getBoard().getTile(new Point(i, j)).toString().charAt(0) + "\t");
            }
            System.out.println();
        }
    }

    public synchronized void addListener(GameUIListener o){
        if(!lst.contains(o)){
            lst.add(o);
        }
    }

    public synchronized void removeListener(GameUIListener o){
        lst.remove(o);
    }
}