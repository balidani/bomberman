package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.screen;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.BombingActivity;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.GameAssets;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.R;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Game;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Input;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Screen;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl.GLGame;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl.GLGraphics;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl.gl.FPSCounter;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl.gl.Texture;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl.gl.Vertices;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.Direction;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.World;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.agent.Player;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.agent.Robot;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.map.Bomb;

/**
 * Created by savasci on 4/29/2014.
 */
public class GameScreen extends Screen {
    enum GameState {
        Ready,
        Running,
        Paused,
        GameOver
    }
    GameState state = GameState.Ready;
    World world;
    Game game;
    FPSCounter fpsCounter;
    GLGraphics glGraphics;

    float currentTime;
    Robot currentRobot;
    Bomb currentBomb;

    Texture robotTexture;
    Texture robotCrispyTexture;
    Texture wallTexture;
    Texture obstacleTexture;
    Texture emptyTexture;
    Texture playerTexture[];
    Texture playerCrispyTexture;
    Texture bombTexture;
    Texture flameTexture;
    Texture tapToStartTexture;
    Texture youFailedTexture;
    Texture levelCompleteTexture;
    Vertices generalModel;

    RectF cameraMovementLimits;
    RectF currentCamera;

    float offset;
    float aspectRatio;


    List<Input.TouchEvent> touchEvents;
    List<Input.KeyEvent> keyEvents;

    boolean isMultiplayer;
    int alivePlayers=0;


    public GameScreen(Game game) {
        super(game);
        this.game = game;
        world = ((BombingActivity) game).currentLevel;
        isMultiplayer = ((BombingActivity) game).multiplayer;

        if(!isMultiplayer) {
            //myPlayer = world.players.get(0);
            for(Player current : world.players)
            {
                if(current != world.myPlayer) {
                    current.isAlive = false;
                }
            }
        }


        fpsCounter = new FPSCounter();
        glGraphics = ((GLGame)game).getGlGraphics();

        generalModel = new Vertices(glGraphics,4,12,false,true);
        generalModel.setVertices(new float[] { 0, -1 * GameAssets.ASSET_DIMENSION, 0, 1,
                GameAssets.ASSET_DIMENSION, -1 * GameAssets.ASSET_DIMENSION, 1, 1,
                GameAssets.ASSET_DIMENSION, 0, 1, 0,
                0, 0, 0, 0, }, 0, 16);
        generalModel.setIndices(new short[] {0, 1, 2, 2, 3, 0}, 0, 6);

        robotTexture = new Texture((GLGame)game,"images/robot.png");
        robotCrispyTexture = new Texture((GLGame)game,"images/robotCrispy.png");
        wallTexture = new Texture((GLGame)game,"images/wall.png");
        obstacleTexture = new Texture((GLGame)game,"images/obstacle.png");
        emptyTexture = new Texture((GLGame)game,"images/empty.png");
        playerTexture = new Texture[3];
        playerTexture[0] = new Texture((GLGame)game,"images/player_1.png");
        playerTexture[1] = new Texture((GLGame)game,"images/player_2.png");
        playerTexture[2] = new Texture((GLGame)game,"images/player_3.png");
        playerCrispyTexture = new Texture((GLGame)game,"images/playerCrispy.png");
        bombTexture = new Texture((GLGame)game,"images/bomb.png");
        flameTexture = new Texture((GLGame)game,"images/explosion.png");
        tapToStartTexture = new Texture((GLGame)game,"images/tap_to_start.png");
        youFailedTexture = new Texture((GLGame)game,"images/you_failed.png");
        levelCompleteTexture = new Texture((GLGame)game,"images/level_complete.png");

        this.offset = 200.0f;
        aspectRatio = (float)(((GLGame) game).glSurfaceView.getHeight())/ ((GLGame) game).glSurfaceView.getWidth();
        cameraMovementLimits = new RectF();
        cameraMovementLimits.left = offset;
        cameraMovementLimits.right = world.map.width*GameAssets.ASSET_DIMENSION-offset>cameraMovementLimits.left?world.map.width*GameAssets.ASSET_DIMENSION-offset:cameraMovementLimits.left;
        cameraMovementLimits.top = -1.0f * offset*aspectRatio;
        cameraMovementLimits.bottom = -1.0f*world.map.height*GameAssets.ASSET_DIMENSION+offset*aspectRatio>cameraMovementLimits.top?cameraMovementLimits.top:-1.0f*world.map.height*GameAssets.ASSET_DIMENSION+offset*aspectRatio;



        currentCamera = new RectF();
        currentCamera.left = world.myPlayer.position.x - offset;
        currentCamera.right = world.myPlayer.position.x + offset;
        currentCamera.top = world.myPlayer.position.y + offset*aspectRatio;
        currentCamera.bottom = world.myPlayer.position.y - offset*aspectRatio ;

    }

