package de.tum.cit.ase.maze.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.maze.MazeMap;
import de.tum.cit.ase.maze.Position;
import de.tum.cit.ase.maze.enums.CollisionActions;
import de.tum.cit.ase.maze.enums.Direction;

import java.util.List;
import java.util.Random;

/**
 * The Ghost class represents a ghost enemy character in a maze-based game.
 * It extends Enemy class and includes specific properties and textures for a ghost character.
 */

public class Ghost extends Enemy {

    // Constants defining properties of the ghost character
    private static final float width = 1;
    private static final float height = 1;
    private static final int tilePixels = 16;
    private static final boolean walkable = true;
    private static final float initialCharacterSpeed = 0.75f;

    private static final Texture ghostSheet = new Texture(Gdx.files.internal("mobs.png"));
    private static final TextureRegion texture = new TextureRegion(new Texture(Gdx.files.internal("mobs.png")),96,64,tilePixels,tilePixels);

    // Animations for different movement directions
    private static final TextureRegion[] walkDownFrames = {
            new TextureRegion(ghostSheet, 96, 64, tilePixels, tilePixels),
            new TextureRegion(ghostSheet, 112, 64, tilePixels, tilePixels),
            new TextureRegion(ghostSheet, 128, 64, tilePixels, tilePixels)
    };
    private static final Animation<TextureRegion> walkDownAnimation = new Animation<>(0.15f, walkDownFrames);
    private static final TextureRegion[] walkUpFrames = {
            new TextureRegion(ghostSheet, 16 * 6, 16 * 7, tilePixels, tilePixels),
            new TextureRegion(ghostSheet, 16 * 7, 16 * 7, tilePixels, tilePixels),
            new TextureRegion(ghostSheet, 16 * 8, 16 * 7, tilePixels, tilePixels)
    };
    private static final Animation<TextureRegion> walkUpAnimation = new Animation<>(0.15f, walkUpFrames);

    private static final TextureRegion[] walkRightFrames = {
            new TextureRegion(ghostSheet, 16 * 6, 16 * 6, tilePixels, tilePixels),
            new TextureRegion(ghostSheet, 16 * 7, 16 * 6, tilePixels, tilePixels),
            new TextureRegion(ghostSheet, 16 * 8, 16 * 6, tilePixels, tilePixels)
    };
    private static final Animation<TextureRegion> walkRightAnimation = new Animation<>(0.15f, walkRightFrames);

    private static final TextureRegion[] walkLeftFrames = {
            new TextureRegion(ghostSheet, 16 * 6, 16 * 5, tilePixels, tilePixels),
            new TextureRegion(ghostSheet, 16 * 7, 16 * 5, tilePixels, tilePixels),
            new TextureRegion(ghostSheet, 16 * 8, 16 * 5, tilePixels, tilePixels)
    };
    private static final Animation<TextureRegion> walkLeftAnimation = new Animation<>(0.15f, walkLeftFrames);

    private static final List<CollisionActions> collisionActions = List.of(CollisionActions.TAKE_DAMAGE);
    private static final Sound collisionSoundEffect = Gdx.audio.newSound(Gdx.files.internal("ghost_sound_effect.mp3"));

    private static final Sound engagedSoundEffect = Gdx.audio.newSound(Gdx.files.internal("ghostbreath.mp3"));

    private float animationTime;
    private float timeSinceLastDamage;
    private final float directionChangeInterval = 1.75f;

    private float timeSinceLastDirectionChange = 0.0f; // Track time since last direction change

    private final Random random = new Random();
    private static final float boundingBoxWidthFactor = 0.6f; // 20% off on both sides
    private static final float boundingBoxWidthOffset = (1f - boundingBoxWidthFactor) / 2f * width;
    private static final float boundingBoxHeightFactor = 1f / height * 0.6f;
    private static final float boundingBoxHeighthOffset = height * 0.2f; // 20% leeway on bottom
    private static final float startingHealth = 1f;

    private boolean engaged;
    private float timeSinceLastEngage;
    /**
     * Constructs a new `Ghost` object with the specified initial position.
     *
     * @param mazeMap the mazeMap this gameobject belongs to
     * @param position The initial position of the ghost character.
     */
    public Ghost(MazeMap mazeMap, Position position){
        super(mazeMap, texture, width, height, walkable, startingHealth, position, initialCharacterSpeed);
        animationTime = 0f;
        timeSinceLastDamage = Float.MAX_VALUE;
        engaged = false;
        timeSinceLastEngage = 100f;
    }

    public boolean move(Direction direction, float delta) {
        return super.move(direction,delta);
    }

