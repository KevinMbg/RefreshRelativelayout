package com.mbg.library.DefaultPositiveRefreshers;


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import com.mbg.library.IRefresher;
import com.mbg.library.Views.CircleImageView;
import com.mbg.library.Views.MaterialProgressDrawable;

/**
 * Created by Administrator on 2017/3/21.
 */

public class OverlayProgressWithArrow implements IRefresher {

    protected int CIRCLE_SIZE=40;
    private int CIRCLE_BG_LIGHT=0xFFFAFAFA;
    private int PROGRESS_RING_COLOR=Color.BLACK;
    private int[] PROGRESS_RING_COLORS;
    private int PROGRESS_BACK=CIRCLE_BG_LIGHT;
    private int PROGRESS_SIZE=MaterialProgressDrawable.DEFAULT;
    private int offset=CIRCLE_SIZE;


    protected CircleImageView circleImageView;
    protected MaterialProgressDrawable mProgress;
    protected int width=0,height=0;

    public OverlayProgressWithArrow(){}

    public OverlayProgressWithArrow(int size){
        this.CIRCLE_SIZE=size;
    }

    public void setCircleBackground(int color){
        CIRCLE_BG_LIGHT=color;
        if(null != circleImageView){
            circleImageView.setBackgroundColor(color);
        }
    }

    public void setProgressColor(int color){
        PROGRESS_RING_COLOR = color;
        if(null != mProgress){
            mProgress.setColorSchemeColors(color);
        }
    }

    public void setProgressBack(int color){
        PROGRESS_BACK=color;
        if(null != mProgress){
            mProgress.setBackgroundColor(color);
        }
    }

    public void setProgressColors(int... colors){
        this.PROGRESS_RING_COLORS=colors;
        if(null != mProgress){
            mProgress.setColorSchemeColors(colors);
        }
    }

    public void setProgressSize(@MaterialProgressDrawable.ProgressDrawableSize int size){
        PROGRESS_SIZE=size;
        if(null != circleImageView && null != mProgress){
            circleImageView.setImageDrawable(null);
            mProgress.updateSizes(size);
            circleImageView.setImageDrawable(mProgress);
        }
    }

    public void setOffset(int offset){
        this.offset=offset;
    }

    @Override
    public View getView(Context context, ViewGroup viewGroup) {
        if(null == circleImageView){
            circleImageView=new CircleImageView(context,CIRCLE_BG_LIGHT,CIRCLE_SIZE);
            mProgress=new MaterialProgressDrawable(context,viewGroup);
            mProgress.setBackgroundColor(PROGRESS_BACK);
            if(null == PROGRESS_RING_COLORS) {
                mProgress.setColorSchemeColors(PROGRESS_RING_COLOR);
            }else{
                mProgress.setColorSchemeColors(PROGRESS_RING_COLORS);
            }
            mProgress.updateSizes(MaterialProgressDrawable.DEFAULT);
            mProgress.showArrow(true);
            mProgress.updateSizes(PROGRESS_SIZE);
            circleImageView.setImageDrawable(mProgress);
        }
        return circleImageView;
    }

    protected int getSize(){
        if(0 == height && null != circleImageView){
            height=circleImageView.getMeasuredHeight();
        }
        return height;
    }

    @Override
    public void onDrag(float offset) {
        int height=getSize();
        if(height == 0){
            return;
        }
        float rate =offset /(height+getOverlayOffset());
        //圈圈的旋转角度
        mProgress.setProgressRotation(rate * 0.5f);
        //圈圈周长，0f-1F
        rate=rate > 1.0f ? 1.0f :rate;
        mProgress.setStartEndTrim(0f,  rate *0.8f);
        //箭头大小，0f-1F
        mProgress.setArrowScale(rate);
        //透明度，0-255
        mProgress.setAlpha((int) (255 * rate));
        circleImageView.setImageAlpha((int) (255 * rate));
        circleImageView.setScaleX(rate);
        circleImageView.setScaleY(rate);
    }

    @Override
    public boolean canRefresh(float offset) {
        return offset >= (getSize()+getOverlayOffset());
    }

    @Override
    public float getOverlayOffset() {
        return offset;
    }

    @Override
    public boolean onStartRefresh() {
        if(null != mProgress){
            if(mProgress.isRunning()){
                return false;
            }
            mProgress.showArrow(false);
            mProgress.start();
        }
        return false;
    }

    @Override
    public void onStopRefresh() {
        onEndRefresh();
    }

    @Override
    public long onRefreshComplete() {
        onEndRefresh();
        return 0;
    }

    public void onEndRefresh() {
        if(null != mProgress){
            mProgress.stop();
            mProgress.showArrow(true);
        }
    }
}