    @Override
    public void update(float deltaTime) {
        touchEvents = game.getInput().getTouchEvents();
        keyEvents = game.getInput().getKeyEvents();
        if(state == GameState.Ready)
            updateReady(touchEvents);
        else if(state == GameState.Running)
            updateRunning(keyEvents, deltaTime);
        else if(state == GameState.Paused)
            updatePaused(touchEvents);
        else if(state == GameState.GameOver)
            updateGameOver(touchEvents);
        // fpsCounter.logFrame();
    }

    private void updateGameOver(List<Input.TouchEvent> touchEvents) {
        if(touchEvents.size() > 0 ) {
            if(alivePlayers == 0 || (World.gameDuration - currentTime)<0.0f) {
                game.setScreen(new ScoreScreen(game));
            }
            else if(((BombingActivity)game).levelNo < GameAssets.LEVEL_COUNT) {
                ((BombingActivity)game).levelNo++;
                game.setScreen(new LoadingScreen(game,((BombingActivity)game).levelNo,world.players,world.myPlayer));
            } else {
                game.setScreen(new ScoreScreen(game));
            }

        }

    }

    private void updatePaused(List<Input.TouchEvent> touchEvents) {
        if(touchEvents.size() > 0)
            state = GameState.Running;
    }

    private void updateReady(List<Input.TouchEvent> touchEvents) {
        if(touchEvents.size() > 0)
            state = GameState.Running;
    }

