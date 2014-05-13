package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.widget.RelativeLayout;
import android.widget.TextView;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Screen;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl.AndroidGame;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl.GLGame;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.screen.LoadingScreen;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.World;


public class BombingActivity extends GLGame  {

    public World currentLevel;
    public int levelNo;
    public boolean multiplayer;
    public String playerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bombing);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.gameLayout);
        relativeLayout.addView(this.glSurfaceView);
        levelNo=1;
        multiplayer = getIntent().getExtras().getBoolean("multiplayer");
        playerName = getIntent().getExtras().getString("playerName");

        ((TextView)findViewById(R.id.player_name)).setText(playerName);
    }

    // From that moment on, using screens!
    @Override
    public Screen getStartScreen() {
        return new LoadingScreen(this,levelNo);
    }

    @Override
    public void lookAt(int left, int top) {

    }

    public void onMoveLeft(View view) {
        BaseInputConnection inputConnection = new BaseInputConnection(glSurfaceView,true);
        inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_A));
    }

    public void onMoveRight(View view) {
        BaseInputConnection inputConnection = new BaseInputConnection(glSurfaceView,true);
        inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_D));
    }

    public void onMoveUp(View view) {
        BaseInputConnection inputConnection = new BaseInputConnection(glSurfaceView,true);
        inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_W));
    }

    public void onMoveDown(View view) {
        BaseInputConnection inputConnection = new BaseInputConnection(glSurfaceView,true);
        inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_S));
    }


    public void onBomb(View view) {
        BaseInputConnection inputConnection = new BaseInputConnection(glSurfaceView,true);
        inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_Q));
    }

    public void setDashBoard(int score,int timeLeft, int numPlayers)
    {
        ((TextView)findViewById(R.id.player_score)).setText(Integer.toString(score));
        ((TextView)findViewById(R.id.time_left)).setText(Integer.toString(timeLeft));
        ((TextView)findViewById(R.id.player_count)).setText(Integer.toString(numPlayers));
    }

    public void onPaused(View view) {
        BaseInputConnection inputConnection = new BaseInputConnection(glSurfaceView,true);
        inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_P));
    }
}
