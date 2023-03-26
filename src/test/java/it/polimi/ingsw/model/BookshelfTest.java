package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BookshelfTest {

    Bookshelf bookshelf;

    @BeforeEach
    void init() {
        bookshelf = new Bookshelf(new Player("testUsername"), new PersonalGoalCard(0));
    }

    @Test
    void checkIllegalPosition(){
        try {
            bookshelf.insertTile(new Point(0,0), new Tile(Constants.TileType.BOOKS));
        } catch (NullPointerException | IndexOutOfBoundsException | IllegalArgumentException e) {
            fail(e);
        }
        assertEquals(bookshelf.getTile(new Point(0,0)), new Tile(Constants.TileType.BOOKS));

        assertThrows(IllegalArgumentException.class, () -> {
            bookshelf.insertTile(new Point(0,0), new Tile(Constants.TileType.FRAMES));
        });
        assertEquals(bookshelf.getTile(new Point(0,0)), new Tile(Constants.TileType.BOOKS));
    }

    @Test
    void checkGravity(){
        try {
            bookshelf.insertTile(new Point(0,0), new Tile(Constants.TileType.BOOKS));
        } catch (NullPointerException | IndexOutOfBoundsException | IllegalArgumentException e) {
            fail(e);
        }
        assertEquals(bookshelf.getTile(new Point(0,0)), new Tile(Constants.TileType.BOOKS));

        assertThrows(IllegalArgumentException.class, () -> {
            bookshelf.insertTile(new Point(0,2), new Tile(Constants.TileType.TROPHIES));
        });

        try {
            bookshelf.insertTile(new Point(0,1), new Tile(Constants.TileType.PLANTS));
        } catch (NullPointerException | IndexOutOfBoundsException | IllegalArgumentException e) {
            fail(e);
        }
        assertEquals(bookshelf.getTile(new Point(0,0)), new Tile(Constants.TileType.BOOKS));
        assertEquals(bookshelf.getTile(new Point(0,1)), new Tile(Constants.TileType.PLANTS));
    }

}
