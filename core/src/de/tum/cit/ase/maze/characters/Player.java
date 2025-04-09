package de.tum.cit.ase.maze.characters;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import de.tum.cit.ase.maze.GameObject;
import de.tum.cit.ase.maze.MazeMap;
import de.tum.cit.ase.maze.Position;
import de.tum.cit.ase.maze.enums.Direction;
import de.tum.cit.ase.maze.items.Health;
import de.tum.cit.ase.maze.tiles.Exit;

import java.util.List;

/**
 * The `Player` class represents a player character in a maze-based game. It extends
 * the `Character` class and includes additional animations and functionality specific
 * to the player character.
 */

public class Player extends Character {

    // Constants defining properties of the player character
    private static final int textureWidth = 16;
    private static final int textureHeight = 32;
    private static final float width = 0.75f;
    private static final float height = (width * textureHeight) / textureWidth;

    private float drawXOffset = 0.0f;
    private float drawYOffset = height * -0.14f;

    private static final float boundingBoxWidthFactor = 0.88f; // 6% off on both sides
    private static final float boundingBoxWidthOffset = (1f - boundingBoxWidthFactor) / 2f * width;

    private static final float boundingBoxHeightFactor = 0.5f; // 45% off top, 5% bottom
    private static final float boundingBoxHeighthOffset = height * 0.05f; // 5% leeway on bottom
    private static final boolean walkable = false;
    private static final float initialCharacterSpeed = 4f;

    public static final int starting_health = 5;
    private static final float dashDuration = 0.15f;
    private static final float dashCooldown = 1f;
    private static final float dashDistance = 2f; // 2 tiles
    private static final float attackDuration = 0.4f;
    private static final float attackDamageTime = 0.3f;

    // Textures and animations for different player actions
    private static final Texture characterSheet = new Texture(Gdx.files.internal("character.png"));

    private static final Texture bloodSheet = new Texture(Gdx.files.internal("dropsplash.png"));

    private static final TextureRegion[] bloodFrames = {
            new TextureRegion(bloodSheet, 0, 0 , 640, 800),
            new TextureRegion(bloodSheet, 640, 0 , 640, 800),
            new TextureRegion(bloodSheet, 1280, 0 , 640, 800),
            new TextureRegion(bloodSheet, 0, 800 , 640, 800),
            new TextureRegion(bloodSheet, 640, 800 , 640, 800),
            new TextureRegion(bloodSheet, 1280, 800 , 640, 800)
    };

    // Animations for blood splash
    private static final Animation<TextureRegion> bloodSplashAnimation = new Animation<>(0.15f, bloodFrames);


    private static final Texture fireSheet = new Texture(Gdx.files.internal("objects.png"));

    private static final TextureRegion[] fireFrames = {
            new TextureRegion(fireSheet, 4*16, 40 , 16, 10),
            new TextureRegion(fireSheet, 5*16, 40 , 16, 10),
            new TextureRegion(fireSheet, 6*16, 40 , 16, 10),
            new TextureRegion(fireSheet, 7*16, 40 , 16, 10)
    };

    private static final Animation<TextureRegion> fireOnPlayerAnimation = new Animation<>(0.15f, fireFrames);

    private static final TextureRegion texture = new TextureRegion(characterSheet, 0, 0, textureWidth, textureHeight);

    // Animations for different movement directions
    private static final TextureRegion[] walkDownFrames = {
            new TextureRegion(characterSheet, 0, 0, textureWidth, textureHeight),
            new TextureRegion(characterSheet, 16, 0, textureWidth, textureHeight),
            new TextureRegion(characterSheet, 32, 0, textureWidth, textureHeight),
            new TextureRegion(characterSheet, 48, 0, textureWidth, textureHeight)
    };
    private static final Animation<TextureRegion> walkDownAnimation = new Animation<>(0.15f, walkDownFrames);

    private static final TextureRegion[] walkUpFrames = {
            new TextureRegion(characterSheet, 0, 64, textureWidth, textureHeight),
            new TextureRegion(characterSheet, 16, 64, textureWidth, textureHeight),
            new TextureRegion(characterSheet, 32, 64, textureWidth, textureHeight),
            new TextureRegion(characterSheet, 48, 64, textureWidth, textureHeight)
    };
    private static final Animation<TextureRegion> walkUpAnimation = new Animation<>(0.15f, walkUpFrames);

