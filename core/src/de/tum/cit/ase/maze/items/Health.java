package de.tum.cit.ase.maze.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.ase.maze.GameObject;
import de.tum.cit.ase.maze.MazeMap;
import de.tum.cit.ase.maze.Position;
import de.tum.cit.ase.maze.enums.CollisionActions;
import de.tum.cit.ase.maze.traps.Fire;

import java.util.List;

/**
 * Health represents collectible lives within the MazeRunnerGame. It extends
 * the `GameObject` class and includes additional animations and attributes specific to Health class.
 * Players can increase their lives by collecting instances of Health.
 */
public class Health extends GameObject {

    // Constants defining the properties of Health instances
    private static final float width = 1.0f;
    private static final float height = 1.0f;
    private static final int tilePixels = 16;
    private static final boolean walkable = true;

    // Texture representing the single heart
    public static final TextureRegion texture = new TextureRegion(new Texture(Gdx.files.internal("objects.png")),48,0,tilePixels,tilePixels);

    // Animation for the heart, making it look dynamic
    private static final Animation<TextureRegion> heartAnimation = Health.loadAdditionalHeartAnimation(); //Burak: I want that single heart looks like an animation

    // List of collision actions associated with Health object
    private static final List<CollisionActions> collisionActions = List.of(CollisionActions.HEART_UP);

    // Sound effect played upon collision with a Health object
    private static final Sound collisionSoundEffect = Gdx.audio.newSound(Gdx.files.internal("heart_collect_sound.mp3"));
    private float animationTime;
    private boolean healthCollected = false;

    // Load additional heart animation frames
    private static Animation<TextureRegion> loadAdditionalHeartAnimation(){
        Texture objectSheet = new Texture(Gdx.files.internal("objects.png"));
        Array<TextureRegion> hearts = new Array<>(TextureRegion.class);
        int frameWidth = 16;
        int frameHeight = 16;
        int animationFrames = 4;

        // Create heart animation frames
        for (int col = 0; col < animationFrames; col++) {
            hearts.add(new TextureRegion(objectSheet, col * frameWidth, 48, frameWidth, frameHeight));
        }
        return new Animation<>(0.1f, hearts);
    }


    /**
     * Constructs a Health object.
     *
     * @param mazeMap  the maze map instance
     * @param position the position of the health object within the maze
     */
    public Health(MazeMap mazeMap, Position position){
        super(mazeMap, texture, width, height, position, walkable, Integer.MAX_VALUE);
        // pick a random starting point for the animation
        animationTime = (float)(Math.random() * heartAnimation.getKeyFrames().length * heartAnimation.getAnimationDuration());
    }

    /**
     * Retrieves the collision actions associated with the Health object.
     *
     * @return a list of collision actions
     */
    @Override
    public List<CollisionActions> getCollisionActions() {
        if (healthCollected) {
            return null;
        } else {
            return collisionActions;
        }
    }

    public Sound getCollisionSoundEffect(){
        return collisionSoundEffect;
    }

    @Override
    public TextureRegion getTexture(){
        return heartAnimation.getKeyFrame(animationTime, true);
    }

    /**
     * Updates the Health object's animation time and time passed since the last damage taken.
     *
     * @param delta the time passed since the last frame was rendered
     */
    @Override
    public void update(float delta){
        super.update(delta);
        animationTime += delta;
    }

    public boolean isHealthCollected() {
        return healthCollected;
    }

    public void setHealthCollected(boolean healthCollected) {
        this.healthCollected = healthCollected;
    }
}
