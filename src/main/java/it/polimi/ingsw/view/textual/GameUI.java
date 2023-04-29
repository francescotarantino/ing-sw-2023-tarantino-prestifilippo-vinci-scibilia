package it.polimi.ingsw.view.textual;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.listeners.GameUIListener;
import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.viewmodel.GameView;
import org.fusesource.jansi.AnsiConsole;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static it.polimi.ingsw.Utils.checkIfColumnHasEnoughSpace;
import static it.polimi.ingsw.Utils.checkIfTilesCanBeTaken;
import static it.polimi.ingsw.listeners.Listener.notifyListeners;
import static org.fusesource.jansi.Ansi.ansi;

public class GameUI implements Runnable {
    private final List<GameUIListener> lst = new ArrayList<>();

    private enum State {
        MY_TURN,
        NOT_MY_TURN,
        ENDED
    }
    private State state = State.NOT_MY_TURN;
    private final Object lock = new Object();

    private Tile[][] currentBoard;
    private Tile[][] currentBookshelf;
    /**
     * R vaulue for living room board's color in RGB format
     */
    private static int livingRoomBoardR = 68;
    /**
     * G vaulue for living room board's color in RGB format
     */
    private static int livingRoomBoardG = 100;
    /**
     * B vaulue for living room board's color in RGB format
     */
    private static int livingRoomBoardB = 88;
    /**
     * Character used by the UI to display vertical lines
     */
    private String wall = "|";
    /**
     * Character used by the UI to display horizontal lines
     */
    private String fiveCeilings = "-----";
    /**
     * Character used by the UI to display short low vertical lines 
     */
    private String cornerUp = ",";
    /**
     * Character used by the UI to display short high vertical lines
     */
    private String cornerDown = "'";
    public GameUI(){
        if(System.getProperty("os.name").contains("Mac OS X") || System.getProperty("os.name").contains("Linux")){
            this.wall = "│";
            this.fiveCeilings = "━━━━━";
            this.cornerUp = "╷";
            this.cornerDown = "╵";
        }
    }

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
        AnsiConsole.systemInstall();

        System.out.print(ansi().eraseScreen());

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

            if(getState() != State.ENDED) {
                this.executeTurn();
            } else {
                break;
            }
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
            System.out.println("Your turn!");

