package com.example.bluetoothapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ActivityResultLauncher<Intent> mActivityResultLauncher;

    public ArrayList<Device> mAvailableDevices = new ArrayList<>();
    private ArrayList<Device> mPairedDevices = new ArrayList<>();

    private Device mDeviceSelected = null;

    ProgressDialog mProgressDialog = null;

    // Create a BroadcastReceiver for ACTION_FOUND Bluetooth Available.
    private final BroadcastReceiver receiverBluetoothAvailableDevices = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                @SuppressLint("MissingPermission") String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Device newDevice = new Device(deviceName, deviceHardwareAddress, false);
                mAvailableDevices.add(newDevice);
            }
        }
    };

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        Log.d(TAG, TAG + " - onCreate");
//
//        mProgressDialog = new ProgressDialog(MainActivity.this);
//
//        TextView txtDeviceConnect = findViewById(R.id.txt_deviceConnect);
//
//        // Load data for Spinner PairedDevices
//        mPairedDevices = BluetoothService.getInstance().getPairedDevices();
//        ArrayList<String> PairedDevicesName = new ArrayList<>();
//        for (Device device : mPairedDevices) {
//            PairedDevicesName.add(device.getName());
//        }
//        Spinner spinnerPairedDevice = (Spinner) findViewById(R.id.spn_pairedDeviceList);
//        ArrayAdapter<String> arrayAdapterPairedDevices = new ArrayAdapter(this, android.R.layout.simple_list_item_1, PairedDevicesName);
//        arrayAdapterPairedDevices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerPairedDevice.setAdapter(arrayAdapterPairedDevices);
//        spinnerPairedDevice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                mDeviceSelected = new Device(mPairedDevices.get(position).getName(), mPairedDevices.get(position).getAddress(), false);
//                txtDeviceConnect.setText(mPairedDevices.get(position).getName());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                mDeviceSelected = new Device(mPairedDevices.get(0).getName(), mPairedDevices.get(0).getAddress(), false);
//                txtDeviceConnect.setText(mPairedDevices.get(0).getName());
//            }
//        });
//
//        // Load data for Spinner AvailableDevices
//        Spinner spinnerAvailableDevice = (Spinner) findViewById(R.id.spn_availableDeviceList);
//        ArrayAdapter<String> arrayAdapterAvailableDevices = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mAvailableDevices);
//        arrayAdapterAvailableDevices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerAvailableDevice.setAdapter(arrayAdapterAvailableDevices);
//        spinnerAvailableDevice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
////                mDeviceSelected = new Device(getAvailableDevices().get(position).getName(), getAvailableDevices().get(position).getAddress(), false);
////                txtDeviceConnect.setText(getAvailableDevices().get(position).getName());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//
//        Button btnScanDevice = findViewById(R.id.btn_scan);
//        btnScanDevice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mProgressDialog.setMessage("Loading ...");
//                mProgressDialog.show();
//
//                // Load data for Spinner AvailableDevices
//                mAvailableDevices = BluetoothService.getInstance().getAvailableDevices();
//                arrayAdapterAvailableDevices.notifyDataSetChanged();
//
//
//                mProgressDialog.dismiss();
//            }
//        });
//
//        Button btnSendData = findViewById(R.id.btn_sendData);
//        btnSendData.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d(TAG, TAG + " - onClick");
//
//                        unregisterReceiver(receiverBluetoothAvailableDevices);
//
//                        sendDataViaBluetooth();
//                    }
//                });
//            }
//        });
//
//        mActivityResultLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        if (result.getResultCode() == Activity.RESULT_OK) {
//                            Log.d(TAG, TAG + " - enable Bluetooth success");
//                        } else {
//                            Log.d(TAG, TAG + " - enable Bluetooth fail");
//                        }
//                    }
//                }
//        );
//
//        // Register for broadcasts when a device is discovered.
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(receiverBluetoothAvailableDevices, filter);
//    }


//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        unregisterReceiver(receiverBluetoothAvailableDevices);
//    }

    private void sendDataViaBluetooth() {
        // Check device support bluetooth or not
        // If not support bluetooth -> display dialog and exit app
        if (BluetoothService.getInstance().getBluetoothAdapter() == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        if (!BluetoothService.getInstance().isEnableBluetooth()) {
            Log.d(TAG, TAG + " - ACTION_REQUEST_ENABLE");
            mActivityResultLauncher.launch(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
        }
    }
}