package de.tum.cit.ase.maze.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.maze.MazeMap;
import de.tum.cit.ase.maze.Position;

import java.util.Random;


/**
 * The StoneFloor class represents a tile that serves as stone floor in the Maze Runner game.
 * It extends the Tile class and includes the visual appearance and properties of the stonefloor tile.
 */
public class StoneFloor extends Tile {

    // Constants for grass tile properties
    private static final float width = 1f;
    private static final float height = 1f;
    private static final int tilePixels = 16;
    private static final boolean walkable = true;

    private static final TextureRegion texture = new TextureRegion(new Texture(Gdx.files.internal("basictiles.png")), 96, 16, tilePixels, tilePixels );

    /**
     * Constructs a new StoneFloor object.
     *
     * @param mazeMap the mazeMap this gameobject belongs to
     * @param position position of the Tile
     */
    public StoneFloor(MazeMap mazeMap, Position position){
        super(mazeMap, texture, width, height, position, walkable);
    }
}
