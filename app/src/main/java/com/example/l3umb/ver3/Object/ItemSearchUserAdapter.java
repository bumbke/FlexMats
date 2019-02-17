package com.example.l3umb.ver3.Object;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.l3umb.ver3.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Dev on 11/3/2018.
 */

public class ItemSearchUserAdapter extends RecyclerView.Adapter<HolderSearchUser>{
    private Context context;
    private List<User> users;
    private ItemClickListener listener;
    private int size;

    public ItemSearchUserAdapter(Context context, List<User> users, int size, ItemClickListener listener) {
        this.context = context;
        this.users = users;
        this.size = size;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HolderSearchUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user,null);
        final HolderSearchUser holderSearchUser = new HolderSearchUser(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, holderSearchUser.getPosition());
            }
        });
        return holderSearchUser;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderSearchUser holder, int position) {
        if (users.get(position).getAvatar_url() !=null) {
            Picasso.with(context).load(users.get(position).getAvatar_url()).placeholder(R.mipmap.ic_launcher).into(holder.ivAvt);
        }

        holder.tvName.setText(users.get(position).getName());
        holder.tvSummary.setText(users.get(position).getBio());
        holder.tvLike.setText("" + users.get(position).getLike_count());
    }

    @Override
    public int getItemCount() {
        return size;
    }
}

class HolderSearchUser extends RecyclerView.ViewHolder implements View.OnClickListener {
    private ItemClickListener itemClickListener;
    ImageView ivAvt;
    TextView tvName, tvSummary, tvLike;

    public HolderSearchUser(View itemView) {
        super(itemView);
        ivAvt = (ImageView) itemView.findViewById(R.id.ivAvt);
        tvName = (TextView) itemView.findViewById(R.id.tvName);
        tvSummary = (TextView) itemView.findViewById(R.id.tvSummary);
        tvLike = (TextView) itemView.findViewById(R.id.tvLike);
    }

    @Override
    public void onClick(View view) {
        this.itemClickListener.onItemClick(view, getLayoutPosition());
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
