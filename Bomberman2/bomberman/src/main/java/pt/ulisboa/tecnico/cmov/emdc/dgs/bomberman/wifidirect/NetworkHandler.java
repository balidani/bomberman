package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.wifidirect;

import android.util.Log;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.BombingActivity;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.Direction;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.agent.Player;

/**
 * Created by dada on 2014.05.15..
 */
public class NetworkHandler {

    public static void parse(String msg) {
        String[] args = msg.split(" ");

        Log.d("Bomberman", "Got message: " + msg);

        // Parse the server message
        if (args[0].equals("MOVE")) {
            int playerId = Integer.parseInt(args[1]);
            int dir = Integer.parseInt(args[2]);

            Player player = BombingActivity.activity.currentLevel.findPlayerById(playerId);
            player.moveIssued(Direction.AllDirections[dir]);

        } else if (args[0].equals("BOMB")) {
            int player = Integer.parseInt(args[1]);

        }
    }
}
