package com.example.android.bluetoothchat;

import java.util.ArrayList;

/**
 * Created by mitch on 10/16/2016.
 */
public class Device {
    private ArrayList<String> conversation;
    private String deviceName;
    private String connectedDeviceName;

    public Device (String name, String connectedDevice){
        deviceName = name;
        connectedDeviceName = connectedDevice;
        conversation = new ArrayList<>();
    }
    public Device(ArrayList<String> a, String name, String connectedDevice){
        this(name, connectedDevice);
        for(int i = 0; i < a.size(); i++){
            conversation.add(a.get(i));
        }
    }

    public void addConversation(String message){
        conversation.add(message);
    }

    public ArrayList<String> getConversation() {
        return conversation;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getConnectedDeviceName() {
        return connectedDeviceName;
    }
}
