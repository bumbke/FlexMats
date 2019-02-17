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
 * Created by l3umb on 11/25/2017.
 */

public class ItemHomeAdapter extends RecyclerView.Adapter<HolderHome>{
    private Context context;
    private List<Product> products;
    private List<User> users;
    private ItemClickListener listener;
    private int size;

    public ItemHomeAdapter(Context context, List<Product> products, List<User> users, int size,ItemClickListener listener) {
        this.context = context;
        this.products = products;
        this.users = users;
        this.size = size;
        this.listener = listener;
    }

    @Override
    public HolderHome onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vertical,null);
        final HolderHome holderHome = new HolderHome(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, holderHome.getPosition());
            }
        });
        return holderHome;
    }

    @Override
    public void onBindViewHolder(HolderHome holder, int position) {
        if (products.get(position) !=null) {
            Picasso.with(context).load(products.get(position).getImage_url()).placeholder(R.drawable.item1).into(holder.ivBackground);
        }

        if (users.get(position).getAvatar_url() != null) {
            Picasso.with(context).load(users.get(position).getAvatar_url()).placeholder(R.mipmap.ic_launcher).resizeDimen(R.dimen.avatarSmall, R.dimen.avatarSmall).into(holder.ivAvt);
        }
        holder.tvTitle.setText(products.get(position).getTitle());
        holder.tvDate.setText(products.get(position).getCreated_date());
        holder.tvAuthor.setText(users.get(position).getName());
        holder.tvComment.setText(""+products.get(position).getComment_count());
        holder.tvView.setText(""+products.get(position).getView_count());
        holder.tvLike.setText(""+products.get(position).getLike_count());
    }

    @Override
    public int getItemCount() {
        return size;
    }
}

class HolderHome extends RecyclerView.ViewHolder implements View.OnClickListener {
    ItemClickListener itemClickListener;
    ImageView ivBackground, ivAvt;
    TextView tvTitle, tvDate, tvAuthor, tvComment, tvView,tvLike;

    public HolderHome(View itemView) {
        super(itemView);
        ivBackground = (ImageView) itemView.findViewById(R.id.ivBackground);
        ivAvt = (ImageView) itemView.findViewById(R.id.ivAvt);
        tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        tvAuthor = (TextView) itemView.findViewById(R.id.tvAuthor);
        tvComment = (TextView) itemView.findViewById(R.id.tvComment);
        tvView = (TextView) itemView.findViewById(R.id.tvView);
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
