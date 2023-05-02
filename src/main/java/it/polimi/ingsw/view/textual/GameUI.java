package it.polimi.ingsw.view.textual;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.listeners.GameUIListener;
import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.viewmodel.GameView;
import it.polimi.ingsw.viewmodel.PlayerInfo;
import org.fusesource.jansi.Ansi;
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

    private Thread inputThread;

    private Tile[][] currentBoard;
    private Tile[][] currentBookshelf;
    /** Integer representing living room board color */
    private static final int[] livingRoomBoardColor = {63,99,86};

    /** Integer representing bookshelf color */
    private static final int[] bookshelfColor = {116,44,17};

    /** Integer representing personal goal card color */
    private static final int[] personalGoalCardColor = {116,44,17};
    /** Character used by the UI to display part of a table */
    private String wall = "|";

    /** Character used by the UI to display part of a table */
    private String fiveCeilings = "-----";

    /** Character used by the UI to display part of a table */
    private String cornerTopLeft = "/";

    /** Character used by the UI to display part of a table */
    private String cornerTopRight = "\\";

    /** Character used by the UI to display part of a table */
    private String cornerBottomLeft = "\\";

    /** Character used by the UI to display part of a table */
    private String cornerBottomLeftAlternative = "!";

    /** Character used by the UI to display part of a table */
    private String cornerBottomRight = "/";


    /** Character used by the UI to display part of a table */
    private String cornerBottomRightAlternative = "!";

    /** Character used by the UI to display part of a table */
    private String edgeTop = "-";

    /** Character used by the UI to display part of a table */
    private String edgeBottom = "-";

    /** Character used by the UI to display part of a table */
    private String edgeLeft = "|";

    /** Character used by the UI to display part of a table */
    private String edgeRight = "|";

    /** Character used by the UI to display part of a table */
    private String cross = "+";

    /** Boolean used to determine how to print characters */
    private boolean isUnicodeCompatible = false;

    public GameUI(){
    if(System.getProperty("os.name").contains("Mac OS X") || System.getProperty("os.name").contains("Linux")){
        this.isUnicodeCompatible = true;
        System.out.println("OS: " + System.getProperty("os.name"));
        System.out.println("Ultra mode activated. Choose your style:");
        System.out.print("1. ╭─────╮\t2. ┏─────┓\t3. ╔═════╗\n" +
                         "   │     │\t   │     │\t   ║     ║\n" +
                         "   ╰─────╯\t   ┗─────┛\t   ╚═════╝\n");
        Scanner s = new Scanner(System.in);
        int choice;
        do {
            choice = TextualUtils.nextInt(s);
            if(choice < 0 || choice > 3)
                System.out.println("Invalid selection.");
        }while(choice < 0 || choice > 3);
        switch (choice) {
            case 1 -> {
                this.wall = "│";
                this.fiveCeilings = "─────";
                this.cornerTopLeft = "╭";
                this.cornerTopRight = "╮";
                this.cornerBottomLeft = "╰";
                this.cornerBottomLeftAlternative = "┴";
                this.cornerBottomRight = "╯";
                this.cornerBottomRightAlternative = "┴";
                this.edgeTop = "┬";
                this.edgeBottom = "┴";
                this.edgeLeft = "├";
                this.edgeRight = "┤";
                this.cross = "┼";
            }
            case 2 -> {
                this.wall = "│";
                this.fiveCeilings = "─────";
                this.cornerTopLeft = "┏";
                this.cornerTopRight = "┓";
                this.cornerBottomLeft = "┗";
                this.cornerBottomLeftAlternative = "┻";
                this.cornerBottomRight = "┛";
                this.cornerBottomRightAlternative = "┻";
                this.edgeTop = "┳";
                this.edgeBottom = "┻";
                this.edgeLeft = "┣";
                this.edgeRight = "┫";
                this.cross = "╋";
            }
            case 3 -> {
                this.wall = "║";
                this.fiveCeilings = "═════";
                this.cornerTopLeft = "╔";
                this.cornerTopRight = "╗";
                this.cornerBottomLeft = "╚";
                this.cornerBottomLeftAlternative = "╩";
                this.cornerBottomRight = "╝";
                this.cornerBottomRightAlternative = "╩";
                this.edgeTop = "╦";
                this.edgeBottom = "╩";
                this.edgeLeft = "╠";
                this.edgeRight = "╣";
                this.cross = "╬";
            }
        }
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
                inputThread = new Thread(() -> {
                    try {
                        executeTurn();
                    } catch (InterruptedException ignored) {}
                });
                inputThread.start();
                try {
                    inputThread.join();
                } catch (InterruptedException ignored) {}
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
        if(gameView.isGamePaused()){
            this.gamePaused();
            setState(State.NOT_MY_TURN);
            return;
        }

        // If it's my turn and I'm already in my turn, I don't need to update the board
        if(gameView.isMyTurn() && getState() == State.MY_TURN){
            return;
        }

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
        gameView.getPlayerInfo().forEach(System.out::println);

        System.out.println("The winner is: " + gameView.getPlayerInfo().get(0).username() + "!");

        setState(State.ENDED);

        notifyListeners(lst, GameUIListener::exit);
    }

    public void gamePaused(){
        System.out.println();
        System.out.println(ansi().bold().a("Game has been paused since you're the only player left in the game.").reset());
        if(inputThread != null) inputThread.interrupt();
    }

    /**
     * This method obtains the things that the player wants to do
     */
    private void executeTurn() throws InterruptedException {
        Scanner input = new Scanner(System.in);

        int howManyPick;
        boolean inputValidity;
        int column;
        Point[] points;
        do {
            do {
                System.out.print("How many tiles do you want to pick? ");
                howManyPick = TextualUtils.nextIntInterruptible(input);

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
                    y = Constants.livingRoomBoardY - TextualUtils.nextIntInterruptible(input);
                    if (y < 0 || y > Constants.livingRoomBoardY)
                        System.out.println("Row coordinate must be between 1 and " + Constants.livingRoomBoardY);
                    System.out.print("Column: ");
                    x = TextualUtils.nextIntInterruptible(input) - 1;
                    if (x < 0 || x > Constants.livingRoomBoardX)
                        System.out.println("Column coordinate must be between 1 and " + Constants.livingRoomBoardX);

                } while (x < 0 || x > Constants.livingRoomBoardX || y < 0 || y > Constants.livingRoomBoardY);
                points[i] = new Point(x, y);
            }

            do {
                System.out.print("In which column do you want to put the tiles? ");
                column = TextualUtils.nextIntInterruptible(input) - 1;
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
        if(getState() == State.MY_TURN) notifyListeners(lst, x -> x.performTurn(finalColumn, finalPoints));

        setState(State.NOT_MY_TURN);
    }

    /**
     * This method prints the actual living room board and the player's bookshelf.
     */
    private void updateBoard(GameView gameView){
        System.out.print(ansi().eraseScreen(Ansi.Erase.BACKWARD).cursor(1, 1).reset());

        System.out.println("Players:");
        for(PlayerInfo playerInfo : gameView.getPlayerInfo()){
            System.out.print(playerInfo.username() + ": ");
            if(playerInfo.username().equals(gameView.getFirstPlayerUsername()))
                System.out.print(ansi().fg(Ansi.Color.YELLOW).a("[FIRST] ").reset() + "| ");
            if(playerInfo.username().equals(gameView.getCurrentPlayerUsername()))
                System.out.print(ansi().fg(Ansi.Color.GREEN).a("[CURRENT] ").reset() + "| ");
            if(gameView.getFinalPlayerUsername() != null && gameView.getFinalPlayerUsername().equals(playerInfo.username()))
                System.out.print(ansi().fg(Ansi.Color.RED).a("[LAST] ").reset() + "| ");
            if(playerInfo.tokens().isEmpty())
                System.out.println("no tokens");
            else
                System.out.println(playerInfo.getTokensString());
            System.out.print("\tLast move: ");
            if(playerInfo.lastMovePoints() != null){
                System.out.print("took ");
                for(int i = 0; i < playerInfo.lastMoveTiles().length; i++){
                    System.out.print(ansi().fg(playerInfo.lastMoveTiles()[i].getType().color())
                            .a(playerInfo.lastMoveTiles()[i]).reset());
                    if(i < playerInfo.lastMovePoints().length - 1)
                        System.out.print(", ");
                }
                System.out.print(" from ");
                for(int i = 0; i < playerInfo.lastMovePoints().length; i++){
                    System.out.print(new Point(Constants.livingRoomBoardY - playerInfo.lastMovePoints()[i].getY(),
                            playerInfo.lastMovePoints()[i].getX() + 1));
                    if(i < playerInfo.lastMovePoints().length - 1)
                        System.out.print(", ");
                }
                System.out.println(".");
            }
            else
                System.out.println("hasn't played yet!");
        }
        System.out.println("Common Goal Cards:");
        for(int i = 0; i < gameView.getCGCData().size(); i++){
            System.out.println(" " + (i+1) + ". " + gameView.getCGCData().get(i).toString().replace("\n", "\n    "));
        }
        System.out.println("Current living room board:");
        System.out.print("   ");
        //Printing column numbers (living room board)
        for(int i = 0; i < Constants.livingRoomBoardX; i++){
            System.out.print("   " + (i + 1) + "  ");
        }
        System.out.println();
        int h = Constants.bookshelfY;
        for (int j = Constants.livingRoomBoardY - 1; j >= 0; j--) {
            for(int rowByHalves = 0; rowByHalves < 2; rowByHalves++) {
                //Printing row numbers (living room board)
                if(rowByHalves == 1)
                    System.out.print(" " + (Constants.livingRoomBoardY - j) + " ");
                else
                    System.out.print("   ");

                for (int i = 0; i < Constants.livingRoomBoardX; i++) {
                //Printing rows except last character (living room board)
                    if(rowByHalves == 0){ //First half of each row
                        if(j == Constants.livingRoomBoardY - 1) { // First row
                            if(i == 0) //First column
                                fgDisambiguationPrint(cornerTopLeft + fiveCeilings, false, livingRoomBoardColor);
                            else //Any other column
                                fgDisambiguationPrint(edgeTop + fiveCeilings, false, livingRoomBoardColor);
                        }
                        else { //Any other row
                            if(i == 0) //First column
                                fgDisambiguationPrint(edgeLeft + fiveCeilings, false, livingRoomBoardColor);
                            else //Any other column
                                fgDisambiguationPrint(cross + fiveCeilings, false, livingRoomBoardColor);
                        }

                    }
                    else { //Second half of the row
                        if (gameView.getLivingRoomBoardMatrix()[i][j] != null && !gameView.getLivingRoomBoardMatrix()[i][j].isPlaceholder()) {
                            fgDisambiguationPrint(wall + "  ", false, livingRoomBoardColor);
                            System.out.print(ansi().bold().fg(gameView.getLivingRoomBoardMatrix()[i][j].getType().color())
                                    .a(gameView.getLivingRoomBoardMatrix()[i][j].toString().charAt(0)).reset() + "  ");
                        } else {
                            fgDisambiguationPrint(wall + "     ", false, livingRoomBoardColor);
                        }
                    }
                }
                //Last character of each row
                if(rowByHalves == 0){ //End of first halves
                    if(j == Constants.livingRoomBoardY - 1) //First row
                        fgDisambiguationPrint(cornerTopRight, false, livingRoomBoardColor);
                    else //Any other row
                        fgDisambiguationPrint(edgeRight, false, livingRoomBoardColor);
                }
                else{ //End of second halves
                    fgDisambiguationPrint(wall, false, livingRoomBoardColor);
                }
                //Going to the next line if there are no other tables to print in this line
                if(Constants.livingRoomBoardY - j < 3)
                    System.out.println();

                //Printing bookshelf and personal goal card
                if(Constants.livingRoomBoardY - j == 3 && rowByHalves == 0){
                    //Space between living room board and bookshelf, text
                    System.out.print("             ");
                    System.out.print("Your bookshelf:");
                    System.out.print("                             ");
                    System.out.println("Your personal goal card:");
                }
                //Printing column numbers (bookshelf and personal goal card)
                if(Constants.livingRoomBoardY - j == 3 && rowByHalves == 1){
                    System.out.print("             ");
                    for(int l = 0; l < Constants.bookshelfX; l++){
                        System.out.print("   " + (l+1) + "  ");
                    }
                    System.out.print("              ");
                    for(int l = 0; l < Constants.bookshelfX; l++){
                        System.out.print("   " + (l+1) + "  ");
                    }
                    System.out.println();
                }
                if(Constants.livingRoomBoardY - j >= 4){
                    System.out.print("             ");
                    if(rowByHalves == 0) //Fake for loop using h variable
                        h--;
                    //Printing rows except last character (bookshelf)
                    for (int k = 0; k < Constants.bookshelfX; k++) {
                        if(rowByHalves == 0){ //First half of each row
                            if(h == Constants.bookshelfY - 1) { //First row
                                if(k == 0) //First column
                                    fgDisambiguationPrint(cornerTopLeft + fiveCeilings, false, bookshelfColor);
                                else //Any other column
                                    fgDisambiguationPrint(edgeTop + fiveCeilings, false, bookshelfColor);
                            }
                            else { //Any other row
                                if(k == 0) //First column
                                    fgDisambiguationPrint(edgeLeft + fiveCeilings, false, bookshelfColor);
                                else //Any other column
                                    fgDisambiguationPrint(cross + fiveCeilings, false, bookshelfColor);
                            }
                        }
                        else { //Second half of each row
                            if(gameView.getBookshelfMatrix()[k][h] != null) {
                                fgDisambiguationPrint(wall + "  ", false, bookshelfColor);
                                System.out.print(ansi().bold().fg(gameView.getBookshelfMatrix()[k][h].getType().color())
                                        .a(gameView.getBookshelfMatrix()[k][h].toString().charAt(0)).reset() + "  ");
                            } else {
                                fgDisambiguationPrint(wall + "     ", false, bookshelfColor);
                            }
                        }
                    }
                    //Last character of each row (bookshelf)
                    if(Constants.livingRoomBoardY - j >= 4) {
                        if (rowByHalves == 0) { //First halves
                            if (h == Constants.bookshelfY - 1) //First row
                                fgDisambiguationPrint(cornerTopRight, false, bookshelfColor);
                            else //Any other row
                                fgDisambiguationPrint(edgeRight, false, bookshelfColor);
                        } else //Second halves
                            fgDisambiguationPrint(wall, false, bookshelfColor);
                    }
                    //Space between bookshelf and personal goal card
                    System.out.print("             ");
                    //Printing rows except last character (personal goal card)
                    for (int k = 0; k < Constants.bookshelfX; k++) {
                        if(rowByHalves == 0){ //First half of each row
                            if(h == Constants.bookshelfY - 1) { //First row
                                if(k == 0) //First column
                                    fgDisambiguationPrint(cornerTopLeft + fiveCeilings, false, personalGoalCardColor);
                                else //Any other column
                                    fgDisambiguationPrint(edgeTop + fiveCeilings, false, personalGoalCardColor);
                            }
                            else { //Any other row
                                if(k == 0) //First column
                                    fgDisambiguationPrint(edgeLeft + fiveCeilings, false, personalGoalCardColor);
                                else //Any other column
                                    fgDisambiguationPrint(cross + fiveCeilings, false, personalGoalCardColor);
                            }
                        }
                        else { //Second half of each row
                            if(gameView.getPersonalGoalCardMatrix()[k][h] != null) {
                                //System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
                                fgDisambiguationPrint(wall + "  ", false, personalGoalCardColor);
                                System.out.print(ansi().bold().fg(gameView.getPersonalGoalCardMatrix()[k][h].color())
                                        .a(gameView.getPersonalGoalCardMatrix()[k][h].toString().charAt(0)).reset() + "  ");
                            } else {
                                fgDisambiguationPrint(wall + "     ", false, personalGoalCardColor);
                            }
                        }
                    }
                    //Last character of each row (personal goal card)
                    if(Constants.livingRoomBoardY - j >= 4) {
                        if (rowByHalves == 0) { //First halves
                            if (h == Constants.bookshelfY - 1) //First row
                                fgDisambiguationPrint(cornerTopRight, true, personalGoalCardColor);
                            else //Any other row
                                fgDisambiguationPrint(edgeRight, true, personalGoalCardColor);
                        } else //Second halves
                            fgDisambiguationPrint(wall, true, personalGoalCardColor);
                    }
                }
            }
        }
        System.out.print("   ");
        //Last row of living room board
        fgDisambiguationPrint(cornerBottomLeft + fiveCeilings, false, livingRoomBoardColor);
        for (int j = Constants.livingRoomBoardY - 2; j >= 0; j--) {
            fgDisambiguationPrint(edgeBottom + fiveCeilings, false, livingRoomBoardColor);
        }
        fgDisambiguationPrint(cornerBottomRight, false, livingRoomBoardColor);
        System.out.print("             ");
        //Last row of bookshelf
        fgDisambiguationPrint(cornerBottomLeftAlternative + fiveCeilings, false, bookshelfColor);
        for (int j = Constants.bookshelfX - 2; j >= 0; j--) {
            fgDisambiguationPrint(edgeBottom + fiveCeilings, false, bookshelfColor);
        }
        fgDisambiguationPrint(cornerBottomRightAlternative, false, bookshelfColor);
        System.out.print("             ");
        //Last row of personal goal card
        fgDisambiguationPrint(cornerBottomLeft + fiveCeilings, false, personalGoalCardColor);
        for (int j = Constants.bookshelfX - 2; j >= 0; j--) {
            fgDisambiguationPrint(edgeBottom + fiveCeilings, false, personalGoalCardColor);
        }
        fgDisambiguationPrint(cornerBottomRight, true, personalGoalCardColor);
    }

    /**
     * This method decides whether to print given string with default color or another given color.
     * Default color is used for Windows, other colors are used for operative systems compatible with Unicode and more colors.
     * @param toPrint String to be printed
     * @param endLine Boolean representing the necessity to end the current line after printing
     * @param color Color to use during printing
     */
    private void fgDisambiguationPrint(String toPrint, boolean endLine, int[] color){
        if(isUnicodeCompatible)
            System.out.print(ansi().fgRgb(color[0],color[1],color[2]).a(toPrint).reset());
        else
            System.out.print(toPrint);
        if(endLine)
            System.out.println();
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