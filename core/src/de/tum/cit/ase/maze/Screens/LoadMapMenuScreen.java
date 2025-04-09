package de.tum.cit.ase.maze.Screens;

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
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.ase.maze.MazeRunnerGame;


/**
 * LoadMapMenuScreen class is responsible for displaying the menu screen
 * to load different maps and levels within the game.
 *
 * It implements the LibGDX Screen class and sets up the UI components for the menu.
 * Users can choose to load levels (1 to 5) and load a custom map or return to the main menu.
 *
 */
public class LoadMapMenuScreen implements Screen {

    private final MazeRunnerGame game;

    /**
     * The stage containing UI elements for the load map menu.
     */

    private final Stage stage;

    private final Image backgroundImage;

    /**
     * Constructs a LoadMapMenuScreen object.
     * @param game game class, used to access global resources and methods.
     */
    
    public LoadMapMenuScreen(MazeRunnerGame game) {
        this.game = game;
        OrthographicCamera camera = new OrthographicCamera();
        ScreenViewport viewport = new ScreenViewport(camera);

        float unitsPerPixel = 1f / Math.max(Gdx.graphics.getWidth() / game.getUiDefaultWidth(), Gdx.graphics.getHeight() / game.getUiDefaultHeight());
        viewport.setUnitsPerPixel(unitsPerPixel);
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements
        stage.getViewport().apply();

        backgroundImage = new Image(new Texture(Gdx.files.internal("mapmenu_4.jpeg"))); // Create a background image and load a texture
        backgroundImage.setBounds(0, 0, stage.getWidth(), stage.getHeight());
        backgroundImage.setScaling(Scaling.fill);
        stage.addActor(backgroundImage); // Add the backgroundImage to the stage

        Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage

        // Add a label as a title
        table.add(new Label("Please Choose the Map", game.getSkin(), "title")).padBottom(50).row(); //It leaves additional space (padding)(50 units) at the bottom of an element in the table structure.
        table.add(new Label("Load the Game!", game.getSkin(), "title")).padBottom(50).row();

        // Create and add a button to go to the game screen

        TextButton LoadLevel1 = new TextButton("Level 1",game.getSkin()); // new Load Level 1 Game Button - Burak
        TextButton LoadLevel2 = new TextButton("Level 2",game.getSkin()); // new Load Level 2 Game Button - Burak
        TextButton LoadLevel3 = new TextButton("Level 3",game.getSkin()); // new Load Level 3 Game Button - Burak
        TextButton LoadLevel4 = new TextButton("Level 4",game.getSkin()); // new Load Level 4 Game Button - Burak
        TextButton LoadLevel5 = new TextButton("Level 5",game.getSkin()); // new Load Level 5 Game Button - Burak
        TextButton LoadMapButton = new TextButton("Load Map",game.getSkin()); //new Load Map Button - Burak
        TextButton BacktoMainMenu = new TextButton("Go To Main Menu", game.getSkin()); //return to main menu

        table.add(LoadLevel1).width(350).row(); // Load Map Level 1 button width
        table.add(LoadLevel2).width(350).row(); // Load Map Level 2 button width
        table.add(LoadLevel3).width(350).row(); // Load Map Level 3 button width
        table.add(LoadLevel4).width(350).row(); // Load Map Level 4 button width
        table.add(LoadLevel5).width(350).row(); // Load Map Level 5 button width
        table.add(LoadMapButton).width(350).row(); // load map button width
        table.add(BacktoMainMenu).width(350).row(); //go to menu screen
        BacktoMainMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu(); // Change to the game screen when button is pressed
            }
        });
        LoadMapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.loadCustomMap(); // Change to the game screen when button is pressed

            }
        });
        LoadLevel1.addListener(new ChangeListener() { // for LoadLevel1 button Burak - 27.12 - 23.20
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {game.loadLevel(1);}
        });
        LoadLevel2.addListener(new ChangeListener() { // for LoadLevel1 button Burak - 27.12 - 23.20
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {game.loadLevel(2);}
        });
        LoadLevel3.addListener(new ChangeListener() { // for LoadLevel1 button Burak - 27.12 - 23.20
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {game.loadLevel(3);}
        });
        LoadLevel4.addListener(new ChangeListener() { // for LoadLevel1 button Burak - 27.12 - 23.20
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {game.loadLevel(4);}
        });
        LoadLevel5.addListener(new ChangeListener() { // for LoadLevel1 button Burak - 27.12 - 23.20
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {game.loadLevel(5);}
        });
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
