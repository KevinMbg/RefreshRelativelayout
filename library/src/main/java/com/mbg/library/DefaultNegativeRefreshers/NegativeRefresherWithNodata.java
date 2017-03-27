package com.mbg.library.DefaultNegativeRefreshers;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.mbg.library.IRefresher;
import com.mbg.library.R;

/**
 * Created by Administrator on 2017/3/24.
 */

public class NegativeRefresherWithNodata implements IRefresher {
    private View contentView;
    private ImageView mProgress;
    private TextView mTextView;
    private int mViewHeight=0;
    private ObjectAnimator animator;
    private boolean hasMoredata=true;
    private int layoutId;

    public NegativeRefresherWithNodata(){
        this(false);
    }

    public NegativeRefresherWithNodata(boolean isOverlay){
        if(isOverlay){
            layoutId=R.layout.negative_refresher_withnodata_overlay;
        }else{
            layoutId=R.layout.negative_refresher_withnodata;
        }
    }

    public boolean isHasMoredata(){
        return hasMoredata;
    }

    public void setHasMoredata(boolean hasMore){
        if(hasMoredata == hasMore){
            return;
        }
        hasMoredata = hasMore;
        refreshView();
    }

    private void refreshView(){
        if(null == contentView){
            return;
        }
        if(hasMoredata){
            mProgress.setVisibility(View.VISIBLE);
            mTextView.setText("正在加载...");
        }else{
            mProgress.setVisibility(View.GONE);
            mTextView.setText("没有更多数据了...");
        }
    }

    @Override
    public View getView(Context context, ViewGroup viewGroup) {
        if(null == contentView){
            contentView= LayoutInflater.from(context).inflate(layoutId,viewGroup,false);
            mProgress= (ImageView) contentView.findViewById(R.id.negative_withnodata_pro);
            mTextView = (TextView)contentView.findViewById(R.id.negative_withnodata_text);
            refreshView();
        }
        return contentView;
    }

    private int getHeight(){
        if(0 == mViewHeight && null != contentView){
            mViewHeight=contentView.getMeasuredHeight();
        }
        return mViewHeight;
    }

    @Override
    public void onDrag(float offset) {
    }

    @Override
    public boolean canRefresh(float offset) {
        if(!hasMoredata){
            return false;
        }
        int height=getHeight();
        if(height == 0){
            return false;
        }
        return offset > height;
    }

    @Override
    public float getOverlayOffset() {
        return 0;
    }

    @Override
    public boolean onStartRefresh() {
        if(!hasMoredata){
            return false;
        }
        if(null == mProgress){
            return false;
        }
        if(null == animator){
            animator=ObjectAnimator.ofFloat(mProgress,"rotation",360);
            animator.setDuration(1500);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setInterpolator(new LinearInterpolator());
        }
        if(!animator.isRunning()){
            animator.start();
        }
        return false;
    }

    @Override
    public void onStopRefresh() {
        if(animator != null && animator.isRunning()){
            animator.end();
        }
    }

    @Override
    public long onRefreshComplete() {
        if(animator != null && animator.isRunning()){
            animator.end();
        }
        return 0;
    }
}
