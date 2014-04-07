package pt.ulisboa.tecnico.bomberman.game.events;

import java.util.Random;

import pt.ulisboa.tecnico.bomberman.game.Config;
import pt.ulisboa.tecnico.bomberman.game.Direction;
import pt.ulisboa.tecnico.bomberman.game.GameActivity;
import android.os.Handler;

public class RobotEvents {

	private GameActivity game;
	private Handler robotHandler;
	
	public RobotEvents(GameActivity game) {
		this.game = game;
		
		robotHandler = new Handler();
		robotHandler.post(robotUpdate);
	}

	private Runnable robotUpdate = new Runnable() {

		public void run() {
			
			if (game.map.getRobots().isEmpty()) {
				robotHandler.removeCallbacks(this);
				return;
			}

			try {

				for (int i = 0; i < game.map.getRobots().size(); i++) {
						
					int directionValue = new Random().nextInt(Direction.values().length);
					Direction randomDirection = Direction.values()[directionValue];
					
					game.moveAgent(game.map.getRobots().get(i), randomDirection);
				}
			} catch (Exception ex) {
				// This will happen when main thread removes a robot. This is expected
				// TODO: change it to the specific exception type that occurs
			} finally {
				robotHandler.postDelayed(this, Config.robotSpeed);
			}

			game.render();
		}
	};
}
