package de.tum.cit.ase.maze.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.maze.MazeMap;
import de.tum.cit.ase.maze.Position;

import java.util.Random;

/**
 * The Grass class represents a tile that serves as grass in the Maze Runner game.
 * It extends the Tile class and includes the visual appearance and properties of the grass tile.
 */

public class Grass extends Tile {

    // Constants for grass tile properties
    private static final float width = 1f;
    private static final float height = 1f;
    private static final int tilePixels = 16;
    private static Texture tilemap = new Texture(Gdx.files.internal("basictiles.png"));
    private static final boolean walkable = true;


    // Array of TextureRegion instances representing different grass textures
    private static TextureRegion[] grassTextures = {
            new TextureRegion(tilemap, 49, 16, tilePixels-1, tilePixels), // this was grabbing a tiny bit from the yellow pixel to the left...
            new TextureRegion(tilemap, 64, 16, tilePixels, tilePixels ),
            new TextureRegion(tilemap, 0, 128, tilePixels, tilePixels ),
            new TextureRegion(tilemap, 16, 128, tilePixels, tilePixels )
    };

    /**
     * Creates random grass texture from the available options.
     *
     * @return randomly selected TextureRegion representing a grass texture.
     */
    private static TextureRegion createTexture(){
       Random random = new Random();
       return grassTextures[random.nextInt(grassTextures.length)];
    }

    /**
     * Constructs a new Grass object.
     * Initializes the grass tile with a randomly selected texture, width, height and walkable property.
     *
     * @param mazeMap the mazeMap this gameobject belongs to
     * @param position position of the Tile
     */
    public Grass(MazeMap mazeMap, Position position){
        super(mazeMap, createTexture(), width, height, position, walkable);
    }
}
