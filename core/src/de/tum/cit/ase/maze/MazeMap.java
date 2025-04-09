package de.tum.cit.ase.maze;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.maze.characters.Character;
import de.tum.cit.ase.maze.characters.EnemyCreator;
import de.tum.cit.ase.maze.characters.Player;
import de.tum.cit.ase.maze.enums.GameObjectID;
import de.tum.cit.ase.maze.exceptions.UnknownGameObjectException;
import de.tum.cit.ase.maze.items.Health;
import de.tum.cit.ase.maze.items.Key;
import de.tum.cit.ase.maze.items.Lighting;
import de.tum.cit.ase.maze.tiles.*;
import de.tum.cit.ase.maze.traps.TimedSpikes;
import de.tum.cit.ase.maze.traps.TrapCreator;


import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Represent the maze map in the game
 */

public class MazeMap {

    /**
     * height of the maze map
     */
    private final int height;

    /**
     * width of the maze map
     */
    private final int width;

    /**
     * 2D representing the tile of the maze map
     */
    private final Tile[][] tiles;

    /**
     * Mapping of the static objects like traps and key in the maze map. With their positions
     */
    private final Map<Point, GameObject> staticObjects;

    //we have Map<Position, GameObject

    /**
     * List of Enemies (like Ghost)
     */
    private final List<GameObject> dynamicObjects;

    /**
     * Player in the maze map
     */
    private final Player player;

    private final World world;

    public static final short WALL_FILTER = 1;