    private static final TextureRegion[] walkRightFrames = {
            new TextureRegion(characterSheet, 0, 32, textureWidth, textureHeight),
            new TextureRegion(characterSheet, 16, 32, textureWidth, textureHeight),
            new TextureRegion(characterSheet, 32, 32, textureWidth, textureHeight),
            new TextureRegion(characterSheet, 48, 32, textureWidth, textureHeight)
    };
    private static final Animation<TextureRegion> walkRightAnimation = new Animation<>(0.15f, walkRightFrames);

    private static final TextureRegion[] walkLeftFrames = {
            new TextureRegion(characterSheet, 0, 96, textureWidth, textureHeight),
            new TextureRegion(characterSheet, 16, 96, textureWidth, textureHeight),
            new TextureRegion(characterSheet, 32, 96, textureWidth, textureHeight),
            new TextureRegion(characterSheet, 48, 96, textureWidth, textureHeight)
    };
    private static final Animation<TextureRegion> walkLeftAnimation = new Animation<>(0.15f, walkLeftFrames);

    private static final TextureRegion[] attackDownFrames = {
            new TextureRegion(characterSheet, 0, 128, 32, 32),
            new TextureRegion(characterSheet, 32, 128, 32, 32),
            new TextureRegion(characterSheet, 64, 128, 32, 32),
            new TextureRegion(characterSheet, 96, 128, 32, 32)
    };
    private static final Animation<TextureRegion> attackDownAnimation = new Animation<>(attackDuration/attackDownFrames.length, attackDownFrames);

    private static final TextureRegion[] attackUpFrames = {
            new TextureRegion(characterSheet, 0, 160, 32, 32),
            new TextureRegion(characterSheet, 32, 160, 32, 32),
            new TextureRegion(characterSheet, 64, 160, 32, 32),
            new TextureRegion(characterSheet, 96, 160, 32, 32)
    };
    private static final Animation<TextureRegion> attackUpAnimation = new Animation<>(attackDuration/attackUpFrames.length, attackUpFrames);

    private static final TextureRegion[] attackRightFrames = {
            new TextureRegion(characterSheet, 0, 192, 32, 32),
            new TextureRegion(characterSheet, 32, 192, 32, 32),
            new TextureRegion(characterSheet, 64, 192, 32, 32),
            new TextureRegion(characterSheet, 96, 192, 32, 32)
    };
    private static final Animation<TextureRegion> attackRightAnimation = new Animation<>(attackDuration/attackRightFrames.length, attackRightFrames);

    private static final TextureRegion[] attackLeftFrames = {
            new TextureRegion(characterSheet, 0, 224, 32, 32),
            new TextureRegion(characterSheet, 32, 224, 32, 32),
            new TextureRegion(characterSheet, 64, 224, 32, 32),
            new TextureRegion(characterSheet, 96, 224, 32, 32)
    };
    private static final Animation<TextureRegion> attackLeftAnimation = new Animation<>(attackDuration/attackLeftFrames.length, attackLeftFrames);

    private static final TextureRegion[] stretchDownFrames = {
            texture,
            new TextureRegion(characterSheet, 80, 0, textureWidth, textureHeight),
            texture,
            new TextureRegion(characterSheet, 80, 0, textureWidth, textureHeight),
            texture,
            new TextureRegion(characterSheet, 112, 0, textureWidth, textureHeight),
            new TextureRegion(characterSheet, 160, 0, textureWidth, textureHeight),
            new TextureRegion(characterSheet, 160, 0, textureWidth, textureHeight),
            new TextureRegion(characterSheet, 176, 0, textureWidth, textureHeight),
            new TextureRegion(characterSheet, 192, 0, textureWidth, textureHeight),
            new TextureRegion(characterSheet, 192, 0, textureWidth, textureHeight),
            new TextureRegion(characterSheet, 216, 0, textureWidth, textureHeight),
            new TextureRegion(characterSheet, 112, 0, textureWidth, textureHeight),
            new TextureRegion(characterSheet, 216, 0, textureWidth, textureHeight),
            new TextureRegion(characterSheet, 112, 0, textureWidth, textureHeight),
            texture
    };
    private static final Animation<TextureRegion> stretchDownAnimation = new Animation<>(0.35f, stretchDownFrames);

