package com.example.bluetoothapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothService {
    public static int REQUEST_BLUETOOTH = 1;
    private static BluetoothService bluetoothServiceInstance;

    public static BluetoothService getInstance() {
        if (bluetoothServiceInstance == null) {
            bluetoothServiceInstance = new BluetoothService();
        }
        return bluetoothServiceInstance;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    public boolean isEnableBluetooth() {
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    public ArrayList<Device> getAvailableDevices() {
        ArrayList<Device> devices = new ArrayList<>();

        return devices;
    }

    @SuppressLint("MissingPermission")
    public ArrayList<Device> getPairedDevices() {
        ArrayList<Device> devices = new ArrayList<>();

        if (null != getBluetoothAdapter()) {
            Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();

            if (null != pairedDevices && pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    @SuppressLint("MissingPermission") String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    Device newDevice = new Device(deviceName, deviceHardwareAddress,false);
                    devices.add(newDevice);
                }
            }
        }

        return devices;
    }
}
