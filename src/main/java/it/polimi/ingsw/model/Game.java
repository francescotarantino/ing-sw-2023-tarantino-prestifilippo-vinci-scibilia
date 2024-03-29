package it.polimi.ingsw.model;

import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.exception.InvalidChoiceException;
import it.polimi.ingsw.exception.PreGameException;
import it.polimi.ingsw.exception.NoFreeBookshelfException;
import it.polimi.ingsw.exception.UsernameTakenException;
import it.polimi.ingsw.listeners.GameListener;
import it.polimi.ingsw.model.goal_cards.CommonGoalCard;
import it.polimi.ingsw.model.goal_cards.PersonalGoalCard;
import it.polimi.ingsw.utils.GameUtils;
import it.polimi.ingsw.viewmodel.PlayerInfo;

import java.util.*;

import static it.polimi.ingsw.utils.Utils.extractRandomIDsWithoutDuplicates;
import static it.polimi.ingsw.listeners.Listener.notifyListeners;

/**
 * This class represents a game.
 */
public class Game {
    /**
     * Identifies the game with a unique and positive ID
     */
    private final int gameID;
    /**
     * Array of bookshelves, each one representing a player
     */
    private final Bookshelf[] bookshelves;
    /**
     * Array of random numbers, used to obtain personal goal cards without repetition
     */
    private final int[] randomPGCNumbers;
    /**
     * Bag of the game, which contains the tiles that a player can pick
     */
    private final Bag bag;
    /**
     * Board of the game
     */
    private final LivingRoomBoard livingRoomBoard;
    /**
     * Array of common goal cards, shared between all players, on which the controller can call the {@link CommonGoalCard#checkValidity(Tile[][])} method
     */
    private final CommonGoalCard[] commonGoalCards;
    /**
     * Index of the first player that has to make a move. It is calculated randomly when the class is instantiated.
     */
    private final int firstPlayerIndex;
    /**
     * Index of the current player that has to make a move. If it's equal to -1, the game has not started yet.
     */
    private int currentPlayerIndex;
    /**
     * Index of the final player. It is equal to -1 until the last round.
     */
    private int finalPlayerIndex;
    /**
     * List of listeners that are notified when the game model changes.
     */
    private final List<GameListener> lst = new ArrayList<>();
    /**
     * Boolean representing whether the game has ended or not.
     */
    private boolean gameEnded = false;
    /**
     * Boolean representing that the game has ended because of a walkover.
     */
    private boolean walkover = false;
    /**
     * Boolean representing whether the game is paused or not.
     * A game is paused when there is only one player connected to the game.
     */
    private boolean isPaused = false;
    /**
     * This string contains the latest error message that occurred in the game.
     * The error message is received from the controller and sent to the view.
     */
    private String errorMessage;

    /**
     * Creates a new game
     * @param ID the ID of the game
     * @param numberOfPlayers the maximum number of players in the game
     * @param newPlayer the first player that will be added to the game
     * @param numberOfCommonGoalCards the number of common goal cards in the game
     */
    public Game(int ID, int numberOfPlayers, Player newPlayer, int numberOfCommonGoalCards) throws PreGameException {
        if(numberOfPlayers < Constants.playersLowerBound || numberOfPlayers > Constants.playersUpperBound)
            throw new InvalidChoiceException("Illegal number of players. A game should have " + Constants.playersLowerBound + " to " + Constants.playersUpperBound + " players");

        if(ID <= Constants.IDLowerBound)
            throw new IllegalArgumentException("Game ID must be greater than " + Constants.IDLowerBound);

        if(numberOfCommonGoalCards < Constants.minCommonGoalCards || numberOfCommonGoalCards > Constants.maxCommonGoalCards)
            throw new InvalidChoiceException("Illegal number of common goal cards. A game should have between " + Constants.minCommonGoalCards + " and " + Constants.maxCommonGoalCards + " common goal cards");

        // Game construction

        // Assigning ID, generating an appropriate board and a bag
        this.gameID = ID;
        this.livingRoomBoard = new LivingRoomBoard(numberOfPlayers);
        this.bag = new Bag();

        // Generating an adequate number of random numbers,
        // used as indexes for class PersonalGoalCards to generate cards with no repetition
        randomPGCNumbers = extractRandomIDsWithoutDuplicates(numberOfPlayers, GameUtils.getPersonalGoalCards().size());

        // Generating an adequate number of bookshelves and creating the first bookshelf for the first player
        this.bookshelves = new Bookshelf[numberOfPlayers];
        this.addBookshelf(newPlayer);

        // Generating the common goal cards
        this.commonGoalCards = new CommonGoalCard[numberOfCommonGoalCards];
        int[] randomCGCNumbers = extractRandomIDsWithoutDuplicates(numberOfCommonGoalCards, Constants.totalNumberOfCommonGoalCards);
        for(int i = 0; i < numberOfCommonGoalCards; i++){
            this.commonGoalCards[i] = CommonGoalCard.create(numberOfPlayers, randomCGCNumbers[i] + 1);
        }

        // Extracting the first player index
        this.firstPlayerIndex = (new Random()).nextInt(numberOfPlayers);

        // Current and final player indexes are set -1 by default
        this.currentPlayerIndex = -1;
        this.finalPlayerIndex = -1;

        this.errorMessage = null;
    }

