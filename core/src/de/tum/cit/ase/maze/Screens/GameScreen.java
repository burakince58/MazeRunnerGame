package de.tum.cit.ase.maze.Screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.ase.maze.GameObject;
import de.tum.cit.ase.maze.MazeMap;
import de.tum.cit.ase.maze.MazeRunnerGame;
import de.tum.cit.ase.maze.Position;
import de.tum.cit.ase.maze.characters.Player;
import de.tum.cit.ase.maze.enums.Direction;
import de.tum.cit.ase.maze.enums.GameState;
import de.tum.cit.ase.maze.items.Key;
import de.tum.cit.ase.maze.items.Lighting;
import de.tum.cit.ase.maze.tiles.Tile;

import java.awt.*;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
 */
public class GameScreen implements Screen {

    private static final TextureRegion unmuted = new TextureRegion(new Texture(Gdx.files.internal("audioOn.png")), 0, 0, 50, 50);
    private static final TextureRegion muted = new TextureRegion(new Texture(Gdx.files.internal("audioOff.png")), 0, 0, 50, 50);

    private final MazeRunnerGame game;

    private float kameraX = 0.0f; // should be never on screen since
    private float kameraY = 0.0f;

    private final ScreenViewport gameViewport;
    private final ScreenViewport hudViewport;

    /**
     * tracks if a direction key (UP, DOWN, LEFT, RIGHT) is pressed
     */
    private LinkedHashSet<Direction> directionKeyPressed;
    private float sinusInput = 0f;

    private boolean showBoundingBoxes = false;

    private MazeMap mazeMap;

    private boolean lighting = true;

    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public GameScreen(MazeRunnerGame game, MazeMap mazeMap) {
        this.game = game;
        this.mazeMap = mazeMap;

        // viewport tutorial: https://github.com/raeleus/viewports-sample-project#libgdx-viewports
        OrthographicCamera cam = new OrthographicCamera();
        gameViewport = new ScreenViewport(cam);
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();

        // Create and configure the camera for the game view
        // scale the game so that we can fit at least 30 tiles in width or height (whichever is larger)
        float unitsPerPixel = 1f / Math.max(screenWidth / 30f, screenHeight / 30f);
        gameViewport.setUnitsPerPixel(unitsPerPixel);
        gameViewport.update(screenWidth, screenHeight);
        Player player = mazeMap.getPlayer();
        Position playerPos = player.getPosition();
        float camX = playerPos.x;
        float camY = playerPos.y;
        if(gameViewport.getWorldWidth() >= mazeMap.getWidth()){
            camX = mazeMap.getWidth() / 2f;
        }else{
            // maze is bigger than what we can put on screen
            // center around the player first
            float halfWorldWidth = gameViewport.getWorldWidth() / 2f;
            if(playerPos.x - halfWorldWidth < 0){
                camX = 0 + halfWorldWidth;
            }else if(playerPos.x + halfWorldWidth > mazeMap.getWidth()){
                camX = mazeMap.getWidth() - halfWorldWidth;
            }
        }

        if(gameViewport.getWorldHeight() >= mazeMap.getHeight()){
            camY = mazeMap.getHeight() / 2f;
        }else{
            // maze is bigger than what we can put on screen
            // center around the player first
            float halfWorldHeight = gameViewport.getWorldHeight() / 2f;
            // if that's out of bounds, adjust the camera to the edge of the maze
            if(playerPos.y - halfWorldHeight < 0){
                camY = 0 + halfWorldHeight;
            }else if(playerPos.y + halfWorldHeight > mazeMap.getWidth()){
                camY = mazeMap.getWidth() - halfWorldHeight;
            }
        }

        gameViewport.getCamera().position.set(camX, camY, 0f);
        System.out.println(String.format("Initial camera position (%f,%f)", camX, camY));
        gameViewport.getCamera().update();

        player.getLightHandler().setCombinedMatrix((OrthographicCamera)gameViewport.getCamera());

        hudViewport = new ScreenViewport();
        hudViewport.setUnitsPerPixel(unitsPerPixel);
        hudViewport.update(screenWidth, screenHeight, true);

        directionKeyPressed = new LinkedHashSet<>();

    }

