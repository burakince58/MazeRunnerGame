package de.tum.cit.ase.maze.tiles;

import de.tum.cit.ase.maze.MazeMap;
import de.tum.cit.ase.maze.Position;
import de.tum.cit.ase.maze.characters.Ghost;

/**
 * The TileCreator class is responsible for creating tiles based on the provided level.
 * It provides a static method to create a tile instance appropriate for a given level.
 */

public class TileCreator {

    /**
     * Creates a tile based on the specified level.
     *
     * @param mazeMap the mazeMap this gameobject belongs to
     * @param level The level for which to create a tile.
     * @param position position of the Tile
     * @return A Tile object corresponding to the specified level.
     */
    public static Tile createTile(MazeMap mazeMap, Position position, int level){
        if(level == 1){
            return new Grass(mazeMap, position);
        }else if(level == 2){
            return new StoneFloor(mazeMap, position);
        }
        // grass implemented for all levels
        return new Grass(mazeMap, position);
    }
}
