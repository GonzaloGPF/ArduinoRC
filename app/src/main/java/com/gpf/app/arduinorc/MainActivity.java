package com.gpf.app.arduinorc;

import android.app.AlertDialog;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gpf.app.arduinorc.adapters.BluetoothDeviceAdapter;
import com.gpf.app.arduinorc.fragments.BluetoothFragment;
import com.gpf.app.arduinorc.fragments.ControllerFragment;
import com.gpf.app.arduinorc.fragments.NavigationFragment;
import com.zerokol.views.JoystickView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements BluetoothFragment.OnBluetoothInteractionListener, BluetoothDeviceAdapter.ClickListener, ControllerFragment.OnBControllerInteractionListener {
    private static final String TAG = "com.gpf.app.arduinorc";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_DISCOVERABLE = 2;
    private Toolbar toolbar;
    private NavigationFragment navigationFragment;
    private BluetoothSocket clientSocket;
    private ArrayList<BluetoothDevice> arrayDevices = new ArrayList<>();
    private BluetoothAdapter bAdapter;
    private BluetoothFragment bluetoothFragment;
    private final BroadcastReceiver bReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                final int estado = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (estado){
                    case BluetoothAdapter.STATE_OFF:
                        Log.v(TAG, "onReceive: Shutting Down");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "onReceive: Shutting On");
                        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
                        startActivity(discoverableIntent);
                        break;
                    default:
                        break;
                }
            }else if(action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceDesc = device.getName() + " [" + device.getAddress() + "]";
                Toast.makeText(getBaseContext(), "Device Detected" + ": " + deviceDesc, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "ACTION_FOUND: Device detected " + deviceDesc);
                addDevice(device);
                bluetoothFragment.setArrayDevices(arrayDevices);
            }
            if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
                Toast.makeText(getBaseContext(), "Discovery Finished", Toast.LENGTH_SHORT).show();
                bluetoothFragment.setArrayDevices(arrayDevices);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationFragment = (NavigationFragment) getFragmentManager().findFragmentById(R.id.navigationDrawer);
        navigationFragment.setUp(drawerLayout, toolbar);

        bAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bAdapter==null){
            Log.d(TAG,"This device has not Bluetooth");
        }

        if(savedInstanceState==null) {
            bluetoothFragment = BluetoothFragment.newInstance(getBluetoothState());
            getFragmentManager().beginTransaction().add(R.id.fragmentContainer, bluetoothFragment, getString(R.string.bt_fragment)).commit();
            toolbar.setTitle(getString(R.string.bt_fragment));
        }
        registrarEventosBluetooth();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.fragmentContainer);
        toolbar.setTitle(currentFragment.getTag());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    @Override
    public void onBluetoothInteraction(int btn_id) {
        switch (btn_id) {
            case R.id.btn_bluetooth:
                if(bAdapter.isEnabled()) {
                    bAdapter.disable();
                    bluetoothFragment.refreshButtons(getBluetoothState());
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                break;
            case R.id.btn_search:
                arrayDevices.clear();
                if (bAdapter.isDiscovering()) {
                    bAdapter.cancelDiscovery();
                }
                if (bAdapter.startDiscovery()) {
                    Toast.makeText(this, "Discovering", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Discovering Error", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_bonded:
                arrayDevices = new ArrayList<>(bAdapter.getBondedDevices());
                bluetoothFragment.setArrayDevices(arrayDevices);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(bReceiver);
    }

    private void registrarEventosBluetooth() {
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bReceiver, filter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "Bluetooth: the user accepts");
                bluetoothFragment.refreshButtons(getBluetoothState());
            } else {
                Log.d(TAG, "Bluetooth: the user does not accept");
            }
        }
        if (requestCode == REQUEST_DISCOVERABLE){
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "Bluetooth: discovery finished");
            } else {
                Log.d(TAG, "Bluetooth: discovery error");
            }
        }
    }

    private void addDevice(BluetoothDevice device){
        arrayDevices.add(device);
    }

    @Override
    public void deviceClick(View view, int position) {
        BluetoothDevice device = arrayDevices.get(position);
        connectDialog("Connection", "Do you want connect with " + device.getName() + " ?", device.getAddress()).show();
    }

    private AlertDialog connectDialog(String title, String msg, final String address) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(msg);

        DialogInterface.OnClickListener listenerOk = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                connectDevice(address);
            }
        };
        DialogInterface.OnClickListener listenerCancel = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        };
        alertDialogBuilder.setPositiveButton(R.string.connect, listenerOk);
        alertDialogBuilder.setNegativeButton(R.string.cancel, listenerCancel);

        return alertDialogBuilder.create();
    }

    public void connectDevice(String address) {
        Toast.makeText(this, "Connecting to " + address, Toast.LENGTH_LONG).show();
        BluetoothDevice remoteDevice = bAdapter.getRemoteDevice(address);
        try {
            String mmUUID = "00001101-0000-1000-8000-00805F9B34FB";
            clientSocket = remoteDevice.createRfcommSocketToServiceRecord(UUID.fromString(mmUUID));
            clientSocket.connect();
        } catch (Exception e) {
            Log.d(TAG,e.getMessage());
        }
    }

    @Override
    public void onControllerInteraction(int direction) {
        Log.d(TAG, "Direction: "+direction);
        if(clientSocket != null && clientSocket.isConnected()){
            switch (direction) {
                case JoystickView.FRONT:
                    sendData("1");
                    break;
                case JoystickView.FRONT_RIGHT:
                    break;
                case JoystickView.RIGHT:
                    break;
                case JoystickView.RIGHT_BOTTOM:
                    break;
                case JoystickView.BOTTOM:
                    break;
                case JoystickView.BOTTOM_LEFT:
                    break;
                case JoystickView.LEFT:
                    break;
                case JoystickView.LEFT_FRONT:
                    break;
                default:
            }
        }
    }

    private void sendData(String data){
        OutputStream mmOutStream;
        try {
            if (clientSocket != null && clientSocket.isConnected()){
                mmOutStream = clientSocket.getOutputStream();
                mmOutStream.write(data.getBytes());
                Log.d(TAG, "Dato Enviado");
            }else{
                Toast.makeText(getApplicationContext(), "Sin Conexion", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.d(TAG,e.getMessage());
        }
    }

    public Boolean getBluetoothState(){
        return bAdapter.isEnabled();
    }
}
