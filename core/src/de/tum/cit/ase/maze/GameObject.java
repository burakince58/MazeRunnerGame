package de.tum.cit.ase.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import de.tum.cit.ase.maze.enums.CollisionActions;

import java.util.List;
import java.util.Objects;

/**
 * GameObject class represents a generic object in the game world.
 * It serves as the base class for various game entities with common attributes.
 * Subclasses are expected to provide specific behavior and additional properties.
 *
 */

public abstract class GameObject {

    private static int nextID = 0;

    private int id;

    private final MazeMap mazeMap;

    /**
     * The width of the game object.
     */
    private float width;

    /**
     * The height of the game object.
     */
    private float height;

    /**
     * Position that character is at
     */
    private final Position position;

    /**
     * bounding box of this game object
     */
    private final Rectangle boundingBox;

    /**
     * Defines weather this game object can be walked on
     */
    private boolean walkable;

    /**
     * The texture representing the appearance of the game object.
     */
    protected TextureRegion texture;

    private boolean destroyed = false;

    private float health;
    private float timeSinceDamageTaken;

    /**
     * Constructs a GameObject with the specified parameters.
     *
     * @param mazeMap the mazeMap this gameobject belongs to
     * @param texture  texture representing the appearance of the game object.
     * @param width    width of the game object.
     * @param height   height of the game object.
     * @param position position of the gameobject
     * @param walkable Defines weather this game object can be walked on
     */
    public GameObject(MazeMap mazeMap, TextureRegion texture, float width, float height, Position position, boolean walkable, float health){
        this.mazeMap = mazeMap;
        this.id = nextID++;
        this.texture = texture;
        this.width = width;
        this.height = height;
        this.position = position;
        this.boundingBox = new Rectangle();
        updateBoundingBox();
        this.walkable = walkable;
        this.health = health;
        timeSinceDamageTaken = 0;

    }

    /**
     * Gets the texture of the game object.
     *
     * @return texture representing the appearance of the game object.
     */
    public TextureRegion getTexture(){
        return texture;
    }
    public void setTexture(TextureRegion texture){
        this.texture = texture;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width){
        this.width = width;
    }
    public float getHeight() {
        return height;
    }

    public void setHeight(float height){
        this.height = height;
    }

    public Position getPosition() {
        return position;
    }

    /**
     * can be overriden by subclasses to track elapsed time and react
     *
     * @param delta
     */
    public void update(float delta){
        timeSinceDamageTaken += delta;
    }

    /**
     *  update GameObject position. also calls updateBoundingBox()
     *
     * @param x new x coordinate
     * @param y new y coordinate
     */
    public void updatePosition(float x, float y){
        position.x = x;
        position.y = y;
        updateBoundingBox();
    }

    /**
     * updates the bounding box. should be called whenever object position changes
     *
     * Can be overriden by subclasses if bounding box shouldn't match texture size
     *
     */
    public void updateBoundingBox(){
        boundingBox.set(position.x, position.y, width, height);
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    /**
     * gets the bounding box at the specified position via getBoundingBoxAtPosition(float x, float y)
     *
     * @param position
     * @return boundingBox at that position
     */
    public Rectangle getBoundingBoxAtPosition(Position position) {
        return getBoundingBoxAtPosition(position.x, position.y);
    }

    public void takeAction(float delta) {}

    /**
     * gets the bounding box at the specified position
     *
     * this is used as a temporary boundingBox to calculate if movement to a position is possible
     * and can be overriden if a box should not match the texture size
     *
     * @param x coordinate
     * @param y coordinate
     * @return boundingBox at that position
     */
    public Rectangle getBoundingBoxAtPosition(float x, float y) {
        return new Rectangle(x, y, width, height);
    }

    public boolean isWalkable() {
        return this.walkable;
    }

    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    /**
     * sets this objects state to destroyed;
     */
    public void destroy() {
        this.destroyed = true;
        System.out.println(this.getClass().getSimpleName() + "(" + id + ") was destroyed.");
    }

    /**
     *  Returns the collision actions this GameObject triggers upon collision
     *
     *  per default null.
     *
     * @return list of CollisionActions. Returns null for no actions
     */
    public List<CollisionActions> getCollisionActions(){
        return null;
    }

    /**
     * Returns the collision sound effect of the given object. should be overriden by subclasses
     *
     * @return collisionSoundEffect
     */
    public Sound getCollisionSoundEffect(){
        return null;
    }

    /**
     * Returns the amount of damage done. Can be modified by subclasses.
     *
     * @return damage done
     */
    public float getDamageDone(){
        return 0.0f;
    }

    /**
     * Returns the time a character gets stuck from the CollisionAction.STUCK
     *
     * @return -1f per default. modified in subclasses
     */
    public float getStuckDuration(){
        return -1f;
    }

    /**
     *  makes the GameObject take damage. objects are immune to damage for 250ms after taking damage
     *
     * @param damageDone how much health is lost
     */
    public void takeDamage(float damageDone) {
        if(timeSinceDamageTaken > 0.25f){
            health -= damageDone;
            timeSinceDamageTaken = 0f;
            System.out.println(this.getClass().getSimpleName() + "(" + id + ") took " + damageDone + " damage. " + health + " health remaining.");
            if(health <= 0f){
                destroy();
            }
        }
    }

    public MazeMap getMazeMap() {
        return mazeMap;
    }

    public float getHealth(){
        return health;
    }

    public void setHealth(float health){
        this.health = health;
    }

    /**
     * Returns a string representation of the game object.
     *
     * @return string containing information about the game object.
     */
    public String toString(){
        return String.format("%s with Texture %s(%d, %d) width %f height %f walkable %b.",
                this.getClass().getSimpleName(),
                this.texture.getTexture().toString(),
                this.texture.getRegionX(),
                this.texture.getRegionY(),
                this.width,
                this.height,
                this.walkable);
    }

    // intellij generated
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameObject that = (GameObject) o;
        return id == that.id;
    }

    // intellij generated
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    protected float distance(GameObject otherObject){
        Position otherPos = otherObject.getPosition();
        float xDiff = otherPos.x - position.x;
        float yDiff = otherPos.y - position.y;
        return (float)Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }
}