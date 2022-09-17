package com.mtime.bussiness.ticket.movie.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.bussiness.ticket.movie.bean.CardList;
import com.mtime.util.ImageURLManager;

import java.util.ArrayList;

public class CardListAdapter extends BaseAdapter {
    private ArrayList<CardList> cardLists;
    private final BaseActivity        context;

    public CardListAdapter(ArrayList<CardList> cardLists, BaseActivity context) {
        this.setCardLists(cardLists);
        this.context = context;
    }
    
    public int getCount() {
        return cardLists.size();
    }
    
    public Object getItem(int position) {
        return position;
    }
    
    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = context.getLayoutInflater().inflate(R.layout.card_list_item, null);
            holder.cardImg = convertView.findViewById(R.id.card_img);
            holder.cardText = convertView.findViewById(R.id.card_text);
            convertView.setTag(holder);
        }
        else {
            holder = (Holder) convertView.getTag();
        }
        holder.cardText.setText(cardLists.get(position).getName());
        context.volleyImageLoader.displayImage(cardLists.get(position).getImgUrl(), holder.cardImg, R.drawable.default_image, R.drawable.default_image, ImageURLManager.ImageStyle.THUMB, null);

        return convertView;
    }
    
    class Holder {
        TextView  cardText;
        ImageView cardImg;
    }
    
    public ArrayList<CardList> getCardLists() {
        return cardLists;
    }
    
    public void setCardLists(ArrayList<CardList> cardLists) {
        this.cardLists = cardLists;
    }
    
}
