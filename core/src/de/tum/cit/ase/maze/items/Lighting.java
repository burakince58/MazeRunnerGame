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
 * The Lighting class represents a collectible light object within a maze game environment. It extends
 * the `GameObject` class and includes additional animations and attributes specific to Lighting class.
 * Players can interact with the light to toggle visibility on the map.
 */
public class Lighting extends GameObject {

    // Dimensions and properties of the light object
    private static final float width = 1.0f;
    private static final float height = 1.0f;
    private static final int tilePixels = 32;
    private static final boolean walkable = true;

    // Texture representing the graphical appearance of the light bulb
    public static final TextureRegion texture = new TextureRegion(new Texture(Gdx.files.internal("light_bulb.png")),32,32,tilePixels,tilePixels);

    // List of collision actions associated with the light
    private static final List<CollisionActions> collisionActions = List.of(CollisionActions.LIGHT_ON);

    /**
     * Constructs a Lighting object within the maze map at a specified position.
     *
     * @param mazeMap  The maze map where the light exists.
     * @param position The position of the light within the maze.
     */
    public Lighting(MazeMap mazeMap, Position position){
        super(mazeMap, texture, width, height, position, walkable, Integer.MAX_VALUE);
    }

    @Override
    public List<CollisionActions> getCollisionActions() {
        return collisionActions;
    }

    // Sound effect played when the light object is collected
    private static final Sound collisionSoundEffect = Gdx.audio.newSound(Gdx.files.internal("light_switch.wav"));

    public Sound getCollisionSoundEffect(){
        return collisionSoundEffect;
    }

}
