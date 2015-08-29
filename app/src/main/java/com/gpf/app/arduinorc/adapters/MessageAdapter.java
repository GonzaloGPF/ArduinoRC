package com.gpf.app.arduinorc.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gpf.app.arduinorc.R;
import com.gpf.app.arduinorc.items.ConsoleMessage;

import java.util.ArrayList;

/**
 * Created by Zalo on 29/08/2015.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder>{
    public static final int ARROW_RIGHT = 0;
    public static final int ARROW_LEFT = 1;
    private LayoutInflater inflater;
    private ArrayList<ConsoleMessage> messages = new ArrayList<>();

    public MessageAdapter(Context context){
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_message, parent, false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageHolder holder, int position) {
        ConsoleMessage current =  messages.get(position);
        holder.setText(current.getText());
        holder.setArrowDirection(current.getArrowDirection());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setMessages(ArrayList<ConsoleMessage> messages){
        this.messages = messages;
        notifyDataSetChanged();
    }

    public class MessageHolder extends RecyclerView.ViewHolder{
        public ImageView icon;
        public TextView text;
        public MessageHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.message_icon);
            text = (TextView) itemView.findViewById(R.id.message_text);
        }
        public void setText(String text){
            this.text.setText(text);
        }
        public void setArrowDirection(int arrowDirection){
            if(arrowDirection == ARROW_RIGHT){
                icon.setImageResource(R.drawable.ic_arrow);
            }else{
                icon.setImageResource(R.drawable.arrow_left);
            }
        }
    }
}
