package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.wifidirect;

import android.util.Log;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.BombingActivity;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.GameAssets;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.Direction;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.World;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.agent.Player;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.agent.Robot;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.map.Bomb;

/**
 * Created by dada on 2014.05.15..
 */
public class NetworkHandler {

    public static void parse(String msg) {
        String[] args = msg.split(" ");

        Log.d("Bomberman", "Got message: " + msg);
        World world =  BombingActivity.activity.currentLevel;
        // Parse the server message
        if (args[0].equals("MOVE")) {
            int playerId = Integer.parseInt(args[1]);
            int x = Integer.parseInt(args[2]);
            int y = Integer.parseInt(args[3]);
            int dir = Integer.parseInt(args[4]);


            Player player = world.findPlayerById(playerId);
            player.receivedX = x;
            player.receivedY = y;
            player.receivedDirection = Direction.AllDirections[dir];


        } else if (args[0].equals("BOMB")) {
            int playerId = Integer.parseInt(args[1]);
            Player player = world.findPlayerById(playerId);
            int i = Integer.parseInt(args[2]);
            int j = Integer.parseInt(args[3]);

            world.bombs.add(new Bomb(world,player,i,j));

        } else if(args[0].equals("ROBOT")) {
            int robotId = Integer.parseInt(args[1]);
            int x = Integer.parseInt(args[2]);
            int y = Integer.parseInt(args[3]);
            int dir = Integer.parseInt(args[4]);


            Robot robot = world.findRobotById(robotId);
            robot.receivedX = x;
            robot.receivedY = y;
            robot.receivedDirection = Direction.AllDirections[dir];
        }
    }
}
