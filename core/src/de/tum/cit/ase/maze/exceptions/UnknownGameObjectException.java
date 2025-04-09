package de.tum.cit.ase.maze.exceptions;

/**
 * The `UnknownGameObjectException` class is an exception that is thrown when
 * an unknown game object is encountered.
 * This exception is a subclass of the general Exception class.
 *
 */

public class UnknownGameObjectException extends Exception{

    /**
     * Constructs a new `UnknownGameObjectException` with the specified error message.
     * @param errorMessage A string describing the specific error condition.
     */
    public UnknownGameObjectException(String errorMessage) {
        super(errorMessage);
    }
}