    private static final Texture dashTexture = new Texture(Gdx.files.internal("DashSprite.png"));
    private static final TextureRegion[] dashFrames = {
            new TextureRegion(dashTexture, 0, 0, 512, 128),
            new TextureRegion(dashTexture, 0, 128, 512, 128),
            new TextureRegion(dashTexture, 0, 256, 512, 128),
            new TextureRegion(dashTexture, 0, 384, 512, 128),
            new TextureRegion(dashTexture, 0, 512, 512, 128),
            new TextureRegion(dashTexture, 0, 640, 512, 128),
            new TextureRegion(dashTexture, 0, 768, 512, 128),
            new TextureRegion(dashTexture, 0, 896, 512, 128)
    };
    private static final Animation<TextureRegion> dashAnimation = new Animation<>(dashDuration/dashFrames.length, dashFrames);

    // Animation timing variable
    private float animationTime;

    private float stuckDuration;

    private float dashTime;
    private boolean isDashing = false;

    private float attackTime;
    private boolean isAttacking = false;
    private Direction dashDirection;

    private boolean keyCollected = false;

    private boolean isVictory = false;

    private Rectangle attackBoundingBox = new Rectangle();

    private RayHandler lightHandler;
    private PointLight pointLight;

    private static final Sound dashSoundEffect = Gdx.audio.newSound(Gdx.files.internal("dash_2.wav"));

    private static final Sound attackSoundEffect = Gdx.audio.newSound(Gdx.files.internal("attack_sound.wav"));
    private float bleedingTime;
    private boolean lightCollected = false;


    /**
     * Constructs a new Player object with the specified initial position.
     *
     * @param mazeMap the mazeMap this gameobject belongs to
     * @param position initial position of the player character.
     *
     */
    public Player(MazeMap mazeMap, Position position) {
        super(mazeMap, texture, width, height, walkable, starting_health, position, initialCharacterSpeed);
        animationTime = 0.35f * stretchDownFrames.length;
        dashTime = 1f;
        bleedingTime = 1f;
        attackTime = 1f;
        stuckDuration = -1f;
        this.setDirection(Direction.DOWN);
        this.dashDirection = getDirection();
        // https://stackoverflow.com/questions/31388986/libgdx-and-box2dlights-too-bright-colors-grayed-out
        RayHandler.useDiffuseLight(true);
        // add lighting taken from:
        // https://stackoverflow.com/questions/45973258/libgdx-basic-2d-lighting-dont-know-what-to-do
        lightHandler = new RayHandler(mazeMap.getWorld());
        pointLight = new PointLight(lightHandler,200, new Color(1,1f,1f,1f),8,position.x + getWidth()/2f,position.y+getHeight()/2f);
        pointLight.setContactFilter(MazeMap.WALL_FILTER, MazeMap.WALL_FILTER, MazeMap.WALL_FILTER);
        pointLight.setSoftnessLength(1.5f);
    }


    /**
     * Moves the player character in the specified direction, updating the animation
     * time if the direction changes during movement.
     *
     * @param direction The direction in which to move the player character.
     * @param delta     The time elapsed since the last frame.
     * @return True if the player character successfully moved, false otherwise.
     */
    @Override
    public boolean move(Direction direction, float delta) {
        if(stuckDuration > 0){
            // stuck. prevent movement
            return false;
        }
        if(isDashing){
            return false;
        }

        Direction currentDirection = getDirection();
        if (super.move(direction, delta)) {
            if (currentDirection != getDirection()) {
                animationTime = 0f;
            }
            if(isAttacking){
                updateAttackBoundingBox();
            }
        }
        return false;
    }

    /**
     * Initiates an attack action for the player entity if it's not already attacking.
     * Plays the attack sound effect, sets the attacking state, resets attack time,
     * adjusts the width and draw offset of the player entity for attack animation, and updates attack bounding box.
     */
    public void attack(){
        if(!isAttacking) {
            attackSoundEffect.play(); // Play the attack sound effect
            System.out.println("attack!");
            isAttacking = true; // Set the player entity to attacking state.
            attackTime = 0f; // Reset attack time.
            setWidth(getWidth() * 2f); // Double the width of the player entity for the attack animation.
            drawXOffset = getWidth() * -0.25f; // Set draw offset for attack animation.
            updateAttackBoundingBox(); // Update the attack bounding box.
            updateBoundingBox();
        }
    }