    // Screen interface methods with necessary functionality
    @Override
    public void render(float delta) {
        updateObjects(delta);
        Player player = mazeMap.getPlayer();
        for(var gameObject : mazeMap.getCollisions(player.getBoundingBox(), player)){
            player.collision(gameObject);
        }
        handleInput(delta);
        ScreenUtils.clear(Color.BLACK); // Clear the screen

        // Move text in a circular path to have an example of a moving object
        sinusInput += delta;

        // Set up and begin drawing with the sprite batch
        gameViewport.apply();
        game.getSpriteBatch().setProjectionMatrix(gameViewport.getCamera().combined);
        game.getSpriteBatch().begin(); // Important to call this before drawing anything

        // draw stuff in order. first tiles, then staticObjects then characters
        for(int y = 0; y < mazeMap.getHeight(); y++){
            for(int x = 0; x < mazeMap.getWidth(); x++){
                Tile tile = mazeMap.getTile(x, y);
                Position position = tile.getPosition();
                game.getSpriteBatch().draw(tile.getTexture(), position.x, position.y, tile.getWidth(), tile.getHeight());
            }
        }

        // staticObjects
        for(var gameObject : mazeMap.getStaticObjects().values()){
            Position position = gameObject.getPosition();
            game.getSpriteBatch().draw(gameObject.getTexture(), position.x, position.y, gameObject.getWidth(), gameObject.getHeight());
        }

        // draw dash animation when pressed
        if(player.getDashAnimation() != null){
            player.getDashAnimation().draw(game.getSpriteBatch());
        }

        if(player.getBleedingAnimation() != null){
            player.getBleedingAnimation().draw(game.getSpriteBatch());
        }

        // Draw Player
        game.getSpriteBatch().draw(player.getTexture(), player.getPosition().x+player.getDrawXOffset(), player.getPosition().y+player.getDrawYOffset(), player.getWidth(), player.getHeight());

        // dynamicObjects
        for(var gameObject : mazeMap.getDynamicObjects()){
            Position position = gameObject.getPosition();
            game.getSpriteBatch().draw(gameObject.getTexture(), position.x, position.y, gameObject.getWidth(), gameObject.getHeight());
        }

        // Show bounding boxes when B-Key is pressed
        if(showBoundingBoxes){
            debugBoundingBoxes();
        }
        game.getSpriteBatch().end(); // Important to call this after drawing everything

        for(var gameObject : mazeMap.getDynamicObjects()){
            gameObject.takeAction(delta);
        }

        if(player.getHealth() <= 0.0f){
            game.goToGameOverScreen();
        }else if(player.isVictory()){
            boolean[] temp;
            temp = game.getLevelPlayed();
            temp[game.getCurrentLevel()] = true;
            //game.setLevelPlayed(temp);
            game.goToVictoryScreen();
        }

        if(player.isLightCollected()){
            lighting = false;
        }

        if(lighting) {
            player.getLightHandler().setCombinedMatrix((OrthographicCamera) gameViewport.getCamera());
            player.getLightHandler().updateAndRender();
        }

        // creating new batch for HUD // Deniz 07.01
        hudViewport.apply();
        game.getSpriteBatch().setProjectionMatrix(hudViewport.getCamera().combined);
        game.getSpriteBatch().begin();

        drawHUD(player.getHealth());
        game.getSpriteBatch().end();

        destroyObjects();
    }

    /**
     * update all GameObjects based on the time elapsed
     *
     * @param delta elapsed time
     */
    private void updateObjects(float delta){
        for(int y = 0; y < mazeMap.getHeight(); y++){
            for(int x = 0; x < mazeMap.getWidth(); x++){
                Tile tile = mazeMap.getTile(x, y);
                tile.update(delta);
            }
        }
        // staticObjects
        for(var gameObject : mazeMap.getStaticObjects().values()){
            gameObject.update(delta);
        }

        // characters
        for(var gameObject : mazeMap.getDynamicObjects()){
            gameObject.update(delta);
        }

        mazeMap.getPlayer().update(delta);

    }

    /**
     * remove objects from mazeMap that got destroyed this frame
     */
    private void destroyObjects(){

        List<Point> staticObjectsToRemove = new LinkedList<>();
        // staticObjects
        for(var entry : mazeMap.getStaticObjects().entrySet()){
            if(entry.getValue().isDestroyed()){
                staticObjectsToRemove.add(entry.getKey());
            }
        }

        for(var point : staticObjectsToRemove){
            mazeMap.getStaticObjects().remove(point);
        }
        List<GameObject> dynamicObjectsToRemove = new LinkedList<>();

        // characters
        for(var gameObject : mazeMap.getDynamicObjects()){
            if(gameObject.isDestroyed()){
                dynamicObjectsToRemove.add(gameObject);
            }
        }

        for(var gameObject : dynamicObjectsToRemove){
            mazeMap.getDynamicObjects().remove(gameObject);
        }
    }

