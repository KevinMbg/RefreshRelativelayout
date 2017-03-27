package com.mbg.refreshlayout.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mbg.library.ISingleRefreshListener;
import com.mbg.library.RefreshRelativeLayout;
import com.mbg.refreshlayout.MorePageActivity;
import com.mbg.refreshlayout.R;
import com.mbg.refreshlayout.Urls;
import com.mbg.refreshlayout.refreshers.MyHorizontalLoadMoreRefresher;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2017/3/26.
 */

public class DoubleAdapter extends RecyclerView.Adapter {

    private static final int TYPE_TEXT=0;
    private static final int TYPE_RECYCLER=1;
    private static final int TYPE_IMAGE=2;
    private static final int TYPE_NORMAL=3;

    private List<Integer> dataList;
    private Context context;
    int urlsSize;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager=recyclerView.getLayoutManager();
        if(!(layoutManager instanceof GridLayoutManager)){
            return;
        }
        ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(position >= TYPE_NORMAL){
                    return 1;
                }
                return 2;
            }
        });

    }

    public DoubleAdapter(Context context){
        this.context=context;
        urlsSize=Urls.urls.length;
    }

    public void setDataList(List<Integer> list){
        dataList =list;
        notifyDataSetChanged();
    }

    public void addDataList(List<Integer> list){
        if(null == dataList){
            dataList=new ArrayList<Integer>();
        }
        if(null != list) {
            dataList.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_TEXT:
                return new TextHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_text,parent,false));
            case TYPE_RECYCLER:
                return new RecyclerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_recycler,parent,false));
            case TYPE_IMAGE:
                return new ImageHolder((LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_image,parent,false)));
            case TYPE_NORMAL:
                return new NormalHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_normal,parent,false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof TextHolder){
            ((TextHolder) holder).textView.setText("这是第"+position+"个Item.");
        }else if(holder instanceof ImageHolder){
            Glide.with(context).load(Urls.urls[0]).into(((ImageHolder) holder).imageView);
        }else if(holder instanceof NormalHolder){
            Glide.with(context).load(Urls.urls[position%urlsSize]).into(((NormalHolder) holder).imageView);
        }
    }

    @Override
    public int getItemCount() {
        if(null == dataList){
            return 0;
        }
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(TYPE_TEXT == position){
            return TYPE_TEXT;
        }else if(TYPE_IMAGE == position){
            return TYPE_IMAGE;
        }else if(TYPE_RECYCLER == position){
            return TYPE_RECYCLER;
        }
        return TYPE_NORMAL;
    }


    public class TextHolder extends RecyclerView.ViewHolder{
        private RefreshRelativeLayout refreshRelativeLayout;
        public TextView textView;
        public TextHolder(final View itemView) {
            super(itemView);
            refreshRelativeLayout=(RefreshRelativeLayout)itemView.findViewById(R.id.recycler_item_refresh);
            textView=(TextView)itemView.findViewById(R.id.recycler_item_text);
            refreshRelativeLayout.addNegativeRefreshListener(new ISingleRefreshListener() {
                @Override
                public void onRefresh() {
                    itemView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(itemView.getContext(),"刚刚刷新完成~",Toast.LENGTH_SHORT).show();
                            refreshRelativeLayout.negativeRefreshComplete();
                        }
                    },1000);

                }
            });
        }
    }

    public class ImageHolder extends RecyclerView.ViewHolder{
        private RefreshRelativeLayout refreshRelativeLayout;
        public ImageView imageView;
        public ImageHolder(final View itemView) {
            super(itemView);
            refreshRelativeLayout=(RefreshRelativeLayout)itemView.findViewById(R.id.recycler_item_refresh);
            imageView=(ImageView)itemView.findViewById(R.id.recycler_item_image);
            refreshRelativeLayout.setNegativeRefresher(new MyHorizontalLoadMoreRefresher());
            refreshRelativeLayout.addNegativeRefreshListener(new ISingleRefreshListener() {
                @Override
                public void onRefresh() {
                    MorePageActivity.skipToMoreInfo(itemView.getContext());
                }
            });
        }
    }

    public class RecyclerHolder extends RecyclerView.ViewHolder{
        private RefreshRelativeLayout refreshRelativeLayout;
        private RecyclerView recyclerView;
        public RecyclerHolder(final View itemView) {
            super(itemView);
            refreshRelativeLayout=(RefreshRelativeLayout)itemView.findViewById(R.id.recycler_item_refresh);
            refreshRelativeLayout.setAnimateDuration(500);
            recyclerView=(RecyclerView)itemView.findViewById(R.id.recycler_item_recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(),LinearLayoutManager.HORIZONTAL,false));
            HorizontalAdapter adapter=new HorizontalAdapter();
            adapter.setDataList(getList());
            recyclerView.setAdapter(adapter);
            refreshRelativeLayout.addNegativeRefreshListener(new ISingleRefreshListener() {
                @Override
                public void onRefresh() {
                    MorePageActivity.skipToMoreInfo(itemView.getContext());
                }
            });
        }
        private List<Integer> getList(){
            List<Integer> data=new ArrayList<Integer>();
            for(int i =0 ; i< 10;i++){
                data.add(i);
            }
            return data;
        }
    }

    public class NormalHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public NormalHolder(View itemView) {
            super(itemView);
            imageView=(ImageView)itemView.findViewById(R.id.recycler_item_normal_img);
        }
    }

}