    /**
     *
     * @param height height of the maze map
     * @param width width of the maze map
     * @param gameObjects map representing of game objects at initialization
     * @param level level of the game
     */
    public MazeMap(int height, int width, Map<Point, Integer> gameObjects, int level){
        this.height = height;
        this.width = width;
        this.tiles = new Tile[height][width];
        this.dynamicObjects = new ArrayList<>(10);
        this.staticObjects = new HashMap<>();
        this.world = new World(new Vector2(0,0), false);

        // make walls impenetrable by light
        // taken from https://stackoverflow.com/questions/18550123/libgdx-make-lights-ignore-bodies
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(0.5f, 0.5f);
        FixtureDef gridBox = new FixtureDef();
        gridBox.shape = groundBox;
        gridBox.density = 1.0f;
        gridBox.filter.groupIndex = WALL_FILTER;
        gridBox.filter.categoryBits = WALL_FILTER;
        gridBox.filter.maskBits = WALL_FILTER;

        Position playerPosition = null;

        for(var gameObject : gameObjects.entrySet()){
            try {
                GameObjectID gameObjectID = GameObjectID.ENEMY.getById(gameObject.getValue());
                Point point = gameObject.getKey();
                Position position = new Position(point.x, point.y);
                switch(gameObjectID){
                    case WALL:
                        tiles[point.y][point.x] = new Wall(this, position);
                        BodyDef bodyDef = new BodyDef();
                        // center of body, not left bottom like in our game
                        bodyDef.position.set(point.x+0.5f, point.y+0.5f);
                        bodyDef.fixedRotation = true;
                        bodyDef.type = BodyDef.BodyType.DynamicBody;
                        Body body = world.createBody(bodyDef);
                        body.createFixture(gridBox);
                        break;
                    case ENTRYPOINT:
                        tiles[point.y][point.x] = new EntryPoint(this, position);
                        // ofset player a bit to center on tile
                        playerPosition = new Position(position.x+0.125f, position.y);
                        break;
                    case EXIT:
                        tiles[point.y][point.x] = new Exit(this, position);
                        break;
                    case TRAP:
                        staticObjects.put(point, TrapCreator.createTrap(this, position, level));
                        break;
                    case ENEMY:
                        dynamicObjects.add(EnemyCreator.createEnemy(this, position, level));
                        break;
                    case KEY:
                        staticObjects.put(point, new Key(this, position));
                        break;
                    case HEALTH:
                        staticObjects.put(point, new Health(this, position));
                        break;
                    case TIMEDSPIKES_1:
                        staticObjects.put(point, new TimedSpikes(this, position, true));
                        break;
                    case TIMEDSPIKES_2:
                        staticObjects.put(point, new TimedSpikes(this, position, false));
                        break;
                    case LIGHTING:
                        staticObjects.put(point, new Lighting(this, position));
                        break;
                }
            } catch (UnknownGameObjectException e) {
                throw new RuntimeException(e);
            }
        }

        if(playerPosition == null){
            playerPosition = new Position(1,1);
        }
        player = new Player(this, playerPosition);

        //Let's create 3 healths on the map
        for (int i = 0; i < 3; i++) {
            //Let's find a position to add Heart on the Map
            int randomX = (int) (Math.random() * width);
            int randomY = (int) (Math.random() * height);
            Point alreadyTaken = new Point(randomX, randomY);
            while (tiles[randomY][randomX] != null || staticObjects.containsKey(alreadyTaken)) {
                randomX = (int) (Math.random() * width);
                randomY = (int) (Math.random() * height);
                alreadyTaken = new Point(randomX, randomY);
            }
            Position position2 = new Position(randomX, randomY);
            staticObjects.put(alreadyTaken, new Health(this, position2));
        }

        //Let's create 1 light on the map
        for (int i = 0; i < 1; i++) {
            //Let's find a position to add Heart on the Map
            int randomX = (int) (Math.random() * width);
            int randomY = (int) (Math.random() * height);
            Point alreadyTaken = new Point(randomX, randomY);

            while (tiles[randomY][randomX] != null || staticObjects.containsKey(alreadyTaken)) {
                randomX = (int) (Math.random() * width);
                randomY = (int) (Math.random() * height);
                alreadyTaken = new Point(randomX, randomY);
            }
            Position position2 = new Position(randomX, randomY);
            staticObjects.put(alreadyTaken, new Lighting(this, position2));
        }

        //Let's create 2 timed spikes retracted:true on the map
        for (int i = 0; i < 2; i++) {
            //Let's find a position to timed spikes on the Map
            int randomX = (int) (Math.random() * width);
            int randomY = (int) (Math.random() * height);
            Point alreadyTaken = new Point(randomX, randomY);
            while (tiles[randomY][randomX] != null || staticObjects.containsKey(alreadyTaken)) {
                randomX = (int) (Math.random() * width);
                randomY = (int) (Math.random() * height);
                alreadyTaken = new Point(randomX, randomY);
            }
            Position position2 = new Position(randomX, randomY);
            staticObjects.put(alreadyTaken, new TimedSpikes(this, position2, true));
        }

        //Let's create 2 timed spikes retracted:false on the map
        for (int i = 0; i < 2; i++) {
            //Let's find a position to add timed spikes on the Map
            int randomX = (int) (Math.random() * width);
            int randomY = (int) (Math.random() * height);
            Point alreadyTaken = new Point(randomX, randomY);
            while (tiles[randomY][randomX] != null || staticObjects.containsKey(alreadyTaken)) {
                randomX = (int) (Math.random() * width);
                randomY = (int) (Math.random() * height);
                alreadyTaken = new Point(randomX, randomY);
            }
            Position position2 = new Position(randomX, randomY);
            staticObjects.put(alreadyTaken, new TimedSpikes(this, position2, false));
        }

        // add walkable tiles
       for(int y = 0 ; y < height; y++){
           for(int x = 0 ; x < width; x++){
               if(tiles[y][x] == null){
                   tiles[y][x] = TileCreator.createTile(this, new Position(x, y), level);
               }
           }
       }
    }

