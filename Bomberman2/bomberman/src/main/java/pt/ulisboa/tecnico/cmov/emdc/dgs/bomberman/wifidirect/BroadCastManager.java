package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.wifidirect;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocket;

public class BroadCastManager 
{
	private static List<SimWifiP2pSocket> clientSocketList=new ArrayList<SimWifiP2pSocket>();

    public static void AddClient(SimWifiP2pSocket client)
    {
        if(!clientSocketList.contains(client))
            clientSocketList.add(client);
    }

    public static int getclientSocketListSize()
    {
        return clientSocketList.size();
    }

    public static void RemoveClient(SimWifiP2pSocket client)
    {
        if(clientSocketList.contains(client))
            clientSocketList.remove(client);
    }

    public static void ClearClientSocketList()
    {
        clientSocketList.clear();
    }
	public static void BroadCastMessage(String message, SimWifiP2pSocket sender)
	{
		try 
		{
			PrintWriter printwriter;
            SimWifiP2pSocket client;
			for(int i=0;i<clientSocketList.size();i++)
			{
				client=clientSocketList.get(i);
				if(!client.equals(sender))
				{
				/*	printwriter = new PrintWriter(client.getOutputStream(), true);
					printwriter.write(message);
					printwriter.flush();*/
                    client.getOutputStream().write(message.getBytes());
				}
			}
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
}
