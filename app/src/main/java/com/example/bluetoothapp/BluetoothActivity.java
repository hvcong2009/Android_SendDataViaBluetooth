package com.example.bluetoothapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

public class BluetoothActivity extends AppCompatActivity {

    private String TAG = BluetoothActivity.class.getSimpleName();
    private ArrayList<Device> pairedDevice = new ArrayList<>();
    private ArrayList<Device> availableDevice = new ArrayList<>();
    private BluetoothDeviceAdapter pairedDeviceAdapter = new BluetoothDeviceAdapter();
    private BluetoothDeviceAdapter availableDeviceAdapter = new BluetoothDeviceAdapter();
    private RecyclerView pairedBluetoothRecyclerView;
    private RecyclerView availableBluetoothRecyclerView;
    private SwitchCompat enableBluetoothSwitch;
    private TextView btnScan;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        enableBluetoothSwitch = findViewById(R.id.enable_bluetooth_switch);
        pairedBluetoothRecyclerView = findViewById(R.id.recycler_view_paired_device);
        availableBluetoothRecyclerView = findViewById(R.id.recycler_view_available_device);
        btnScan = findViewById(R.id.btn_scan);

        pairedBluetoothRecyclerView.setAdapter(pairedDeviceAdapter);
        availableBluetoothRecyclerView.setAdapter(availableDeviceAdapter);

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            enableBluetoothSwitch.setChecked(true);
            getPairedDevice();
            getAvailableDevice();
            scanDevice();
        }

        enableBluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    mBluetoothAdapter.enable();
                } else {
                    mBluetoothAdapter.disable();
                }
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                scanDevice();
//                getAvailableDevice();
            }
        });
    }

    private void scanDevice() {
        mBluetoothAdapter.startDiscovery();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getPairedDevice() {
        pairedDevice = BluetoothService.getInstance().getPairedDevices();
        pairedBluetoothRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        pairedDeviceAdapter.setData(pairedDevice);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getAvailableDevice() {
        availableDevice = BluetoothService.getInstance().getAvailableDevices();
        availableBluetoothRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        availableDeviceAdapter.setData(availableDevice);
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Device newDevice = new Device(deviceName, deviceHardwareAddress, false);
                availableDevice.add(newDevice);// MAC address
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }
}