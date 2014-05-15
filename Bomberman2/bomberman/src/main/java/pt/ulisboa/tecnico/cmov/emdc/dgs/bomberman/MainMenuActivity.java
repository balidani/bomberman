package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;


public class MainMenuActivity extends Activity implements OnClickListener {
    Button singlePlayer;
    Button multiPlayer;
    String playerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        playerName = "Unknown";

        singlePlayer = (Button) findViewById(R.id.playSinglePlayerButton);
        singlePlayer.setOnClickListener(this);
        multiPlayer = (Button) findViewById(R.id.playMultiPlayerButton);
        multiPlayer.setOnClickListener(this);

        ((TextView) findViewById(R.id.playerName)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                playerName = ((TextView) findViewById(R.id.playerName)).getText().toString().isEmpty() ?
                        "Unknown" : ((TextView) findViewById(R.id.playerName)).getText().toString();
            }
        });
    }

    @Override
    public void onClick(View view) {
        // TODO : check if player name is valid, otherwise ask for another one
        // TODO : store name in app specific storage
        Intent i;
        if (view == multiPlayer) {
            // initialize multiplayer needs
            // set multiplayer flag here!
            i = new Intent(MainMenuActivity.this, WiFiConnectionActivity.class);
            i.putExtra("multiplayer", true);
        } else {
            i = new Intent(MainMenuActivity.this, BombingActivity.class);
            i.putExtra("multiplayer", false);
        }
        i.putExtra("playerName", playerName);
        startActivity(i);
        //finish();
    }
}