    /**
     * Adds a new bookshelf to the game
     * @param player the new player that will be added to the game
     * @return the index of the new bookshelf
     */
    public int addBookshelf(Player player) throws PreGameException {
        int i;
        for (i = 0; i < this.getTotalPlayersNumber(); i++) {
            if (this.bookshelves[i] != null){
                if(this.bookshelves[i].getPlayer().getUsername().equals(player.getUsername())) {
                    throw new UsernameTakenException();
                }
            } else break;
        }

        if(i == this.getTotalPlayersNumber()) {
            throw new NoFreeBookshelfException();
        }

        bookshelves[i] = new Bookshelf(player, new PersonalGoalCard(randomPGCNumbers[i]));
        notifyListeners(lst, GameListener::playerJoinedGame);
        if(this.isFull()){
            notifyListeners(lst, GameListener::gameIsFull);
        }
        return i;
    }

    public int getGameID(){
        return this.gameID;
    }

    public int getTotalPlayersNumber(){
        return this.bookshelves.length;
    }

    public LivingRoomBoard getLivingRoomBoard(){
        return this.livingRoomBoard;
    }

    public Bag getBag(){
        return this.bag;
    }

    public Bookshelf[] getBookshelves() {
        return this.bookshelves;
    }

    public CommonGoalCard[] getCommonGoalCards() {
        return this.commonGoalCards;
    }

    public int getFirstPlayerIndex() {
        return this.firstPlayerIndex;
    }

    public int getCurrentPlayerIndex() {
        return this.currentPlayerIndex;
    }

    /**
     * This method returns the current player that has to make a move, or null if the game has not started yet.
     * @return the current player, or null if the game has not started yet
     */
    public Player getCurrentPlayer() {
        if(this.currentPlayerIndex != -1)
            return this.bookshelves[this.currentPlayerIndex].getPlayer();
        else
            return null;
    }

    /**
     * This method returns a player from the bookshelves, given its index.
     * @param i the index
     * @return the player at the given index
     */
    public Player getPlayer(int i) {
        if (this.bookshelves[i] != null) {
            return this.bookshelves[i].getPlayer();
        } else {
            return null;
        }
    }

    /**
     * This method returns a list of all the players in the game, from the bookshelves.
     * @return a list of players
     */
    public List<Player> getPlayers() {
        return Arrays.stream(this.bookshelves)
                .filter(Objects::nonNull)
                .map(Bookshelf::getPlayer)
                .toList();
    }

    /**
     * Sets the current player index.
     * @param currentPlayerIndex the new current player index
     */
    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        if(currentPlayerIndex < 0 || currentPlayerIndex >= this.getTotalPlayersNumber())
            throw new IllegalArgumentException("Illegal player index");

        this.currentPlayerIndex = currentPlayerIndex;

