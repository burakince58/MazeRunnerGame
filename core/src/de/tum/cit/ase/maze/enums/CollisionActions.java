package de.tum.cit.ase.maze.enums;

/**
 *  Possible actions taken upon a collision
 *
 *  GameObject includes appropriate methods to provide values for such actions
 */
public enum CollisionActions {
    TAKE_DAMAGE, // GameObject.getDamageDone();
    PICK_UP,
    HEART_UP,
    EXIT,
    STUCK,
    LIGHT_ON;
}
