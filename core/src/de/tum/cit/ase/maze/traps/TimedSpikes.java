package de.tum.cit.ase.maze.traps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.maze.MazeMap;
import de.tum.cit.ase.maze.Position;
import de.tum.cit.ase.maze.enums.CollisionActions;

import java.util.List;

/**
 * TimedSpikes class represents a timed spike trap in the Maze Runner game.
 * It extends the Trap class and includes animations for the timed spike trap.
 */
public class TimedSpikes  extends Trap {
    // Constants for firetrap properties
    private static final float width = 1f;
    private static final float height = 1f;
    private static final int tilePixels = 128;
    private static final boolean walkable = true;

    // non animated Texture region for the spike trap
    private static final TextureRegion texture = new TextureRegion(new Texture(Gdx.files.internal("spikes.png")), 0, 0, tilePixels, tilePixels);

    // Animation for the spike trap
    private static final Texture spikeTexture = new Texture(Gdx.files.internal("spikes.png"));
    private static final TextureRegion[] spikeOutFrames = {
            new TextureRegion(spikeTexture, 0, 0, 128, 128),
            new TextureRegion(spikeTexture, 128, 0, 128, 128),
            new TextureRegion(spikeTexture, 256, 0, 128, 128),
            new TextureRegion(spikeTexture, 384, 0, 128, 128),
    };

    private static final Animation<TextureRegion> spikeOutAnimation = new Animation<>(0.05f, spikeOutFrames);

    private static final TextureRegion[] spikeInFrames = {
            new TextureRegion(spikeTexture, 384, 0, 128, 128),
            new TextureRegion(spikeTexture, 256, 0, 128, 128),
            new TextureRegion(spikeTexture, 128, 0, 128, 128),
            new TextureRegion(spikeTexture, 0, 0, 128, 128),
    };

    private static final Animation<TextureRegion> spikeInAnimation = new Animation<>(0.075f, spikeInFrames);
    private static final Sound triggerSoundEffect = Gdx.audio.newSound(Gdx.files.internal("spikes_sound.wav"));

    private static final List<CollisionActions> collisionActions = List.of(CollisionActions.STUCK, CollisionActions.TAKE_DAMAGE);

    private static final float boundingBoxWidthFactor = 0.7f; // 15% off on both sides
    private static final float boundingBoxWidthOffset = (1f - boundingBoxWidthFactor) / 2f * width;

    private static final float boundingBoxHeightFactor = 1f / height * 0.8f;
    private static final float boundingBoxHeighthOffset = height * 0.1f; // 10% leeway on bottom

    private static final float outTime = 2f;
    private static final float retractedTime = 4f;
    private static final float damageCooldown = outTime+spikeInAnimation.getAnimationDuration();

    private boolean retracted;
    private float timeSinceLastDamage;
    private float animationTime;

    /**
     *
     * Constructs a new timed spike trap
     *
     * @param mazeMap the mazeMap this gameobject belongs to
     * @param position position of this gameobject
     * @param retracted boolean indicating if this starts retracted or out
     */
    public TimedSpikes(MazeMap mazeMap, Position position, boolean retracted){
        super(mazeMap, texture, width, height, position, walkable, Integer.MAX_VALUE);
        animationTime = 0f;
        timeSinceLastDamage = Float.MAX_VALUE;
        this.retracted = retracted; // start retracted
        if(retracted){
            animationTime = (retractedTime - outTime) / 2f;
        }
    }

    @Override
    public void update(float delta){
        super.update(delta);
        animationTime += delta;
        timeSinceLastDamage += delta;
        if(retracted && animationTime > retractedTime){
            // within earshot
            if(distance(getMazeMap().getPlayer()) < 4){
                triggerSoundEffect.play();
            }
            setWalkable(false);
            retracted = false;
            animationTime = 0f;
        }else if(!retracted && animationTime > outTime){
            retracted = true;
            setWalkable(true);
            animationTime = 0;
        }
    }

    public float getDamageDone(){
        return 1.0f;
    }

    public List<CollisionActions> getCollisionActions(){
        if(retracted || timeSinceLastDamage < damageCooldown){
            return null;
        }else {
            timeSinceLastDamage = 0f;
            return collisionActions;
        }
    }

    @Override
    public Sound getCollisionSoundEffect(){
        return null;
    }

    /**
     * Gets the current texture frame for the spike trap animation.
     *
     * @return TextureRegion representing the current frame of the spike trap animation.
     */
    @Override
    public TextureRegion getTexture(){
        if(retracted){
            return spikeInAnimation.getKeyFrame(animationTime);
        }else{
            return spikeOutAnimation.getKeyFrame(animationTime);
        }
    }

    public void updateBoundingBox(){
        Position pos = getPosition();
        getBoundingBox().set(pos.x+boundingBoxWidthOffset, pos.y+boundingBoxHeighthOffset, getWidth()*boundingBoxWidthFactor, getHeight()*boundingBoxHeightFactor);
    }

    public float getStuckDuration(){
        if(retracted){
            return 0;
        }else{
            return Math.max(outTime - animationTime+0.2f, 0.3f);
        }
    }

    public boolean isWalkable() {
        return retracted;
    }

}