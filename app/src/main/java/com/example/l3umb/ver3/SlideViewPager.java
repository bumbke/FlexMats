package com.example.l3umb.ver3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.l3umb.ver3.Function.Constants;
import com.example.l3umb.ver3.Object.Banner;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SlideViewPager extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<Banner> images;
    private int size;

    public SlideViewPager(Context context, List<Banner> images, int size){
        this.context = context;
        this.images = images;
        this.size = size;
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_layout_slider,null);
        ImageView imageView = view.findViewById(R.id.imgCarousel);
        Picasso.with(context).load(images.get(position).getImage_url()).placeholder(R.drawable.item1).into(imageView);
        ViewPager vp = (ViewPager)container;
        vp.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}
