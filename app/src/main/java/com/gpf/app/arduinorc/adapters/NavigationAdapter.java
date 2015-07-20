package com.gpf.app.arduinorc.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gpf.app.arduinorc.R;
import com.gpf.app.arduinorc.items.NavigationRow;

import java.util.List;

public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.NavigationHolder> {
    private LayoutInflater inflater;
    private List<NavigationRow> data;
    private ClickListener clickListener;

    public NavigationAdapter(Context context, List<NavigationRow> data){
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public NavigationHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_navigation, viewGroup, false);
        return new NavigationHolder(view);
    }

    @Override
    public void onBindViewHolder(NavigationHolder holder, int i) {
        NavigationRow current = data.get(i);
        holder.setIcon(current.getIconId());
        holder.setTitle(current.getTitle());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface ClickListener{
        void itemClick(View view, int position);
    }

    public void setClickListener(ClickListener cl){
        clickListener = cl;
    }

    public class NavigationHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView icon;
        TextView title;
        public NavigationHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.navigationRowIcon);
            title = (TextView) itemView.findViewById(R.id.navigationRowTitle);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(clickListener != null){
                clickListener.itemClick(view, getAdapterPosition());
            }
        }

        public void setIcon(int id){
            this.icon.setImageResource(id);
        }

        public void setTitle(String text){
            this.title.setText(text);
        }
    }
}
