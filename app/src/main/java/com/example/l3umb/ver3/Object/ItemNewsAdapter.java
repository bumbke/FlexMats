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

public class ItemNewsAdapter extends RecyclerView.Adapter<HolderNews>{
    private Context context;
    private List<News> newsList;
    private ItemClickListener listener;
    private int size;

    public ItemNewsAdapter(Context context, List<News> newsList, int size, ItemClickListener listener) {
        this.context = context;
        this.newsList = newsList;
        this.size = size;
        this.listener = listener;
    }

    @Override
    public HolderNews onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news,null);
        final HolderNews holderNews = new HolderNews(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, holderNews.getPosition());
            }
        });
        return holderNews;
    }

    @Override
    public void onBindViewHolder(HolderNews holder, int position) {
        holder.tvTitle.setText(newsList.get(position).getTitle());
        holder.tvDate.setText(newsList.get(position).getCreated_date());

        if (newsList.get(position).getImage_url() != null)
            Picasso.with(context).load(newsList.get(position).getImage_url()).placeholder(R.drawable.item1).into(holder.ivImage);

        holder.tvContent.setText(newsList.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return size;
    }
}

class HolderNews extends RecyclerView.ViewHolder implements View.OnClickListener {
    ItemClickListener itemClickListener;
    ImageView ivImage;
    TextView tvTitle, tvContent, tvDate;

    public HolderNews(View itemView) {
        super(itemView);
        ivImage = (ImageView) itemView.findViewById(R.id.ivImage);

        tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        tvDate = (TextView) itemView.findViewById(R.id.tvDate);
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