    private void handleInput(float delta) {
        // Check for escape key press to go back to the menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setGameState(GameState.PAUSED);
            System.out.println("game paused. going to menu");
            game.goToMenu();
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            mazeMap.getPlayer().dash();
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.A)){
            mazeMap.getPlayer().attack();
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.L)){
            lighting = !lighting;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            showBoundingBoxes = !showBoundingBoxes;
        }

        // keep track of which keys are pressed in which order
        // so we can press multiple keys and the newer one is
        // in effect until it's released or an even newer one pressed
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            directionKeyPressed.add(Direction.UP);
        } else {
            directionKeyPressed.remove(Direction.UP);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            directionKeyPressed.add(Direction.DOWN);
        } else {
            directionKeyPressed.remove(Direction.DOWN);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            directionKeyPressed.add(Direction.RIGHT);
        } else {
            directionKeyPressed.remove(Direction.RIGHT);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            directionKeyPressed.add(Direction.LEFT);
        } else {
            directionKeyPressed.remove(Direction.LEFT);
        }

        // move in the direction of the last pressed key
        if (!directionKeyPressed.isEmpty()) {
            var it = directionKeyPressed.iterator();
            Direction direction = it.next();
            while (it.hasNext()) {
                direction = it.next();
            }
            mazeMap.getPlayer().move(direction, delta);
            adjustCamera();
        }

        //Let keep in the class but in commented
        //Uncomment to get manual camera movement and zoom
