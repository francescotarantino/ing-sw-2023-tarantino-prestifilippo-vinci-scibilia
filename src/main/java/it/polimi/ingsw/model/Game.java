package it.polimi.ingsw.model;

import java.util.Random;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.goal_cards.CommonGoalCard;
import it.polimi.ingsw.model.goal_cards.PersonalGoalCard;

public class Game {
    // Attributes
    private final int gameID;
    private final Bookshelf[] bookshelves;
    private final int[] randomPGCNumbers;
    private final Bag bag;
    private final LivingRoomBoard livingRoomBoard;
    private final CommonGoalCard[] commonGoalCards;

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
            System.out.println("Common goal card number " + i + " is " + randomCGCNumbers[i]);
            this.commonGoalCards[i] = CommonGoalCard.create(numberOfPlayers, randomCGCNumbers[i] + 1);
        }
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

    // Private methods

    /**
     * Extracts random numbers without duplicates from 0 to bound-1
     *
     * @param amount the amount of random numbers to be extracted
     * @param bound the upper bound (exclusive) of the random numbers to be extracted
     */
    private static int[] extractRandomIDsWithoutDuplicates(int amount, int bound){
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
}