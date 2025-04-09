package de.tum.cit.ase.maze.traps;

import de.tum.cit.ase.maze.MazeMap;
import de.tum.cit.ase.maze.Position;

/**
 * The TrapCreator class is responsible for creating traps based on the provided level.
 * It provides a static method to create a trap instance appropriate for a given level.
 */

public class TrapCreator {

    /**
     * Creates a trap based on the specified level.
     *
     * @param mazeMap the mazeMap this gameobject belongs to
     * @param position position of the Trap
     * @param level The level for which to create a trap.
     * @return A Trap object corresponding to the specified level.
     */

    public static Trap createTrap(MazeMap mazeMap, Position position, int level){
        if(level == 1){
            return new Fire(mazeMap, position);
        }else {
            double rng = Math.random();
            if(rng < 0.5){
                return new TriggerSpikes(mazeMap, position);
            }else{
                return new Fire(mazeMap, position);
            }
        }
    }
}
