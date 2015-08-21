package com.gpf.app.arduinorc.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gpf.app.arduinorc.R;
import com.gpf.app.arduinorc.items.InputRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zalo on 19/07/2015.
 */
public class InputRowAdapter extends RecyclerView.Adapter<InputRowAdapter.InputRowHolder>{

    private LayoutInflater inflater;
    private List<InputRow> data = new ArrayList<>();

    public InputRowAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }

    public List<InputRow> getData(){
        return data;
    }
    public void setData(List<InputRow> data){
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public InputRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_input, parent, false);
        return new InputRowHolder(view);
    }

    @Override
    public void onBindViewHolder(InputRowHolder holder, int position) {
        InputRow current = data.get(position);
        holder.setTitle(current.getTitle());
        holder.setValue(current.getValue());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class InputRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView icon;
        TextView title, value;

        public InputRowHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.input_icon);
            title = (TextView) itemView.findViewById(R.id.input_name);
            value = (TextView) itemView.findViewById(R.id.input_value);
            itemView.setOnClickListener(this);
        }

        public void setTitle(String text){
            this.title.setText(text);
        }

        public void setValue(String text){
            value.setText(text);
        }

        @Override
        public void onClick(View v) {
            editDialog(getAdapterPosition()).show();
        }

        public AlertDialog editDialog(final int position){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(inflater.getContext());
            alertDialog.setTitle(R.string.edit_input);
            alertDialog.setMessage(R.string.enter_name);

            final EditText input = new EditText(inflater.getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            input.setText(data.get(position).getTitle());
            alertDialog.setView(input);
            alertDialog.setIcon(R.drawable.ic_edit);

            DialogInterface.OnClickListener listenerOk = new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String inputText = input.getText().toString();
                    if(inputText.length()>0){
                        if(inputText.contains("/")){
                            inputText = inputText.replace("/", "-");
                        }
                        InputRow inputRow = data.get(position);
                        inputRow.setTitle(inputText);
                        notifyDataSetChanged();
                    }
                }
            };
            DialogInterface.OnClickListener listenerCancel = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            };
            alertDialog.setPositiveButton(R.string.btn_edit, listenerOk);
            alertDialog.setNegativeButton(R.string.btn_cancel, listenerCancel);
            return alertDialog.create();
        }
    }
}
