package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.wifidirect;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocket;

public class MessageProcessor extends Thread
{
    SimWifiP2pSocket sender;
	public MessageProcessor(SimWifiP2pSocket sender)
	{
		this.sender=sender;
        BroadCastManager.AddClient(sender);
	}
	
	public void run()
	{
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(sender.getInputStream()));			
			while (true)
			{
				String message = in.readLine();	
				System.out.println("Message : "+message);
				if (message == null)
				{
                    Log.d("Server", "Disconnected a client");
					break;
				}
				else
				{
					BroadCastManager.BroadCastMessage(message, sender);
				}				
			}
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(sender!=null) {
                    BroadCastManager.RemoveClient(sender);
                    sender.close();
                }
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}

		}
	}
}
