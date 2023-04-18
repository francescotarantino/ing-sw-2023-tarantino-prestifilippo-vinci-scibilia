package it.polimi.ingsw.view.textual;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.GameView;
import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.util.Observable;

import java.util.Scanner;

public class GameUI extends Observable<GameUI.Event> implements Runnable {
    private enum State {
        MY_TURN,
        NOT_MY_TURN
    }

    public enum Event {
        TURN_START,

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
        System.out.println("Starting GameUI...");

        while (true) {
            while (getState() == State.NOT_MY_TURN) {
                System.out.println("Waiting others turn...");

                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        System.err.println("Error while waiting for turn" + e.getMessage());
                    }
                }
            }

            System.out.println("Your turn!");
            this.myTurn();
        }
    }

    private void myTurn(){
    }

    /**
     * This method obtains the things that the player wants to do
     *
     */
    private void executeTurn() {
        Scanner input = new Scanner(System.in);
        int howMany;
        do{
            do {
                System.out.println("Quante tessere vuoi prendere dal soggiorno?");
                howMany = input.nextInt();
                if(howMany < Constants.minPick || howMany > Constants.maxPick)
                    System.out.println("Numero di tessere non valido.");
            } while(howMany < Constants.minPick || howMany > Constants.maxPick);
            for(int i=0; i < howMany; i++){

            }
        } while(true);
    }

    /**
     * This method prints the actual living room board
     * @param model
     */
    public void updateBoard(GameView model) {
        System.out.println("Stato attuale del soggiorno:");
        for(int i = 0; i < Constants.livingRoomBoardX; i++){
            for(int j=0; j < Constants.livingRoomBoardY; j++){
                System.out.print(model.getBoard().getTile(new Point(i, j)) + "\t");
            }
            System.out.println();
        }
    }
}