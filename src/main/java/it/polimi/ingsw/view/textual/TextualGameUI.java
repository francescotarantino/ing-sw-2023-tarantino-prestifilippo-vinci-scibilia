package it.polimi.ingsw.view.textual;

import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.listeners.GameUIListener;
import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.utils.TextualUtils;
import it.polimi.ingsw.view.GameUI;
import it.polimi.ingsw.viewmodel.GameView;
import it.polimi.ingsw.viewmodel.PlayerInfo;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import static it.polimi.ingsw.utils.Constants.*;
import static it.polimi.ingsw.utils.GameUtils.checkIfColumnHasEnoughSpace;
import static it.polimi.ingsw.utils.GameUtils.checkIfTilesCanBeTaken;
import static it.polimi.ingsw.listeners.Listener.notifyListeners;
import static it.polimi.ingsw.view.textual.Charset.getUnicodeCharsets;

import static it.polimi.ingsw.utils.TextualUtils.*;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * Main class for the textual version of the GameUI.
 */
public class TextualGameUI extends GameUI {
    /** Enumeration representing the state of the game from the perspective of the player */
    private enum State {
        MY_TURN,
        NOT_MY_TURN,
        ENDED
    }

    /** Current state of the player associated to this GameUI. */
    private State state = State.NOT_MY_TURN;

    /** Lock used to synchronize methods inside Textual GameUI. */
    private final Object lock = new Object();

    /** A thread dedicated to accepting inputs from the player. */
    private Thread inputThread;

    /** The latest GameView received from the server.
     * Contains data about every aspect of the current Game (board state, player state...).
     * */
    private GameView lastGameView;

    /** Integers representing living room board color */
    private static final int[] livingRoomBoardColor = {63,99,86};

    /** Integers representing bookshelf color */
    private static final int[] bookshelfColor = {116,44,17};

    /** Integers representing personal goal card color */
    private static final int[] personalGoalCardColor = {116,44,17};

    /** Charset used to print tables. */
    private Charset c = new Charset();

    /** Boolean used to determine how to print characters */
    private boolean isUnicodeCompatible = false;

    /**
     * TextualGameUI constructor method. Decides whether to activate certain functionalities based on the OS the game is currently running on.
     */
    public TextualGameUI() {
        if(System.getProperty("os.name").contains("Mac OS X") || System.getProperty("os.name").contains("Linux")){
            this.isUnicodeCompatible = true;
            showUnicodeCharsets(getUnicodeCharsets());
            int choice = nextInt(new Scanner(System.in), 1, getUnicodeCharsets().size(), "Invalid selection.");
            System.out.println("You chose " + getUnicodeCharsets().get(choice - 1).name + "!");
            this.c = getUnicodeCharsets().get(choice - 1);
        }
    }

    /**
     * Used in {@link #showUnicodeCharsets(List)}} to print the first line of available charsets.
     * @param charsets list of available charsets
     */
    private void printFirstLine(List<Charset> charsets) {
        for (int i = 0; i < charsets.size(); i++) {
            System.out.print((i + 1) + ". ");
            System.out.print(charsets.get(i).cornerTopLeft);
            System.out.print(charsets.get(i).fiveCeilings);
            System.out.print(charsets.get(i).cornerTopRight);
            System.out.print("\t");
        }
        System.out.println();
    }

    /**
     * Used in {@link #showUnicodeCharsets(List)}} to print the second line of available charsets.
     * @param charsets list of available charsets
     */
    private void printSecondLine(List<Charset> charsets) {
        for (Charset charset : charsets) {
            System.out.print("   ");
            System.out.print(charset.wall);
            System.out.print("     ");
            System.out.print(charset.wall);
            System.out.print("\t");
        }
        System.out.println();
    }

    /**
     * Used in {@link #showUnicodeCharsets(List)}} to print the third line of available charsets.
     * @param charsets list of available charsets
     */
    private void printThirdLine(List<Charset> charsets) {
        for (Charset charset : charsets) {
            System.out.print("   ");
            System.out.print(charset.cornerBottomLeft);
            System.out.print(charset.fiveCeilings);
            System.out.print(charset.cornerBottomRight);
            System.out.print("\t");
        }
        System.out.println();
    }

    /**
     * This method is used to print the available unicode charsets.
     * @param charsets the list of charsets
     */
    private void showUnicodeCharsets(List<Charset> charsets) {
        System.out.println("OS: " + System.getProperty("os.name"));
        System.out.println("Ultra mode activated. Choose your style:");
        printFirstLine(charsets);
        printSecondLine(charsets);
        printThirdLine(charsets);
    }

