package de.tum.cit.ase.maze.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.maze.GameObject;
import de.tum.cit.ase.maze.MazeMap;
import de.tum.cit.ase.maze.Position;
import de.tum.cit.ase.maze.enums.CollisionActions;

import java.util.List;

/**
 * The Key class represents a collectible key object within a MazeRunner game environment. It extends
 * the `GameObject` class and includes additional animations and attributes specific to Key class.
 * Players can collect the key to unlock the exit gate and progress through the maze.
 */
public class Key extends GameObject {

    // Dimensions and properties of the key object
    private static final float width = 1.0f;
    private static final float height = 1.0f;
    private static final int tilePixels = 32;
    private static final boolean walkable = true;

    // Texture representing the graphical appearance of the key
    public static final TextureRegion texture = new TextureRegion(new Texture(Gdx.files.internal("KeyIcons.png")),32,0,tilePixels,tilePixels);

    // List of collision actions associated with the key
    private static final List<CollisionActions> collisionActions = List.of(CollisionActions.PICK_UP);

    /**
     * Constructs a Key object within the maze map at a specified position.
     *
     * @param mazeMap  The maze map where the key exists.
     * @param position The position of the key within the maze.
     */
    public Key(MazeMap mazeMap, Position position){
        super(mazeMap, texture, width, height, position, walkable, Integer.MAX_VALUE);
    }

    @Override
    public List<CollisionActions> getCollisionActions() {
        return collisionActions;
    }

    // Sound effect played when the key is picked up
    private static final Sound collisionSoundEffect = Gdx.audio.newSound(Gdx.files.internal("key_pickup.mp3"));

    public Sound getCollisionSoundEffect(){
        return collisionSoundEffect;
    }

}
