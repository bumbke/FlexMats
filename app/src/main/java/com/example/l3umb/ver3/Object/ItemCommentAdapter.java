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

public class ItemCommentAdapter extends RecyclerView.Adapter<HolderComment>{
    private Context context;
    private List<User> users;
    private List<Comment> comments;
    private ItemClickListener listener;

    public ItemCommentAdapter(Context context, List<User> users, List<Comment> comments, ItemClickListener listener) {
        this.context = context;
        this.users = users;
        this.comments = comments;
        this.listener = listener;
    }

    @Override
    public HolderComment onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment,null);
        final HolderComment holderComment = new HolderComment(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, holderComment.getPosition());
            }
        });
        return holderComment;
    }

    @Override
    public void onBindViewHolder(HolderComment holder, int position) {
        if (!users.get(position).getAvatar_url().isEmpty()) {
            Picasso.with(context).load(users.get(position).getAvatar_url()).placeholder(R.mipmap.ic_launcher).into(holder.ivAvt);
        }

        holder.tvDate.setText(comments.get(position).getCreated_date());
        holder.tvContent.setText(comments.get(position).getContent());
        holder.tvAuthor.setText(users.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}

class HolderComment extends RecyclerView.ViewHolder implements View.OnClickListener {
    ItemClickListener itemClickListener;
    ImageView ivAvt;
    TextView tvDate, tvAuthor, tvContent;

    public HolderComment(View itemView) {
        super(itemView);
        ivAvt = (ImageView) itemView.findViewById(R.id.ivAvt);
        tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        tvAuthor = (TextView) itemView.findViewById(R.id.tvAuthor);
        tvContent = (TextView) itemView.findViewById(R.id.tvContent);
    }

    @Override
    public void onClick(View view) {
        this.itemClickListener.onItemClick(view, getLayoutPosition());
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
