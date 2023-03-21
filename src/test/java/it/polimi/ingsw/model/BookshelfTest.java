package it.polimi.ingsw.model;

import it.polimi.ingsw.model.exceptions.GravityException;
import it.polimi.ingsw.model.exceptions.IllegalPositionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BookshelfTest {

    Bookshelf bookshelf;

    @BeforeEach
    void init() {
        bookshelf = new Bookshelf("TestPlayer", new PersonalGoalCard(0));
    }

    @Test
    void checkIllegalPosition(){
        try {
            bookshelf.insertTile(new Point(0,0), new Tile(TileType.BOOKS));
        } catch (IllegalPositionException | GravityException e) {
            fail(e);
        }
        assertEquals(bookshelf.getTile(new Point(0,0)), new Tile(TileType.BOOKS));

        assertThrows(IllegalPositionException.class, () -> {
            bookshelf.insertTile(new Point(0,0), new Tile(TileType.FRAMES));
        });
        assertEquals(bookshelf.getTile(new Point(0,0)), new Tile(TileType.BOOKS));
    }

    @Test
    void checkGravity(){
        try {
            bookshelf.insertTile(new Point(0,0), new Tile(TileType.BOOKS));
        } catch (IllegalPositionException | GravityException e) {
            fail(e);
        }
        assertEquals(bookshelf.getTile(new Point(0,0)), new Tile(TileType.BOOKS));

        assertThrows(GravityException.class, () -> {
            bookshelf.insertTile(new Point(0,2), new Tile(TileType.TROPHIES));
        });

        try {
            bookshelf.insertTile(new Point(0,1), new Tile(TileType.PLANTS));
        } catch (IllegalPositionException | GravityException e) {
            fail(e);
        }
        assertEquals(bookshelf.getTile(new Point(0,0)), new Tile(TileType.BOOKS));
        assertEquals(bookshelf.getTile(new Point(0,1)), new Tile(TileType.PLANTS));
    }

}
