package pt.ulisboa.tecnico.bomberman.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientHandler implements Runnable {

	private Socket socket;
	private Server server;

	private static InputStreamReader inputStreamReader;
	private static BufferedReader bufferedReader;
	private static String message;

	public ClientHandler(Socket socket, Server server) throws IOException {
		this.socket = socket;
		this.server = server;

		inputStreamReader = new InputStreamReader(socket.getInputStream());
		bufferedReader = new BufferedReader(inputStreamReader);
	}

	@Override
	public void run() {

		while (true) {
			try {
				message = bufferedReader.readLine();
				server.broadcast(socket, message);
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}

		try {
			inputStreamReader.close();
			bufferedReader.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
