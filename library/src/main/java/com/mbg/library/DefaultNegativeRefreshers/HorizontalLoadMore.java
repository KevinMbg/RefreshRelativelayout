package com.mbg.library.DefaultNegativeRefreshers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mbg.library.IRefresher;
import com.mbg.library.R;

/**
 * Created by Administrator on 2017/3/25.
 */

public class HorizontalLoadMore implements IRefresher {

    private View mContentView;
    private BezierRefreshView mBezierView;
    private ImageView mImageView;
    private TextView mTextView;
    private LinearLayout mTipView;

    private int width=0;
    private int tipWidth=0;
    private boolean draging=true;

    @Override
    public View getView(Context context, ViewGroup viewGroup) {
        if(null == mContentView){
            mContentView= LayoutInflater.from(context).inflate(R.layout.bezier_loadmore,viewGroup,false);
            mBezierView=(BezierRefreshView)mContentView.findViewById(R.id.bezier_view);
            mTipView=(LinearLayout)mContentView.findViewById(R.id.rightview_move_area);
            mImageView=(ImageView)mContentView.findViewById(R.id.rightview_change_icon);
            mTextView=(TextView)mContentView.findViewById(R.id.dragtoloadmore_text);
        }
        return mContentView;
    }

    private int getWidth(){
        if(width == 0 && null != mContentView){
            width=mContentView.getMeasuredWidth();
        }
        return width;
    }
    private int getTipWidth(){
        if(tipWidth == 0 && null != mTipView){
            tipWidth=mTipView.getWidth();
        }
        return tipWidth;
    }

    @Override
    public void onDrag(float offset) {
        int width=getWidth();
        int tipWidth=getTipWidth();
        if(width == 0 || tipWidth == 0){
            return;
        }
        mBezierView.setWidth((int) offset);
        if(!draging){
            if(offset > tipWidth) {
                mTipView.setTranslationX((offset - tipWidth));
            }else{
                mTipView.setTranslationX(0);
            }
            return;
        }
        if(offset > tipWidth){
            mTipView.setTranslationX((offset -tipWidth));
            mTextView.setText("释放查看");
            mImageView.setRotation(180);
        }else {
            mTipView.setTranslationX(0);
            mTextView.setText("查看更多");
            mImageView.setRotation(0);
        }

    }

    @Override
    public boolean canRefresh(float offset) {
        int width=getTipWidth();
        if(width == 0){
            return false;
        }if(offset > width){
            draging=false;
            return true;
        }
        return false;
    }

    @Override
    public float getOverlayOffset() {
        return -getWidth();
    }

    @Override
    public boolean onStartRefresh() {
        draging=true;
        return true;
    }

    @Override
    public void onStopRefresh() {
        draging=true;
    }

    @Override
    public long onRefreshComplete() {
        draging=true;
        return 0;
    }
}
