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
import android.os.Parcelable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;

public class BluetoothActivity extends AppCompatActivity {

    //    private String TAG = BluetoothActivity.class.getSimpleName();
    private ArrayList<BluetoothDevice> pairedDevice;
    private ArrayList<BluetoothDevice> availableDevice;
    private BluetoothDeviceAdapter pairedDeviceAdapter;
    private BluetoothDeviceAdapter availableDeviceAdapter;
    private RecyclerView pairedBluetoothRecyclerView;
    private RecyclerView availableBluetoothRecyclerView;
    private SwitchCompat enableBluetoothSwitch;
    private TextView btnScan;
    private TextView btnSendData;
    private TextView scanStatus;
    private LinearLayout deviceLayout;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        // Init
        enableBluetoothSwitch = findViewById(R.id.enable_bluetooth_switch);
        pairedBluetoothRecyclerView = findViewById(R.id.recycler_view_paired_device);
        availableBluetoothRecyclerView = findViewById(R.id.recycler_view_available_device);
        btnScan = findViewById(R.id.btn_scan);
        scanStatus = findViewById(R.id.scan_status);
        deviceLayout = findViewById(R.id.layout_device);
        // Visible when connected a device
        btnSendData = findViewById(R.id.btn_send_data);


        pairedDevice = new ArrayList<>();
        pairedDeviceAdapter = new BluetoothDeviceAdapter();
        availableDeviceAdapter = new BluetoothDeviceAdapter();
        availableDevice = new ArrayList<>();

        pairedBluetoothRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        availableBluetoothRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        pairedBluetoothRecyclerView.setAdapter(pairedDeviceAdapter);
        availableBluetoothRecyclerView.setAdapter(availableDeviceAdapter);

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver, filter);

        // Set up layout if bluetooth is enabling
        if (mBluetoothAdapter.isEnabled()) {
            deviceLayout.setVisibility(View.VISIBLE);
            enableBluetoothSwitch.setChecked(true);
            btnScan.setEnabled(true);
            getPairedDevice();
        }

        enableBluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b && !mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.enable();
                    setUpLayoutBluetoothOn();

                } else if (!b && mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.disable();
                    setUpLayoutBluetoothOff();
                    clearScanDevice();
                }
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearScanDevice();
                mBluetoothAdapter.startDiscovery();
                scanStatus.setVisibility(View.VISIBLE);
                btnScan.setEnabled(false);
            }
        });

        btnSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DataActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpLayoutBluetoothOn() {
        deviceLayout.setVisibility(View.VISIBLE);
        btnScan.setEnabled(true);
        getPairedDevice();
    }

    private void setUpLayoutBluetoothOff() {
        deviceLayout.setVisibility(View.GONE);
        scanStatus.setVisibility(View.GONE);
        btnScan.setEnabled(false);
    }

    // Cancel scan and clead found devices
    private void clearScanDevice() {
        mBluetoothAdapter.cancelDiscovery();
        availableDevice.clear();
        availableDeviceAdapter.setData(availableDevice);
    }

    // Get paried devices
    private void getPairedDevice() {
        pairedDevice = BluetoothService.getInstance().getPairedDevices();
        if (pairedDevice.size() != 0) {
            pairedDeviceAdapter.setData(pairedDevice);
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // Scanning
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                scanStatus.setVisibility(View.VISIBLE);
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Device newDevice = new Device(deviceName, deviceHardwareAddress, false);
                availableDevice.add(device);// MAC address
                btnScan.setEnabled(false);
                availableDeviceAdapter.setData(availableDevice);
            }

            // Scan finished
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                scanStatus.setVisibility(View.GONE);
                btnScan.setEnabled(true);
            }

            // Detect bluetooth state
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        enableBluetoothSwitch.setChecked(false);
                        setUpLayoutBluetoothOff();
                        clearScanDevice();
                        break;
                    case BluetoothAdapter.STATE_ON:
                    case BluetoothAdapter.STATE_TURNING_ON:
                        enableBluetoothSwitch.setChecked(true);
                        setUpLayoutBluetoothOn();
                        break;
                }

            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        mBluetoothAdapter.cancelDiscovery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }
}