    /**
     * Attempts a move for the given character to the given coordinates
     *
     * This can cause collision
     *
     * @param character the character that's trying to move
     * @param x coordinate
     * @param y coordinate
     * @return true if the character can make the move, false otherwise.
     */
    public boolean characterAttemptMoveTo(Character character, float x, float y){
        if(x < 0.0 || x >= width || y < 0.0 || y >= height){
            return false;
        }

        Rectangle boundingBox = character.getBoundingBoxAtPosition(x, y);

        // create all 2d points the bounding box touches
        Set<Point> relevantPoints = new HashSet<>();
        relevantPoints.add(new Point((int)boundingBox.getX(), (int)boundingBox.getY()));
        relevantPoints.add(new Point((int)(boundingBox.getX() + boundingBox.getWidth()), (int)boundingBox.getY()));
        relevantPoints.add(new Point((int)boundingBox.getX(), (int)(boundingBox.getY() + boundingBox.getHeight())));
        relevantPoints.add(new Point((int)(boundingBox.getX() + boundingBox.getWidth()), (int)(boundingBox.getY() + boundingBox.getHeight())));

        // check for tile and static collision
        for(Point point : relevantPoints){
            if(point.x < 0 || point.y < 0 || point.x >= width || point.y >= height){
                // skip if point is out of bounds
                continue;
            }
            // if we overlap with the tile bounding box and it's not walkable prevent move(wall, exit etc.)
            if(boundingBox.overlaps(tiles[point.y][point.x].getBoundingBox())){
                // player collides with tile
                if(character.equals(player)){
                    player.collision(tiles[point.y][point.x]);
                }
                if(!tiles[point.y][point.x].isWalkable()){
                    return false;
                }
            }
            GameObject staticObject = staticObjects.get(point);
            // if there is a static object saved at that point and the bounding boxes overlap and it's not walkable return false
            if(staticObject != null && boundingBox.overlaps(staticObject.getBoundingBox())){
                // if character is the player it touches with staticObject
                if(character.equals(player)){
                    player.collision(staticObject);
                }
                if(!staticObject.isWalkable()){
                    return false;
                }
            }
        }

        // check for dynamic object collision
        // these are objects that can move themselves and can also be bigger than 1x1. so we always check against all of them
        for(var dynamicObject : dynamicObjects){
            // ignore overlaps with oneself
            if(!character.equals(dynamicObject)){
                if(boundingBox.overlaps(dynamicObject.getBoundingBox())){
                    if(character.equals(player)){
                        // player touches dynamicObject
                        player.collision(dynamicObject);
                    }
                    if(!dynamicObject.isWalkable()){
                        return false;
                    }
                }
            }
        }
        if(!character.equals(player)){
            if(boundingBox.overlaps(player.getBoundingBox())){
                // npc touches the player
                player.collision(character);
                return false;
            }
        }
        return true;
    }

    /**
     * Returns all collisions with a given boundingBox ignoring the given gameObject
     *
     * @param boundingBox box that's used to check for collisions
     * @param self a gameObject the boundingBox is attached to. can be provided to ignore self-collision
     *
     */
    public List<GameObject> getCollisions(Rectangle boundingBox, GameObject self){

        List<GameObject> collisions = new LinkedList<>();

        // create all 2d points the bounding box touches
        Set<Point> relevantPoints = new HashSet<>();
        relevantPoints.add(new Point((int)boundingBox.getX(), (int)boundingBox.getY()));
        relevantPoints.add(new Point((int)(boundingBox.getX() + boundingBox.getWidth()), (int)boundingBox.getY()));
        relevantPoints.add(new Point((int)boundingBox.getX(), (int)(boundingBox.getY() + boundingBox.getHeight())));
        relevantPoints.add(new Point((int)(boundingBox.getX() + boundingBox.getWidth()), (int)(boundingBox.getY() + boundingBox.getHeight())));

        // check for tile and static collision
        for(Point point : relevantPoints){
            if(point.x < 0 || point.y < 0 || point.x >= width || point.y >= height){
                // skip if point is out of bounds
                continue;
            }
            // if we overlap with the tile bounding box and it's not walkable prevent move(wall, exit etc.)
            if(boundingBox.overlaps(tiles[point.y][point.x].getBoundingBox())){
                if(!tiles[point.y][point.x].equals(self)){
                    collisions.add(tiles[point.y][point.x]);
                }
            }
            GameObject staticObject = staticObjects.get(point);
            // if there is a static object saved at that point and the bounding boxes overlap
            if(staticObject != null && boundingBox.overlaps(staticObject.getBoundingBox())){
                if(!staticObject.equals(self)){
                    collisions.add(staticObject);
                }
            }
        }

        // check for dynamic object collision
        // these are objects that can move themselves and can also be bigger than 1x1. so we always check against all of them
        for(var dynamicObject : dynamicObjects){
            if(boundingBox.overlaps(dynamicObject.getBoundingBox())){
                if(!dynamicObject.equals(self)){
                    collisions.add(dynamicObject);
                }
            }
        }
        if(boundingBox.overlaps(player.getBoundingBox())){
            if(!player.equals(self)){
                collisions.add(player);
            }
        }
        return collisions;
    }

    public Player getPlayer() {
        return player;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Tile getTile(int x, int y){
        return tiles[y][x];
    }

    public GameObject getStaticObject(float x, float y){
        return staticObjects.get(new Position(x, y));
    }

    public Map<Point, GameObject> getStaticObjects() {
        return staticObjects;
    }

    public List<GameObject> getDynamicObjects() {
        return dynamicObjects;
    }

    public World getWorld() {
        return world;
    }

}
