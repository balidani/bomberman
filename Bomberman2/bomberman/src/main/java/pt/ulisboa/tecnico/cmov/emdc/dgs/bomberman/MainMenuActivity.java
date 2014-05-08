package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;


public class MainMenuActivity extends Activity implements OnClickListener {
    Button singlePlayer;
    Button multiPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        singlePlayer = (Button) findViewById(R.id.playSinglePlayerButton);
        singlePlayer.setOnClickListener(this);
        multiPlayer = (Button) findViewById(R.id.playMultiPlayerButton);
        multiPlayer.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // TODO : check if player name is valid, otherwise ask for another one
        // TODO : store name in app specific storage
        if(view == multiPlayer) {
            // initialize multiplayer needs
            // set multiplayer flag here!
        }
        Intent i = new Intent(MainMenuActivity.this, BombingActivity.class);
        startActivity(i);
        //finish();
    }
}
