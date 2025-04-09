package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.ase.maze.enums.GameState;

/**
 * The HowToPlay class represents the screen displayed when players read the instructions and how to play.
 * It provides options to return to the main menu.
 */
public class HowToPlay implements Screen {
    private final MazeRunnerGame game;

    /**
     * The stage containing UI elements for the how to play menu.
     */

    private final Stage stage;

    private final Image backgroundImage;

    /**
     * Constructs a GameOverScreen object.
     * @param game is the MazeRunnerGame instance, used to access global resources and methods.
     */

    public HowToPlay(MazeRunnerGame game) {
        this.game = game;

        // Set up camera and viewport
        OrthographicCamera camera = new OrthographicCamera();
        ScreenViewport viewport = new ScreenViewport(camera);

        // Configure units per pixel based on screen size and default UI size
        float unitsPerPixel = 1f / Math.max(Gdx.graphics.getWidth() / game.getUiDefaultWidth(), Gdx.graphics.getHeight() / game.getUiDefaultHeight());
        viewport.setUnitsPerPixel(unitsPerPixel);

        // Initialize stage with viewport and sprite batch
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements
        stage.getViewport().apply();

        // Set background image
        backgroundImage = new Image(new Texture(Gdx.files.internal("How_To_Play.png")));
        backgroundImage.setBounds(0, 0, stage.getWidth(), stage.getHeight());
        backgroundImage.setScaling(Scaling.fill);
        stage.addActor(backgroundImage);

    }

    /**
     * Renders the how to play screen.
     * @param delta The time in seconds since the last render.
     */

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // Update the stage
        stage.draw(); // Draw the stage
        handleInput(delta);
    }

    public void handleInput(float delta){
        game.handleInput(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setGameState(GameState.PAUSED);
            System.out.println("game paused. going to menu");
            game.goToMenu();
        }
    }

    /**
     * Resizes the stage viewport when the screen is resized.
     * @param width new width of the screen.
     * @param height new heıght of the screen.
     */

    @Override
    public void resize(int width, int height) {
        float unitsPerPixel = 1f / Math.max(width/ game.getUiDefaultWidth(), height / game.getUiDefaultWidth());
        ((ScreenViewport)stage.getViewport()).setUnitsPerPixel(unitsPerPixel);
        stage.getViewport().update(width, height, true); // Update the stage viewport on resize
        backgroundImage.setSize(stage.getWidth(), stage.getHeight());
    }

    /**
     * Disposes of resources when the screen is disposed.
     */
    @Override
    public void dispose() {
        // Dispose of the stage when screen is disposed
        stage.dispose();
    }

    /**
     * Called when the screen becomes the current screen.
     */
    @Override
    public void show() {
        // Set the input processor so the stage can receive input events
        Gdx.input.setInputProcessor(stage);
    }

    // The following methods are part of the Screen interface but are not used in this screen.
    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
}
