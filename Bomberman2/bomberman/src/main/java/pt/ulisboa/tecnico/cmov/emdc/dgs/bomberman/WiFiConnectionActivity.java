package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.*;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.wifidirect.WiFiDirectBroadcastReceiver;

public class WiFiConnectionActivity extends Activity implements
        PeerListListener, GroupInfoListener {

    WifiP2pManager manager;
    Channel channel;
    BroadcastReceiver receiver;
    IntentFilter filter = new IntentFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_connection);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);

        filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        WiFiDirectBroadcastReceiver receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, filter);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("Bomberman", "Found new peers, P2P_PEERS_CHANGED_ACTION is coming");
                    }
                    @Override
                    public void onFailure(int reasonCode) { }
                });
            }
        }, 200, 200);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {

    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
        for (WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()) {
            Toast.makeText(getApplicationContext(), "New device: " + device.deviceAddress, Toast.LENGTH_SHORT).show();
        }

    }
}
