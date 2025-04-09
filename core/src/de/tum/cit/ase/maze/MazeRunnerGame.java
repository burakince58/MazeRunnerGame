package de.tum.cit.ase.maze;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.ase.maze.Screens.*;
import de.tum.cit.ase.maze.enums.GameState;
import games.spooky.gdx.nativefilechooser.NativeFileChooser; //File choosing related library
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback; //File choosing related library
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration; //File choosing related library
import games.spooky.gdx.nativefilechooser.NativeFileChooserIntent; //File choosing related library
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * The MazeRunnerGame class represents the core of the Maze Runner game.
 * It manages the screens and global resources like SpriteBatch and Skin.
 * This class extends the LibGDX Game class and serves as the entry point for the game.
 * It handles the creation, updating, and disposal of game screens and resources.
 */
public class MazeRunnerGame extends Game {
    //TODO clean unused attributes at the end add more documentation?

    //Using one screen attribute and changing it over different screens
    private Screen currentScreen;
    // these are the default dimensions of the UI that we use for scaling
    private final float uiDefaultWidth = 1920f;
    private final float uiDefaultHeight = 1080f;
    private MazeMap mazeMap;

    // Sprite Batch for rendering
    private SpriteBatch spriteBatch;

    // UI Skin
    private Skin skin;
    private GameState gameState = GameState.MENU;
    private Animation<TextureRegion> heartsAnimation; //Deniz 30.12
    private TextureRegion HUD; //Deniz 06.01
    private TextureRegion hearts; //Deniz 06.01
    private NativeFileChooser fileChooser; //Burak Test

    private Music backgroundMusic;
    private float backgroundMusicVolume = 0.50f;
    private float mazeMapMusicVolume = 0.10f;
    private boolean backgroundMusicMuted = false;

    private int currentLevel;

    private boolean[] levelPlayed = new boolean[6];

    /**
     * Constructor for MazeRunnerGame.
     *
     * @param fileChooser The file chooser for the game, typically used in desktop environment.
     */
    public MazeRunnerGame(NativeFileChooser fileChooser) {
        super();
        this.fileChooser = fileChooser; //Burak Test 27.12 18.00
        this.backgroundMusic = null;
        Arrays.fill(levelPlayed, false);
        levelPlayed[0] = true;
    }

