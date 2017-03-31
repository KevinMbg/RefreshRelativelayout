package com.mbg.refreshlayout.refreshers;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.mbg.library.IRefresher;
import com.mbg.refreshlayout.R;

/**
 * Created by Administrator on 2017/3/30.
 */

public class LoadMoreWithNomoreRefresher implements IRefresher {

    private View mContentView;
    private ImageView mImageView;
    private TextView mTextView;
    private int height;

    private ObjectAnimator animator;
    private boolean hasMore=true;

    public boolean getHasMore(){
        return hasMore;
    }

    public void setHasMore(boolean hasMore){
        this.hasMore=hasMore;
        if(null == mTextView || null == mImageView){
            return;
        }
        if(hasMore){
            mImageView.setVisibility(View.VISIBLE);
            mTextView.setText("正在加载……");
        }else{
            mImageView.setVisibility(View.GONE);
            mTextView.setText("没有更多数据了……");
        }
    }

    @Override
    public View getView(Context context, ViewGroup viewGroup) {
        if(null == mContentView){
            mContentView= LayoutInflater.from(context).inflate(R.layout.refresher_loadmorewithnodata,viewGroup,false);
            mImageView= (ImageView) mContentView.findViewById(R.id.negative_withnodata_pro);
            mTextView=(TextView)mContentView.findViewById(R.id.negative_withnodata_text);
        }
        return mContentView;
    }

    private int getHeight(){
        if(0 == height && null != mContentView){
            height=mContentView.getMeasuredHeight();
        }
        return height;
    }

    @Override
    public void onDrag(float offset) {
    }

    @Override
    public boolean canRefresh(float offset) {
        int height=getHeight();
        //Log.i("customRefresher","height:"+height+",offset:"+offset+",hasmore:"+hasMore);
        if(0 == height){
            return false;
        }
        if(offset >= height && hasMore){
            return true;
        }
        return false;
    }

    @Override
    public float getOverlayOffset() {
        return 0;
    }

    @Override
    public boolean onStartRefresh() {
        if(null == animator){
            animator=ObjectAnimator.ofFloat(mImageView,"rotation",360);
            animator.setDuration(1500);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setInterpolator(new LinearInterpolator());
        }
        if(!animator.isStarted()){
            animator.start();
        }
        return false;
    }

    @Override
    public void onStopRefresh() {
        if(null != animator && animator.isStarted()){
            animator.cancel();
        }
    }

    @Override
    public long onRefreshComplete() {
        if(null != animator && animator.isStarted()){
            animator.cancel();
        }
        if(hasMore){
            return 0;
        }else{
            return 500;
        }
    }
}
