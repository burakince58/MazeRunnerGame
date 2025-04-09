package de.tum.cit.ase.maze.traps;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.maze.GameObject;
import de.tum.cit.ase.maze.MazeMap;
import de.tum.cit.ase.maze.Position;

/**
 * The Trap class represents a generic trap in the Maze Runner game.
 * It extends the GameObject class and serves as a base class for specific trap implementations.
 */

public class Trap extends GameObject{

    /**
     * Constructs a new Trap object with the specified attributes.
     *
     * @param mazeMap the mazeMap this gameobject belongs to
     * @param texture  The TextureRegion representing the visual appearance of the trap.
     * @param width    The width of the trap in game units.
     * @param height   The height of the trap in game units.
     * @param position position of the Trap
     * @param walkable A boolean indicating whether the trap is walkable or blocks movement.
     * @param health how much health this GameObject has
     */

    public Trap(MazeMap mazeMap, TextureRegion texture, float width, float height, Position position, boolean walkable, float health){
        super(mazeMap, texture, width, height, position, walkable, health);
    }
}
