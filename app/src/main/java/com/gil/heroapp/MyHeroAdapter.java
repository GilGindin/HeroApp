package com.gil.heroapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyHeroAdapter extends RecyclerView.Adapter<MyHeroAdapter.MyHolder> {
    private static final String TAG = "MyHeroAdapter";
    private List<Item> myList;
    private Context mContext;
    private OnItemClickListener mlistener;
    private ArrayList<String> stringArray = new ArrayList<>();
    private int[] selectedItems;

    //interface of the item click lisener
    public interface OnItemClickListener {
        void onItemClick(int position);

        void swapItem(int fromPostion, int toPosition);

        void updateAppHeader(String photo, String name);

    }

    //constractur for the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mlistener = listener;
    }

    //constractur for the adapter
    public MyHeroAdapter(Context context, List<Item> myList) {
        this.myList = myList;
        mContext = context;
        //initial selecteditems list with the size of the original heroapp list
        //that came from the api
        selectedItems = new int[myList.size()];
        initializeSeledtedItems();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        private ImageView image_view;
        private TextView text_view_name;
        private TextView text_view_abilities;
        private ImageView heart_image_view;

        public MyHolder(@NonNull final View itemView, final OnItemClickListener listener) {
            super(itemView);

            image_view = itemView.findViewById(R.id.image_view);
            text_view_name = itemView.findViewById(R.id.text_view_name);
            text_view_abilities = itemView.findViewById(R.id.text_view_abilities);
            heart_image_view = itemView.findViewById(R.id.heart_image_view);
            heart_image_view.setImageResource(R.drawable.heart);
            heart_image_view.setVisibility(View.GONE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                            setSelectedItem(position);
                            notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_item_layout, viewGroup, false);
        MyHolder holder = new MyHolder(v, mlistener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int position) {

        Item item = myList.get(position);
        myHolder.text_view_name.setText(item.getTitle());

        //make the position item's imageview visible and all the rest of the list invisible
        if (selectedItems[position] == position)
            myHolder.heart_image_view.setVisibility(View.VISIBLE);
        else myHolder.heart_image_view.setVisibility(View.GONE);

        //adding the hero's list of abilities to the current textview
        stringArray = item.getAbilitiesObj();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < stringArray.size(); i++) {
            try {
                builder.append(stringArray.get(i));
                builder.append(", ");

            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
            myHolder.text_view_abilities.setText(builder);
        }
        //adding the hero's image
        if (myHolder.image_view != null) {
            String photoUrl = item.getImageUrl();
            Picasso.with(mContext).load(photoUrl).into(myHolder.image_view);
        }
    }

    @Override
    public int getItemCount() {
        return myList.size();

    }

    //function that seperate between the clicked item to all others in list
    //to highlight is imageview (heart imageview)
    private void setSelectedItem(int position) {
        for (int i = 0; i < selectedItems.length; i++) {
            if (i == position) {
                if (i == 0) {
                    selectedItems[i] = position;
                } else
                    selectedItems[i] = position + 1;
            } else selectedItems[i] = 0;
        }
    }

    //initial selecteditems list with all the items get from the original list
    //that came from the heroApp api
    private void initializeSeledtedItems() {
        for (int item : selectedItems) item = 1;
    }


}
