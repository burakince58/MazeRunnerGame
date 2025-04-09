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
 * The TriggerSpikes class represents a spike trap that activates upon proximity to the player.
 * It extends the Trap class and implements specific behavior for triggered spike traps.
 */
public class TriggerSpikes extends Trap {
    // Constants for spike trap properties
    private static final float width = 1f;
    private static final float height = 1f;
    private static final int tilePixels = 128;
    private static final boolean walkable = true;

    // Non-animated Texture region for the spike trap
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

    private static final Animation<TextureRegion> spikeInAnimation = new Animation<>(0.1f, spikeInFrames);
    private static final Sound triggerSoundEffect = Gdx.audio.newSound(Gdx.files.internal("spikes_sound.wav"));

    private static final List<CollisionActions> collisionActions = List.of(CollisionActions.STUCK, CollisionActions.TAKE_DAMAGE);

    private static final float boundingBoxWidthFactor = 0.7f; // 15% off on both sides
    private static final float boundingBoxWidthOffset = (1f - boundingBoxWidthFactor) / 2f * width;

    private static final float boundingBoxHeightFactor = 1f / height * 0.8f;
    private static final float boundingBoxHeighthOffset = height * 0.1f; // 10% leeway on bottom

    private static final float triggerDelay = 0.4f;
    private static final float retractDelay = 2f;
    private static final float triggerCooldown = 3f;
    private static final float damageCooldown = retractDelay+spikeInAnimation.getAnimationDuration();


    private boolean retracted;
    private boolean triggered;
    private float triggerTime;
    private float timeSinceLastDamage;
    private float animationTime;

    /**
     * Constructs a new triggered Spike trap.
     *
     *  @param mazeMap the mazeMap this gameobject belongs to
     *  @param position position of this gameobject
     */
    public TriggerSpikes(MazeMap mazeMap, Position position){
        super(mazeMap, texture, width, height, position, walkable, Integer.MAX_VALUE);
        triggerTime = triggerCooldown + 1; // start after cool down
        animationTime = 0f;
        timeSinceLastDamage = Float.MAX_VALUE;
        retracted = true; // start retracted
        triggered = false; // start not triggered
    }

    /**
     * Updates the state of the spike trap.
     *
     * @param delta time since the last update
     */
    @Override
    public void update(float delta){
        super.update(delta);
        animationTime += delta;
        triggerTime += delta;
        timeSinceLastDamage += delta;
        if(retracted && triggered && triggerTime > triggerDelay){
            retracted = false;
            setWalkable(false);
            animationTime = 0;
            triggerSoundEffect.play();
            // last animation frame doesn't damage
        }else if(!retracted && triggerTime > retractDelay + spikeInAnimation.getFrameDuration() * 3){
            retracted = true;
            triggered = false;
            setWalkable(true);
        }
    }

    public float getDamageDone(){
        return 1.0f;
    }

    public List<CollisionActions> getCollisionActions(){
        if(retracted && triggerTime > triggerCooldown){
            System.out.println("Spike trap triggered");
            triggered = true;
            triggerTime = 0f;
            return null;
        }else if(!retracted && timeSinceLastDamage > damageCooldown && triggerTime > triggerDelay) {
            timeSinceLastDamage = 0f;
            return collisionActions;
        }

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
            return texture;
        }else{
            if(triggerTime > retractDelay){
                if(animationTime > retractDelay-triggerDelay-0.1f){
                    animationTime = 0;
                }
                return spikeInAnimation.getKeyFrame(animationTime);
            }else{
                return spikeOutAnimation.getKeyFrame(animationTime);
            }
        }
    }

    public void updateBoundingBox(){
        Position pos = getPosition();
        getBoundingBox().set(pos.x+boundingBoxWidthOffset, pos.y+boundingBoxHeighthOffset, getWidth()*boundingBoxWidthFactor, getHeight()*boundingBoxHeightFactor);
    }

    public float getStuckDuration(){
        return Math.max(retractDelay-triggerTime+0.2f, 0.5f);
    }

}