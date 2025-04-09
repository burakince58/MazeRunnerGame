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
import de.tum.cit.ase.maze.MazeRunnerGame;

/**
 * The MenuScreen class is responsible for displaying the main menu of the game.
 * It extends the LibGDX Screen class and sets up the UI components for the menu.
 */
public class MenuScreen implements Screen {

    private final MazeRunnerGame game;

    /**
     * The stage containing UI elements for the load map menu.
     */
    private final Stage stage;

    private final Image backgroundImage;

    /**
     * Constructor for MenuScreen. Sets up the camera, viewport, stage, and UI elements.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public MenuScreen(MazeRunnerGame game) {
        this.game = game;

        OrthographicCamera camera = new OrthographicCamera();
        ScreenViewport viewport = new ScreenViewport(camera);

        float unitsPerPixel = 1f / Math.max(Gdx.graphics.getWidth() / game.getUiDefaultWidth(), Gdx.graphics.getHeight() / game.getUiDefaultHeight());
        viewport.setUnitsPerPixel(unitsPerPixel);
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements
        stage.getViewport().apply();

        backgroundImage = new Image(new Texture(Gdx.files.internal("menu_5.jpeg"))); // Create a background image and load a texture
        backgroundImage.setBounds(0, 0, stage.getWidth(), stage.getHeight());
        backgroundImage.setScaling(Scaling.fill);
        stage.addActor(backgroundImage); // Add the backgroundImage to the stage

        Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage

        // Add a label as a title
        table.add(new Label("Welcome to", game.getSkin(), "title")).row();
        table.add(new Label("MazeRunner", game.getSkin(), "title")).padBottom(220).row();

        // Create and add a button to go to the game screen

        TextButton LoadMapButton = new TextButton("Load Map",game.getSkin()); //new Load Map Button - Burak
        TextButton HowToPlay = new TextButton("How to Play",game.getSkin()); //new How to Play Button - Burak
        TextButton Exit = new TextButton("Exit",game.getSkin()); // new Exit Game Button - Burak

        /**
         * Only we have loaded map
         * If not victory screen and game over screen
         * */
        if(game.getMazeMap()!=null && !game.getMazeMap().getPlayer().isVictory() && game.getMazeMap().getPlayer().getHealth()>0){
            TextButton goToGameButton = new TextButton("Go To Game", game.getSkin());
            table.add(goToGameButton).width(350).row();
            goToGameButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.goToGame(); // Change to the game screen when button is pressed
                }
            });
        }

        table.add(LoadMapButton).width(350).row(); // load map button width
        table.add(HowToPlay).width(350).row(); // load how to play menu screen
        table.add(Exit).width(350).row(); // exit game button width

        table.padBottom(180);

        LoadMapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                 game.goToLoadMap(); // Change to the Load Map screen when button is pressed
            }
        });

        HowToPlay.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToHowToPlay(); // Change to the Load Map screen when button is pressed
            }
        });

        Exit.addListener(new ChangeListener() { // for Exit button Burak - 27.12 - 23.20
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                Gdx.app.exit();
            }
        });
    }
    /**
     * Renders the menu screen.
     *
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
     *
     * @param width  The new width of the screen.
     * @param height The new height of the screen.
     */
    @Override
    public void resize(int width, int height) {
        float unitsPerPixel = 1f / Math.max(width/ game.getUiDefaultWidth(), height / game.getUiDefaultWidth());
        ((ScreenViewport)stage.getViewport()).setUnitsPerPixel(unitsPerPixel);
        stage.getViewport().update(width, height, true); // Update the stage viewport on resize
        backgroundImage.setSize(stage.getWidth(), stage.getHeight());
    }

    @Override
    public void dispose() {
        // Dispose of the stage when screen is disposed
        stage.dispose();
    }

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
