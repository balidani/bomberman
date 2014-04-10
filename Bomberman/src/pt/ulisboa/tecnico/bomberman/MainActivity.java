package pt.ulisboa.tecnico.bomberman;

import pt.ulisboa.tecnico.bomberman.game.GameActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;

public class MainActivity extends Activity {

	public enum PlayerColor {
		WHITE, BLUE, RED
	};

	public PlayerColor player;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		player = PlayerColor.WHITE;
		
		Spinner playerChoice = (Spinner) findViewById(R.id.playerSpinner);
		playerChoice.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				
				player = PlayerColor.values()[pos];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}

		});
		
		Button startButton = (Button) findViewById(R.id.playButton);
		startButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, GameActivity.class);
				i.putExtra("player_color", player.ordinal());
				startActivity(i);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
