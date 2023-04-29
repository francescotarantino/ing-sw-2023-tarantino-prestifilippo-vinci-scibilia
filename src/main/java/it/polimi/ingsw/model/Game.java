package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.listeners.GameListener;
import it.polimi.ingsw.model.goal_cards.CommonGoalCard;
import it.polimi.ingsw.model.goal_cards.PersonalGoalCard;

import java.util.*;

import static it.polimi.ingsw.listeners.Listener.notifyListeners;

public class Game {
    // Attributes
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
     * Index of the current player that has to make a move. If it's equal to -1 the game has not started yet.
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
     * Boolean representing whether the game has finished or not
     */
    private boolean gameFinished = false;

    // Constructor
    /**
     * Creates a new game
     * @param ID the ID of the game
     * @param numberOfPlayers the maximum number of players in the game
     * @param newPlayer the first player that will be added to the game
     * @param numberOfCommonGoalCards the number of common goal cards in the game
     */
    public Game(int ID, int numberOfPlayers, Player newPlayer, int numberOfCommonGoalCards){
        if(numberOfPlayers < Constants.playersLowerBound || numberOfPlayers > Constants.playersUpperBound)
            throw new IllegalArgumentException("Illegal number of players. A game should have " + Constants.playersLowerBound + " to " + Constants.playersUpperBound + " players");

        if(ID <= Constants.IDLowerBound)
            throw new IllegalArgumentException("Game ID must be greater than " + Constants.IDLowerBound);

        if(numberOfCommonGoalCards < Constants.minCommonGoalCards || numberOfCommonGoalCards > Constants.maxCommonGoalCards)
            throw new IllegalArgumentException("Illegal number of common goal cards. A game should have between " + Constants.minCommonGoalCards + " and " + Constants.maxCommonGoalCards + " common goal cards");

        // Game construction

        // Assigning ID, generating an appropriate board and a bag
        this.gameID = ID;
        this.livingRoomBoard = new LivingRoomBoard(numberOfPlayers);
        this.bag = new Bag();

        // Generating an adequate amount of random numbers, used as indexes for class PersonalGoalCards to generate cards with no repetition
        randomPGCNumbers = extractRandomIDsWithoutDuplicates(numberOfPlayers, Constants.getPersonalGoalCards().size());

        // Generating an adequate amount of bookshelves and creating the first bookshelf for the first player
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
    }

    // Public methods
    /**
     * Adds a new bookshelf to the game
     * @param player the new player that will be added to the game
     * @return the index of the new bookshelf
     */
    public int addBookshelf(Player player){
        int i;
        for (i = 0; i < this.getTotalPlayersNumber(); i++) {
            if (this.bookshelves[i] != null){
                if(this.bookshelves[i].getPlayer().getUsername().equals(player.getUsername())) {
                    throw new IllegalArgumentException("Username already present in this game");
                }
            } else break;
        }

        if(i == this.getTotalPlayersNumber()) {
            throw new IllegalStateException("No free bookshelf available");
        }

        bookshelves[i] = new Bookshelf(player, new PersonalGoalCard(randomPGCNumbers[i]));
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
     * Sets the current player index
     * @param currentPlayerIndex the new current player index
     */
    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        if(currentPlayerIndex < 0 || currentPlayerIndex >= this.getTotalPlayersNumber())
            throw new IllegalArgumentException("Illegal player index");

        this.currentPlayerIndex = currentPlayerIndex;

        notifyListeners(lst, GameListener::modelChanged);
    }

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
     * This method return the list of players in the game
     * @return an ArrayList of Strings containing the usernames of the players in the game
     */
    public ArrayList<String> playersList() {
        ArrayList<String> players = new ArrayList<>();
        for (Bookshelf bookshelf : bookshelves) {
            if (bookshelf != null) {
                players.add(bookshelf.getPlayer().getUsername());
            }
        }

        return players;
    }

    public void setGameFinished(){
        this.gameFinished = true;
        notifyListeners(lst, GameListener::gameFinished);
    }

    public boolean isFinished(){
        return this.gameFinished;
    }

    // Private methods
    /**
     * Extracts random numbers without duplicates from 0 to bound-1
     * @param amount the amount of random numbers to be extracted
     * @param bound the upper bound (exclusive) of the random numbers to be extracted
     */

    //TODO move to utils
    protected static int[] extractRandomIDsWithoutDuplicates(int amount, int bound){
        if(amount > bound)
            throw new IllegalArgumentException("Amount cannot be greater than bound");

        int[] numbers = new int[amount];
        Random rand = new Random();

        boolean flag;
        for(int i = 0; i < amount; i++){
            do {
                flag = true;
                numbers[i] = rand.nextInt(bound);
                for(int j=0;j<i;j++){
                    if (numbers[j] == numbers[i]) {
                        flag = false;
                        break;
                    }
                }
            } while(!flag);
        }

        return numbers;
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

    public void addListener(GameListener o){
        if(!lst.contains(o)){
            lst.add(o);
        }
    }

    public void removeListener(GameListener o){
        lst.remove(o);
    }
}