package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.screen;

import android.app.Activity;
import android.graphics.Bitmap;
import android.opengl.GLUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.Comparator;
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
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.agent.Player;

/**
 * Created by savasci on 5/13/2014.
 */
public class ScoreScreen extends Screen {
    GLGraphics glGraphics ;
    List<Player> players;
    Game game;
    FloatBuffer vertices;
    Bitmap currentBitmap;
    int textureId;
    private List<Input.TouchEvent> touchEvents;
    private List<Input.KeyEvent> keyEvents;

    public ScoreScreen(Game game1) {
        super(game1);
        this.game = game1;
        glGraphics = ((GLGame)game).getGlGraphics();
        players = ((BombingActivity)game).currentLevel.players;

        createScoreboard();
        ((BombingActivity)game).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((BombingActivity) game).setScoreboard();
            }
        });


    }

    private void createScoreboard() {
        TableLayout tableLayout = (TableLayout)((Activity) game).findViewById(R.id.score_table);
        Collections.sort(players,new Comparator<Player>() {
            @Override
            public int compare(Player player1, Player player2) {
                return player1.score - player2.score;
            }
        });
        for(int i=0;i<players.size();i++)
        {
            if(players.get(i).playerName.equals("Unnamed")) {
                continue;
            }
            TableRow tr = new TableRow((Activity)game);
            tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT));

            TextView tv1 = new TextView((Activity)game);
            tv1.setText(Integer.toString(i+1)+".  "+players.get(i).playerName);
            tv1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            tv1.setGravity(Gravity.LEFT);
            tr.addView(tv1);


            TextView tv2 = new TextView((Activity)game);
            tv2.setText(Integer.toString(players.get(i).score));
            tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            tv2.setGravity(Gravity.RIGHT);
            tr.addView(tv2);



            tableLayout.addView(tr,new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        }
    }

    @Override
    public void update(float deltaTime) {
    }

    @Override
    public void present(float deltaTime) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
