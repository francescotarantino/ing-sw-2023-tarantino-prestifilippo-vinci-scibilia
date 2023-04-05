package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.goal_cards.CommonGoalCard;
import it.polimi.ingsw.model.goal_cards.PersonalGoalCard;
import java.util.Random;

public class Game {
    // Attributes
    private final int gameID;
    private final Bookshelf[] bookshelves;
    private final int[] randomPGCNumbers;
    private final Bag bag;
    private final LivingRoomBoard livingRoomBoard;
    private final CommonGoalCard[] commonGoalCards;
    private final int firstPlayerIndex;
    private int currentPlayerIndex;
    private int finalPlayerIndex;


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
        this.currentPlayerIndex = this.firstPlayerIndex;

        // Final player index is -1 by default
        this.finalPlayerIndex = -1;
    }

    // Public methods
    /**
     * Adds a new bookshelf to the game
     * @param player: the new player that will be added to the game
     */
    public void addBookshelf(Player player){
        int i;
        for (i = 0; i < this.getTotalPlayersNumber(); i++) {
            if (this.bookshelves[i] != null){
                if(this.bookshelves[i].getPlayer().getUsername().equals(player.getUsername())) {
                    throw new IllegalArgumentException("Username already present in this game");
                }
            } else
                break;
        }
        if(i == this.getTotalPlayersNumber())
            throw new IllegalStateException("No free bookshelf available");
        else
            bookshelves[i] = new Bookshelf(player, new PersonalGoalCard(randomPGCNumbers[i]));
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

    public Player getCurrentPlayer() {
        return this.bookshelves[this.currentPlayerIndex].getPlayer();
    }

    /**
     * Sets the current player index
     * @param currentPlayerIndex the new current player index
     */
    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        if(currentPlayerIndex < 0 || currentPlayerIndex >= this.getTotalPlayersNumber())
            throw new IllegalArgumentException("Illegal player index");

        this.currentPlayerIndex = currentPlayerIndex;
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

    // Private methods
    /**
     * Extracts random numbers without duplicates from 0 to bound-1
     * @param amount the amount of random numbers to be extracted
     * @param bound the upper bound (exclusive) of the random numbers to be extracted
     */
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

        string.append(this.getGameID()).append("\t");

        for(int i=0; i<getTotalPlayersNumber(); i++){
            if(this.getBookshelves()[i] != null)
                string.append(this.getBookshelves()[i].getPlayer().getUsername()).append("\t");
        }

        if(this.isFull()){
            string.append("(FULL)");
        }

        return string.toString();
    }
}