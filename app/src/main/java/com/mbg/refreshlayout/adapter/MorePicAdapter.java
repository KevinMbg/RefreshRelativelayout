package com.mbg.refreshlayout.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mbg.refreshlayout.R;
import com.mbg.refreshlayout.Urls;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/27.
 */

public class MorePicAdapter extends RecyclerView.Adapter<MorePicAdapter.MorePicHolder> {


    private List<Integer> datalist;
    private int urlsSize;

    public MorePicAdapter(){
        urlsSize= Urls.urls.length;
    }

    public void setDataList(){
        if(null == datalist){
            datalist=new ArrayList<Integer>();
        }else {
            datalist.clear();
        }
        for(int i=0;i<20;i++){
            datalist.add(i);
        }
        notifyDataSetChanged();
    }
    public void addDataList(){
        if(null == datalist){
            datalist=new ArrayList<Integer>();
        }
        for(int i=0;i<15;i++){
            datalist.add(i);
        }
        notifyDataSetChanged();
    }

    @Override
    public MorePicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MorePicHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_morepic,parent,false));
    }

    @Override
    public void onBindViewHolder(MorePicHolder holder, int position) {
        holder.bindValue(position % urlsSize);
    }

    @Override
    public int getItemCount() {
        if(null == datalist){
            return 0;
        }
        return datalist.size();
    }

    public class MorePicHolder extends  RecyclerView.ViewHolder{
        public ImageView imageView;
        public MorePicHolder(View itemView) {
            super(itemView);
            imageView=(ImageView)itemView.findViewById(R.id.recycler_item_normal_img);
        }

        public void bindValue(int pos){
            Glide.with(imageView.getContext()).load(Urls.urls[pos]).into(imageView);
        }
    }
}