    /**
     * Updates the attack bounding box based on the player's direction.
     * Adjusts the attack bounding box position depending on the player's facing direction.
     */
    public void updateAttackBoundingBox(){
        Rectangle playerBoundingBox = getBoundingBox(); // Get the player's bounding box
        attackBoundingBox.set(playerBoundingBox); // Set the attack bounding box initially to player's bounding box.

        // Adjust the attack bounding box position based on the player's facing direction.
        switch(getDirection()){
            case UP:
                attackBoundingBox.setY(attackBoundingBox.getY() + attackBoundingBox.getHeight()*0.4f);
                // 20% extra to right and 10% to left
                attackBoundingBox.setWidth(attackBoundingBox.getWidth() * 1.3f);
                attackBoundingBox.setX(attackBoundingBox.getX() - attackBoundingBox.getWidth() * 0.1f);
                break;
            case DOWN:
                attackBoundingBox.setY(attackBoundingBox.getY() - attackBoundingBox.getHeight()*0.3f);
                // 20% extra to left and 20% to right
                attackBoundingBox.setWidth(attackBoundingBox.getWidth() * 1.3f);
                attackBoundingBox.setX(attackBoundingBox.getX() - attackBoundingBox.getWidth() * 0.2f);
                break;
            case LEFT:
                attackBoundingBox.setX(attackBoundingBox.getX() - attackBoundingBox.getWidth()*0.5f);
                // 10% extra to top and 20% to bottom
                attackBoundingBox.setHeight(attackBoundingBox.getHeight() * 1.3f);
                attackBoundingBox.setY(attackBoundingBox.getY() - attackBoundingBox.getHeight() * 0.2f);
                break;
            case RIGHT:
                attackBoundingBox.setX(attackBoundingBox.getX() + attackBoundingBox.getWidth()*0.4f);
                // 10% extra to top and 20% to bottom
                attackBoundingBox.setHeight(attackBoundingBox.getHeight() * 1.3f);
                attackBoundingBox.setY(attackBoundingBox.getY() - attackBoundingBox.getHeight() * 0.2f);
                break;
        }
    }

    /**
     * Checks for collisions between the player's attack bounding box and game objects.
     * Deals damage to the collided game objects based on the player's attack strength.
     */
    public void checkAttack(){
        // Retrieve a list of game objects colliding with the player's attack bounding box.
        List<GameObject> collisions = getMazeMap().getCollisions(attackBoundingBox, this);

        // Iterate through the colliding game objects and apply damage to them based on the player's attack strength.
        for(var gameObject : collisions){
            gameObject.takeDamage(getDamageDone());
        }
    }

    /**
     *
     * @return damage 1.0f if attackDamageTime is greater than attackTime else @return 0.0f
     */
    @Override
    public float getDamageDone(){
        if(attackTime < attackDamageTime){
            return 1.0f;
        }else{
            return 0.0f;
        }
    }

