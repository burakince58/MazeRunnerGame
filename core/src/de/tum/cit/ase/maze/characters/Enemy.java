package de.tum.cit.ase.maze.characters;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.maze.MazeMap;
import de.tum.cit.ase.maze.Position;

/**
 *  * The Enemy class is an abstract class that serves as the base class for
 *  * different enemy characters in the game. It extends the Character class
 *  * and provides a common structure for enemy characters.
 */

public abstract class Enemy extends Character{

    /**
     * Constructs a new `Enemy` object with the specified properties.
     *
     * @param mazeMap the mazeMap this gameobject belongs to
     * @param texture        The texture representing the visual appearance of the enemy.
     * @param width          The width of the enemy character.
     * @param height         The height of the enemy character.
     * @param walkable       A boolean indicating whether the enemy is walkable.
     * @param health         How much health this enemy has
     * @param position       The initial position of the enemy character.
     * @param characterSpeed The speed at which the enemy character can move.
     *
     */
    public Enemy(MazeMap mazeMap, TextureRegion texture, float width, float height, boolean walkable, float health, Position position, float characterSpeed){
        super(mazeMap, texture, width, height, walkable, health, position, characterSpeed);
    }

}