        notifyListeners(lst, GameListener::modelChanged);
    }

    /**
     * Sets the final player index, that is the index of the player that has first filled its bookshelf.
     * @param finalPlayerIndex the final player index
     */
    public void setFinalPlayerIndex(int finalPlayerIndex) {
        if(finalPlayerIndex < 0 || finalPlayerIndex >= this.getTotalPlayersNumber())
            throw new IllegalArgumentException("Illegal player index");
        if (this.finalPlayerIndex != -1) {
            throw new IllegalArgumentException("Final player already set");
        }

        this.finalPlayerIndex = finalPlayerIndex;
    }

    public int getFinalPlayerIndex() {
        return this.finalPlayerIndex;
    }

    /**
     * Checks if the game has reached the maximum number of players allowed.
     * @return true if the game is full, false otherwise
     */
    public boolean isFull() {
        for (Bookshelf bookshelf : bookshelves) {
            if (bookshelf == null) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the game has started.
     * @return true if the game has already started, false otherwise
     */
    public boolean isStarted() {
        return this.currentPlayerIndex != -1;
    }

    /**
     * This method returns the list of players in the game
     * @return an ArrayList of Strings containing the usernames of the players in the game
     */
    public List<String> playersList() {
        List<String> players = new ArrayList<>();
        for (Bookshelf bookshelf : bookshelves) {
            if (bookshelf != null) {
                players.add(bookshelf.getPlayer().getUsername());
            }
        }

        return players;
    }

    /**
     * This method sets the game as ended and notifies the listeners.
     */
    public void setGameEnded(){
        this.gameEnded = true;
        notifyListeners(lst, GameListener::gameEnded);

        System.out.println("Game " + this.getGameID() + " has ended.");
        if(!isWalkover()){
            getPlayerInfo()
                    .stream()
                    .max(Comparator.comparingInt(PlayerInfo::points))
                    .ifPresent(winner ->
                        System.out.println("The winner is " + winner.username() + " with " + winner.points() + " points.")
                    );
        }
    }

    /**
     * @return true if the game is ended, false otherwise
     */
    public boolean isEnded(){
        return this.gameEnded;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();

        string.append(this.getGameID()).append(" ");

        for(int i=0; i<getTotalPlayersNumber(); i++){
            if(this.getBookshelves()[i] != null)
                string.append(this.getBookshelves()[i].getPlayer().getUsername()).append(" ");
        }

        if(this.isStarted()){
            string.append("(STARTED)");
        } else if(this.isFull()){
            string.append("(FULL)");
        }

        return string.toString();
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public int getConnectedPlayersNumber() {
        return Math.toIntExact(this.getPlayers().stream().filter(Player::isConnected).count());
    }

    /**
     * This method sets the connected status of a player and notifies the listeners of the change.
     * @param playerIndex index of the player to set the connected status
     * @param connected new status
     */
    public void setPlayerConnected(int playerIndex, boolean connected) {
        if(playerIndex < 0 || playerIndex >= this.getTotalPlayersNumber())
            throw new IllegalArgumentException("Illegal player index");

        this.getPlayer(playerIndex).setConnected(connected);

        // Set game paused only if there is only one player left
        this.setPaused(getConnectedPlayersNumber() == 1);

        notifyListeners(lst, GameListener::modelChanged);
    }

    public List<PlayerInfo> getPlayerInfo(){
        return this.getPlayers()
                .stream()
                .map(p -> {
                    boolean isLast = false;
                    if (getFinalPlayerIndex() != -1) {
                        isLast = p.equals(
                                getPlayer((getFinalPlayerIndex() - 1) >= 0 ? (getFinalPlayerIndex() - 1) : (getTotalPlayersNumber() - 1))
                        );
                    }

                    return new PlayerInfo(
                            p.getUsername(),
                            p.getPoints(),
                            p.getScoringTokens(),
                            p.getLastMovePoints(),
                            p.getLastMoveTiles(),
                            p.isConnected(),
                            isLast
                    );
                })
                .toList();
    }

    public boolean isWalkover() {
        return walkover;
    }

    public void setWalkover(boolean walkover) {
        this.walkover = walkover;
    }

    public void setErrorMessage(String error) {
        this.errorMessage = error;
        if(error != null){
            notifyListeners(lst, GameListener::modelChanged);
        }
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void addListener(GameListener o){
        if(!lst.contains(o)){
            lst.add(o);
        }
    }

    public void removeListener(GameListener o){
        lst.remove(o);
    }
}