    /**
     * tells the character to start dashing
     */
    public void dash(){
        if(stuckDuration > 0){
            // stuck. prevent movement
            return;
        }
        if(dashTime > dashCooldown){
            System.out.println("Dash!");
            dashDirection = getDirection();
            // do dash in 20 smaller steps so we can stop if we hit something
            float stepSize = dashDistance / 20f;
            float distanceMoved = 0.0f;
            float nextPosition;
            boolean collision = false;
            attack();
            dashSoundEffect.play();
            while(!collision && distanceMoved < dashDistance){
                Position position = getPosition();
                switch(dashDirection){
                    case UP:
                        nextPosition = position.y + stepSize;
                        if(getMazeMap().characterAttemptMoveTo(this, position.x, nextPosition)){
                            updatePosition(position.x, nextPosition);
                            distanceMoved += stepSize;
                        }else{
                            collision = true;
                        }
                        break;
                    case DOWN:
                        nextPosition = position.y - stepSize;
                        if(getMazeMap().characterAttemptMoveTo(this, position.x, nextPosition)){
                            updatePosition(position.x, nextPosition);
                            distanceMoved += stepSize;
                        }else{
                            collision = true;
                        }
                        break;
                    case LEFT:
                        nextPosition = position.x - stepSize;
                        if(getMazeMap().characterAttemptMoveTo(this, nextPosition, position.y)){
                            updatePosition(nextPosition, position.y );
                            distanceMoved += stepSize;
                        }else{
                            collision = true;
                        }
                        break;
                    case RIGHT:
                        nextPosition = position.x + stepSize;
                        if(getMazeMap().characterAttemptMoveTo(this, nextPosition, position.y)){
                            updatePosition(nextPosition, position.y);
                            distanceMoved += stepSize;
                        }else{
                            collision = true;
                        }
                        break;
                }
                updateAttackBoundingBox();
                checkAttack();
            }
            if(distanceMoved > 0){
                if(distanceMoved >= dashDistance){
                    dashTime = 0f;
                }else{
                    // if we didn't move full distance, get percentage distance moved
                    // and set dashTime so we only play the animation for the actual move distance
                    // e.g. if we only moved 10% (distanceMoved / dashDistance == 0.1)
                    // we set dashTime to 90% of dashDuration to only play the last 10% of the dash animation
                    dashTime = dashDuration * (1 - distanceMoved / dashDistance);
                }
                isDashing = true;
            }
        }
    }


    /**
     * Handles collision interactions between the player entity and a game object.
     * This method processes collision actions defined for the collided game object,
     * such as taking damage, getting stuck, picking up items, or reaching an exit.
     * It performs corresponding actions based on the collision action type and updates
     * the player's state accordingly.
     *
     * @param gameObject The game object with which the player entity collides.
     */
    public void collision(GameObject gameObject) {
        // Retrieve collision actions associated with the collided game object.
        var collisionActions = gameObject.getCollisionActions();

        // If no collision actions are defined, return without further processing.
        if (collisionActions == null) {
            return;
        }

        // Iterate through each collision action defined for the collided game object.
        for (var collisionAction : collisionActions) {
            switch (collisionAction) {
                case TAKE_DAMAGE:
                    // Take damage from the collided game object and play collision sound effect.
                    takeDamage(gameObject.getDamageDone());
                    if(gameObject.getCollisionSoundEffect() != null){
                        gameObject.getCollisionSoundEffect().play();
                    }
                    bleedingTime = 0f;
                    break;
                case STUCK:
                    // Set the player entity as stuck for a specified duration and play collision sound effect.
                    stuckDuration = gameObject.getStuckDuration();
                    System.out.println("Player stuck for " + stuckDuration + "s.");
                    break;
                case PICK_UP:
                    // If the key has not been collected, collect it, destroy the game object, and play collision sound effect.
                    if (!keyCollected){
                        if(gameObject.getCollisionSoundEffect() != null){
                            gameObject.getCollisionSoundEffect().play();
                        }
                        keyCollected = true;
                        Exit.doorsOpenSoundEffect.play();
                        gameObject.destroy();
                        System.out.println("Found the key");
                    }
                    break;
                case HEART_UP:
                    if (gameObject instanceof Health && !((Health) gameObject).isHealthCollected()){
                        if(gameObject.getCollisionSoundEffect() != null){
                            gameObject.getCollisionSoundEffect().play();
                        }
                        setHealth(Math.max(getHealth()+1,0));
                        ((Health) gameObject).setHealthCollected(true);
                        gameObject.destroy();
                        System.out.println("Found the Heart");
                    }
                    break;
                case LIGHT_ON:
                    if (!lightCollected){
                        if(gameObject.getCollisionSoundEffect() != null){
                            gameObject.getCollisionSoundEffect().play();
                        }
                        lightCollected = true;
                        gameObject.destroy();
                        System.out.println("Found the Light");
                    }
                    break;
                case EXIT:
                    // Play collision sound effect and set victory state if the key has been collected.
                    if (keyCollected) {
                        if(gameObject.getCollisionSoundEffect() != null){
                            gameObject.getCollisionSoundEffect().play();
                        }
                        isVictory = true;
                    }
                    break;
            }
        }
    }

