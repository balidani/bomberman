package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.wifidirect;
 
import android.util.Log;

import java.io.IOException;

import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocketServer;

public class Server {
 
    private SimWifiP2pSocketServer serverSocket;

    public Server()
    {
        BroadCastManager.ClearClientSocketList();
    }

    public void StartServer()
    {
        try {
            serverSocket = new SimWifiP2pSocketServer(10001);
        } catch (IOException e) {
            Log.d("Server","Could not listen on port: 10001");
        }

        Log.d("Server","Server started. Listening to the port 10001");
 
        while (true) 
        {
            try 
            {
                new MessageProcessor(serverSocket.accept()).start(); 
            } 
            catch (IOException ex) 
            {
                Log.e("Server","Problem in message reading",ex);
            }
        }
    }
}

