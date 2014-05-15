package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.wifidirect;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.BombingActivity;

/**
 * A simple server socket that accepts connection and writes some data on
 * the stream.
 */
public class BroadcastServerAsyncTask extends AsyncTask {

    private Context context;
    private ServerSocket serverSocket;
    private List<Socket> clients;

    /**
     * @param context
     */
    public BroadcastServerAsyncTask(Context context) {
        this.context = context;
        clients = new ArrayList<Socket>();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            serverSocket = new ServerSocket(44444);
            Socket client = serverSocket.accept();
            clients.add(client);

            Log.d("Bomberman", "Accepted a client");

            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while (true) {
                String msg = in.readLine();
                NetworkHandler.parse(msg);
            }
        } catch (IOException e) {
            Log.e(WiFiDirectActivity.TAG, e.getMessage());
        }

        return null;
    }

    public void sendMessage(String msg) {

        try {
            for (Socket client : clients) {
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                out.write(msg + "\n");
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}