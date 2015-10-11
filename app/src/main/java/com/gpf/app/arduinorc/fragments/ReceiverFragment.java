package com.gpf.app.arduinorc.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.gpf.app.arduinorc.R;
import com.gpf.app.arduinorc.adapters.InputRowAdapter;
import com.gpf.app.arduinorc.items.InputRow;
import com.gpf.app.arduinorc.services.BluetoothService;
import com.gpf.app.arduinorc.utils.Commander;

import java.util.ArrayList;
import java.util.List;


public class ReceiverFragment extends Fragment implements BluetoothService.BTListener, View.OnClickListener {

    private static final String TAG = "ReceiverFragment";
    private static final String INPUTS = "inputs";
    private InputRowAdapter adapter;
    private List<InputRow> inputRows;

    public static ReceiverFragment newInstance() {
        return new ReceiverFragment();
    }

    public ReceiverFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BluetoothService bluetoothService = BluetoothService.bluetoothService;
        if(bluetoothService !=null){
            bluetoothService.setListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receiver, container, false);
        ImageButton btn_refresh = (ImageButton) view.findViewById(R.id.btn_refresh);
        Button btn_settings = (Button) view.findViewById(R.id.btn_add_input);
        ImageButton btn_delete = (ImageButton) view.findViewById(R.id.btn_delete);
        btn_refresh.setOnClickListener(this);
        btn_settings.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.input_list);
        adapter = new InputRowAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        readInputs();
    }

    @Override
    public void onStop() {
        super.onStop();
        saveInputs();
    }

    @Override
    public void onStateChanged(int state) {

    }

    @Override
    public void onMsgReceived(byte[] data, int bytes) {
        final String msg = new String(data, 0, bytes);
        Log.d(TAG, "msg: " + msg);
        String[] receivedValues = msg.split(" ");
        setInputRowValues(receivedValues);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_refresh:
                String command = Commander.getInstance(getActivity()).getCommand(v);
                BluetoothService.write(command.getBytes());
                break;
            case R.id.btn_add_input:
                addDialog().show();
                break;
            case R.id.btn_delete:
                deleteDialog().show();
                break;
        }
    }

    public void setInputRowValues(String[] receivedValues){
        for(int i=0; i<inputRows.size() && i < receivedValues.length; i++){
            InputRow input = inputRows.get(i);
            for (String received : receivedValues){

                String[] receivedIDValue = received.split("-");
                if(receivedIDValue.length == 2) {
                    String receivedID = receivedIDValue[0];
                    String receivedValue = receivedIDValue[1];
                    if (receivedID.equals(input.getID())) {
                        input.setValue(receivedValue);
                    }
                }

            }
        }
        adapter.notifyDataSetChanged();
    }

    public AlertDialog addDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.add_input);
        alertDialog.setMessage(R.string.enter_input);

        final EditText inputID = new EditText(getActivity());
        inputID.setHint("ID");
        final EditText inputName = new EditText(getActivity());
        inputName.setHint("Name");

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        inputID.setLayoutParams(lp);
        inputName.setLayoutParams(lp);

        LinearLayout ll = new LinearLayout(getActivity());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(inputID);
        ll.addView(inputName);

        alertDialog.setView(ll);
        alertDialog.setIcon(R.drawable.ic_input);

        DialogInterface.OnClickListener listenerOk = new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputIDText = inputID.getText().toString();
                String inputNameText = inputName.getText().toString();
                if(inputIDText.length()>0 && inputName.length()>0){
                    InputRow inputRow = new InputRow(inputIDText, inputNameText, getString(R.string.no_value));
                    inputRows.add(inputRow);
                    adapter.setData(inputRows);
                }
            }
        };
        DialogInterface.OnClickListener listenerCancel = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        };
        alertDialog.setPositiveButton(R.string.btn_add, listenerOk);
        alertDialog.setNegativeButton(R.string.btn_cancel, listenerCancel);
        return alertDialog.create();
    }

    public AlertDialog deleteDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.delete_all_inputs);
        alertDialog.setMessage(R.string.confirm);
        alertDialog.setIcon(R.drawable.ic_delete_black);

        DialogInterface.OnClickListener listenerOk = new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                inputRows = new ArrayList<>();
                adapter.setData(inputRows);
                inputRows.add(new InputRow("1", "Input", getString(R.string.no_value)));
                saveInputs();
            }
        };
        DialogInterface.OnClickListener listenerCancel = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        };
        alertDialog.setPositiveButton(R.string.btn_delete, listenerOk);
        alertDialog.setNegativeButton(R.string.btn_cancel, listenerCancel);
        return alertDialog.create();
    }

    public void saveInputs(){
        String inputs = "";
        for (InputRow inputRow : inputRows) {
            inputs += inputRow.getID() + "-" + inputRow.getTitle() + "/";
        }
        if(!inputs.isEmpty()) {
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(INPUTS, inputs);
            editor.apply();
        }
    }

    public void readInputs(){
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String defaultInput = "1-Input/";
        String inputs = sharedPref.getString(INPUTS, defaultInput);
        setInputRows(inputs);
    }

    public void setInputRows(String inputs){
        inputRows = new ArrayList<>();
        for(String input : inputs.split("/")){
            String inputIDName[] = input.split("-");
            String inputID = inputIDName[0];
            String inputName = inputIDName[1];
            InputRow inputRow = new InputRow(inputID, inputName, getString(R.string.no_value));
            inputRows.add(inputRow);
        }
        adapter.setData(inputRows);
    }
}
