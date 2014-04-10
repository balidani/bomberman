package pt.ulisboa.tecnico.bomberman.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
	
	private static ServerSocket serverSocket;
    private List<Socket> clients;
    
    public Server() {
    	
    	clients = new ArrayList<Socket>();
    	
    	try {
            serverSocket = new ServerSocket(4444);
        } catch (IOException e) {
            System.out.println("Could not listen on port: 4444");
        }
 
        System.out.println("Server started. Listening on the port 4444");
 
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                clients.add(clientSocket);
                
                new Thread(new ClientHandler(clientSocket, this)).start();
 
            } catch (IOException ex) {
                System.out.println("Problem in message reading");
            }
        }
    }
    
    public static void main(String[] args) {
    	
        new Server();
    }
    
    public void broadcast(Socket sender, String message) throws IOException {
    	for (Socket client : clients) {
    		if (client.equals(sender)) {
    			continue;
    		}
			
    		PrintWriter out = new PrintWriter(client.getOutputStream(), true);
    		out.write(message);
		}
    }
}