//        OrthographicCamera cam = (OrthographicCamera) gameViewport.getCamera();
//        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
//            cam.zoom += 0.01f;
//            cam.update();
//            System.out.println(cam.zoom);
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
//            cam.zoom -= 0.01f;
//            cam.update();
//            System.out.println(cam.zoom);
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
//            cam.translate(-0.5f, 0, 0);
//            System.out.println(cam.position);
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
//            cam.translate(0.5f, 0, 0);
//            System.out.println(cam.position);
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
//            cam.translate(0, -0.5f, 0);
//            System.out.println(cam.position);
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
//            cam.translate(0, 0.5f, 0);
//            System.out.println(cam.position);
//        }

    //    if (Gdx.input.isKeyJustPressed(Input.Keys.V)){
    //        //Victory Screen Method
    //        game.goToVictoryScreen();
    //    }
    //
    //    if (Gdx.input.isKeyJustPressed(Input.Keys.G)){
    //        //Game Over Screen
    //        game.goToGameOverScreen();
    //    }
    //
    //    if(Gdx.input.isKeyJustPressed(Input.Keys.H)){
    //        mazeMap.getPlayer().setHealth(5);
    //    }
    //

        game.handleInput(delta);
    }

    /**
     * Continuously adjust camera position with respect to player position and screen szie
     */
    private void adjustCamera(){
        OrthographicCamera cam = (OrthographicCamera) gameViewport.getCamera();
        //The camera's viewing angle should show the map and the player,
        // it will always be at a point in the middle, not showing the black area outside the map.
        // Otherwise, it won't move.
        kameraX = gameViewport.getCamera().position.x;
        kameraY = gameViewport.getCamera().position.y;
        float playerY = mazeMap.getPlayer().getPosition().y;
        float playerX = mazeMap.getPlayer().getPosition().x;
        float halfWorldHeight = gameViewport.getWorldHeight() / 2f;
        float halfWorldWidth = gameViewport.getWorldWidth() / 2f;

        // if the maze is bigger than the screen adjust camera when player moves to the edge
        if (gameViewport.getWorldWidth() <= mazeMap.getWidth()) {
            // center camera on player if player comes within 3 tiles of left or right edge (player character has 1 width and position is bottom left)
            if (playerX <= kameraX - halfWorldWidth + 1.6f || playerX >= kameraX + halfWorldWidth - 2.6f) {
                kameraX = playerX;
            }

            if (kameraX - halfWorldWidth < 0) { // if we moved the camera to black space, adjust it
                kameraX = 0 + halfWorldWidth;
            } else if (kameraX + halfWorldWidth > mazeMap.getWidth()) {
                kameraX = mazeMap.getWidth() - halfWorldWidth;
            }
        }

        if (gameViewport.getWorldHeight() <= mazeMap.getHeight()) {
            // center camera on player if player comes within 3 tiles of top or bottom edge (player character has 1 width and position is bottom left)
            if (playerY <= kameraY - halfWorldHeight + 1.5f || playerY >= kameraY + halfWorldHeight - 2.9f) {
                kameraY = playerY;
            }

            if(kameraY - halfWorldHeight < 0){ // if we moved the camera to black space, adjust it
                kameraY = 0 + halfWorldHeight;
            }else if(kameraY + halfWorldHeight > mazeMap.getHeight()){
                kameraY = mazeMap.getHeight() - halfWorldHeight;
            }
        }

        // adjust position if any spot changed
        if (kameraX != gameViewport.getCamera().position.x || kameraY != gameViewport.getCamera().position.y){
            cam.position.set(kameraX, kameraY, 0);
        }
    }

    /**
     *
     * @param width
     * @param height
     */
    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height);
        float camX = 0.0f; // should be never on screen since
        boolean camXchanged = false;
        float camY = 0.0f;
        boolean camYchanged = false;
        if(gameViewport.getWorldWidth() >= mazeMap.getWidth()){
            camX = mazeMap.getWidth() / 2f;
            camXchanged = true;
        }else{

            float playerX = mazeMap.getPlayer().getPosition().x;
            float halfWorldWidth = gameViewport.getWorldWidth() / 2f;
            camX = gameViewport.getCamera().position.x;

            // if our player is no longer visible adjust the camera
            if(playerX < camX - halfWorldWidth){
                camX = playerX + halfWorldWidth;
                camXchanged = true;
            }else if(playerX > camX + halfWorldWidth){
                camX = playerX - halfWorldWidth;
                camXchanged = true;
                // if we moved the camera to black space, adjust it
            }else if(playerX - halfWorldWidth < 0){
                camX = 0 + halfWorldWidth;
                camXchanged = true;
            }else if(playerX + halfWorldWidth > mazeMap.getWidth()){
                camX = mazeMap.getWidth() - halfWorldWidth;
                camXchanged = true;
            }
        }
        if(camXchanged){
            Vector3 camPos = gameViewport.getCamera().position;
            camPos.x =camX;
            gameViewport.getCamera().position.set(camPos);
            gameViewport.getCamera().update();
        }
        if(gameViewport.getWorldHeight() >= mazeMap.getHeight()){
            camY = mazeMap.getHeight() / 2f;
            camYchanged = true;
        }else{

            float playerY = mazeMap.getPlayer().getPosition().y;
            float halfWorldHeight = gameViewport.getWorldHeight() / 2f;
            camY = gameViewport.getCamera().position.y;

            // if our player is no longer visible adjust the camera
            if(playerY < camY - halfWorldHeight){
                camY = playerY + halfWorldHeight;
                camYchanged = true;
            }else if(playerY > camY + halfWorldHeight){
                camY = playerY - halfWorldHeight;
                camYchanged = true;
                // if we moved the camera to black space, adjust it
            }else if(playerY - halfWorldHeight < 0){
                camY = 0 + halfWorldHeight;
                camYchanged = true;
            }else if(playerY + halfWorldHeight > mazeMap.getHeight()){
                camY = mazeMap.getHeight() - halfWorldHeight;
                camYchanged = true;
            }
        }
        if(camYchanged){
            Vector3 camPos = gameViewport.getCamera().position;
            camPos.y =camY;
            gameViewport.getCamera().position.set(camPos);
            gameViewport.getCamera().update();
        }

        hudViewport.update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }

    public void drawHUD(float health){
        int remainingHearts = (int)Math.floor(health);
        int maxHearts = Math.max((int)Math.ceil(mazeMap.getPlayer().getHealth()), Player.starting_health);
        float xOffSet = maxHearts/2f;
        Sprite hud = new Sprite(game.getHUD());
        hud.setAlpha(0.5f);
        hud.setSize(1f, 0.9f);

        for(int i = 0; i < maxHearts+3; i++){
            hud.setPosition(hudViewport.getWorldWidth()/2f - xOffSet + i,hudViewport.getWorldHeight() - 0.9f);
            hud.draw(game.getSpriteBatch());
        }

        for (int i = 0; i < remainingHearts; i++) {
            game.getSpriteBatch().draw(
                    game.getHeartsAnimation().getKeyFrame(sinusInput, true),
                    (hudViewport.getWorldWidth()/2f  - xOffSet) + i,
                    hudViewport.getWorldHeight() - 1,
                    1.25f,
                    1.25f
            );
        }
        if(this.mazeMap.getPlayer().isKeyCollected()){
            Sprite key = new Sprite(Key.texture);
            key.setSize(0.7f, 0.7f);
            key.setPosition(hudViewport.getWorldWidth()/2f + xOffSet + 0.2f,hudViewport.getWorldHeight() - 0.8f);
            key.draw(game.getSpriteBatch());
        }else {
            Sprite key = new Sprite(Key.texture);
            key.setColor(Color.GRAY);
            key.setAlpha(0.75f);
            key.setSize(0.7f, 0.7f);
            key.setPosition(hudViewport.getWorldWidth()/2f + xOffSet + 0.2f,hudViewport.getWorldHeight() - 0.8f);
            key.draw(game.getSpriteBatch());
        }

       if (game.isBackgroundMusicMuted()){
           Sprite audio = new Sprite(muted);
           audio.setSize(0.8f, 0.8f);
           audio.setPosition(hudViewport.getWorldWidth()/2f + xOffSet + 1f, hudViewport.getWorldHeight() - 0.8f);
           audio.draw(game.getSpriteBatch());
       }else{
           Sprite audio = new Sprite(unmuted);
           audio.setSize(0.8f, 0.8f);
           audio.setPosition(hudViewport.getWorldWidth()/2f + xOffSet + 1f, hudViewport.getWorldHeight() - 0.8f);
           audio.draw(game.getSpriteBatch());
       }

        if (lighting){
            Sprite light = new Sprite(Lighting.texture);
            light.setColor(Color.GRAY);
            light.setSize(0.8f, 0.8f);
            light.setPosition(hudViewport.getWorldWidth()/2f + xOffSet + 1.8f, hudViewport.getWorldHeight() - 0.8f);
            light.draw(game.getSpriteBatch());
        }else{
            Sprite light = new Sprite(Lighting.texture);
            light.setSize(0.8f, 0.8f);
            light.setPosition(hudViewport.getWorldWidth()/2f + xOffSet + 1.8f, hudViewport.getWorldHeight() - 0.8f);
            light.draw(game.getSpriteBatch());
        }
    }

    private Texture tilemap = new Texture(Gdx.files.internal("basictiles.png"));
    private TextureRegion redTile = new TextureRegion(tilemap, 16, 16, 16, 16);
    private void debugBoundingBoxes(){
        Sprite boundingBoxSprite;
        SpriteBatch spriteBatch = game.getSpriteBatch();
        float boundingBoxAlpha = 0.6f;
        Rectangle boundingBox;
        for(int y = 0; y < mazeMap.getHeight(); y++){
            for(int x = 0; x < mazeMap.getWidth(); x++){
                Tile tile = mazeMap.getTile(x, y);
                boundingBox = tile.getBoundingBox();
                boundingBoxSprite = new Sprite(redTile);
                boundingBoxSprite.setColor(Color.PURPLE);
                boundingBoxSprite.setAlpha(boundingBoxAlpha*0.5f);
                boundingBoxSprite.setSize(boundingBox.getWidth(), boundingBox.getHeight());
                boundingBoxSprite.setPosition(boundingBox.getX(), boundingBox.getY());
                boundingBoxSprite.draw(spriteBatch);
            }
        }

        // staticObjects
        for(var gameObject : mazeMap.getStaticObjects().values()){
            boundingBox = gameObject.getBoundingBox();
            boundingBoxSprite = new Sprite(redTile);
            boundingBoxSprite.setAlpha(boundingBoxAlpha);
            boundingBoxSprite.setSize(boundingBox.getWidth(), boundingBox.getHeight());
            boundingBoxSprite.setPosition(boundingBox.getX(), boundingBox.getY());
            boundingBoxSprite.draw(spriteBatch);
        }

        // characters
        for(var gameObject : mazeMap.getDynamicObjects()){
            boundingBox = gameObject.getBoundingBox();
            boundingBoxSprite = new Sprite(redTile);
            boundingBoxSprite.setAlpha(boundingBoxAlpha);
            boundingBoxSprite.setSize(boundingBox.getWidth(), boundingBox.getHeight());
            boundingBoxSprite.setPosition(boundingBox.getX(), boundingBox.getY());
            boundingBoxSprite.draw(spriteBatch);
        }
        Player player = mazeMap.getPlayer();
        boundingBox = player.getBoundingBox();
        boundingBoxSprite = new Sprite(redTile);
        boundingBoxSprite.setAlpha(boundingBoxAlpha);
        boundingBoxSprite.setSize(boundingBox.getWidth(), boundingBox.getHeight());
        boundingBoxSprite.setPosition(boundingBox.getX(), boundingBox.getY());
        boundingBoxSprite.draw(spriteBatch);

        boundingBox = player.getAttackBoundingBox();
        boundingBoxSprite = new Sprite(redTile);
        boundingBoxSprite.setColor(Color.GREEN);
        boundingBoxSprite.setAlpha(boundingBoxAlpha);
        boundingBoxSprite.setSize(boundingBox.getWidth(), boundingBox.getHeight());
        boundingBoxSprite.setPosition(boundingBox.getX(), boundingBox.getY());
        boundingBoxSprite.draw(spriteBatch);
    }

}
