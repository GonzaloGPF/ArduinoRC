package com.gpf.app.arduinorc.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gpf.app.arduinorc.R;
import com.gpf.app.arduinorc.utils.Commander;

import java.util.ArrayList;
import java.util.List;

public class ProgrammerFragment extends Fragment implements View.OnClickListener {

    private List<TextView> textViews = new ArrayList<>();

    public static ProgrammerFragment newInstance() {
        return new ProgrammerFragment();
    }

    public ProgrammerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_programmer, container, false);
        referenceTextViews(view);
        setClickListeners();
        setTextViewsValues();
        return view;
    }

    private void referenceTextViews(View v){
        TextView arrow_up = (TextView) v.findViewById(R.id.arrow_up);
        TextView arrow_left = (TextView) v.findViewById(R.id.arrow_left);
        TextView arrow_right = (TextView) v.findViewById(R.id.arrow_right);
        TextView arrow_down = (TextView) v.findViewById(R.id.arrow_down);
        TextView arrow_right_down = (TextView) v.findViewById(R.id.arrow_right_down);
        TextView arrow_down_left = (TextView) v.findViewById(R.id.arrow_down_left);
        TextView arrow_up_left = (TextView) v.findViewById(R.id.arrow_up_left);
        TextView arrow_up_right = (TextView) v.findViewById(R.id.arrow_up_right);
        TextView btn_stop = (TextView) v.findViewById(R.id.btn_stop);
        TextView btn_a = (TextView) v.findViewById(R.id.btn_a);
        TextView btn_b = (TextView) v.findViewById(R.id.btn_b);
        TextView btn_x = (TextView) v.findViewById(R.id.btn_x);
        TextView btn_y = (TextView) v.findViewById(R.id.btn_y);
        TextView btn_plus = (TextView) v.findViewById(R.id.btn_plus);
        TextView btn_minus = (TextView) v.findViewById(R.id.btn_minus);
        TextView btn_on = (TextView) v.findViewById(R.id.btn_on);
        TextView btn_off = (TextView) v.findViewById(R.id.btn_off);
        TextView btn_refresh = (TextView) v.findViewById(R.id.btn_refresh);
        TextView speed_0 = (TextView) v.findViewById(R.id.speed_0);
        TextView speed_1 = (TextView) v.findViewById(R.id.speed_1);
        TextView speed_2 = (TextView) v.findViewById(R.id.speed_2);
        TextView speed_3 = (TextView) v.findViewById(R.id.speed_3);
        TextView speed_4 = (TextView) v.findViewById(R.id.speed_4);
        TextView speed_5 = (TextView) v.findViewById(R.id.speed_5);
        TextView speed_6 = (TextView) v.findViewById(R.id.speed_6);
        TextView speed_7 = (TextView) v.findViewById(R.id.speed_7);
        TextView speed_8 = (TextView) v.findViewById(R.id.speed_8);
        TextView speed_9 = (TextView) v.findViewById(R.id.speed_9);
        textViews.add(arrow_right);
        textViews.add(arrow_right_down);
        textViews.add(arrow_left);
        textViews.add(arrow_down);
        textViews.add(arrow_down_left);
        textViews.add(arrow_up_left);
        textViews.add(arrow_up_right);
        textViews.add(arrow_up);
        textViews.add(btn_stop);
        textViews.add(btn_a);
        textViews.add(btn_b);
        textViews.add(btn_x);
        textViews.add(btn_y);
        textViews.add(btn_plus);
        textViews.add(btn_minus);
        textViews.add(btn_on);
        textViews.add(btn_off);
        textViews.add(btn_refresh);
        textViews.add(speed_0);
        textViews.add(speed_1);
        textViews.add(speed_2);
        textViews.add(speed_3);
        textViews.add(speed_4);
        textViews.add(speed_5);
        textViews.add(speed_6);
        textViews.add(speed_7);
        textViews.add(speed_8);
        textViews.add(speed_9);
    }

    private void setClickListeners(){
        for(TextView textView : textViews){
            textView.setOnClickListener(this);
        }
    }

    private void setTextViewsValues(){
        for(TextView textView : textViews){
            textView.setText(Commander.getInstance(getActivity()).getCommandValue(textView));
        }
    }

    @Override
    public void onClick(View v) {
        createInputDialog(v).show();
    }

    public AlertDialog createInputDialog(View view){
        final TextView textView = (TextView) view;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Command Modification");
        alertDialog.setMessage("Enter a Value");

        final EditText input = new EditText(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.ic_edit);

        DialogInterface.OnClickListener listenerOk = new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputText = input.getText().toString();
                if(inputText.length()>0){
                    inputText = inputText.replace("/","-");
                    textView.setText(inputText.trim());
                    Commander.getInstance(getActivity()).setCommand(textView);
                }
            }
        };
        DialogInterface.OnClickListener listenerCancel = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        };
        alertDialog.setPositiveButton("Ok", listenerOk);
        alertDialog.setNegativeButton("Cancel", listenerCancel);
        return alertDialog.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        setTextViewsValues();
    }

    @Override
    public void onStop() {
        super.onStop();
        Commander.getInstance(getActivity()).saveCommandsMap();
    }
}
