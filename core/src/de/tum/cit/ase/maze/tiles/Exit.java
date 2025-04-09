package de.tum.cit.ase.maze.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.maze.MazeMap;
import de.tum.cit.ase.maze.Position;
import de.tum.cit.ase.maze.enums.CollisionActions;

import java.util.List;

/**
 * The Exit class represents a tile that serves as the exit point in the Maze Runner game.
 * It extends the Tile class and includes the visual appearance and properties of the exit tile.
 */

public class Exit extends Tile{

    // Constants for exit tile properties
    private static final float width = 1f;
    private static final float height = 1f;
    private static final int tilePixels = 16;
    private static final boolean walkable = false;

    // Texture region for the exit tile
    private static final TextureRegion textureClosedDoor = new TextureRegion(new Texture(Gdx.files.internal("basictiles.png")), 16, 96, tilePixels, tilePixels );
    private static final TextureRegion textureOpenDoor = new TextureRegion(new Texture(Gdx.files.internal("things.png")), 64, 16, tilePixels, tilePixels );

    private static final Sound collisionSoundEffect = Gdx.audio.newSound(Gdx.files.internal("exit_gate_sound_effect.ogg"));

    // plays when key is collected
    public static final Sound doorsOpenSoundEffect = Gdx.audio.newSound(Gdx.files.internal("stone_door.ogg"));

    private static final List<CollisionActions> collisionActions = List.of(CollisionActions.EXIT);

    private float timeSinceLastCollisionAction = 0f;

    /**
     * Constructs a new Exit object.
     * Initializes the exit tile with its texture, width, height and walkable property.
     *
     * @param mazeMap the mazeMap this gameobject belongs to
     * @param position position of the Tile
     */
    public Exit(MazeMap mazeMap, Position position){
        super(mazeMap, textureClosedDoor, width, height, position, walkable);
    }

    @Override
    public void update(float delta){
        super.update(delta);
        timeSinceLastCollisionAction += delta;
    }

    @Override
    public List<CollisionActions> getCollisionActions() {
        if(timeSinceLastCollisionAction < 0.5f){
            return null;
        }
        timeSinceLastCollisionAction = 0f;
        return collisionActions;
    }

    public Sound getCollisionSoundEffect(){
        return collisionSoundEffect;
    }

    public TextureRegion getTexture(){
        if(getMazeMap().getPlayer().isKeyCollected()){
            return textureOpenDoor;
        }else{
            return textureClosedDoor;
        }

    }

}
