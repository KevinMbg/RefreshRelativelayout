package com.mbg.refreshlayout.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mbg.refreshlayout.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/26.
 */

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder>{

    private List<Integer> dataList;

    public HorizontalAdapter(){
        dataList=new ArrayList<>();
    }

    @Override
    public HorizontalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HorizontalAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_horizontal,parent,false));
    }

    @Override
    public void onBindViewHolder(HorizontalAdapter.MyViewHolder holder, int position) {
        holder.textView.setText((position+1)+"");
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void addList(List<Integer> list){
        if(null == list){
            return;
        }
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    public void setDataList(List<Integer> list){
        if(null == list){
            return;
        }
        dataList = list;
        notifyDataSetChanged();
    }
    class MyViewHolder extends RecyclerView.ViewHolder{
        public MyViewHolder(View itemView) {
            super(itemView);
            textView=(TextView)itemView.findViewById(R.id.recycler_item_text);
        }
        public TextView textView;
    }
}
