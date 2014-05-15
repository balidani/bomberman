package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.wifidirect.BroadCastManager;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.wifidirect.MessageProcessor;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.wifidirect.SimWifiP2pBroadcastReceiver;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pBroadcast;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pDevice;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pDeviceList;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pInfo;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pManager;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pManager.Channel;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pManager.GroupInfoListener;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pManager.PeerListListener;
import pt.utl.ist.cmov.wifidirect.service.SimWifiP2pService;
import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocket;
import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.utl.ist.cmov.wifidirect.sockets.SimWifiP2pSocketServer;


public class ConnectionActivity extends Activity implements
        PeerListListener, GroupInfoListener {

    public static final String TAG = "WifiDirect";

    private SimWifiP2pManager mManager = null;
    private Channel mChannel = null;
    private Messenger mService = null;
    private boolean mBound = false;
    private SimWifiP2pSocketServer mSrvSocket = null;
    private ReceiveCommTask mComm = null;
    private SimWifiP2pSocket mCliSocket = null;
    private ConnectionActivity mConactivity = null;

    private List<SimWifiP2pDevice> mCliPeerList;
    private List<SimWifiP2pDevice> mCliNeighbourList;
    private List<String> peerList;
    private List<String> neighbourList;
    private ListView peerListView;
    private ListView neighbourListView;

    private boolean hasGameStarted;
    private boolean hasServerStarted;
    private boolean hasClientConnected;
    private boolean isServer;
    private List<SimWifiP2pSocket> serverSocketList;
    private List<SimWifiP2pDevice> serverDeviceList;
    private SimWifiP2pSocket clientSocket;
    private SimWifiP2pDevice clientDevice;

    public SimWifiP2pManager getManager() {
        return mManager;
    }

    public Channel getChannel() {
        return mChannel;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        mCliNeighbourList = new ArrayList<SimWifiP2pDevice>();
        mCliPeerList = new ArrayList<SimWifiP2pDevice>();
        peerList = new ArrayList<String>();
        neighbourList = new ArrayList<String>();
        peerListView = (ListView) findViewById(R.id.peerview);
        neighbourListView = (ListView) findViewById(R.id.neighbourview);
        mConactivity = this;
        hasGameStarted = false;
        hasServerStarted = false;
        hasClientConnected = false;
        serverSocketList = new ArrayList<SimWifiP2pSocket>();
        serverDeviceList = new ArrayList<SimWifiP2pDevice>();

        // initialize the WDSim API
        SimWifiP2pSocketManager.Init(getApplicationContext());

        // register broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        SimWifiP2pBroadcastReceiver receiver = new SimWifiP2pBroadcastReceiver(this);
        registerReceiver(receiver, filter);

        Timer deviceMonitor = new Timer();
        deviceMonitor.schedule(new TimerTask() {

            @Override
            public void run() {
                mConactivity.runOnUiThread(new Thread() {
                    @Override
                    public void run() {
                        if (serverDeviceList.size() == 2) {
                            if (!hasGameStarted) {
                                hasGameStarted = true;
                                for (SimWifiP2pDevice device : serverDeviceList) {
                                    if ((isServer || !device.equals(clientDevice)) && !hasClientConnected) {
                                        Log.d("WIFI", "device = " + device.getVirtIp() + " IsServer = " + isServer);
                                        new OutgoingCommTask().execute(device.getVirtIp());
                                    }
                                }
                                Toast.makeText(mConactivity, "Start the game now",
                                        Toast.LENGTH_SHORT).show();


                            } else {
                                //   Toast.makeText(mConactivity, "Socket size ="+BroadCastManager.getclientSocketListSize()+" IsServer="+isServer,
                                //            Toast.LENGTH_SHORT).show();
                                if (serverSocketList.size() == 2) {
                                    if (isServer)
                                        BroadCastManager.BroadCastMessage("Server trigger start", new SimWifiP2pSocket());
                                }
                            }
                        }
                    }
                });

            }
        }, 1000, 1000);

        Intent intent = new Intent(ConnectionActivity.this, SimWifiP2pService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    public void PeersChange() {
        if (mBound) {
            mManager.requestPeers(mChannel, (PeerListListener) ConnectionActivity.this);
        } else {
            Toast.makeText(mConactivity, "Service not bound",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void NeighboursChange() {
        if (mBound) {
            mManager.requestGroupInfo(mChannel, (GroupInfoListener) ConnectionActivity.this);
        } else {
            Toast.makeText(mConactivity, "Service not bound",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.connection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mManager = new SimWifiP2pManager(mService);
            mChannel = mManager.initialize(getApplication(), getMainLooper(), null);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            mManager = null;
            mChannel = null;
            mBound = false;
        }
    };

    public class IncommingCommTask extends AsyncTask<Void, SimWifiP2pSocket, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Log.d(TAG, "IncommingCommTask started .");

            try {
                mSrvSocket = new SimWifiP2pSocketServer(10001);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    SimWifiP2pSocket sock = mSrvSocket.accept();
                    publishProgress(sock);

                } catch (IOException e) {
                    Log.d("Error accepting socket:", e.getMessage());
                    break;
                    //e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(SimWifiP2pSocket... values) {
            //  mCliSocket = values[0];
            Log.d(TAG, "IncommingCommTask success .");
            serverSocketList.add(values[0]);
            new MessageProcessor(values[0]).start();

            //   mComm = new ReceiveCommTask();
            //   mComm.execute(mCliSocket);

        }
    }


    public class OutgoingCommTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            //mTextOutput.setText("Connecting...");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Log.d("WIFI", "Client before = " + params[0] + " isServer" + isServer);
                clientSocket = new SimWifiP2pSocket(params[0], 10001);
                Log.d("WIFI", "Client after = " + params[0] + " isServer" + isServer);
                hasClientConnected = true;

            } catch (UnknownHostException e) {
                Log.d("WIFI", "Unknown Host = " + params[0]);
                return "Unknown Host:" + e.getMessage();
            } catch (IOException e) {
                Log.d("WIFI", "IO error = " + params[0]);
                return "IO error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // mTextOutput.setText(result);
                // findViewById(R.id.idConnectButton).setEnabled(true);
                Log.d(TAG, "Result is null at OutgoingCommTask onPostExecute");
            } else {
                mComm = new ReceiveCommTask();
                mComm.execute(clientSocket);
                Toast.makeText(mConactivity, "Client connected now",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class ReceiveCommTask extends AsyncTask<SimWifiP2pSocket, String, Void> {
        SimWifiP2pSocket s;

        @Override
        protected Void doInBackground(SimWifiP2pSocket... params) {
            BufferedReader sockIn;
            String st;

            s = params[0];
            try {
                sockIn = new BufferedReader(new InputStreamReader(s.getInputStream()));

                while ((st = sockIn.readLine()) != null) {
                    Log.d("Message:", st);
                    publishProgress(st);

                }
            } catch (IOException e) {
                Log.d("Error reading socket:", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
        /*    mTextOutput.setText("");
            findViewById(R.id.idSendButton).setEnabled(true);
            findViewById(R.id.idDisconnectButton).setEnabled(true);
            findViewById(R.id.idConnectButton).setEnabled(false);
            mTextInput.setHint("");
            mTextInput.setText("");
        */
        }

        @Override
        protected void onProgressUpdate(String... values) {
            //  mTextOutput.append(values[0]+"\n");
            Toast.makeText(mConactivity, "Message-received = " + values[0],
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!s.isClosed()) {
                try {
                    s.close();
                } catch (Exception e) {
                    Log.d("Error closing socket:", e.getMessage());
                }
            }
            s = null;
     /*       if (mBound) {
                guiUpdateDisconnectedState();
            } else {
                guiUpdateInitState();
            } */
        }
    }

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        DisplayPeerInfoUpdates(peers);
    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList devices,
                                     SimWifiP2pInfo groupInfo) {
        DisplayGroupInfoUpdates(devices, groupInfo);
        ConnectionSetup(devices, groupInfo);
    }


    private void ConnectionSetup(SimWifiP2pDeviceList devices,
                                 SimWifiP2pInfo groupInfo) {
        SimWifiP2pDevice device;
        if (!hasGameStarted) {
            serverDeviceList.clear();

            if (groupInfo.askIsGO()) {
                Toast.makeText(mConactivity, "Server call",
                        Toast.LENGTH_SHORT).show();
                isServer = true;

                //Start Server
                if (!hasServerStarted) {
                    serverSocketList.clear();
                    BroadCastManager.ClearClientSocketList();
                    new IncommingCommTask().execute();
                    hasServerStarted = true;
                }
            }

            //Local Device
            device = devices.getByName(groupInfo.getDeviceName());
            clientDevice = device;
            serverDeviceList.add(device);

            //Neighbour Devices
            for (String deviceName : groupInfo.getDevicesInNetwork()) {
                device = devices.getByName(deviceName);
                serverDeviceList.add(device);
            }

        }
    }

    private void DisplayPeerInfoUpdates(SimWifiP2pDeviceList peers) {
        ArrayAdapter<String> adapror = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, peerList);
        peerListView.setAdapter(adapror);
        peerList.clear();
        mCliPeerList.clear();
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            String devstr = "" + device.deviceName + " (" + device.getVirtIp() + ")\n";
            if (!mCliPeerList.contains(device)) {
                mCliPeerList.add(device);
                peerList.add(devstr);
            }
            adapror.notifyDataSetChanged();
        }
    }


    private void DisplayGroupInfoUpdates(SimWifiP2pDeviceList devices, SimWifiP2pInfo groupInfo) {
        // compile list of network members
        ArrayAdapter<String> adapror = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, neighbourList);
        neighbourListView.setAdapter(adapror);
        mCliNeighbourList.clear();
        neighbourList.clear();
        String devstr;

        if (groupInfo.getDevicesInNetwork().size() > 0) {
            String localDeviceName = groupInfo.getDeviceName();
            SimWifiP2pDevice localDevice = devices.getByName(localDeviceName);
            devstr = "" + localDeviceName + " (" + ((localDevice == null) ? "??" : localDevice.getVirtIp()) + ")";
            if (groupInfo.askIsGO())
                devstr += ", GO\n";
            else
                devstr += ", Client\n";
            if (!mCliNeighbourList.contains(localDevice)) {
                mCliNeighbourList.add(localDevice);
                neighbourList.add(devstr);
            }
            adapror.notifyDataSetChanged();
        }
        for (String deviceName : groupInfo.getDevicesInNetwork()) {
            SimWifiP2pDevice device = devices.getByName(deviceName);
            devstr = "" + deviceName + " (" + ((device == null) ? "??" : device.getVirtIp()) + ")\n";
            if (!mCliNeighbourList.contains(device)) {
                mCliNeighbourList.add(device);
                neighbourList.add(devstr);
            }
            adapror.notifyDataSetChanged();
        }

    }

}
