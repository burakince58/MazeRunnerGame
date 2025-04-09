package de.tum.cit.ase.maze.traps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.ase.maze.MazeMap;
import de.tum.cit.ase.maze.Position;
import de.tum.cit.ase.maze.enums.CollisionActions;
import de.tum.cit.ase.maze.traps.Trap;
import java.util.List;

/**
 * Fire class represents a firetrap in the Maze Runner game.
 * It extends the Trap class and includes animations for the firetrap.
 */
public class Fire extends Trap {

    // Constants for firetrap properties
    private static final float width = 1f;
    private static final float height = 1f;
    private static final int tilePixels = 16;
    private static final boolean walkable = true;

    // Texture region for the firetrap
    private static final TextureRegion texture = new TextureRegion(new Texture(Gdx.files.internal("objects.png")), 16*5, 16*3, tilePixels, tilePixels);

    // Animation for the firetrap
    private static final Animation<TextureRegion> fireAnimation = Fire.loadFireTrapAnimation();
    private static final Sound collisionSoundEffect = Gdx.audio.newSound(Gdx.files.internal("fire_sound_effect.ogg"));

    private float timeSinceLastDamage;
    private float animationTime;
    private static final List<CollisionActions> collisionActions = List.of(CollisionActions.TAKE_DAMAGE);

    private static final float boundingBoxWidthFactor = 0.7f; // 15% off on both sides
    private static final float boundingBoxWidthOffset = (1f - boundingBoxWidthFactor) / 2f * width;

    private static final float boundingBoxHeightFactor = 1f / height * 0.8f;
    private static final float boundingBoxHeighthOffset = height * 0.1f; // 10% leeway on bottom

    /**
     * Loads the firetrap animation from the texture sheet.
     *
     * @return Animation object representing the firetrap animation.
     */
    private static Animation<TextureRegion> loadFireTrapAnimation(){
        Texture walkSheet = new Texture(Gdx.files.internal("objects.png"));

        Array<TextureRegion> fire = new Array<>(TextureRegion.class); // Deniz 30.12

        int frameWidth = 16;
        int frameHeight = 16;
        int animationFrames = 7;

        //Generate frames for the fire animation
        for (int col = 0; col < animationFrames; col++) { // Deniz 30.12
            fire.add(new TextureRegion(walkSheet, (col + 4) * frameWidth, 48, frameWidth, frameHeight));
        }
        return new Animation<>(0.1f, fire);
    }

    /**
     * Constructs a new Fire object.
     * Initializes the firetrap with its texture, dimensions, and walkable property.
     * Sets a random starting point for the animation.
     *
     * @param mazeMap the mazeMap this gameobject belongs to
     * @param position position of this gameobject
     */

    public Fire(MazeMap mazeMap, Position position){
        super(mazeMap, texture, width, height, position, walkable, Integer.MAX_VALUE);
        // pick a random starting point for the animation
        animationTime = (float)(Math.random() * fireAnimation.getKeyFrames().length * fireAnimation.getAnimationDuration());
        timeSinceLastDamage = Float.MAX_VALUE;
    }

    @Override
    public void update(float delta){
        super.update(delta);
        animationTime += delta;
        timeSinceLastDamage += delta;
    }

    public float getDamageDone(){
        return 1.0f;
    }

    public List<CollisionActions> getCollisionActions(){
        if(timeSinceLastDamage > 1.0f) {
            timeSinceLastDamage = 0f;
            return collisionActions;
        }else{
            return null;
        }
    }

    @Override
    public Sound getCollisionSoundEffect(){
        return collisionSoundEffect;
    }

    /**
     * Gets the current texture frame for the firetrap animation.
     *
     * @return TextureRegion representing the current frame of the firetrap animation.
     */
    @Override
    public TextureRegion getTexture(){
        return fireAnimation.getKeyFrame(animationTime, true);
    }

    public void updateBoundingBox(){
        Position pos = getPosition();
        getBoundingBox().set(pos.x+boundingBoxWidthOffset, pos.y+boundingBoxHeighthOffset, getWidth()*boundingBoxWidthFactor, getHeight()*boundingBoxHeightFactor);
    }

}
