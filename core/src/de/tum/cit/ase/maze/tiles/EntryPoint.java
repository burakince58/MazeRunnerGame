package de.tum.cit.ase.maze.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.maze.MazeMap;
import de.tum.cit.ase.maze.Position;


/**
 * The EntryPoint class represents a tile that serves as the entry point in the Maze Runner game.
 * It extends the Tile class and includes the visual appearance and properties of the entry point tile.
 */

public class EntryPoint extends Tile {

    // Constants for entry point tile properties
    private static final float width = 1f;
    private static final float height = 1f;
    private static final int tilePixels = 16;
    private static final boolean walkable = true;

    // Texture region for the entry point tile
    private static final TextureRegion texture = new TextureRegion(new Texture(Gdx.files.internal("basictiles.png")), 32, 96, tilePixels, tilePixels );


    /**
     * Constructs a new EntryPoint object.
     * Initializes the entry point tile with its texture, width, height and walkable property.
     *
     * @param mazeMap the mazeMap this gameobject belongs to
     * @param position position of the Tile
     */
    public EntryPoint(MazeMap mazeMap, Position position){
        super(mazeMap, texture, width, height, position, walkable);
    }

}
