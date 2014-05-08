package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.screen;

import android.app.Activity;
import android.opengl.GLU;
import android.view.KeyEvent;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.BombingActivity;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.GameAssets;

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

    Player myPlayer;
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
    Vertices generalModel;


    public GameScreen(Game game) {
        super(game);
        this.game = game;
        world = ((BombingActivity) game).currentLevel;
        myPlayer = world.players.get(0);


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

    }

    @Override
    public void update(float deltaTime) {
        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
        List<Input.KeyEvent> keyEvents = game.getInput().getKeyEvents();
        if(state == GameState.Ready)
            updateReady(touchEvents);
        if(state == GameState.Running)
            updateRunning(keyEvents, deltaTime);
        if(state == GameState.Paused)
            updatePaused(touchEvents);
        if(state == GameState.GameOver)
            updateGameOver(touchEvents);
        fpsCounter.logFrame();
    }

    private void updateGameOver(List<Input.TouchEvent> touchEvents) {
    }

    private void updatePaused(List<Input.TouchEvent> touchEvents) {
    }

    private void updateReady(List<Input.TouchEvent> touchEvents) {
        if(touchEvents.size() > 0)
            state = GameState.Running;
    }

    public void updateRunning(List<Input.KeyEvent> keyEvents,float deltaTime) {
        currentTime+=deltaTime;
        ((BombingActivity)game).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((BombingActivity) game).setDashBoard(myPlayer.score, (int) (World.gameDuration - currentTime), world.players.size());
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



        for(Input.KeyEvent e : keyEvents) {


            if(myPlayer.isDying || !myPlayer.isAlive) continue;
            if(e.type == Input.KeyEvent.KEY_UP && e.keyCode == KeyEvent.KEYCODE_A ){
                myPlayer.moveIssued(Direction.LEFT);
            } else if(e.type == Input.KeyEvent.KEY_UP && e.keyCode == KeyEvent.KEYCODE_S ){
                myPlayer.moveIssued(Direction.DOWN);
            } else if(e.type == Input.KeyEvent.KEY_UP && e.keyCode == KeyEvent.KEYCODE_D ){
                myPlayer.moveIssued(Direction.RIGHT);
            } else if(e.type == Input.KeyEvent.KEY_UP && e.keyCode == KeyEvent.KEYCODE_W ){
                myPlayer.moveIssued(Direction.UP);
            } else if(e.type == Input.KeyEvent.KEY_UP && e.keyCode == KeyEvent.KEYCODE_Q && myPlayer.hasBomb) {
                world.bombs.add(new Bomb(world,myPlayer, (int)(-1*myPlayer.position.y/GameAssets.ASSET_DIMENSION) ,(int)(myPlayer.position.x/GameAssets.ASSET_DIMENSION)));
            }
        }
        for(Player player: world.players)
        {
            player.simulate(deltaTime);
        }

    }

    @Override
    public void present(float deltaTime) {
        if(state == GameState.Ready)
            presentReady();
        if(state == GameState.Running)
            presentRunning(deltaTime);
        if(state == GameState.Paused)
            presentPaused();
        if(state == GameState.GameOver)
            presentGameOver();
    }

    private void presentGameOver() {

    }

    private void presentPaused() {
        
    }

    private void presentReady() {

    }

    public void presentRunning(float deltaTime) {
        GL10 gl = glGraphics.getGl10();
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(myPlayer.position.x-200,myPlayer.position.x+200,myPlayer.position.y - 1.0f * (200)* ((GLGame) game).glSurfaceView.getHeight()/ ((GLGame) game).glSurfaceView.getWidth() , myPlayer.position.y+200, 1, -1);


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

    }

    @Override
    public void resume() {
        GL10 gl = glGraphics.getGl10();
        gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(0,640,-1.0f * 640* ((GLGame) game).glSurfaceView.getHeight()/ ((GLGame) game).glSurfaceView.getWidth() , 0, 1, -1);

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        robotTexture.reload();
        wallTexture.reload();
        obstacleTexture.reload();
        emptyTexture.reload();
        playerTexture[0].reload();
        playerTexture[1].reload();
        playerTexture[2].reload();
        bombTexture.reload();
    }

    @Override
    public void dispose() {

    }
}
