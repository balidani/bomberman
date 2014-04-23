package pt.ulisboa.tecnico.bomberman.game.events;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

import pt.ulisboa.tecnico.bomberman.game.Coordinate;
import pt.ulisboa.tecnico.bomberman.game.GameActivity;
import pt.ulisboa.tecnico.bomberman.game.agents.Player;
import pt.ulisboa.tecnico.bomberman.game.agents.Robot;

public class MultiplayerEvents {

	public static final String serverAddress = "54.200.218.24";
	public static final int serverPort = 5001;
	
	public enum MessageType {
		START, BOMB, PLAYER_MOVE, ROBOT_MOVE, PLAYER_DEATH, ROBOT_DEATH, OBSTACLE_DEATH, STOP
	}

	public boolean started;

	private Socket socket;
	private BufferedWriter out;
	private BufferedReader in;

	public MultiplayerEvents(final GameActivity game) {

		new Thread() {
			public void run() {
				
				started = false;

				// Join server
				try {
					socket = new Socket(serverAddress, serverPort);

					out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					out.flush();

					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

					boolean listening = true;
					int x, y, id, dir;

					while (listening) {
						String msg = in.readLine();
						String args[] = msg.split(" ");
						String cmd = args[0];
						MessageType type = MessageType.valueOf(cmd);

						Log.d("Bomberman", msg);
						
						switch (type) {
						case START:
							started = true;
							break;
						case BOMB:
							id = Integer.parseInt(args[1]);
							x = Integer.parseInt(args[2]);
							y = Integer.parseInt(args[3]);
							game.bombEvents.addBomb(new Coordinate(x, y), id);
							break;
						case PLAYER_MOVE:
							id = Integer.parseInt(args[1]);
							dir = Integer.parseInt(args[2]);

							game.movePlayer(id, dir);
							break;
						case ROBOT_MOVE:
							id = Integer.parseInt(args[1]);
							dir = Integer.parseInt(args[1]);

							game.moveRobot(id, dir);
							break;
						case PLAYER_DEATH:

							break;
						case ROBOT_DEATH:

							break;
						case OBSTACLE_DEATH:

							break;
						case STOP:
							listening = false;
							break;
						}
					}

					in.close();
					out.close();
					socket.close();

				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			};
		}.start();

	}

	public void addBomb(Player player) {
		String msg = String.format("{0} {1} {2} {3}", MessageType.BOMB.toString(), player.color.ordinal(), player.position.x, player.position.x);
		send(msg);
	}

	public void playerMove(Player player, int dir) {
		String msg = String.format("{0} {1} {2}", MessageType.PLAYER_MOVE.toString(), player.color.ordinal(), dir);
		send(msg);
	}

	public void robotMove(Robot robot, int dir) {
		String msg = String.format("{0} {1} {2}", MessageType.PLAYER_MOVE.toString(), robot.id, dir);
		send(msg);
	}
	
	private void send(String msg) {
		try {
			out.write(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
