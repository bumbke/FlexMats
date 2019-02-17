package com.example.l3umb.ver3.Object;

import android.content.Context;
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

public class ItemScoreboardAdapter extends RecyclerView.Adapter<HolderScoreboard>{
    private Context context;
    private List<User> users;
    private ItemClickListener listener;
    private int size;

    public ItemScoreboardAdapter(Context context, List<User> users, int size, ItemClickListener listener) {
        this.context = context;
        this.users = users;
        this.size = size;
        this.listener = listener;
    }

    @Override
    public HolderScoreboard onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scoreboard_user,null);
        final HolderScoreboard holderScoreboard = new HolderScoreboard(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, holderScoreboard.getPosition());
            }
        });
        return holderScoreboard;
    }

    @Override
    public void onBindViewHolder(HolderScoreboard holder, int position) {
        holder.tvName.setText(users.get(position).getName());
        holder.tvSummary.setText(users.get(position).getBio());
        holder.tvLike.setText("" + users.get(position).getLike_count());

        if (!users.get(position).getAvatar_url().isEmpty())
            Picasso.with(context).load(users.get(position).getAvatar_url()).placeholder(R.mipmap.ic_launcher).into(holder.ivAvt);


        switch (position) {
            case 0:
                holder.ivIconBest.setImageResource(R.drawable.icon_top1);
                break;

            case 1:
                holder.ivIconBest.setImageResource(R.drawable.icon_top2);
                break;

            case 2:
                holder.ivIconBest.setImageResource(R.drawable.icon_top3);
                break;

            default:
                holder.ivIconBest.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return size;
    }
}

class HolderScoreboard extends RecyclerView.ViewHolder implements View.OnClickListener {
    ItemClickListener itemClickListener;
    ImageView ivAvt, ivIconBest;
    TextView tvLike, tvName, tvSummary;

    public HolderScoreboard(View itemView) {
        super(itemView);
        ivAvt = (ImageView) itemView.findViewById(R.id.ivAvt);
        ivIconBest = (ImageView) itemView.findViewById(R.id.ivIconBest);

        tvLike = (TextView) itemView.findViewById(R.id.tvLike);
        tvSummary = (TextView) itemView.findViewById(R.id.tvSummary);
        tvName = (TextView) itemView.findViewById(R.id.tvName);
    }

    @Override
    public void onClick(View view) {
        this.itemClickListener.onItemClick(view, getLayoutPosition());
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
