package it.polimi.ingsw.view.textual;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.listeners.GameUIListener;
import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.view.textual.charset.Charset;
import it.polimi.ingsw.view.textual.charset.DoubleLineCharset;
import it.polimi.ingsw.view.textual.charset.FuturisticCharset;
import it.polimi.ingsw.view.textual.charset.RoundedCharset;
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

    private GameView lastGameView;

    /** Integer representing living room board color */
    private static final int[] livingRoomBoardColor = {63,99,86};

    /** Integer representing bookshelf color */
    private static final int[] bookshelfColor = {116,44,17};

    /** Integer representing personal goal card color */
    private static final int[] personalGoalCardColor = {116,44,17};

    /** Charset used to print tables. */
    private Charset c = new Charset();

    /** Boolean used to determine how to print characters */
    private boolean isUnicodeCompatible = false;

    public GameUI(){
        if(System.getProperty("os.name").contains("Mac OS X") || System.getProperty("os.name").contains("Linux")){
            this.isUnicodeCompatible = true;
            System.out.println("OS: " + System.getProperty("os.name"));
            System.out.println("Ultra mode activated. Choose your style:");
            System.out.print("""
                1. ╭─────╮\t2. ┏─────┓\t3. ╔═════╗
                   │     │\t   │     │\t   ║     ║
                   ╰─────╯\t   ┗─────┛\t   ╚═════╝
                """);
            Scanner s = new Scanner(System.in);
            int choice;
            do {
                choice = TextualUtils.nextInt(s);
                if(choice < 0 || choice > 3)
                    System.out.println("Invalid selection.");
            } while(choice < 0 || choice > 3);
            switch (choice) {
                case 1 -> this.c = new RoundedCharset();
                case 2 -> this.c = new FuturisticCharset();
                case 3 -> this.c = new DoubleLineCharset();
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

            if(getState() == State.MY_TURN) {
                inputThread = new Thread(() -> {
                    try {
                        executeTurn();
                    } catch (InterruptedException ignored) {}
                });
                inputThread.start();
                try {
                    inputThread.join();
                } catch (InterruptedException ignored) {}
            } else if (getState() == State.ENDED){
                break;
            }
        }
    }

    /**
     * This method should be called when there is a change in the model of the game.
     * @param gameView the new model-view of the game
     */
    public void update(GameView gameView){
        this.lastGameView = gameView;
        if(gameView.isGamePaused()){
            this.gamePaused();
            setState(State.NOT_MY_TURN);
            return;
        }
        // If it's my turn and I'm already in my turn, I don't need to update the board
        if(gameView.getCurrentPlayerIndex() == gameView.getMyIndex() && getState() == State.MY_TURN){
            return;
        }
        this.updateBoard(gameView);
        if(gameView.getCurrentPlayerIndex() == gameView.getMyIndex()) {
            System.out.println("Your turn!");

            setState(State.MY_TURN);
        } else {
            System.out.println("Player " + gameView.getCurrentPlayerUsername() + "'s turn.");

            setState(State.NOT_MY_TURN);
        }
    }

    public void gameFinished(GameView gameView){
        this.lastGameView = gameView;
        this.updateBoard(gameView);

        System.out.println("Game has finished. Final points:");
        gameView.getPlayerInfo().forEach(System.out::println);

        System.out.println("The winner is: " + gameView.getPlayerInfo().get(0).username() + "!");

        setState(State.ENDED);

        notifyListeners(lst, GameUIListener::exit);
    }

    public void gamePaused(){
        System.out.println();
        updateBoard(lastGameView);
        System.out.println(ansi().bold().fg(Ansi.Color.YELLOW)
                .a("Game has been paused since you're the only player left in the game.\nWaiting for someone else to reconnect...").reset());
        if(inputThread != null) inputThread.interrupt();
    }

    /**
     * This method obtains the things that the player wants to do
     */
    private void executeTurn() throws InterruptedException {
        Scanner input = new Scanner(System.in);
        int howManyPick;
        boolean inputValidity;
        int column = 0;
        Point[] points;
        do {
            System.out.print("How many tiles do you want to pick? ");
            howManyPick = TextualUtils.getInputWithBounds(input, Constants.minPick, Constants.maxPick,
                    ("You can pick from " + Constants.minPick + " to " + Constants.maxPick + " tiles."));

            points = new Point[howManyPick];
            updateBoard(this.lastGameView);
            System.out.println("Enter value \"0\" at any time to reset your input.");
            System.out.println("Pick " + howManyPick + " tile(s) in the order you want them to be inserted inside the bookshelf.");
            inputValidity = true;
            for (int i = 0; i < howManyPick; i++) {
                if(inputValidity) {
                    int x, y;
                    System.out.println("Tile #" + (i + 1));
                    System.out.print("Row: ");
                    y = TextualUtils.getInputWithBounds(input, 0, Constants.livingRoomBoardY,
                            ("Row coordinate must be between 1 and " + Constants.livingRoomBoardY + "."));
                    if (y == 0) {
                        inputValidity = false;
                        updateBoard(this.lastGameView);
                    }
                    y = Constants.livingRoomBoardY - y;
                    if(inputValidity){
                        System.out.print("Column: ");
                        x = TextualUtils.getInputWithBounds(input, 0, Constants.livingRoomBoardX,
                                ("Column coordinate must be between 1 and " + Constants.livingRoomBoardX + "."));
                        if (x == 0){
                            inputValidity = false;
                            updateBoard(this.lastGameView);
                        }
                        x = x - 1;
                        points[i] = new Point(x, y);
                    }
                }
            }
            if(inputValidity){
                if(!checkIfTilesCanBeTaken(this.lastGameView.getLivingRoomBoardMatrix(), points)){
                    inputValidity = false;
                    updateBoard(this.lastGameView);
                    System.out.println("Invalid selection. Taken tiles must form a straight line and have at least one free side.");
                }
            }
            if(inputValidity){
                System.out.print("In which column do you want to put the tiles? ");
                column = TextualUtils.getInputWithBounds(input, 0, Constants.bookshelfX,
                        ("Column must be between 1 and " + Constants.bookshelfX + "."));
                if(column == 0) {
                    inputValidity = false;
                    updateBoard(this.lastGameView);
                }
                column = column - 1;
            }
            if(inputValidity){
                if(!checkIfColumnHasEnoughSpace(this.lastGameView.getBookshelfMatrix(), column, points.length)){
                    inputValidity = false;
                    updateBoard(this.lastGameView);
                    System.out.println("Chosen column does not have enough space.");
                }
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
        this.showPlayers(gameView);
        this.showCommonGoalCards(gameView);
        System.out.println(ansi().fg(Ansi.Color.BLUE).a("Current Living Room Board:").reset());
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
                                fgDisambiguationPrint(c.cornerTopLeft + c.fiveCeilings, false, livingRoomBoardColor);
                            else //Any other column
                                fgDisambiguationPrint(c.edgeTop + c.fiveCeilings, false, livingRoomBoardColor);
                        }
                        else { //Any other row
                            if(i == 0) //First column
                                fgDisambiguationPrint(c.edgeLeft + c.fiveCeilings, false, livingRoomBoardColor);
                            else //Any other column
                                fgDisambiguationPrint(c.cross + c.fiveCeilings, false, livingRoomBoardColor);
                        }

                    }
                    else { //Second half of the row
                        if (gameView.getLivingRoomBoardMatrix()[i][j] != null && !gameView.getLivingRoomBoardMatrix()[i][j].isPlaceholder()) {
                            fgDisambiguationPrint(c.wall + "  ", false, livingRoomBoardColor);
                            System.out.print(ansi().bold().fg(gameView.getLivingRoomBoardMatrix()[i][j].getType().color())
                                    .a(gameView.getLivingRoomBoardMatrix()[i][j].toString().charAt(0)).reset() + "  ");
                        } else {
                            fgDisambiguationPrint(c.wall + "     ", false, livingRoomBoardColor);
                        }
                    }
                }
                //Last character of each row
                if(rowByHalves == 0){ //End of first halves
                    if(j == Constants.livingRoomBoardY - 1) //First row
                        fgDisambiguationPrint(c.cornerTopRight, false, livingRoomBoardColor);
                    else //Any other row
                        fgDisambiguationPrint(c.edgeRight, false, livingRoomBoardColor);
                }
                else{ //End of second halves
                    fgDisambiguationPrint(c.wall, false, livingRoomBoardColor);
                }
                //Going to the next line if there are no other tables to print in this line
                if(Constants.livingRoomBoardY - j < 3)
                    System.out.println();

                //Printing bookshelf and personal goal card
                if(Constants.livingRoomBoardY - j == 3 && rowByHalves == 0){
                    //Space between living room board and bookshelf, text
                    System.out.print("             ");
                    System.out.print(ansi().fg(Ansi.Color.BLUE).a("Your Bookshelf:").reset());
                    System.out.print("                             ");
                    System.out.println(ansi().fg(Ansi.Color.BLUE).a("Your Personal Goal Card:").reset());
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
                                    fgDisambiguationPrint(c.cornerTopLeft + c.fiveCeilings, false, bookshelfColor);
                                else //Any other column
                                    fgDisambiguationPrint(c.edgeTop + c.fiveCeilings, false, bookshelfColor);
                            }
                            else { //Any other row
                                if(k == 0) //First column
                                    fgDisambiguationPrint(c.edgeLeft + c.fiveCeilings, false, bookshelfColor);
                                else //Any other column
                                    fgDisambiguationPrint(c.cross + c.fiveCeilings, false, bookshelfColor);
                            }
                        }
                        else { //Second half of each row
                            if(gameView.getBookshelfMatrix()[k][h] != null) {
                                fgDisambiguationPrint(c.wall + "  ", false, bookshelfColor);
                                System.out.print(ansi().bold().fg(gameView.getBookshelfMatrix()[k][h].getType().color())
                                        .a(gameView.getBookshelfMatrix()[k][h].toString().charAt(0)).reset() + "  ");
                            } else {
                                fgDisambiguationPrint(c.wall + "     ", false, bookshelfColor);
                            }
                        }
                    }
                    //Last character of each row (bookshelf)
                    if(Constants.livingRoomBoardY - j >= 4) {
                        if (rowByHalves == 0) { //First halves
                            if (h == Constants.bookshelfY - 1) //First row
                                fgDisambiguationPrint(c.cornerTopRight, false, bookshelfColor);
                            else //Any other row
                                fgDisambiguationPrint(c.edgeRight, false, bookshelfColor);
                        } else //Second halves
                            fgDisambiguationPrint(c.wall, false, bookshelfColor);
                    }
                    //Space between bookshelf and personal goal card
                    System.out.print("             ");
                    //Printing rows except last character (personal goal card)
                    for (int k = 0; k < Constants.bookshelfX; k++) {
                        if(rowByHalves == 0){ //First half of each row
                            if(h == Constants.bookshelfY - 1) { //First row
                                if(k == 0) //First column
                                    fgDisambiguationPrint(c.cornerTopLeft + c.fiveCeilings, false, personalGoalCardColor);
                                else //Any other column
                                    fgDisambiguationPrint(c.edgeTop + c.fiveCeilings, false, personalGoalCardColor);
                            }
                            else { //Any other row
                                if(k == 0) //First column
                                    fgDisambiguationPrint(c.edgeLeft + c.fiveCeilings, false, personalGoalCardColor);
                                else //Any other column
                                    fgDisambiguationPrint(c.cross + c.fiveCeilings, false, personalGoalCardColor);
                            }
                        }
                        else { //Second half of each row
                            if(gameView.getPersonalGoalCardMatrix()[k][h] != null) {
                                //System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
                                fgDisambiguationPrint(c.wall, false, personalGoalCardColor);
                                if(gameView.getBookshelfMatrix()[k][h] == null)
                                    System.out.print("  " + ansi().bold().fg(gameView.getPersonalGoalCardMatrix()[k][h].color())
                                            .a(gameView.getPersonalGoalCardMatrix()[k][h].toString().charAt(0)).reset() + "  ");
                                else if(gameView.getBookshelfMatrix()[k][h].getType() == gameView.getPersonalGoalCardMatrix()[k][h])
                                    System.out.print(" " + ansi().bold().fg(Ansi.Color.DEFAULT).bg(Ansi.Color.GREEN)
                                            .a(" " + gameView.getPersonalGoalCardMatrix()[k][h].toString().charAt(0) + " ").reset() + " ");
                                else
                                    System.out.print(" " + ansi().bold().fg(Ansi.Color.DEFAULT).bg(Ansi.Color.RED)
                                            .a(" " + gameView.getPersonalGoalCardMatrix()[k][h].toString().charAt(0) + " ").reset() + " ");
                            } else {
                                fgDisambiguationPrint(c.wall + "     ", false, personalGoalCardColor);
                            }
                        }
                    }
                    //Last character of each row (personal goal card)
                    if(Constants.livingRoomBoardY - j >= 4) {
                        if (rowByHalves == 0) { //First halves
                            if (h == Constants.bookshelfY - 1) //First row
                                fgDisambiguationPrint(c.cornerTopRight, true, personalGoalCardColor);
                            else //Any other row
                                fgDisambiguationPrint(c.edgeRight, true, personalGoalCardColor);
                        } else //Second halves
                            fgDisambiguationPrint(c.wall, true, personalGoalCardColor);
                    }
                }
            }
        }
        System.out.print("   ");
        //Last row of living room board
        fgDisambiguationPrint(c.cornerBottomLeft + c.fiveCeilings, false, livingRoomBoardColor);
        for (int j = Constants.livingRoomBoardY - 2; j >= 0; j--) {
            fgDisambiguationPrint(c.edgeBottom + c.fiveCeilings, false, livingRoomBoardColor);
        }
        fgDisambiguationPrint(c.cornerBottomRight, false, livingRoomBoardColor);
        System.out.print("             ");
        //Last row of bookshelf
        fgDisambiguationPrint(c.cornerBottomLeftAlternative + c.fiveCeilings, false, bookshelfColor);
        for (int j = Constants.bookshelfX - 2; j >= 0; j--) {
            fgDisambiguationPrint(c.edgeBottom + c.fiveCeilings, false, bookshelfColor);
        }
        fgDisambiguationPrint(c.cornerBottomRightAlternative, false, bookshelfColor);
        System.out.print("             ");
        //Last row of personal goal card
        fgDisambiguationPrint(c.cornerBottomLeft + c.fiveCeilings, false, personalGoalCardColor);
        for (int j = Constants.bookshelfX - 2; j >= 0; j--) {
            fgDisambiguationPrint(c.edgeBottom + c.fiveCeilings, false, personalGoalCardColor);
        }
        fgDisambiguationPrint(c.cornerBottomRight, true, personalGoalCardColor);
    }

    /**
     * This method prints the current status of all players connected to the game.
     * @param gameView Current game's data from which to extract player info.
     */
    private void showPlayers(GameView gameView){
        System.out.println(ansi().fg(Ansi.Color.BLUE).a("Players:").reset());
        for(PlayerInfo playerInfo : gameView.getPlayerInfo()){
            System.out.print(playerInfo.username() + ": ");
            if(playerInfo.isConnected())
                System.out.print(ansi().fg(Ansi.Color.GREEN).a("(CONNECTED) ").reset() + "| ");
            else
                System.out.print(ansi().fg(Ansi.Color.RED).a("(DISCONNECTED) ").reset() + "| ");
            if(playerInfo.username().equals(gameView.getFirstPlayerUsername()))
                System.out.print(ansi().fg(Ansi.Color.YELLOW).a("[FIRST] ").reset() + "| ");
            if(playerInfo.username().equals(gameView.getCurrentPlayerUsername()))
                System.out.print(ansi().fg(Ansi.Color.GREEN).a("[CURRENT] ").reset() + "| ");
            if(gameView.getFinalPlayerUsername() != null && gameView.getFinalPlayerUsername().equals(playerInfo.username()))
                System.out.print(ansi().fg(Ansi.Color.RED).a("[LAST] ").reset() + "| ");
            if(playerInfo.tokens().isEmpty())
                System.out.println("no tokens");
            else
                System.out.println(ansi().fg(Ansi.Color.YELLOW).a(playerInfo.getTokensString()).reset());
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
    }

    /**
     * This method prints the common goal cards present in the current game.
     * @param gameView Current game's data from which to extract common goal card info.
     */
    private void showCommonGoalCards(GameView gameView){
        System.out.println(ansi().fg(Ansi.Color.BLUE).a("Common Goal Cards:").reset());
        for(int i = 0; i < gameView.getCGCData().size(); i++){
            System.out.println(" " + (i+1) + ". " + gameView.getCGCData().get(i).toString().replace("\n", "\n    "));
        }
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