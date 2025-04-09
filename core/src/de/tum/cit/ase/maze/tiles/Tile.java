package de.tum.cit.ase.maze.tiles;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.maze.GameObject;
import de.tum.cit.ase.maze.MazeMap;
import de.tum.cit.ase.maze.Position;

/**
 * The Tile class represents a generic tile in the Maze Runner game.
 * It extends the GameObject class and serves as a base class for specific tile implementations.
 */

public abstract class Tile extends GameObject {

    /**
     * Constructs a new Tile object with the specified attributes.
     *
     * @param mazeMap the mazeMap this gameobject belongs to
     * @param texture  The TextureRegion representing the visual appearance of the tile.
     * @param width    The width of the tile in game units.
     * @param height   The height of the tile in game units.
     * @param position position of the Trap
     * @param walkable A boolean indicating whether the tile is walkable or blocks movement.
     */
    public Tile(MazeMap mazeMap, TextureRegion texture, float width, float height, Position position, boolean walkable){
        super(mazeMap, texture, width, height, position, walkable, Integer.MAX_VALUE);
    }

    /**
     * tiles can't be destroyed
     * @return always returns false
     */
    @Override
    public boolean isDestroyed() {
        return false;
    }

    /**
     * tiles can't be destroyed. does nothing
     */
    @Override
    public void destroy() {
        System.out.println("Tiles can't be destroyed.");
    }

    /**
     *  does nothing. tiles can't take damage
     *
     * @param damageDone unused
     */
    @Override
    public void takeDamage(float damageDone) {
    }

    /**
     *  override bounding box to make tile bounding boxes slightly smaller than the tile so we don't hang at edges so often
     */
    public void updateBoundingBox(){
        Position pos = getPosition();
        getBoundingBox().set(pos.x+0.05f, pos.y+0.05f, getWidth()*0.9f, getHeight()*0.9f);
    }
}
