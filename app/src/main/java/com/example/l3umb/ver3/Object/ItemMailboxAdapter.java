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

public class ItemMailboxAdapter extends RecyclerView.Adapter<HolderMailbox>{
    private Context context;
    private List<User> users;
    private List<Mail> mails;
    private ItemClickListener listener;
    private int size;

    public ItemMailboxAdapter(Context context, List<Mail> mails, List<User> users, int size, ItemClickListener listener) {
        this.context = context;
        this.mails = mails;
        this.users = users;
        this.size = size;
        this.listener = listener;
    }

    @Override
    public HolderMailbox onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_email,null);
        final HolderMailbox holderMailbox = new HolderMailbox(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, holderMailbox.getPosition());
            }
        });
        return holderMailbox;
    }

    @Override
    public void onBindViewHolder(HolderMailbox holder, int position) {
        holder.tvTitle.setText(mails.get(position).getTitle());
        holder.tvDate.setText(mails.get(position).getCreated_date());

        if (!users.get(position).getAvatar_url().isEmpty())
            Picasso.with(context).load(users.get(position).getAvatar_url()).placeholder(R.mipmap.ic_launcher).into(holder.ivAvt);

        holder.tvName.setText(users.get(position).getName());

        if(mails.get(position).getType() == 2)
            holder.ivIconType.setImageResource(R.drawable.ic_receive_arrow);
        else
            holder.ivIconType.setImageResource(R.drawable.ic_send_arrow);
    }

    @Override
    public int getItemCount() {
        return size;
    }
}

class HolderMailbox extends RecyclerView.ViewHolder implements View.OnClickListener {
    ItemClickListener itemClickListener;
    ImageView ivAvt, ivIconType;
    TextView tvTitle, tvName, tvDate;

    public HolderMailbox(View itemView) {
        super(itemView);
        ivAvt = (ImageView) itemView.findViewById(R.id.ivAvt);
        ivIconType = (ImageView) itemView.findViewById(R.id.ivIconType);

        tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        tvDate = (TextView) itemView.findViewById(R.id.tvDate);
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
