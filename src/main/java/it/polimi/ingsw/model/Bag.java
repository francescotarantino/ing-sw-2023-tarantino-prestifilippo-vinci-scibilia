package it.polimi.ingsw.model;

import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.utils.Constants.TileType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Bag {
    private final List<Tile> tiles;

    /**
     * Creates a bag filled with the right number of tiles.
     */
    public Bag(){
        tiles = new ArrayList<>();
        for(TileType tile: Arrays.stream(TileType.values()).filter(x -> x != TileType.PLACEHOLDER).toArray(TileType[]::new)){
            for(int i=0;i<Constants.tilesPerType;i++){
                tiles.add(new Tile(tile, i % Constants.tileVariants));
            }
        }
    }

    /**
     * Removes a tile from the bag and returns it.
     * @param index the index of the tile to be removed
     * @return the removed tile
     */
    protected Tile popTile(int index){
        if(index < 0 || index >= this.tiles.size())
            throw new IndexOutOfBoundsException("Invalid tile index");


        Tile returnTile = this.tiles.get(index);
        this.tiles.remove(index);
        return returnTile;
    }

    /**
     * Returns a random tile from the bag and removes it.
     * @return the extracted tile
     */
    public Tile getRandomTile(){
        if(this.tiles == null || this.tiles.size() == 0)
            throw new IllegalStateException("Bag is empty");

        Random rand = new Random();
        int randomIndex = rand.nextInt(this.tiles.size());
        return this.popTile(randomIndex);
    }

    /**
     * @return the number of tiles left in the bag
     */
    public int getRemainingTilesQuantity(){
        return this.tiles.size();
    }
}