    @Override
    public void takeAction(float delta) {
        // move towards players
        if(distance(getMazeMap().getPlayer()) < 4){
            // charge at player
            if(!engaged){
                System.out.println("Ghost engaging!");
                engaged = true;
                if(timeSinceLastEngage > 5){
                    engagedSoundEffect.play();
                }
                timeSinceLastEngage = 0f;
            }
            moveToPlayer(delta);
        }else{
            engaged = false;
            moveRandomDirection(delta);
        }

    }

    public void update(float delta){
        super.update(delta);
        animationTime += delta;
        timeSinceLastDamage += delta;
        timeSinceLastDirectionChange += delta;
        timeSinceLastEngage += delta;
    }

    /**
     *
     * @return
     */
    public float getDamageDone(){
        return 1.0f;
    }

    /**
     *  Returns the collision actions this GameObject triggers upon collision
     *
     *  per default null.
     *
     * @return list of CollisionActions. Returns null for no actions
     */
    public List<CollisionActions> getCollisionActions(){
        if(timeSinceLastDamage > 2.0f && !isDestroyed()) {
            timeSinceLastDamage = 0f;
            return collisionActions;
        }else{
            return null;
        }
    }

    public Sound getCollisionSoundEffect(){
        return collisionSoundEffect;
    }

    /**
     * Retrieves the appropriate texture region based on the current direction of movement.
     * This method overrides the superclass method to provide direction-specific textures
     * for animated movement in different directions.
     *
     * @return The texture region corresponding to the current movement direction.
     */
    public TextureRegion getTexture(){
        super.getTexture();
        switch(getDirection()){
            case UP:
                return walkUpAnimation.getKeyFrame(animationTime, true);
            case DOWN:
                return walkDownAnimation.getKeyFrame(animationTime, true);
            case LEFT:
                return walkLeftAnimation.getKeyFrame(animationTime, true);
            case RIGHT:
                return walkRightAnimation.getKeyFrame(animationTime, true);
        }
        return texture;
    }

    /**
     * Moves the the ghost in a random direction.
     * It selects a random direction from the available directions and assigns it to the ghost.
     * If the time since the last direction change exceeds the direction change interval,
     * it changes the direction to a new random direction, ensuring it's different from the current one.
     * This method is typically used to add unpredictability to the movement of the ghost in the game.
     *
     * @param delta time elapsed
     */
    public void moveRandomDirection(float delta) {

         if(timeSinceLastDirectionChange > directionChangeInterval){
             int randomDirection;
             // change ghost direction
             randomDirection = random.nextInt(Direction.values().length);
             Direction direction = Direction.values()[randomDirection];
             // While the current direction matches the randomly chosen direction,
             // pick a new random direction until it's different.
             while(getDirection() == direction){
                 randomDirection = random.nextInt(Direction.values().length);
                 direction = Direction.values()[randomDirection];
             }
             setDirection(direction);
             animationTime = 0f;
             timeSinceLastDirectionChange = 0.0f;
         }
         if(!move(getDirection(), delta)){
             // turn around if move doesn't work
             if(isStuck(delta)){
                 // ghost stuck, probably from chasing player so keep chasing until unstuck
                 moveToPlayer(delta);
             }
         }
    }

    /**
     * moves the ghost in the direction of the player ignoring obstacles
     *
     * @param delta time elapsed
     */
    public void moveToPlayer(float delta){
        Player player = getMazeMap().getPlayer();
        Position myPosition = getPosition();
        float distance = distance(player);
        float xMove = ((player.getPosition().x - myPosition.x) / distance) * getCharacterSpeed() * delta;
        float yMove = ((player.getPosition().y - myPosition.y) / distance)  * getCharacterSpeed() * delta;

        // set character direction based on the biggest move distance
        // so we render the correct model
        if(Math.abs(xMove) > Math.abs(yMove)){
            if(xMove < 0){
                setDirection(Direction.LEFT);
            }else{
                setDirection(Direction.RIGHT);
            }
        }else{
            if(yMove < 0){
                setDirection(Direction.DOWN);
            }else{
                setDirection(Direction.UP);
            }
        }
        // attempt the move to trigger collisions with player, but ignore return value as we move anyway
        getMazeMap().characterAttemptMoveTo(this, myPosition.x + xMove, myPosition.y + yMove);
        updatePosition(myPosition.x + xMove, myPosition.y + yMove);
    }

    public void updateBoundingBox(){
        Position pos = getPosition();
        getBoundingBox().set(pos.x+boundingBoxWidthOffset, pos.y+boundingBoxHeighthOffset, getWidth()*boundingBoxWidthFactor, getHeight()*boundingBoxHeightFactor);
    }


}