    @Override
    public void updatePosition(float x, float y){
        super.updatePosition(x, y);

        pointLight.setPosition(x + getWidth()/2f, y + getHeight()/2f );
    }

    /**
     * override updateBoundingBox as boundingBox is supposed to be slightly smaller than texture and texture width changes during attack
     */
    @Override
    public void updateBoundingBox(){
        Position pos = getPosition();
        if(isAttacking){
            getBoundingBox().set(pos.x + boundingBoxWidthOffset, pos.y + boundingBoxHeighthOffset, getWidth() * 0.5f * boundingBoxWidthFactor, getHeight() * boundingBoxHeightFactor);
        }else{
            getBoundingBox().set(pos.x + boundingBoxWidthOffset, pos.y + boundingBoxHeighthOffset, getWidth() * boundingBoxWidthFactor, getHeight() * boundingBoxHeightFactor);
        }
    }

    /**
     * override getBoundingBoxAtPosition as boundingBox is supposed to be slightly smaller than texture and texture width changes during attack
     *
     * @param x coordinate of new position
     * @param y coordinate of new position
     * @return boundingBox at that position
     */
    public Rectangle getBoundingBoxAtPosition(float x, float y) {
        if(isAttacking){
            return new Rectangle(x + boundingBoxWidthOffset, y + boundingBoxHeighthOffset, getWidth() * 0.5f * boundingBoxWidthFactor, getHeight() * boundingBoxHeightFactor);
        }else{
            return new Rectangle(x + boundingBoxWidthOffset, y + boundingBoxHeighthOffset, getWidth() * boundingBoxWidthFactor, getHeight() * boundingBoxHeightFactor);
        }

    }

    /**
     * Updates the state of the game entity over time based on the elapsed delta time.
     * It updates animation time, dash time, attack time, and decreases the stuck duration.
     * Additionally, it manages the state of dashing and attacking actions, updating relevant properties
     * and bounding boxes accordingly.
     *
     * @param delta The time elapsed since the last update, in seconds.
     */
    @Override
    public void update(float delta) {
        // Call the superclass update method to update entity state.
        super.update(delta);

        // Update animation time, dash time, attack time, and decrease stuck duration.
        animationTime += delta;
        dashTime += delta;
        attackTime += delta;
        stuckDuration -= delta;
        bleedingTime +=delta;

        if(dashTime > dashDuration){
            isDashing = false;
        }

        // If the entity is currently attacking, perform attack checks and manage attack duration.
        if(isAttacking){
            checkAttack();
            if(attackTime > attackDuration){
                isAttacking = false;
                setWidth(width);
                drawXOffset = 0;
                animationTime = 0.35f * stretchDownFrames.length; // set animation to after idle
                attackBoundingBox.set(0,0,0,0); // Reset attack bounding box.
                updateBoundingBox();
            }
        }
    }

    /**
     * Gets the current texture region of the player character based on its
     * direction and movement status.
     *
     * @return The current texture region of the player character.
     */
    @Override
    public TextureRegion getTexture() {
        // check if we were still moving
        boolean wasMoving = isMoving();
        // update movement tracking in superclass
        super.getTexture();

        if(isAttacking()) {
            switch (getDirection()) {
                case UP:
                    return attackUpAnimation.getKeyFrame(attackTime);
                case DOWN:
                    return attackDownAnimation.getKeyFrame(attackTime);
                case LEFT:
                    return attackLeftAnimation.getKeyFrame(attackTime);
                case RIGHT:
                    return attackRightAnimation.getKeyFrame(attackTime);
            }
        }
        // Check if the player character is currently moving
        // (doesn't necessarily mean position changes, but the texture is considered moving)
       else if (isMoving()) {
            switch (getDirection()) {
                case UP:
                    return walkUpAnimation.getKeyFrame(animationTime, true); //walkUpAnimation
                case DOWN:
                    return walkDownAnimation.getKeyFrame(animationTime, true); //walkDownAnimation
                case LEFT:
                    return walkLeftAnimation.getKeyFrame(animationTime, true); //walkLeftAnimation
                case RIGHT:
                    return walkRightAnimation.getKeyFrame(animationTime, true); //walkRightAnimation
            }
        }else {
            // Player is not moving
            if (wasMoving) {
                // start after animation end time
                animationTime = 0.35f * stretchDownFrames.length;
            }
            // reset animationTime after 10 seconds to play idle animation
            if (animationTime > 10) {
                animationTime = 0f;
                setDirection(Direction.DOWN);
            }
            switch (getDirection()) {
                case UP:
                    return walkUpFrames[0];
                case DOWN:
                    return stretchDownAnimation.getKeyFrame(animationTime);
                case LEFT:
                    return walkLeftFrames[0];
                case RIGHT:
                    return walkRightFrames[0];
            }
        }

        return texture;
    }

