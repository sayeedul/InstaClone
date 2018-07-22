package com.sayeedul.instaclone;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.myViewHolder>{

    private OnItemClickListener mListener;

    public interface OnItemClickListener
    {
        void onItemClick(int position);
        void onFollowClick(int position);
    }

    public void setOnClickListener(OnItemClickListener listener)
    {
        mListener = listener;
    }

    Context context;
    List<ItemDataUserList> myList;

    public UserListAdapter(Context context, List<ItemDataUserList> myList) {
        this.context = context;
        this.myList = myList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recyc_user_layout,parent,false);
        myViewHolder myviewholder = new myViewHolder(view,mListener);
        return myviewholder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        ItemDataUserList itemData = myList.get(position);

        holder.user1.setText(itemData.getUsername());
        holder.followPic.setImageDrawable(context.getDrawable(itemData.getImage()));

    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder
    {
        ImageView followPic;
        TextView user1;

        public myViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            // from recy_user_layout.
            followPic = itemView.findViewById(R.id.followImage);
            user1 = itemView.findViewById(R.id.usernameTV);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null)
                    {
                        int position = getAdapterPosition();
                        if(position !=RecyclerView.NO_POSITION)
                        {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            followPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            listener.onFollowClick(position);
                        }
                    }
                }
            });
        }
    }


}