    /**
     * This method is used to get the current state of the player.
     * @return the current state
     */
    private State getState() {
        synchronized (lock) {
            return this.state;
        }
    }

    /**
     * This method is used to set the current state of the player.
     * @param state  the state to set the player to
     */
    private void setState(State state) {
        synchronized (lock) {
            this.state = state;
            lock.notifyAll();
        }
    }

    /**
     * Runs the Textual GameUI.
     * This method enables the usage of AnsiConsole for custom textual output
     * and enters a loop to wait for and execute player's turns.
     */
    @Override
    public void run() {
        AnsiConsole.systemInstall();

        while (true) {
            while (getState() == State.NOT_MY_TURN) {
                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException ignored) {}
                }
            }

            if (getState() == State.MY_TURN) {
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
     * If this method is called whilst the player is already performing its turn, it doesn't need to update the board, so it returns
     * @param gameView the new model-view of the game
     */
    @Override
    public void update(GameView gameView) {
        this.lastGameView = gameView;
        if (gameView.isGamePaused()) {
            this.gamePaused();
            setState(State.NOT_MY_TURN);
            return;
        }

        if(gameView.getErrorMessage() != null){
            System.out.println("\n" + ansi().fg(RED).bold().a(gameView.getErrorMessage()).reset() + "\n");
        }

        if (gameView.getCurrentPlayerIndex() == gameView.getMyIndex() && getState() == State.MY_TURN) {
            return;
        }

        this.updateBoard(gameView);
        if (gameView.getCurrentPlayerIndex() == gameView.getMyIndex()) {
            System.out.println("Your turn!");
            setState(State.MY_TURN);
        } else {
            System.out.println("Player " + gameView.getCurrentPlayerUsername() + "'s turn.");
            setState(State.NOT_MY_TURN);
        }
    }

    /**
     * Called by the client implementation when the game ends, shows scores for all players in a dialog.
     * @param gameView Information about the current state of the game, received from the server.
     */
    @Override
    public void gameEnded(GameView gameView) {
        this.lastGameView = gameView;
        this.updateBoard(gameView);
        if (!gameView.isWalkover()) {
            List<PlayerInfo> orderedPlayers = gameView.getPlayerInfo()
                    .stream()
                    .sorted(Comparator.comparingInt(PlayerInfo::points).reversed())
                    .toList();

            System.out.println("Game has ended. Final points:");
            orderedPlayers.forEach(System.out::println);
            System.out.println("The winner is: " + orderedPlayers.get(0).username() + "!");

        } else {
            System.out.println("Game has ended. You were the only player left in the game. You win!");
        }

        setState(State.ENDED);
        notifyListeners(lst, GameUIListener::exit);
    }

    /**
     * This method is called when the game is paused.
     */
    public void gamePaused() {
        System.out.println();
        updateBoard(lastGameView);
        System.out.println(ansi().bold().fg(Ansi.Color.YELLOW)
                .a("""
                        Game has been paused since you're the only player left in the game.
                        Waiting for someone else to reconnect...
                        """).reset());
        if (inputThread != null) {
            inputThread.interrupt();
        }
    }

    /**
     * This method performs the player's turn. It asks the user to select the tiles to place and the column to place them in.
     */
    private void executeTurn() throws InterruptedException {
        Scanner input = new Scanner(System.in);
        while (true) {
            List<Point> points = selectLivingRoomTiles(input);
            if(!points.isEmpty()) {
                Integer column = selectColumn(input, points);
                if (column != null) {
                    if (getState() == State.MY_TURN)
                        notifyListeners(lst, x -> x.performTurn(column, points.toArray(Point[]::new)));
                    setState(State.NOT_MY_TURN);
                    break;
                } else {
                    revertPick(points);
                }
            }
        }
    }

    /**
     * This method is used to confirm the tiles' selection validity.
     * @param input the scanner used to read the input
     * @return the list of selected tiles to insert in the bookshelf
     * @throws InterruptedException if the thread is interrupted
     */
    private List<Point> selectLivingRoomTiles(Scanner input) throws InterruptedException {
        List<Point> points = new ArrayList<>();
        boolean tilesCanBeTaken = false;
        while(!tilesCanBeTaken) {
            System.out.print("How many tiles do you want to pick? ");
            int howManyPick = TextualUtils.nextIntInterruptible(input, Constants.minPick, Constants.maxPick,
                    ("You can pick from " + Constants.minPick + " to " + Constants.maxPick + " tiles."));
            updateBoard(this.lastGameView);
            System.out.println("Enter value \"0\" at any time to reset your input.");
            while (points.size() < howManyPick) {
                System.out.print("Row: ");
                int row = nextIntInterruptible(input, 0, livingRoomBoardY, "Row coordinate must be between 1 and " + livingRoomBoardY + ".");
                if (row == 0) {
                    break;
                }
                System.out.print("Column: ");
                int column = nextIntInterruptible(input, 0, livingRoomBoardX, ("Column coordinate must be between 1 and " + livingRoomBoardX + "."));
                if (column == 0) {
                    break;
                }
                points.add(new Point( column - 1, livingRoomBoardY - row));
            }

            if (points.size() != howManyPick) {
                revertPick(points);
            }
            else{
                tilesCanBeTaken = checkIfTilesCanBeTaken(lastGameView.getLivingRoomBoardMatrix(), points.toArray(Point[]::new));
                if(!tilesCanBeTaken){
                    revertPick(points);
                    System.out.println("Invalid selection. Taken tiles must form a straight line and have at least one free side.");
                }
            }
        }
        return points;
    }

    /**
     * This method clears all the tiles selected by the user.
     * @param points the list of tiles to clear
     */
    private void revertPick(List <Point> points) {
        points.clear();
        updateBoard(this.lastGameView);
    }

    /**
     * This method is used to select the column in which the tiles will be placed, checking if the column has enough space.
     * @param input the scanner used to read the input
     * @param points the list of selected tiles
     * @return the column in which the tiles will be placed
     * @throws InterruptedException if the thread is interrupted
     */
    private Integer selectColumn(Scanner input, List<Point> points) throws InterruptedException {
        System.out.print("In which column do you want to put the " + (points.size() == 1 ? "tile" : "tiles") + "? ");
        int column = nextIntInterruptible(input, 0, bookshelfX, ("Column must be between 1 and " + bookshelfX + ".")) - 1;
        if(column == -1)
            return null;
        boolean columnHasEnoughSpace = checkIfColumnHasEnoughSpace(this.lastGameView.getBookshelfMatrix(), column, points.size());
        if(columnHasEnoughSpace) {
            System.out.print("You are placing " + points.size() + (points.size() == 1 ? " tile" : " tiles")+ " in column " + (column + 1) + ":");
            printPoints(points);
            System.out.println(".");
            System.out.print("Do you want to continue? Select \"Y\" to confirm, or \"N\" to pick again: ");
            if(!isN(input, "Select either \"N\" or \"Y\".")) {
                return column;
            }
        }
        if (!columnHasEnoughSpace) {
            System.out.println("Chosen column does not have enough space.");
        }
        return null;
    }

    /**
     * This method prints the actual living room board and the player's bookshelf.
     * @param gameView Information about the current state of the game.
     */
    private void updateBoard(GameView gameView) {
        clearScreen(gameView);
        printLivingRoomColumnNumbers();
        for (int screenLine = (2 * (livingRoomBoardY)); screenLine >= 1; screenLine--) {
            printLivingRoomRowNumber(screenLine);
            for (int column = 0; column < livingRoomBoardX; column++) {
                if (screenLine % 2 == 0) {
                    printBorderRow(column, (screenLine/2), livingRoomBoardY, livingRoomBoardColor);
                } else {
                    printCenterRow(column, (screenLine/2), gameView.getLivingRoomBoardMatrix(), livingRoomBoardColor);
                }
            }
            printLastCharacter(screenLine, livingRoomBoardColor, false, livingRoomBoardY);
            updateBookShelfAndPGC(screenLine, gameView);
        }
        printLastRows();
    }

    /** Clears the screen and reprints the game state.
     * @param gameView Information about the current state of the game.
     * */
    private void clearScreen(GameView gameView) {
        System.out.print(ansi().eraseScreen(Ansi.Erase.BACKWARD).cursor(1, 1).reset());
        this.showPlayers(gameView);
        this.showCommonGoalCards(gameView);
        System.out.println(ansi().fg(Ansi.Color.BLUE).a("Current Living Room Board:").reset());
        System.out.print("   ");
    }

    /**
     * Prints a row of integers representing column indexes.
     * */
    private void printLivingRoomColumnNumbers() {
        for(int i = 0; i < livingRoomBoardX; i++){
            System.out.print("   " + (i + 1) + "  ");
        }
        System.out.println();
    }

    /**
     * Prints an integer representing a row index.
     * @param screenLine An integer representing the current line to print.
     */
    private void printLivingRoomRowNumber(int screenLine) {
        if (screenLine % 2 == 0) {
            System.out.print("   ");
        }
        else {
            System.out.print(" " + (livingRoomBoardY - (screenLine/2))  + " ");
        }
    }

    /**
     * Prints the border of a matrix's row, used when printing Living Room Board, Bookshelf and Personal Goal Card.
     * @param column Integer representing the column to print.
     * @param row Integer representing the row to print.
     * @param numOfRows Integer representing the total number of rows of the matrix being currently printed.
     * @param color Array of integers representing the color to use when printing characters, in RGB.
     * */
    private void printBorderRow(int column, int row, int numOfRows, int[] color) {
        if(row == numOfRows) {
            if (column == 0) {
                fgDisambiguationPrint(c.cornerTopLeft + c.fiveCeilings, false, color);
            }
            else {
                fgDisambiguationPrint(c.edgeTop + c.fiveCeilings, false, color);
            }
        }

        else {
            if (column == 0) {
                fgDisambiguationPrint(c.edgeLeft + c.fiveCeilings, false, color);
            }
            else {
                fgDisambiguationPrint(c.cross + c.fiveCeilings, false, color);
            }
        }
    }

    /**
     * Prints the center of a matrix's row, used when printing Living Room Board and Bookshelf.
     * @param column Integer representing the column to print.
     * @param row Integer representing the row to print.
     * @param matrix Contains data about the content of the matrix.
     * @param color Array of integers representing the color to use when printing characters, in RGB.
     * */
    private void printCenterRow(int column, int row, Tile[][] matrix, int[] color) {
        if (matrix[column][row] != null && !matrix[column][row].isPlaceholder()) {
            fgDisambiguationPrint(c.wall + "  ", false, color);
            System.out.print(ansi().bold().fg(matrix[column][row].getType().color())
                    .a(matrix[column][row].toString().charAt(0)).reset() + "  ");
        } else {
            fgDisambiguationPrint(c.wall + "     ", false, color);
        }
    }

    /**
     * Prints the last character of a matrix's row, used when printing Living Room Board, Bookshelf and Personal Goal Card.
     * @param screenLine Integer representing the line being currently printed.
     * @param color Array of integers representing the color to use when printing characters, in RGB.
     * @param endLine Boolean value specifying whether it is necessary to end the current line after printing.
     * @param numOfColumns Integer representing the total number of columns of the matrix being currently printed.
     * */
    private void printLastCharacter(int screenLine, int[] color, boolean endLine, int numOfColumns) {
        if(screenLine % 2 == 0) {
            if((screenLine / 2) == numOfColumns) {
                fgDisambiguationPrint(c.cornerTopRight, endLine, color);
            }
            else {
                fgDisambiguationPrint(c.edgeRight, endLine, color);
            }
        }
        else {
            fgDisambiguationPrint(c.wall, endLine, color);
        }
    }

    /**
     * Prints the current state of Bookshelf and Personal Goal Card.
     * @param screenLine Integer representing the line being currently printed.
     * @param gameView Contains information about the game, used to print Bookshelf and Personal Goal Card.
     * */
    private void updateBookShelfAndPGC(int screenLine, GameView gameView) {
        if (screenLine > 14) {
            System.out.println();
        }
        else if (screenLine == 14) {
            printBookshelfAndPGCText();
        }
        else if (screenLine == 13) {
            printBookshelfAndPGCColumnNumbers();
        }
        else {
            System.out.print("             ");
            for (int column = 0; column < bookshelfX; column++) {
                if (screenLine % 2 == 0) {
                    printBorderRow(column, (screenLine/2), bookshelfY, bookshelfColor);
                } else {
                    printCenterRow(column, (screenLine/2), gameView.getBookshelfMatrix(), bookshelfColor);
                }
                if(column == bookshelfX - 1){

                    printLastCharacter(screenLine, bookshelfColor, false, bookshelfY);
                    System.out.print("             ");
                }
            }

            for (int column = 0; column < bookshelfX; column++) {
                if (screenLine % 2 == 0) {
                    printBorderRow(column, screenLine/2, bookshelfY, personalGoalCardColor);
                }
                else {
                    printPCGCenterRow(column, screenLine/2, gameView.getBookshelfMatrix(), gameView.getPersonalGoalCardMatrix());
                }
                if(column == bookshelfX - 1){
                    printLastCharacter(screenLine, personalGoalCardColor, true, bookshelfY);
                }
            }
        }
    }

    /**
     * Prints the border of a matrix's row, used when printing Personal Goal Card.
     * @param column Integer representing the column to print.
     * @param row Integer representing the row to print.
     * @param BookshelfMatrix Contains data about the content of the matrix, specifically related to the Bookshelf.
     * @param PGCMatrix Contains data about the content of the matrix, specifically related to the Personal Goal Card.
     * */
    private void printPCGCenterRow(int column, int row, Tile[][] BookshelfMatrix, Constants.TileType[][] PGCMatrix) {
        if(PGCMatrix[column][row] != null) {
            fgDisambiguationPrint(c.wall, false, personalGoalCardColor);
            if(BookshelfMatrix[column][row] == null)
                System.out.print("  " + ansi().bold().fg(PGCMatrix[column][row].color())
                        .a(PGCMatrix[column][row].toString().charAt(0)).reset() + "  ");
            else if(BookshelfMatrix[column][row].getType() == PGCMatrix[column][row])
                System.out.print(" " + ansi().bold().fg(Ansi.Color.DEFAULT).bg(Ansi.Color.GREEN)
                        .a(" " + PGCMatrix[column][row].toString().charAt(0) + " ").reset() + " ");
            else
                System.out.print(" " + ansi().bold().fg(Ansi.Color.DEFAULT).bg(RED)
                        .a(" " + PGCMatrix[column][row].toString().charAt(0) + " ").reset() + " ");
        } else {
            fgDisambiguationPrint(c.wall + "     ", false, personalGoalCardColor);
        }
    }

    /**
     * Prints text used to identify the Bookshelf and Personal Goal Card.
     */
    private void printBookshelfAndPGCText() {
        System.out.print("             ");
        System.out.print(ansi().fg(Ansi.Color.BLUE).a("Your Bookshelf:").reset());
        System.out.print("                             ");
        System.out.println(ansi().fg(Ansi.Color.BLUE).a("Your Personal Goal Card:").reset());
    }

    /**
     * Prints a row of integers representing column indexes.
     */
    private void printBookshelfAndPGCColumnNumbers() {
        System.out.print("             ");
        for(int l = 0; l < bookshelfX; l++){
            System.out.print("   " + (l+1) + "  ");
        }
        System.out.print("              ");
        for(int l = 0; l < bookshelfX; l++){
            System.out.print("   " + (l+1) + "  ");
        }
        System.out.println();
    }

    /**
     * Prints the last row of each matrix.
     */
    private void printLastRows() {
        System.out.print("   ");
        printLivingRoomLastRow();
        printBookshelfLastRow();
        printPersonalGoalCardLastRow();
    }

    /**
     * Prints the last row of the Living Room Board.
     */
    private void printLivingRoomLastRow() {
        fgDisambiguationPrint(c.cornerBottomLeft + c.fiveCeilings, false, livingRoomBoardColor);
        for (int j = livingRoomBoardY - 2; j >= 0; j--) {
            fgDisambiguationPrint(c.edgeBottom + c.fiveCeilings, false, livingRoomBoardColor);
        }
        fgDisambiguationPrint(c.cornerBottomRight, false, livingRoomBoardColor);
        System.out.print("             ");
    }

    /**
     * Prints the last row of the Bookshelf.
     */
    private void printBookshelfLastRow() {
        fgDisambiguationPrint(c.cornerBottomLeftAlternative + c.fiveCeilings, false, bookshelfColor);
        for (int j = bookshelfX - 2; j >= 0; j--) {
            fgDisambiguationPrint(c.edgeBottom + c.fiveCeilings, false, bookshelfColor);
        }
        fgDisambiguationPrint(c.cornerBottomRightAlternative, false, bookshelfColor);
        System.out.print("             ");
    }

    /**
     * Prints the last row of the Personal Goal Card.
     */
    private void printPersonalGoalCardLastRow() {
        fgDisambiguationPrint(c.cornerBottomLeft + c.fiveCeilings, false, personalGoalCardColor);
        for (int j = bookshelfX - 2; j >= 0; j--) {
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
                System.out.print(ansi().fg(RED).a("(DISCONNECTED) ").reset() + "| ");
            if(playerInfo.username().equals(gameView.getFirstPlayerUsername()))
                System.out.print(ansi().fg(Ansi.Color.YELLOW).a("[FIRST] ").reset() + "| ");
            if(playerInfo.username().equals(gameView.getCurrentPlayerUsername()))
                System.out.print(ansi().fg(Ansi.Color.GREEN).a("[CURRENT] ").reset() + "| ");
            if(playerInfo.isLast())
                System.out.print(ansi().fg(RED).a("[LAST] ").reset() + "| ");
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
                    System.out.print(new Point(livingRoomBoardY - playerInfo.lastMovePoints()[i].y(),
                            playerInfo.lastMovePoints()[i].x() + 1));
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
     * This method decides whether to print the given string with default color or another given color.
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
}