    /**
     * Retrieves the dash animation sprite based on the current dash state and direction.
     * If the dash animation duration has elapsed, it returns null, indicating that no dash animation is currently active.
     * Otherwise, it creates and configures a sprite representing the dash animation, adjusting its size, position, and orientation
     * based on the dash direction.
     *
     * @return The dash animation sprite if a dash is active and within its duration; otherwise, returns null.
     */
    public Sprite getDashAnimation(){
        if(dashTime > dashDuration){
            return null;
        }
        // Create a new sprite for the dash using the current frame from the dash animation sequence.
        Sprite dash = new Sprite(dashAnimation.getKeyFrame(dashTime));
        // animation is 4 times as wide as high
        dash.setSize(dashDistance, dashDistance/3f);
        float dashWidth = dashDistance*0.9f;
        float dashHeight = getHeight()*0.6f;

        // Adjust sprite properties based on the direction of the dash.
        switch (dashDirection) {
            case UP:
                dash.flip(false, true); // Flip sprite vertically
                dash.rotate90(false); // Rotate sprite 90 degrees counter-clockwise
                dashWidth = dashWidth * 0.8f; // reduce dash width when going up as there is no character model above it
                dash.setSize(dashHeight, dashWidth); // swap height/width due to rotation
                dash.setPosition(getPosition().x-getHeight()*0.05f, getPosition().y-dashWidth+getHeight()*0.3f);
                return dash;
            case DOWN:
                dash.flip(false, false); // Flip sprite vertically
                dash.rotate90(true); // Rotate sprite 90 degrees clockwise
                dash.setSize(dashHeight, dashWidth); // swap height/width due to rotation
                dash.setPosition(getPosition().x-getHeight()*0.05f, getPosition().y+getHeight()*0.1f);
                return dash;
            case LEFT:
                dash.flip(true, false); // Flip sprite horizontally
                dash.setSize(dashWidth, dashHeight);
                dash.setPosition(getPosition().x+getWidth()*0.1f, getPosition().y);
                return dash;
            case RIGHT:
                dash.setSize(dashWidth, dashHeight);
                dash.setPosition(getPosition().x-dashWidth+getWidth()*0.4f, getPosition().y);
                return dash;
        }
        return null;
    }

    public Sprite getBleedingAnimation(){
        if(bleedingTime > bloodSplashAnimation.getAnimationDuration()){
            return null;
        }
        Sprite bloodSplash = new Sprite(bloodSplashAnimation.getKeyFrame(bleedingTime));
        float width = getWidth()*2f;
        if(isAttacking){
            width = width*0.5f; // width is doubled when attacking
        }
        float height = width * 1.25f;
        bloodSplash.setPosition(getPosition().x-width*0.28f, getPosition().y-height*0.1f);
        bloodSplash.setSize(width, height);

        return bloodSplash;

    }


    public float getDrawXOffset() {
        return drawXOffset;
    }

    public float getDrawYOffset() {
        return drawYOffset;
    }

    public boolean isKeyCollected() {
        return keyCollected;
    }

    public void setKeyCollected(boolean keyCollected) {
        this.keyCollected = keyCollected;
    }

    public boolean isLightCollected() {
        return lightCollected;
    }

    public void setLightCollected(boolean lightCollected) {
        this.lightCollected = lightCollected;
    }

    public boolean isVictory() {
        return isVictory;
    }

    public void setVictory(boolean victory) {
        isVictory = victory;
    }

    public boolean isDashing() {
        return isDashing;
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    public Rectangle getAttackBoundingBox(){
        return attackBoundingBox;
    }

    public RayHandler getLightHandler() {
        return lightHandler;
    }

}
