package de.tum.cit.ase.maze.characters;

import de.tum.cit.ase.maze.MazeMap;
import de.tum.cit.ase.maze.Position;
import de.tum.cit.ase.maze.characters.Enemy;
import de.tum.cit.ase.maze.characters.Ghost;

/**
 * The EnemyCreator class is responsible for creating enemy characters
 * that picks which enemy type to create for a certain level
 * It provides a static method called createEnemy that returns an
 * instance of the appropriate enemy type for the given level
 */

public final class EnemyCreator {

    /**
     * Creates an enemy character based on the specified level
     *
     * @param mazeMap the mazeMap this gameobject belongs to
     * @param position The initial position of the enemy character
     * @param level    The level for which to create the enemy
     * @return An instance of the appropriate enemy type for the given level
     */
    public static Enemy createEnemy(MazeMap mazeMap, Position position, int level){
        // method to create different enemy types based on the level.
        if(level == 1){
            return new Ghost(mazeMap, position);
        }

        return new Ghost(mazeMap, position);
    }

}
