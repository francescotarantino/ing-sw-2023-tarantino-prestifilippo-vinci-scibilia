package it.polimi.ingsw.model;
import java.util.Random;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
public class Game {
    //attributes
    private final int gameID;
    private Bookshelf[] bookshelves;
    private PersonalGoalCard[] personalGoalCards;
    private int[] randomNumbers;
    private Bag bag;
    private LivingRoomBoard livingRoomBoard;

    public Game(int numberOfPlayers,String hostName,int ID) throws InvalidNumberOfPlayersException,InvalidIDException{
        if(numberOfPlayers<2 || numberOfPlayers>4)
            throw new InvalidNumberOfPlayersException();
        if(ID<=0)
            throw new InvalidIDException();
        //game construction
        //assigning ID, generating a board and a bag
        this.gameID = ID;
        this.livingRoomBoard = new LivingRoomBoard(numberOfPlayers);
        this.bag = new Bag();
        //generating an adequate amount of personal goal cards
        this.personalGoalCards = new PersonalGoalCard[numberOfPlayers];
        randomNumbers = extractRandomCardIDs(numberOfPlayers);
        personalGoalCards[0] = new PersonalGoalCard(randomNumbers[0]);
        //generating an adequate amount of bookshelves
        this.bookshelves = new Bookshelf[numberOfPlayers];
        bookshelves[0] = new Bookshelf(hostName,personalGoalCards[0]);
    }
    public void addShelf(String playerName) throws NoFreeBookshelfException,DuplicateNameException {
        int found = -1;
        for (int i = 0; i < this.getTotalPlayersNumber() && found==-1; i++) {
            if (this.bookshelves[i] != null){
                found = i;
                if(this.bookshelves[i].getPlayerName().equals(playerName))
                    throw new DuplicateNameException();
            }
        }
        if(found == -1)
            throw new NoFreeBookshelfException();
        else
            bookshelves[found] = new Bookshelf(playerName,personalGoalCards[found]);
    }
    //methods
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

    public Bookshelf[] getBookshelf() {
        return this.bookshelves;
    }
    public int[] extractRandomCardIDs(int amount){ //may be updated to a different file type than txt
        //extracts indexes for random personal goal cards with no duplicates
        //reads rand generation boundary from a file
        int bound = -1;
        do {
            try {
                File data = new File("src/main/resources/personalGoalCardsData.txt");
                Scanner scan = new Scanner(data);
                if (scan.hasNextInt())
                    bound = scan.nextInt();
                scan.close();
            } catch (FileNotFoundException e) {
                System.out.println("File not found.");
                e.printStackTrace();
            }
        }while(bound == -1);
        int[] numbers = new int[amount];
        for(int i=0;i<amount;i++)
            numbers[i] = -1;
        Random rand = new Random();
        boolean flag;
        for(int i=0;i<amount;i++){
            do{
                flag = true;
                numbers[i] = rand.nextInt(bound);
                for(int j=0;j<i;j++){
                    if (numbers[j] == numbers[i]) {
                        flag = false;
                        break;
                    }
                }
            }while(!flag);
        }
        return numbers;
    }
}