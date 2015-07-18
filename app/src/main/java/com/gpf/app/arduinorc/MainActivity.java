package com.gpf.app.arduinorc;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gpf.app.arduinorc.fragments.BluetoothFragment;
import com.gpf.app.arduinorc.fragments.NavigationFragment;
import com.gpf.app.arduinorc.services.BluetoothService;

public class MainActivity extends AppCompatActivity implements BluetoothFragment.OnBluetoothInteractionListener, BluetoothService.BTListener, NavigationFragment.NavigationListener {
    private static final String TAG = "com.gpf.app.arduinorc";
    private static final String BT_STATE = "bt_state";

    private Toolbar toolbar;

    private NavigationFragment navigationFragment;
    private BluetoothSocket clientSocket;
    private BluetoothService bluetoothService;
    private boolean isBound;
    private String bluetoothState;
    private String currentFragmentName;
    private MainActivity self;

    private BluetoothFragment bluetoothFragment;
    private final BroadcastReceiver bReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state){
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
                BluetoothFragment bluetoothFragment = (BluetoothFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.bluetooth_fragment));
                bluetoothFragment.addDevice(device);
                //addDevice(device);
            }
            if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
                Toast.makeText(getBaseContext(), "Discovery Finished", Toast.LENGTH_SHORT).show();
                showProgressBar(false);
//                BluetoothFragment bluetoothFragment = (BluetoothFragment) getFragmentManager().findFragmentByTag(getString(R.string.bluetooth_fragment));
//                bluetoothFragment.setDevices();
            }
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bluetoothService = ((BluetoothService.LocalBinder)service).getInstance();
            bluetoothService.setListener(self);
            //bluetoothService.setHandler(handler);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bluetoothService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        setContentView(R.layout.activity_main);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationFragment = (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.navigationDrawer);
        navigationFragment.setUp(drawerLayout, toolbar);
        navigationFragment.setListener(this);

        if(savedInstanceState==null){
            bluetoothFragment = BluetoothFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, bluetoothFragment, getString(R.string.bluetooth_fragment)).commit();
        }else{
            bluetoothState = savedInstanceState.getString(BT_STATE);
        }
        setToolbarTitle();
        registerBluetoothEvents();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BT_STATE, bluetoothState);
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
//        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
//        toolbar.setTitle(currentFragment.getTag());
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
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bReceiver);
        unBoundBluetoothService();
    }

    private void registerBluetoothEvents() {
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bReceiver, filter);
    }

    @Override
    public void onDeviceClick(BluetoothDevice device) {
        connectDialog("Connection", "Do you want connect with " + device.getName() + " ?", device).show();
    }

    private AlertDialog connectDialog(String title, String msg, final BluetoothDevice device) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(msg);

        DialogInterface.OnClickListener listenerOk = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplication(), BluetoothService.class);
                intent.putExtra(BluetoothService.BT_DEVICE, device);
                startService(intent);
                bindBluetoothService(intent);
                //connectDevice(device.address);
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

    private void bindBluetoothService(Intent intent) {
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        isBound = true;
    }
    private void unBoundBluetoothService(){
        if(isBound){
            unbindService(mConnection);
            isBound = false;
        }
    }
    public void showProgressBar(boolean visibility){
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_spinner);
        if(visibility){
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStateChanged(int state) {
        switch (state){
            case BluetoothService.STATE_NONE:
                bluetoothState = getString(R.string.not_connected);
                break;
            case BluetoothService.STATE_CONNECTING:
                bluetoothState = getString(R.string.connecting);
                break;
            case BluetoothService.STATE_CONNECTED:
                bluetoothState = getString(R.string.connected);
                break;
            case BluetoothService.STATE_LISTEN:
                break;
        }
        setToolbarTitle();
    }

    @Override
    public void onNavigationChanged() {
        setToolbarTitle();
    }

    private void setToolbarTitle(){
        if(bluetoothState==null) {
            bluetoothState = getString(R.string.not_connected);
        }
        currentFragmentName = navigationFragment.getCurrentFragmentName();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toolbar.setTitle(currentFragmentName +" "+ bluetoothState);
            }
        });
    }
}
