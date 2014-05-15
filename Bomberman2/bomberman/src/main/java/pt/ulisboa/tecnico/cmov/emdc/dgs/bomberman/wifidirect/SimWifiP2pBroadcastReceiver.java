package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.wifidirect;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.ConnectionActivity;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pBroadcast;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pInfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class SimWifiP2pBroadcastReceiver extends BroadcastReceiver {

    private ConnectionActivity mActivity;

    public SimWifiP2pBroadcastReceiver(ConnectionActivity activity) {
        super();
        this.mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

        	// This action is triggered when the WDSim service changes state:
        	// - creating the service generates the WIFI_P2P_STATE_ENABLED event
        	// - destroying the service generates the WIFI_P2P_STATE_DISABLED event
            Log.d("WIFID","HIT HERE");
            int state = intent.getIntExtra(SimWifiP2pBroadcast.EXTRA_WIFI_STATE, -1);
            if (state == SimWifiP2pBroadcast.WIFI_P2P_STATE_ENABLED) {
        		Toast.makeText(mActivity, "WiFi Direct enabled",
        				Toast.LENGTH_SHORT).show();
            } else {
        		Toast.makeText(mActivity, "WiFi Direct disabled",
        				Toast.LENGTH_SHORT).show();
            }

        } else if (SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // Request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            Log.d("WIFID","HIT PEER CHANGE");
            mActivity.PeersChange();
        	Toast.makeText(mActivity, "Peer list changed",
    				Toast.LENGTH_SHORT).show();

        } else if (SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION.equals(action)) {

        	SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
        			SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
        	ginfo.print();
            mActivity.NeighboursChange();
    		Toast.makeText(mActivity, "Network membership changed",
    				Toast.LENGTH_SHORT).show();

        } else if (SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION.equals(action)) {

        	SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
        			SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
        	ginfo.print();
            mActivity.NeighboursChange();
    		Toast.makeText(mActivity, "Group ownership changed",
    				Toast.LENGTH_SHORT).show();
        }
    }
}
