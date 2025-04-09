package de.tum.cit.ase.maze.enums;

import de.tum.cit.ase.maze.exceptions.UnknownGameObjectException;

/**
 * The GameObjectID enum represents the possible types of game objects in a maze
 * or a similar context. Each game object type is associated with a unique identifier.
 */

public enum GameObjectID {

    WALL(0),
    ENTRYPOINT(1),
    EXIT(2),
    TRAP(3),
    ENEMY(4),
    KEY(5),
    HEALTH(6),
    TIMEDSPIKES_1(7),
    TIMEDSPIKES_2(8),
    LIGHTING(9);

    private final int id;


    private GameObjectID(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    /**
     * Returns the GameObjectID associated with the specified identifier.
     *
     * @param id The identifier of the game object type.
     * @return The corresponding GameObjectID.
     * @throws UnknownGameObjectException If the specified identifier is not associated with any known game object type.
     */

    public GameObjectID getById(int id) throws UnknownGameObjectException {
        if(id == WALL.id){
            return WALL;
        }
        else if(id == ENTRYPOINT.id){
            return ENTRYPOINT;
        }
        else if(id == EXIT.id){
            return EXIT;
        }
        else if(id == TRAP.id){
            return TRAP;
        }
        else if(id == ENEMY.id){
            return ENEMY;
        }
        else if(id == KEY.id){
            return KEY;
        }
        else if(id == HEALTH.id){
            return HEALTH;
        }else if(id == TIMEDSPIKES_1.id){
            return TIMEDSPIKES_1;
        }else if(id == TIMEDSPIKES_2.id){
            return TIMEDSPIKES_2;
        }else if(id == LIGHTING.id){
            return LIGHTING;
        }

        throw new UnknownGameObjectException("ID " + id  + " is not a known GameObject type.");
    }

}
