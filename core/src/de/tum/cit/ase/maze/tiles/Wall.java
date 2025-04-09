package de.tum.cit.ase.maze.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.maze.MazeMap;
import de.tum.cit.ase.maze.Position;

/**
 * The Wall class represents a tile that serves as a wall in the Maze Runner game.
 * It extends the Tile class and includes the visual appearance and properties of the wall tile.
 */

public class Wall extends Tile{
    // Constants for wall tile properties
    private static final float width = 1;
    private static final float height = 1;
    private static final int tilePixels = 16;
    private static final boolean walkable = false;


    // Texture region for the wall tile
    private static final TextureRegion texture =  new TextureRegion(new Texture(Gdx.files.internal("basictiles.png")), 48, 0, tilePixels, tilePixels );

    /**
     * Constructs a new Wall object.
     * Initializes the wall tile with its texture, width, height and walkable property.
     *
     *
     * @param mazeMap the mazeMap this gameobject belongs to
     * @param position position of the Tile
     */
    public Wall(MazeMap mazeMap, Position position){
        super(mazeMap, texture, width, height, position, walkable);
    }

}
