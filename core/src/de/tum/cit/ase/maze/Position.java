package de.tum.cit.ase.maze;

import java.util.Objects;

/**
 * Position class represents a position in a 2D space with x and y coordinates.
 * This class provides methods for setting the position using either individual
 * coordinates or another Position object. It also overrides the equals and hashCode
 * methods for proper comparison and hashing based on the x and y coordinates.
 *
 */

public class Position {

    /**
     * X coordinate of the position
     */
    public float x;

    /**
     * Y coordinate of the position
     */
    public float y;

    /**
     * Constructs a Position object with the specified x and y coordinates.
     * @param x X coordinate of the position
     * @param y Y coordinate of the position
     */
    public Position(float x, float y){
        this.x = x;
        this.y = y;
    }

    public Position(Position otherPosition){
        this(otherPosition.x, otherPosition.y);
    }


    public void setPosition(Position position){
        this.x = position.x;
        this.y = position.y ;
    }

    /**
     * Checks if this Position is equal to another object.
     * @param o object to compare with this Position.
     * @return true if the objects are equal, otherwise false.
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }


    /**
     * Generates a hash code for this Position.
     * @return hash code based on the x and y coordinates.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Position{" + x + "," + y + '}';
    }
}
