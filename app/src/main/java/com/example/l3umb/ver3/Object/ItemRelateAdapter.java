package com.example.l3umb.ver3.Object;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.l3umb.ver3.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by l3umb on 11/25/2017.
 */

public class ItemRelateAdapter extends RecyclerView.Adapter<HolderRelate>{
    private Context context;
    private List<Product> products;
    private ItemClickListener listener;
    private int size;

    public ItemRelateAdapter(Context context, List<Product> products, int size,ItemClickListener listener) {
        this.context = context;
        this.products = products;
        this.size = size;
        this.listener = listener;
    }

    @Override
    public HolderRelate onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_relate,null);
        final HolderRelate holderRelate = new HolderRelate(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, holderRelate.getPosition());
            }
        });
        return holderRelate;
    }

    @Override
    public void onBindViewHolder(HolderRelate holder, int position) {
        if (!products.get(position).getImage_url().isEmpty()) {
            Picasso.with(context).load(products.get(position).getImage_url()).placeholder(R.drawable.item1).into(holder.ivProduct);
        }
    }

    @Override
    public int getItemCount() {
        return size;
    }
}

class HolderRelate extends RecyclerView.ViewHolder implements View.OnClickListener {
    ItemClickListener itemClickListener;
    ImageView ivProduct;

    public HolderRelate(View itemView) {
        super(itemView);
        ivProduct = (ImageView) itemView.findViewById(R.id.ivProduct);
    }

    @Override
    public void onClick(View view) {
        this.itemClickListener.onItemClick(view, getLayoutPosition());
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
