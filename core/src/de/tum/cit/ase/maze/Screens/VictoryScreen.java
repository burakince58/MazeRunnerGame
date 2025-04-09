package de.tum.cit.ase.maze.Screens;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.ase.maze.MazeRunnerGame;


public class VictoryScreen implements Screen {

    private final MazeRunnerGame game;

    /**
     * The stage containing UI elements for the victory screen.
     */

    private final Stage stage;
    private final Image backgroundImage;
    private int nextNumber;

    /**
     * Constructs a Victory object.
     * @param game game class, used to access global resources and methods.
     */
    public VictoryScreen(MazeRunnerGame game) {
        this.game = game;
        OrthographicCamera camera = new OrthographicCamera();
        ScreenViewport viewport = new ScreenViewport(camera);

        float unitsPerPixel = 1f / Math.max(Gdx.graphics.getWidth() / game.getUiDefaultWidth(), Gdx.graphics.getHeight() / game.getUiDefaultHeight());
        viewport.setUnitsPerPixel(unitsPerPixel);
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements
        stage.getViewport().apply();

        backgroundImage = new Image(new Texture(Gdx.files.internal("victory_5.jpg"))); // Create a background image and load a texture
        backgroundImage.setBounds(0, 0, stage.getWidth(), stage.getHeight());
        backgroundImage.setScaling(Scaling.fill);
        stage.addActor(backgroundImage); // Add the backgroundImage to the stage

        Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage

        // Add a label as a title
        table.add(new Label("VICTORY", game.getSkin(), "title")).padBottom(80).row(); //It leaves additional space (padding)(30 units) at the bottom of an element in the table structure.

        // Create and add a button to go to the game screen
        TextButton backToMainMenu = new TextButton("Go To Main Menu", game.getSkin()); //return to main menu
        TextButton nextLevel = new TextButton("Next Level", game.getSkin()); //Play the next level

        table.add(backToMainMenu).width(350).row(); //go to menu screen


        backToMainMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu(); // Change to the game screen when button is pressed
            }
        });

        boolean[] temp = game.getLevelPlayed();
        int nextLevelNumber = 6;
        for (int i = 0; i < temp.length; i++) {
            if (!temp[i]) {
                nextLevelNumber = Math.min(nextLevelNumber, i);
            }
        }
        nextNumber=nextLevelNumber;

        if (nextNumber<6) {
            table.add(nextLevel).width(350).row(); //go to next level
            nextLevel.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.loadLevel(nextNumber); // Change to the game screen when button is pressed
                }
            });
        }

    }

    /**
     * Renders the load map menu screen.
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