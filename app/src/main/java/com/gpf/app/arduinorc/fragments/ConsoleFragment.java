package com.gpf.app.arduinorc.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gpf.app.arduinorc.R;
import com.gpf.app.arduinorc.adapters.MessageAdapter;
import com.gpf.app.arduinorc.items.ConsoleMessage;
import com.gpf.app.arduinorc.services.BluetoothService;

import java.util.ArrayList;

public class ConsoleFragment extends Fragment implements BluetoothService.BTListener {

    private static final String TAG = "ConsoleFragment";
    private static final String MESSAGES = "messages";
    private MessageAdapter adapter;
    private EditText consoleInput;
    private ArrayList<ConsoleMessage> messages = new ArrayList<>();
    private StringBuffer mOutStringBuffer;

    public static ConsoleFragment newInstance() {
        return new ConsoleFragment();
    }

    public ConsoleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BluetoothService bluetoothService = BluetoothService.bluetoothService;
        if(bluetoothService !=null){
            bluetoothService.setListener(this);
        }
        if(savedInstanceState != null){
            messages = savedInstanceState.getParcelableArrayList(MESSAGES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_console, container, false);
        mOutStringBuffer = new StringBuffer("");
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.console_conversation);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MessageAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        consoleInput = (EditText) view.findViewById(R.id.console_editText);
        ImageButton sendButton = (ImageButton) view.findViewById(R.id.console_send);

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                View view = getView();
                if (null != view) {
                    TextView textView = (TextView) view.findViewById(R.id.console_editText);
                    String message = textView.getText().toString();
                    sendMessage(message);
                }
            }
        });
        adapter.setMessages(messages);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MESSAGES, messages);
    }

    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (BluetoothService.mState != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            //return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            BluetoothService.write(message.getBytes());
            messages.add(new ConsoleMessage(message, MessageAdapter.ARROW_RIGHT));
            adapter.setMessages(messages);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            consoleInput.setText(mOutStringBuffer);
        }
    }

    @Override
    public void onStateChanged(int state) {

    }

    @Override
    public void onMsgReceived(byte[] data, int bytes) {
        String readMessage = new String(data, 0, bytes);
        Log.d(TAG, "msg: " + readMessage);
        messages.add(new ConsoleMessage(readMessage.trim(), MessageAdapter.ARROW_LEFT));
        adapter.setMessages(messages);
    }
}
