package it.polimi.ingsw.view.textual;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.listeners.GameUIListener;
import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.viewmodel.GameView;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static it.polimi.ingsw.Utils.checkIfColumnHasEnoughSpace;
import static it.polimi.ingsw.Utils.checkIfTilesCanBeTaken;
import static it.polimi.ingsw.listeners.Listener.notifyListeners;

public class GameUI implements Runnable {
    private final List<GameUIListener> lst = new ArrayList<>();

    private enum State {
        MY_TURN,
        NOT_MY_TURN
    }
    private State state = State.NOT_MY_TURN;
    private final Object lock = new Object();

    private Tile[][] currentBoard;
    private Tile[][] currentBookshelf;

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
        this.currentBoard = gameView.getLivingRoomBoardMatrix();
        this.currentBookshelf = gameView.getBookshelfMatrix();

        if(gameView.isMyTurn()) {
            System.out.println("It's now your turn!");

            setState(State.MY_TURN);
        } else {
            System.out.println("Now is player " + gameView.getCurrentPlayerUsername() + "'s turn.");

            setState(State.NOT_MY_TURN);
        }
    }

    public void gameFinished(GameView gameView){
        System.out.println("Game has finished. Final points:");
        gameView.getFinalScores().forEach((username, points) -> {
            System.out.println(username + ": " + points);
        });

        System.out.println("The winner is: " + gameView.getFinalScores().keySet().toArray()[0]);
    }

    /**
     * This method obtains the things that the player wants to do
     */
    private void executeTurn() {
        Scanner input = new Scanner(System.in);

        int howManyPick;
        do {
            System.out.print("How many tiles do you want to pick? ");
            howManyPick = TextualUtils.nextInt(input);

            if(howManyPick < Constants.minPick || howManyPick > Constants.maxPick)
                System.out.println("You can pick from " + Constants.minPick + " to " + Constants.maxPick + " tiles.");
        } while (howManyPick < Constants.minPick || howManyPick > Constants.maxPick);


        Point[] points = new Point[howManyPick];
        System.out.println("Pick " + howManyPick + " tiles in the order you want them to be inserted inside the bookshelf.");
        boolean inputValidity;
        int column;
        do {
            inputValidity = true;
            for (int i = 0; i < howManyPick; i++) {
                int x, y;
                do {
                    System.out.println("Tile #" + (i + 1));

                    System.out.print("Riga: ");
                    y = Constants.livingRoomBoardY - TextualUtils.nextInt(input);
                    if (y < 0 || y > Constants.livingRoomBoardY)
                        System.out.println("Row coordinate must be between 1 and " + Constants.livingRoomBoardY);

                    System.out.print("Colonna: ");
                    x = TextualUtils.nextInt(input) - 1;
                    if (x < 0 || x > Constants.livingRoomBoardX)
                        System.out.println("Column coordinate must be between 1 and " + Constants.livingRoomBoardX);

                } while (x < 0 || x > Constants.livingRoomBoardX || y < 0 || y > Constants.livingRoomBoardY);
                points[i] = new Point(x, y);
            }

            do {
                System.out.print("In which column do you want to put the tiles? ");
                column = TextualUtils.nextInt(input) - 1;
                if(column < 0 || column > Constants.bookshelfX)
                    System.out.println("Column must be between 1 and " + Constants.bookshelfX + ".");
            } while(column < 0 || column > Constants.bookshelfX);

            if(!checkIfTilesCanBeTaken(currentBoard, points)){
                System.out.println("Invalid selection. Taken tiles must form a straight line and have at least one free side.");
                inputValidity = false;
            }

            if(!checkIfColumnHasEnoughSpace(currentBookshelf, column, points.length)){
                System.out.println("Chosen column does not have enough space.");
                inputValidity = false;
            }
        } while(!inputValidity);

        int finalColumn = column;
        notifyListeners(lst, x -> x.performTurn(finalColumn, points));

        setState(State.NOT_MY_TURN);
    }

    /**
     * This method prints the actual living room board and the player's bookshelf.
     */
    private void updateBoard(GameView gameView){
        System.out.println("Current living room board:");

        System.out.print("   ");
        for(int i = 0; i < Constants.livingRoomBoardX; i++){
            System.out.print("   " + (i + 1) + "  ");
        }
        System.out.println();
        int h = Constants.bookshelfY;
        for (int j = Constants.livingRoomBoardY - 1; j >= 0; j--) {
            for(int rowByHalves = 0; rowByHalves < 2; rowByHalves++) {
                if(rowByHalves == 1)
                    System.out.print(" " + (Constants.livingRoomBoardY - j) + " ");
                else
                    System.out.print("   ");

                for (int i = 0; i < Constants.livingRoomBoardX; i++) {

                    if(rowByHalves == 0){
                        if(j == Constants.livingRoomBoardY - 1)
                            System.out.print("│" + "▔▔▔▔▔");
                        else
                            System.out.print("│" + "━━━━━");
                    }
                    else {
                        if (gameView.getLivingRoomBoardMatrix()[i][j] != null && !gameView.getLivingRoomBoardMatrix()[i][j].isPlaceholder()) {
                            System.out.print("│  " + gameView.getLivingRoomBoardMatrix()[i][j].toString().charAt(0) + "  ");
                        } else {
                            System.out.print("│     ");
                        }
                    }
                }
                if(Constants.livingRoomBoardY - j >= 4)
                    System.out.print("│");
                else if(Constants.livingRoomBoardY - j >= 2 && rowByHalves == 1)
                    System.out.print("│");
                else
                    System.out.println("│");


                //Printing bookshelf
                if(Constants.livingRoomBoardY - j == 2 && rowByHalves == 1){
                    System.out.print("             ");
                    System.out.println("Your bookshelf:");
                }
                if(Constants.livingRoomBoardY - j == 3 && rowByHalves == 1){
                    System.out.print("             ");
                    for(int l = 0; l < Constants.bookshelfX; l++){
                        System.out.print("   " + (l+1) + "  ");
                    }
                    System.out.println();
                }
                if(Constants.livingRoomBoardY - j >= 4){
                    System.out.print("             ");
                    if(rowByHalves == 0)
                        h--;
                    for (int k = 0; k < Constants.bookshelfX; k++) {
                        if(rowByHalves == 0){
                            if(h == Constants.bookshelfY - 1)
                                System.out.print("│" + "▔▔▔▔▔");
                            else
                                System.out.print("│" + "━━━━━");
                        }
                        else {
                            if(gameView.getBookshelfMatrix()[k][h] != null) {
                                System.out.print("│  " + gameView.getBookshelfMatrix()[k][h].toString().charAt(0) + "  ");
                            } else {
                                System.out.print("│     ");
                            }
                        }
                    }
                    System.out.println("│");
                }
            }
        }
        System.out.print("   ");
        for (int j = Constants.livingRoomBoardY - 1; j >= 0; j--) {
            System.out.print("│" + "▁▁▁▁▁");
        }
        System.out.print("│");
        System.out.print("             ");
        for (int j = Constants.bookshelfX - 1; j >= 0; j--) {
            System.out.print("│" + "▁▁▁▁▁");
        }
        System.out.print("│");
        System.out.println();

        /*System.out.println("Your bookshelf:");
        for(int j = Constants.bookshelfY - 1; j >= 0; j--){
            for(int i = 0; i < Constants.bookshelfX; i++){
                if(gameView.getBookshelfMatrix()[i][j] != null) {
                    System.out.print(gameView.getBookshelfMatrix()[i][j].toString().charAt(0) + "\t");
                } else {
                    System.out.print(" \t");
                }
            }
            System.out.println();
        }*/
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