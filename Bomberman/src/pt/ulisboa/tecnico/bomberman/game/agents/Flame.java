package pt.ulisboa.tecnico.bomberman.game.agents;

import java.util.Timer;
import java.util.TimerTask;

import pt.ulisboa.tecnico.bomberman.game.Coordinate;
import pt.ulisboa.tecnico.bomberman.game.events.BombEvents;

public class Flame extends Agent {

	public int alpha;
	protected final BombEvents bombEvents;
	
	public Flame(Coordinate position, final BombEvents bombEvents) {
		super(position);
		
		this.bombEvents = bombEvents;
		alpha = 255;
		
		Timer alphaTimer = new Timer();
		alphaTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				
				alpha -= 3;
				
				if (alpha <= 0) {
					alpha = 0;
					this.cancel();
				}
				
				bombEvents.requestRender();
			}
			
		}, 10, 10); 
	}
	
}
