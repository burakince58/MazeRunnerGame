package de.tum.cit.ase.maze.enums;

/**
 * Understand and apply respective states in the game.
 * These states dictate the current phase or condition of the game's progression.
 */
public enum GameState {
    /**
     * The game is in the main menu state.
     * Player typically interacts with the game's main menu options.
     */
    MENU,

    /**
     * The game is currently running.
     * Player is actively engaged in gameplay.
     */
    RUNNING,

    /**
     * The game is paused.
     * Gameplay is temporarily halted, allowing players to perform other actions
     * or access menu options without progressing the game state.
     */
    PAUSED,

    /**
     * The game has concluded with the player losing.
     * This state typically occurs when all lives are lost
     */
    GAME_OVER,

    /**
     * The game has concluded with the player achieving victory.
     * Victory is usually attained by collecting key and reaching the gate without losing all lives.
     */
    VICTORY
}
