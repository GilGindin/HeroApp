package com.gil.heroapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyHeroAdapter extends RecyclerView.Adapter<MyHeroAdapter.MyHolder> {

    private List<Item> myList;
    private Context mContext;
    private OnItemClickListener mlistener;
    private boolean isRadioBtnClicked;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mlistener = listener;
    }

    public MyHeroAdapter( Context context , List<Item> myList) {
        this.myList = myList;
        mContext = context;
    }

    public static class MyHolder extends RecyclerView.ViewHolder{

        private ImageView image_view;
        private TextView text_view_name;
        private TextView text_view_abilities;
        private RadioButton heart_radio_btn;

        public MyHolder(@NonNull View itemView , final OnItemClickListener listener) {
            super(itemView);

            image_view = itemView.findViewById(R.id.image_view);
            text_view_name = itemView.findViewById(R.id.text_view_name);
            text_view_abilities = itemView.findViewById(R.id.text_view_abilities);
            heart_radio_btn = itemView.findViewById(R.id.heart_radio_btn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);

                        }
                    }
                }
            });
        }
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_item_layout , viewGroup , false);
        MyHolder holder = new MyHolder(v , mlistener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int position) {

        Item item = myList.get(position);
        myHolder.text_view_name.setText(item.getTitle());


            myHolder.text_view_abilities.setText(String.valueOf(item.getAbilitiesObj().toString().trim()));


        if (myHolder.image_view != null){
            String photoUrl = item.getImageUrl();
            Picasso.with(mContext).load(photoUrl).into(myHolder.image_view);
        }
    }


    @Override
    public int getItemCount() {
        return myList.size();
    }
}
