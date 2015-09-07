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
        holder.setID(current.getID());
        holder.setTitle(current.getTitle());
        holder.setValue(current.getValue());
    }

    private void deleteItem(int position){
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class InputRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView remove;
        TextView id, title, value;

        public InputRowHolder(View itemView) {
            super(itemView);
            remove = (ImageView) itemView.findViewById(R.id.input_remove);
            id = (TextView) itemView.findViewById(R.id.input_id);
            title = (TextView) itemView.findViewById(R.id.input_name);
            value = (TextView) itemView.findViewById(R.id.input_value);
            title.setOnClickListener(this);
            remove.setOnClickListener(this);
        }
        public void setID(String id){
            this.id.setText(id);
        }

        public void setTitle(String title){
            this.title.setText(title);
        }

        public void setValue(String value){
            this.value.setText(value);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.input_remove:
                    deleteItem(getAdapterPosition());
                    break;
                case R.id.input_name:
                    editDialog(getAdapterPosition()).show();
                    break;
            }
        }

        public AlertDialog editDialog(final int position){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(inflater.getContext());
            alertDialog.setTitle(R.string.edit_input);
            alertDialog.setMessage(R.string.enter_input);

            final EditText inputID = new EditText(inflater.getContext());
            final EditText inputName = new EditText(inflater.getContext());

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            inputID.setLayoutParams(lp);
            inputName.setLayoutParams(lp);

            inputID.setText(data.get(position).getID());
            inputName.setText(data.get(position).getTitle());

            LinearLayout ll = new LinearLayout(inflater.getContext());
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.addView(inputID);
            ll.addView(inputName);

            alertDialog.setView(ll);
            alertDialog.setIcon(R.drawable.ic_edit);

            DialogInterface.OnClickListener listenerOk = new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String inputIDText = inputID.getText().toString();
                    String inputNameText = inputName.getText().toString();
                    if(inputIDText.length()>0 && inputNameText.length()>0){
                        InputRow inputRow = data.get(position);
                        inputRow.setID(inputIDText);
                        inputRow.setTitle(inputNameText);
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