            setState(State.MY_TURN);
        } else {
            System.out.println("Player " + gameView.getCurrentPlayerUsername() + "'s turn.");

            setState(State.NOT_MY_TURN);
        }
    }

    public void gameFinished(GameView gameView){
        this.updateBoard(gameView);

        System.out.println("Game has finished. Final points:");
        gameView.getFinalScores().forEach(System.out::println);

        System.out.println("The winner is: " + gameView.getFinalScores().get(0).username() + "!");

        setState(State.ENDED);

        notifyListeners(lst, GameUIListener::exit);
    }

    /**
     * This method obtains the things that the player wants to do
     */
    private void executeTurn() {
        Scanner input = new Scanner(System.in);

        int howManyPick;
        boolean inputValidity;
        int column;
        Point[] points;
        do {
            do {
                System.out.print("How many tiles do you want to pick? ");
                howManyPick = TextualUtils.nextInt(input);

                if(howManyPick < Constants.minPick || howManyPick > Constants.maxPick)
                    System.out.println("You can pick from " + Constants.minPick + " to " + Constants.maxPick + " tiles.");
            } while (howManyPick < Constants.minPick || howManyPick > Constants.maxPick);

            points = new Point[howManyPick];

            System.out.println("Pick " + howManyPick + " tiles in the order you want them to be inserted inside the bookshelf.");

            inputValidity = true;
            for (int i = 0; i < howManyPick; i++) {
                int x, y;
                do {
                    System.out.println("Tile #" + (i + 1));

                    System.out.print("Row: ");
                    y = Constants.livingRoomBoardY - TextualUtils.nextInt(input);
                    if (y < 0 || y > Constants.livingRoomBoardY)
                        System.out.println("Row coordinate must be between 1 and " + Constants.livingRoomBoardY);

                    System.out.print("Column: ");
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
        Point[] finalPoints = points;
        notifyListeners(lst, x -> x.performTurn(finalColumn, finalPoints));

        setState(State.NOT_MY_TURN);
    }

    /**
     * This method prints the actual living room board and the player's bookshelf.
     */
    private void updateBoard(GameView gameView){
        System.out.print(ansi().eraseScreen());
        System.out.println("Common Goal Cards:");
        for(int i = 0; i < gameView.getCGCDescriptions().size(); i++){
            System.out.println(" " + (i+1) + ". " + gameView.getCGCDescriptions().get(i).replace("\n", "\n    "));
        }
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
                            System.out.print(ansi().fgCyan()
                                    .a(cornerUp + fiveCeilings).reset());
                        else
                            System.out.print(ansi().fgCyan()
                                    .a(wall + fiveCeilings).reset());

                    }
                    else {
                        if (gameView.getLivingRoomBoardMatrix()[i][j] != null && !gameView.getLivingRoomBoardMatrix()[i][j].isPlaceholder()) {
                            System.out.print(ansi().fgCyan()
                                    .a(wall + "  ").reset());
                            System.out.print(ansi().bold().fg(gameView.getLivingRoomBoardMatrix()[i][j].getType().color())
                                    .a(gameView.getLivingRoomBoardMatrix()[i][j].toString().charAt(0)).reset() + "  ");
                        } else {
                            System.out.print(ansi().fgCyan()
                                    .a(wall + "     ").reset());
                        }
                    }
                }
                if(j == Constants.livingRoomBoardY - 1 && rowByHalves == 0)
                    System.out.println(ansi().fgCyan()
                            .a(cornerUp).reset());
                else if(Constants.livingRoomBoardY - j >= 4)
                    System.out.print(ansi().fgCyan()
                            .a(wall).reset());
                else if(Constants.livingRoomBoardY - j >= 3)
                    System.out.print(ansi().fgCyan()
                            .a(wall).reset());
                else
                    System.out.println(ansi().fgCyan()
                            .a(wall).reset());

                //Printing bookshelf and personal goal card
                if(Constants.livingRoomBoardY - j == 3 && rowByHalves == 0){
                    System.out.print("             ");
                    System.out.println("Your bookshelf:");
                }
                if(Constants.livingRoomBoardY - j == 3 && rowByHalves == 1){
                    System.out.print("             ");
                    for(int l = 0; l < Constants.bookshelfX; l++){
                        System.out.print("   " + (l+1) + "  ");
                    }
                    System.out.print("              ");
                    System.out.println("Your personal goal card:");
                }
                if(Constants.livingRoomBoardY - j >= 4){
                    System.out.print("             ");
                    if(rowByHalves == 0)
                        h--;
                    for (int k = 0; k < Constants.bookshelfX; k++) {
                        if(rowByHalves == 0){
                            if(h == Constants.bookshelfY - 1)
                                System.out.print(cornerUp + fiveCeilings);
                            else
                                System.out.print(wall + fiveCeilings);
                        }
                        else {
                            if(gameView.getBookshelfMatrix()[k][h] != null) {
                                System.out.print(wall + "  " + ansi().bold().fg(gameView.getBookshelfMatrix()[k][h].getType().color())
                                        .a(gameView.getBookshelfMatrix()[k][h].toString().charAt(0)).reset() + "  ");
                            } else {
                                System.out.print(wall + "     ");
                            }
                        }
                    }
                    if(Constants.livingRoomBoardY - j == 4 && rowByHalves == 0)
                        System.out.print(cornerUp);
                    else
                        System.out.print(wall);
                    System.out.print("             ");
                    for (int k = 0; k < Constants.bookshelfX; k++) {
                        if(rowByHalves == 0){
                            if(h == Constants.bookshelfY - 1)
                                System.out.print(cornerUp + fiveCeilings);
                            else
                                System.out.print(wall + fiveCeilings);
                        }
                        else {
                            if(gameView.getPersonalGoalCardMatrix()[k][h] != null) {
                                System.out.print(wall + "  " + ansi().bold().fg(gameView.getPersonalGoalCardMatrix()[k][h].color())
                                        .a(gameView.getPersonalGoalCardMatrix()[k][h].toString().charAt(0)).reset() + "  ");
                            } else {
                                System.out.print(wall + "     ");
                            }
                        }
                    }
                    if(Constants.livingRoomBoardY - j == 4 && rowByHalves == 0)
                        System.out.println(cornerUp);
                    else
                        System.out.println(wall);

                }
            }
        }
        System.out.print("   ");
        for (int j = Constants.livingRoomBoardY - 1; j >= 0; j--) {
            System.out.print(ansi().fgCyan()
                    .a(cornerDown + fiveCeilings).reset());
        }
        System.out.print(ansi().fgCyan()
                .a(cornerDown).reset());
        System.out.print("             ");
        for (int j = Constants.bookshelfX - 1; j >= 0; j--) {
            System.out.print(cornerDown + fiveCeilings);
        }
        System.out.print(cornerDown);
        System.out.print("             ");
        for (int j = Constants.bookshelfX - 1; j >= 0; j--) {
            System.out.print(cornerDown + fiveCeilings);
        }
        System.out.println(cornerDown);
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