package com.mbg.refreshlayout.refreshers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mbg.library.IRefresher;
import com.mbg.refreshlayout.R;

/**
 * Created by Administrator on 2017/3/26.
 */

public class MyHorizontalLoadMoreRefresher implements IRefresher {

    private View mContentView;
    private ImageView imageView;
    private int width;
    private int containerWidth;
    @Override
    public View getView(Context context, ViewGroup viewGroup) {
        if(null == mContentView){
            mContentView= LayoutInflater.from(context).inflate(R.layout.custom_loadmore_refresher,viewGroup,false);
            imageView=(ImageView)mContentView.findViewById(R.id.custom_refresher_image);
        }
        return mContentView;
    }
    private int getWidth(){
        if(0 == width && null != imageView){
            width=imageView.getMeasuredWidth();
        }
        return width;
    }

    private int getContainerWidth(){
        if(0 == containerWidth && null != mContentView){
            containerWidth=mContentView.getMeasuredWidth();
        }
        return  containerWidth;
    }

    @Override
    public void onDrag(float offset) {
        getWidth();
        if(width == 0){
            return;
        }
        if(offset >= width){
            imageView.setRotation(180);
        }else{
            imageView.setRotation(0);
        }
    }

    @Override
    public boolean canRefresh(float offset) {
        getWidth();
        if(width == 0){
            return false;
        }
        return offset >= width;
    }


    @Override
    public float getOverlayOffset() {
        return -getContainerWidth();
    }

    @Override
    public boolean onStartRefresh() {
        return true;
    }

    @Override
    public void onStopRefresh() {

    }

    @Override
    public long onRefreshComplete() {
        return 0;
    }
}
