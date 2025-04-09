package de.tum.cit.ase.maze.characters;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.maze.GameObject;
import de.tum.cit.ase.maze.MazeMap;
import de.tum.cit.ase.maze.Position;
import de.tum.cit.ase.maze.enums.Direction;

import static com.badlogic.gdx.math.MathUtils.random;

/**
 * This is the base character abstract class that extends the GameObject abstract class.
 * It represents Character information, movement capabilities
 */

public abstract class Character extends GameObject {

    /**
     * characterSpeed tile per unit of time
     */
    private float characterSpeed;
    /**
     * direction the character is facing
     */
    private Direction direction;

    /**
     * Time elapsed since the last movement
     */
    private float lastMoveDelta = 0f;

    /**
     * indicates if the character is currently moving
     */
    private boolean moving;

    /**
     * Constructer arguments for the abstract character class that subclasses must provide
     *
     * @param mazeMap The maze map where the character and other game objects exist.
     * @param texture The texture region representing the character.
     * @param width The width of the character.
     * @param height The height of the character.
     * @param walkable Indicates if the character can walk over it.
     * @param health The health of the character.
     * @param position The initial position of the character.
     * @param characterSpeed The speed of the character.
     */
    public Character(MazeMap mazeMap, TextureRegion texture, float width, float height, boolean walkable, float health, Position position, float characterSpeed){
        super(mazeMap, texture, width, height, position, walkable, health);
        this.characterSpeed = characterSpeed;
        this.direction = Direction.values()[random.nextInt(Direction.values().length)];
        this.moving = false;
    }

    /**
     * Moves the character one space in the given direction if the move is possible
     *
     * @param direction UP, DOWN, LEFT or RIGHT
     * @param delta time elapsed since the last frame
     * @return true if character moved, false otherwise.
     */
    public boolean move(Direction direction, float delta){
        float moveDistance = characterSpeed * delta;

        // reset movement tracking since a move attempt was made
        moving = true;
        lastMoveDelta = 0f;
        this.direction = direction; // change direction character looks even if we won't move
        Position position = getPosition();
        switch(direction){
            case UP:
                if(getMazeMap().characterAttemptMoveTo(this, position.x, position.y + moveDistance)){
                    updatePosition(position.x, position.y + moveDistance);
                    return true;
                }
                break;
            case DOWN:
                if(getMazeMap().characterAttemptMoveTo(this, position.x, position.y - moveDistance)){
                    updatePosition(position.x, position.y - moveDistance);
                    return true;
                }
                break;
            case LEFT:
                if(getMazeMap().characterAttemptMoveTo(this, position.x - moveDistance, position.y)){
                    updatePosition(position.x - moveDistance, position.y );
                    return true;
                }
                break;
            case RIGHT:
                if(getMazeMap().characterAttemptMoveTo(this, position.x + moveDistance, position.y)){
                    updatePosition(position.x + moveDistance, position.y);
                    return true;
                }
                break;
        }
        return false;
    }

    /**
     * Sets the character as moving.
     */
    public void setMoving(){
        moving = true;
        lastMoveDelta = 0f;
    }

    /**
     * Updates the character's state.
     * @param delta The time elapsed since the last update.
     */
    @Override
    public void update(float delta){
        super.update(delta);
        lastMoveDelta += delta;
    }

    /**
     * Checks if the character is stuck.
     * @param delta The time elapsed since the last update.
     * @return true if the character is stuck, false otherwise.
     */
    public boolean isStuck(float delta){
        float moveDistance = characterSpeed * delta;
        Position position = getPosition();
        // if we can move in any direction, we are not stuck
        return !(getMazeMap().characterAttemptMoveTo(this, position.x, position.y + moveDistance) ||
                getMazeMap().characterAttemptMoveTo(this, position.x - moveDistance, position.y) ||
                getMazeMap().characterAttemptMoveTo(this, position.x, position.y - moveDistance) ||
                getMazeMap().characterAttemptMoveTo(this, position.x + moveDistance, position.y)
                );
    }

    public float getCharacterSpeed() {
        return characterSpeed;
    }

    public void setCharacterSpeed(float characterSpeed) {
        this.characterSpeed = characterSpeed;
    }

    public float getLastMoveDelta() {
        return lastMoveDelta;
    }


    public boolean isMoving() {
        return moving;
    }


    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public TextureRegion getTexture(){
        // character is considered movıng for 300ms longer than actually changing positions
        // this is so animations can finish
        if(lastMoveDelta > 0.3f){
            moving = false;
        }
        return texture;
    }
}