    public void updateRunning(List<Input.KeyEvent> keyEvents,float deltaTime) {
        currentTime+=deltaTime;
        alivePlayers = 0;
        for(Player player : world.players)
        {
            if(player.isAlive)
            {
                alivePlayers++;
            }
        }
        if(alivePlayers == 0 || (world.robots.size() == 0 && alivePlayers <= 1) || (World.gameDuration - currentTime)<0.0f )
        {
            Log.d("Bomberman","Game Over!");
            state = GameState.GameOver;
            return;
        }



        ((BombingActivity)game).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((BombingActivity) game).setDashBoard(world.myPlayer.score, (int) (World.gameDuration - currentTime), alivePlayers);
            }
        });


        for(Iterator<Robot> it = world.robots.iterator(); it.hasNext(); ) {
            currentRobot = it.next();
            if(currentRobot.isAlive) currentRobot.simulate(deltaTime, world.players);
            else it.remove();
        }

        for(Iterator<Bomb> it = world.bombs.iterator(); it.hasNext();) {
            currentBomb = it.next();
            if(currentBomb.ended) it.remove();
            else currentBomb.simulate(deltaTime);
        }

        for(Player player: world.players)
        {
            player.simulate(deltaTime);
        }


        for(Input.KeyEvent e : keyEvents) {


            if(world.myPlayer.isDying || !world.myPlayer.isAlive) continue;
            if(e.type == Input.KeyEvent.KEY_UP && e.keyCode == KeyEvent.KEYCODE_P ){
                state = GameState.Paused;
            } else if(e.type == Input.KeyEvent.KEY_UP && e.keyCode == KeyEvent.KEYCODE_A ){
                world.myPlayer.moveIssued(Direction.LEFT);
            } else if(e.type == Input.KeyEvent.KEY_UP && e.keyCode == KeyEvent.KEYCODE_S ){
                world.myPlayer.moveIssued(Direction.DOWN);
            } else if(e.type == Input.KeyEvent.KEY_UP && e.keyCode == KeyEvent.KEYCODE_D ){
                world.myPlayer.moveIssued(Direction.RIGHT);
            } else if(e.type == Input.KeyEvent.KEY_UP && e.keyCode == KeyEvent.KEYCODE_W ){
                world.myPlayer.moveIssued(Direction.UP);
            } else if(e.type == Input.KeyEvent.KEY_UP && e.keyCode == KeyEvent.KEYCODE_Q && world.myPlayer.hasBomb) {
                world.bombs.add(new Bomb(world,world.myPlayer, (int)(-1*world.myPlayer.position.y/GameAssets.ASSET_DIMENSION) ,
                        (int)(world.myPlayer.position.x/GameAssets.ASSET_DIMENSION)));
                if(((BombingActivity)game).multiplayer) {
                    ((BombingActivity)game).sendMessage("BOMB "+ world.myPlayer.id +" "+ (int)(-1*world.myPlayer.position.y/GameAssets.ASSET_DIMENSION)+ " "+ (int)(world.myPlayer.position.x/GameAssets.ASSET_DIMENSION) );
                 }
            }
        }


    }

    @Override
    public void present(float deltaTime) {
        if(state == GameState.Ready)
            presentReady();
        else if(state == GameState.Running)
            presentRunning(deltaTime);
        else if(state == GameState.Paused)
            presentPaused();
        else if(state == GameState.GameOver)
            presentGameOver();
    }

    private void presentGameOver() {
        GL10 gl = glGraphics.getGl10();
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        world.map.draw(gl,generalModel,wallTexture,obstacleTexture,emptyTexture);

        gl.glEnable(GL10.GL_TEXTURE_2D);
        if(alivePlayers == 0 || (World.gameDuration - currentTime)<0.0f )
        {
            //TODO present game over
            youFailedTexture.bind();

        } else {
            //TODO present level complete!
            levelCompleteTexture.bind();
        }

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef(world.myPlayer.position.x,world.myPlayer.position.y,0.0f);
        //gl.glTranslatef(GameAssets.ASSET_DIMENSION / 2, -1 * GameAssets.ASSET_DIMENSION / 2, 0);
        gl.glScalef(10.0f, 10.0f, 1.0f);
        gl.glTranslatef(-1 * GameAssets.ASSET_DIMENSION / 2, GameAssets.ASSET_DIMENSION / 2, 0);
        generalModel.draw(GL10.GL_TRIANGLES,0,6);


    }

    private void presentPaused() {
        GL10 gl = glGraphics.getGl10();
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        world.map.draw(gl,generalModel,wallTexture,obstacleTexture,emptyTexture);

        gl.glEnable(GL10.GL_TEXTURE_2D);
        tapToStartTexture.bind();

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef(world.myPlayer.position.x,world.myPlayer.position.y,0.0f);
        //gl.glTranslatef(GameAssets.ASSET_DIMENSION / 2, -1 * GameAssets.ASSET_DIMENSION / 2, 0);
        gl.glScalef(10.0f, 10.0f, 1.0f);
        gl.glTranslatef(-1 * GameAssets.ASSET_DIMENSION / 2, GameAssets.ASSET_DIMENSION / 2, 0);
        generalModel.draw(GL10.GL_TRIANGLES,0,6);

    }

    private void presentReady() {
        GL10 gl = glGraphics.getGl10();
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        world.map.draw(gl,generalModel,wallTexture,obstacleTexture,emptyTexture);

        gl.glEnable(GL10.GL_TEXTURE_2D);
        tapToStartTexture.bind();

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef(world.myPlayer.position.x,world.myPlayer.position.y,0.0f);
        //gl.glTranslatef(GameAssets.ASSET_DIMENSION / 2, -1 * GameAssets.ASSET_DIMENSION / 2, 0);
        gl.glScalef(10.0f, 10.0f, 1.0f);
        gl.glTranslatef(-1 * GameAssets.ASSET_DIMENSION / 2, GameAssets.ASSET_DIMENSION / 2, 0);
        generalModel.draw(GL10.GL_TRIANGLES,0,6);

    }

    public void presentRunning(float deltaTime) {
        GL10 gl = glGraphics.getGl10();
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        currentCamera.left = world.myPlayer.position.x - offset;
        currentCamera.right = world.myPlayer.position.x + offset;
        currentCamera.bottom = world.myPlayer.position.y - offset * aspectRatio;
        currentCamera.top =world.myPlayer.position.y + offset * aspectRatio;

//        if(myPlayer.position.x >= cameraMovementLimits.left && myPlayer.position.x <= cameraMovementLimits.right){
//            currentCamera.left = myPlayer.position.x - offset;
//            currentCamera.right = myPlayer.position.x + offset;
//        }
//        if(myPlayer.position.y >= cameraMovementLimits.bottom && myPlayer.position.y <= cameraMovementLimits.top){
//            currentCamera.top = myPlayer.position.y + offset* aspectRatio;
//            currentCamera.bottom = myPlayer.position.y - offset * aspectRatio;
//        }

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(currentCamera.left,currentCamera.right,currentCamera.bottom,currentCamera.top, 1.0f, -1.0f);

        world.map.draw(gl,generalModel,wallTexture,obstacleTexture,emptyTexture);
        for(Bomb current: world.bombs) {
            current.draw(gl,generalModel,bombTexture,flameTexture );
        }
        for(Robot current : world.robots){
            current.draw(gl, generalModel, robotTexture,robotCrispyTexture);
        }
        for(Player current : world.players){
            current.draw(gl,generalModel,playerTexture[current.id],playerCrispyTexture);
        }


        //GLU.gluLookAt(gl,0,0,0,0,0,0,1,0,0);

    }

    @Override
    public void pause() {
        state = GameState.Paused;

    }

    @Override
    public void resume() {
        GL10 gl = glGraphics.getGl10();
        gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(currentCamera.left,currentCamera.right,currentCamera.bottom,currentCamera.top,1.0f,-1.0f);

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        robotTexture.reload();
        robotCrispyTexture.reload();
        wallTexture.reload();
        obstacleTexture.reload();
        emptyTexture.reload();
        playerTexture[0].reload();
        playerTexture[1].reload();
        playerTexture[2].reload();
        playerCrispyTexture.reload();
        bombTexture.reload();
        flameTexture.reload();
        tapToStartTexture.reload();
        youFailedTexture.reload();
        levelCompleteTexture.reload();
        aspectRatio = (float)(((GLGame) game).glSurfaceView.getHeight())/ ((GLGame) game).glSurfaceView.getWidth();
    }

    @Override
    public void dispose() {

    }
}