    /**
     * Listen if M key is pressed.
     * @param delta The time in seconds since the last render.
     */
    public void handleInput(float delta){
        // for global inputs. Should be called in all subclasses handleInput/render methods
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            muteBackgroundMusic();
        }
    }

    /**
     * Loads the level of the maze map
     * @param level level of the maze map
     */
    public void loadLevel(int level){
        try {
            FileHandle fileHandle = Gdx.files.internal(String.format("maps/level-%d.properties", level));
            // Do something with fileHandle
            String fileContent = fileHandle.readString();
            String[] lines = fileContent.split("\\r?\\n");
            currentLevel = level;

            createMazeMap(lines, level);
            goToGame();

        }catch (Exception exception) {
            System.err.println("Error picking maze file: " + exception.getMessage());
            Gdx.app.error("File Loading Error", "An error occurred while loading the file: " + exception.getMessage());
        }
    }

    /**
     *
     * Starts or changes the background music based on the current game state and level.
     * If the game is in a running or paused state, it loads level-specific background music.
     * If in the menu state, it loads menu background music. In victory or game-over states, it loads
     * corresponding background music. The method also handles muting and setting volume levels.
     *
     */
    public void startBackgroundMusic() {
        stopBackgroundMusic(); // Stop any existing background music

        // Check the current game state to determine the appropriate background music
        if (gameState == GameState.RUNNING || gameState == GameState.PAUSED) {
            // Load game background music based on the current level
            if (currentLevel == 1) {
                backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("maze_background_1.ogg"));
            }else if (currentLevel == 2) {
                backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("maze_background_2.wav"));
            }else if (currentLevel == 3) {
                backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("maze_background_custom.ogg"));
            }else if (currentLevel == 4) {
                backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("maze_background_4.mp3"));
            }else if(currentLevel == 5) {
                backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("maze_background_5.mp3"));
            }else{
                backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("maze_background_custom.ogg"));
            }
            backgroundMusic.setLooping(true);
        }else if(gameState == GameState.MENU){  // Load menu background music
            // in menu
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("menu_background.mp3")); // Replace with your menu background music file
            backgroundMusic.setLooping(true);
        }else if(gameState == GameState.VICTORY){
            // Load victory background music
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("victory_background.mp3"));
            backgroundMusic.setLooping(false);
        }else if(gameState == GameState.GAME_OVER){
            // Load game over background music
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("game_over_bad_chest.wav"));
            backgroundMusic.setLooping(false);
        }

        //  Set volume and play background music
        if (backgroundMusic != null) {
            if(backgroundMusicMuted){
                backgroundMusic.setVolume(0);
            }else{
                if (gameState == GameState.RUNNING || gameState == GameState.PAUSED) {
                    backgroundMusic.setVolume(mazeMapMusicVolume);
                }else{
                    backgroundMusic.setVolume(backgroundMusicVolume);
                }
            }
            backgroundMusic.play();
        }
    }

    /**
     * Stops the currently playing background music and disposes of its resources.
     */
    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
            backgroundMusic = null;
        }
    }

    /**
     * Mutes or unmutes the background music based on its current state.
     * If the music is currently muted, it unmutes it; otherwise, it mutes it.
     * Also, prints a message indicating the current mute state.
     */
    public void muteBackgroundMusic() {
        if (backgroundMusic != null) {
            if(backgroundMusicMuted){
                // Unmute the background music
                if (gameState == GameState.RUNNING || gameState == GameState.PAUSED) {
                    backgroundMusic.setVolume(mazeMapMusicVolume);
                }else{
                    backgroundMusic.setVolume(backgroundMusicVolume);
                }
                backgroundMusicMuted = false;
                System.out.println("music unmuted");
            }else {
                // Mute the background music
                backgroundMusic.setVolume(0);
                backgroundMusicMuted = true;
                System.out.println("music muted");
            }
        }
    }

    /**
     * Loads a custom map
     */
    public void loadCustomMap(){
        var fileChooserConfig = new NativeFileChooserConfiguration();
        fileChooserConfig.title = "Pick a maze file"; // Title of the window that will be opened
        fileChooserConfig.intent = NativeFileChooserIntent.OPEN; // We want to open a file
        fileChooserConfig.nameFilter = (file, name) -> name.endsWith("properties"); // Only accept .properties files
        fileChooserConfig.directory = Gdx.files.absolute(System.getProperty("user.home")); // Open at the user's home directory
        fileChooser.chooseFile(fileChooserConfig, new NativeFileChooserCallback() {
            @Override
            public void onFileChosen(FileHandle fileHandle) {
                // Do something with fileHandle
                String fileContent = fileHandle.readString();
                String[] lines = fileContent.split("\\r?\\n");
                createMazeMap(lines, 100);
                goToGame();
            }

            @Override
            public void onCancellation() {
                // User closed the window, don't need to do anything
            }

            @Override
            public void onError(Exception exception) {
                System.err.println("Error picking maze file: " + exception.getMessage());
            }
        });
    }

    /**
     * Creates maze map
     */
    public void createMazeMap(String[] lines, int level){

        Map<Point, Integer> gameObjects = new HashMap<>();
        int height = 0; //understand height of maze map
        int width = 0; //understand width of maze map

        for (String line : lines) {
            String[] parts = line.split("="); // Split the line based on the '=' character
            if (parts.length == 2) {
                if(!parts[0].equals("Height")&& !parts[0].equals("Width")){
                    String[] coordinates = parts[0].split(","); // Split the coordinates by comma
                    int x = Integer.parseInt(coordinates[0]);
                    int y = Integer.parseInt(coordinates[1]);
                    int value = Integer.parseInt(parts[1].trim());
                    gameObjects.put(new Point(x, y), value);
                    if (x > width) {width = x;}
                    if (y > height) {height = y;}
                }
                else{
                    System.out.println("To Be Decided :)");
                }
            }
        }

        this.mazeMap = new MazeMap(height+1, width+1, gameObjects, level);

    }

    /**
     * Called when the game is created. Initializes the SpriteBatch and Skin.
     */
    @Override
    public void create() {
        spriteBatch = new SpriteBatch(); // Create SpriteBatch

        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json")); // Load UI skin
        Texture basictiles = new Texture(Gdx.files.internal("basictiles.png")); // Load basictiles from assets
        Texture objects = new Texture(Gdx.files.internal("objects.png")); //Load objects from assets

        this.hearts = new TextureRegion(objects, 16*1, 16*5, 16, 16 ); //create heart texture
        this.HUD = new TextureRegion(basictiles, 16*6, 16*2, 16, 16 ); // create HUD texture

        this.loadLivesAnimation();

        goToMenu(); // Navigate to the menu screen
    }

     /**
     * Switches to the map choose menu screen.
     */
    public void goToLoadMap() {
        // if currentScreen isn't null, dispose of previous screen
        if (currentScreen != null) {
            currentScreen.dispose();
        }
        this.gameState = GameState.MENU;
        currentScreen = new LoadMapMenuScreen(this);
        this.setScreen(currentScreen);
    }

    /**
     * Switches to the HowToPlay screen from menu screen.
     */
    public void goToHowToPlay() {
        // if currentScreen isn't null, dispose of previous screen
        if (currentScreen != null) {
            currentScreen.dispose();
        }
        this.gameState = GameState.MENU;
        currentScreen = new HowToPlay(this);
        this.setScreen(currentScreen);
    }

    /**
     * Switches to the victory screen.
     */
    public void goToVictoryScreen() {
        // if currentScreen isn't null, dispose of previous screen
        if (currentScreen != null) {
            currentScreen.dispose();
        }
        this.gameState = GameState.VICTORY;
        currentScreen = new VictoryScreen(this);
        this.setScreen(currentScreen);
        startBackgroundMusic();
    }

    public void goToGameOverScreen(){
        // if currentScreen isn't null, dispose of previous screen
        if (currentScreen != null) {
            currentScreen.dispose();
        }
        this.gameState = GameState.GAME_OVER;
        currentScreen = new GameOverScreen(this);
        this.setScreen(currentScreen);
        startBackgroundMusic();
    }

    /**
     * Switches to the menu screen.
     */
    public void goToMenu() {
        // if currentScreen isn't null, dispose of previous screen
        boolean wasLoadMenu = currentScreen instanceof LoadMapMenuScreen;
        if (currentScreen != null) {
            currentScreen.dispose();
        }
        this.gameState = GameState.MENU;
        currentScreen = new MenuScreen(this);
        this.setScreen(currentScreen);
        if(!wasLoadMenu){
            startBackgroundMusic();
        }
    }

    /**
     * Switches to the game screen.
     */
    public void goToGame(){
        // if currentScreen isn't null, dispose of previous screen
        if (currentScreen != null) {
            currentScreen.dispose();
        }
        this.gameState = GameState.RUNNING;
        currentScreen = new GameScreen(this, mazeMap);
        this.setScreen(currentScreen);
        startBackgroundMusic();
    }

    /**
     * Loads heart animation for lives
     */
    public void loadLivesAnimation(){ // Deniz 30.12
        Texture walkSheet = new Texture(Gdx.files.internal("objects.png"));
        Array<TextureRegion> hearts = new Array<>(TextureRegion.class); // Deniz 30.12

        int frameWidth = 16;
        int frameHeight = 16;
        int animationFrames = 4;

        //Hearts animation:
        for (int col = 0; col < animationFrames; col++) { // Deniz 30.12
            hearts.add(new TextureRegion(walkSheet, col * frameWidth, 48, frameWidth, frameHeight));
        }
        heartsAnimation = new Animation<>(0.1f, hearts);
    }

    /**
     * Cleans up resources when the game is disposed.
     */
    @Override
    public void dispose() {
        getScreen().hide(); // Hide the current screen
        getScreen().dispose(); // Dispose the current screen
        spriteBatch.dispose(); // Dispose the spriteBatch
        backgroundMusic.dispose(); //Dispose the backgroundMusic
        skin.dispose(); // Dispose the skin
    }

    // Getter methods //TODO Clean at the end
    public Skin getSkin() {
        return skin;
    }


    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }


    public Animation<TextureRegion> getHeartsAnimation() {
        return heartsAnimation;
    }


    public TextureRegion getHUD() {
        return HUD;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
    public MazeMap getMazeMap() {
        return mazeMap;
    }

    public float getUiDefaultWidth() {
        return uiDefaultWidth;
    }

    public float getUiDefaultHeight() {
        return uiDefaultHeight;
    }

    public boolean[] getLevelPlayed() {
        return levelPlayed;
    }

    public void setLevelPlayed(boolean[] levelPlayed) {
        this.levelPlayed = levelPlayed;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }
    public boolean isBackgroundMusicMuted() {
        return backgroundMusicMuted;
    }
}
