package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Screen;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl.GLGame;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.screen.LoadingScreen;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.wifidirect.BroadcastServerAsyncTask;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.wifidirect.NetworkHandler;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.World;


public class BombingActivity extends GLGame {

    public static BombingActivity activity;

    public World currentLevel;
    public int levelNo;
    public boolean multiplayer;
    public String playerName;

    public InetAddress ownerAddress;
    public int playerId;
    public boolean isOwner;

    public Socket client;
    public BufferedWriter out;
    public BufferedReader in;
    private BroadcastServerAsyncTask broadcastServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bombing_and_scoreboard_flipper);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.gameLayout);
        relativeLayout.addView(this.glSurfaceView);
        levelNo = 1;
        activity = this;
        multiplayer = getIntent().getExtras().getBoolean("multiplayer");
        playerName = getIntent().getExtras().getString("playerName");

        if (multiplayer) {
            ownerAddress = (InetAddress) getIntent().getExtras().getSerializable("ownerAddress");
            isOwner = getIntent().getExtras().getBoolean("isOwner");
            playerId = getIntent().getExtras().getInt("playerIntent");

            Log.d("Bomberman", "Are we owner? " + isOwner);

            if (isOwner) {
                // Start a server
                broadcastServer = new BroadcastServerAsyncTask(getApplicationContext());
                broadcastServer.execute();
            } else {
                // Start a client
                new Thread() {
                    @Override
                    public void run() {

                        try {
                            client = new Socket(ownerAddress.getHostAddress(), 44444);
                            client.setTcpNoDelay(true);
                            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

                            while (true) {
                                String msg = in.readLine();
                                NetworkHandler.parse(msg);
                            }

                        } catch (IOException e) {
                            Log.d("Bomberman", e.getMessage());
                            e.printStackTrace();
                        }
                    }

                }.start();
            }
        }

        ((TextView) findViewById(R.id.player_name)).setText(playerName);
    }

    // From that moment on, using screens!
    @Override
    public Screen getStartScreen() {
        return new LoadingScreen(this, levelNo);
    }

    public void onMoveLeft(View view) {
        BaseInputConnection inputConnection = new BaseInputConnection(glSurfaceView, true);
        inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_A));
    }

    public void onMoveRight(View view) {
        BaseInputConnection inputConnection = new BaseInputConnection(glSurfaceView, true);
        inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_D));
    }

    public void onMoveUp(View view) {
        BaseInputConnection inputConnection = new BaseInputConnection(glSurfaceView, true);
        inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_W));
    }

    public void onMoveDown(View view) {
        BaseInputConnection inputConnection = new BaseInputConnection(glSurfaceView, true);
        inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_S));
    }


    public void onBomb(View view) {
        BaseInputConnection inputConnection = new BaseInputConnection(glSurfaceView, true);
        inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_Q));
    }

    public void setDashBoard(int score, int timeLeft, int numPlayers) {
        ((TextView) findViewById(R.id.player_score)).setText(Integer.toString(score));
        ((TextView) findViewById(R.id.time_left)).setText(Integer.toString(timeLeft));
        ((TextView) findViewById(R.id.player_count)).setText(Integer.toString(numPlayers));
    }

    public void setScoreboard() {
        ((ViewFlipper) findViewById(R.id.flipper)).showNext();
    }

    public void onPaused(View view) {
        BaseInputConnection inputConnection = new BaseInputConnection(glSurfaceView, true);
        inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_P));
    }

    public void onEndSession(View view) {
        this.finish();
    }

    public void sendMessage(String msg) {
        try {
            if (isOwner) {
                broadcastServer.sendMessage(msg);
            } else {
                out.write(msg + "\n");
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onQuit(View view) {
        this.finish();
    }
}
