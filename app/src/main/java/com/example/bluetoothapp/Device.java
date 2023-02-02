package com.example.bluetoothapp;

public class Device {
    private String name;
    private String address;
    private boolean isConnected;

    public Device(String name, String address, boolean isConnected) {
        this.name = name;
        this.address = address;
        this.isConnected = isConnected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnect(boolean isConnected) {
        this.isConnected = isConnected;
    }

//    public String getName() {
//        return android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;
//    }
}
