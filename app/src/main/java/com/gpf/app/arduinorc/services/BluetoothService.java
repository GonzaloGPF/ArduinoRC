package com.gpf.app.arduinorc.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.UUID;

public class BluetoothService extends Service {

    public static final String TAG = "BluetoothService";
    public static final String BT_DEVICE = "bt_device";
    public static final String BT_STOP = "bt_stop";
    public static final String BT_STOP_VALUE = "bt_stop_value";
    public static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    public static final int STATE_NONE = 0; // we're doing nothing
    public static final int STATE_LISTEN = 1; // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3; // now connected to a remote device
    public static BluetoothService bluetoothService;

    public static final int MSG_STATE_CHANGE = 10;
    public static final int MSG_READ = 11;
    public static final int MSG_WRITE = 12;
    public static final int MSG_TOAST = 13;

    public static int mState = STATE_NONE;

    private BluetoothAdapter mBluetoothAdapter;
    private ConnectThread mConnectThread;
    private static ConnectedThread mConnectedThread;
    // public mInHangler mHandler = new mInHangler(this);
    //private static Handler mHandler = null;
    private final IBinder mIBinder = new LocalBinder();
    private Handler mHandler = null;

    public static String deviceName;
    public static BluetoothDevice device = null;
    public BTListener BTListener;

    @Override
    public void onCreate() {
        Log.d(TAG, "Service started");
        mHandler = new IncomingHandler(this);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "OnStart Command");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null && intent != null) {
            device = intent.getParcelableExtra(BT_DEVICE);
            deviceName = device.getName();
            String address = device.getAddress();
            if (address != null && address.length() > 0) {
                connectToDevice(address);
            } else {
                stopSelf();
                return 0;
            }
        }
        bluetoothService = this;
        return START_STICKY;
    }

//    public void setHandler(Handler handler) {
//        mHandler = handler;
//    }

    private synchronized void connectToDevice(String address) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    private void setState(int state) {
        mState = state;
        if (mHandler != null) {
            mHandler.obtainMessage(MSG_STATE_CHANGE, state, -1).sendToTarget();
        }
    }

    public synchronized void stop() {
        setState(STATE_NONE);
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
        stopSelf();
    }

    @Override
    public boolean stopService(Intent name) {
        setState(STATE_NONE);
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        mBluetoothAdapter.cancelDiscovery();
        return super.stopService(name);
    }

    private void connectionFailed() {
        stop();
        Message msg = mHandler.obtainMessage(MSG_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("Toast", "Connection failed");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    private void connectionLost() {
        stop();
        Message msg = mHandler.obtainMessage(MSG_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("Toast", "Connection lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    private static final Object obj = new Object();

    public static void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (obj) {
            if (mState != STATE_CONNECTED)
                return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    private synchronized void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();

        // Message msg =
        // mHandler.obtainMessage(AbstractActivity.MESSAGE_DEVICE_NAME);
        // Bundle bundle = new Bundle();
        // bundle.putString(AbstractActivity.DEVICE_NAME, "p25");
        // msg.setData(bundle);
        // mHandler.sendMessage(msg);
        setState(STATE_CONNECTED);
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            this.mmDevice = device;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID));
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        @Override
        public void run() {
            setName("ConnectThread");
            mBluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException e) {
                try {
                    mmSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                connectionFailed();
                return;

            }
            synchronized (this) {
                mConnectThread = null;
            }
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (!Thread.interrupted()) {
                try {
                    if(mmInStream.available()>=4) {
                        bytes = mmInStream.read(buffer);
                        mHandler.obtainMessage(MSG_READ, bytes, -1, buffer).sendToTarget();
                        sleep(500);
                        buffer = new byte[1024];
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    setState(STATE_NONE);
                    connectionLost();
                    interrupt();
                    //stop();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(MSG_WRITE, buffer.length, -1, buffer).sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    public void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        stop();
        Log.d(TAG, "Destroyed");
        if(mHandler != null) {
            mHandler = null;
        }
        super.onDestroy();
    }

    private void sendMsg(int flag) {
        Message msg = new Message();
        msg.what = flag;
        mHandler.sendMessage(msg);
    }

    @Override
    public IBinder onBind(Intent intent) {
        //mHandler = ((MainActivity) getApplication()).getHandler();
        return mIBinder;
    }

    public class LocalBinder extends Binder {
        public BluetoothService getInstance(){
            return BluetoothService.this;
        }
    }
    static class IncomingHandler extends Handler {
        private final WeakReference<BluetoothService> mService;

        IncomingHandler(BluetoothService service) {
            mService = new WeakReference<>(service);
        }
        @Override
        public void handleMessage(Message msg) {
            BluetoothService service = mService.get();
            if (service != null) {
                super.handleMessage(msg);
                service.handleMessage(msg);
            }
        }
    }

    private void handleMessage(Message msg){
        if (!Thread.currentThread().isInterrupted()) {
            switch (msg.what) {
                case MSG_STATE_CHANGE:
                    Log.d(TAG, "State changed: "+msg.arg1);
                    if(BTListener != null){
                        BTListener.onStateChanged(mState);
                    }
                    break;
                case MSG_READ:
                    if(BTListener != null){
                        BTListener.onMsgReceived((byte[])msg.obj, msg.arg1);
                    }
                    break;
                case MSG_WRITE:
                    Log.d(TAG, "Write");
                    break;
                case MSG_TOAST:
                    Log.d(TAG, "Toast");
                    break;
            }
        }
    }

    public void setListener(BTListener bt_listener){
        BTListener = bt_listener;
    }

    public interface BTListener{
        void onStateChanged(int state);
        void onMsgReceived(byte[] data, int bytes);
    }
}