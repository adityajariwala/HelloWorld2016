package com.structure.p2psending;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mitch on 10/15/2016.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;
    private List peers = new ArrayList();
    private HashMap m = new HashMap<String, String>();
    private int loc;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (mManager != null) {
                mManager.requestPeers(mChannel, peerListListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            if (mManager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                // We are connected with the other device, request connection
                // info to find group owner IP

                mManager.requestConnectionInfo(mChannel, connectionInfoListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            // Out with the old, in with the new.
            peers.clear();
            peers.addAll(peerList.getDeviceList());
            getNameAndAddress();
            if (m.containsValue("c2:bd:d1:ca:cf:3c")){
                System.out.println("Connecting");
                connect();
            }
            for(int i = 0; i < peers.size(); i++){
                //System.out.println("---------------------------------------------------------------------------------------");
                //System.out.println(peers.get(i).toString());
            }

            // If an AdapterView is backed by this data, notify it
            // of the change.  For instance, if you have a ListView of available
            // peers, trigger an update.
            if (peers.size() == 0) {
                Log.d("peers", "No devices found");
                return;
            }

        }
    };

    private void getNameAndAddress(){
        String deviceInfo, deviceName, deviceAddress;
        for(int i = 0; i < peers.size(); i++){
            deviceInfo = peers.get(i).toString();
            deviceName = deviceInfo.substring(deviceInfo.indexOf("Device: ") + 7, deviceInfo.indexOf("dev")-2);
            //System.out.println(deviceName);
            deviceAddress = deviceInfo.substring(deviceInfo.indexOf("deviceAddress: ") + 15, deviceInfo.indexOf("primary")-2);
           // System.out.println(deviceAddress);
            m.put(deviceName,deviceAddress);
        }
        System.out.println(m.toString());
    }

    public void connect(){
// Picking the first device found on the network.
        WifiP2pDevice device = (WifiP2pDevice) peers.get(1);

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                System.out.println("Connection Succeed");
            }

            @Override
            public void onFailure(int reason) {
                System.out.println("Connection Failed");
            }
        });
    }
    private WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(final WifiP2pInfo info) {

            // InetAddress from WifiP2pInfo struct.
            InetAddress groupOwnerAddress = info.groupOwnerAddress.getHostAddress();

            // After the group negotiation, we can determine the group owner.
            if (info.groupFormed && info.isGroupOwner) {
                // Do whatever tasks are specific to the group owner.
                // One common case is creating a server thread and accepting
                // incoming connections.
            } else if (info.groupFormed) {
                // The other device acts as the client. In this case,
                // you'll want to create a client thread that connects to the group
                // owner.
            }
        }
    }

}
