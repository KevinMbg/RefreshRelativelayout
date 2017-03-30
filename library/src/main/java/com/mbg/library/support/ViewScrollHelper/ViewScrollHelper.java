package com.mbg.library.support.ViewScrollHelper;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;

import static android.R.attr.action;

/**
 * Created by Administrator on 2017/3/25.
 */

public class ViewScrollHelper implements IViewScrollHelper {

    public static final int EDGE_UP=0;
    public static final int EDGE_DOWN=1;
    public static final int EDGE_LEFT=2;
    public static final int EDGE_RIGHT=3;

    private IViewScrollHelper mViewScrollHelperInner;
    private boolean orientationIsHorizontal=false;
    private boolean positiveDragEnable=true,negativeDragEnable=true;
    private onScrollToEdgeListener listener;
    private onChildTouchChangeListener mChildTouchChangeListener;
    private View.OnTouchListener mTouchListener;

    public ViewScrollHelper(boolean isHorizontal,boolean positiveDragEnable,boolean negativeDragEnable){
        this.orientationIsHorizontal=isHorizontal;
        this.positiveDragEnable=positiveDragEnable;
        this.negativeDragEnable=negativeDragEnable;
    }

    @Override
    public boolean canViewScrollUp(View view) {
        return null != mViewScrollHelperInner && mViewScrollHelperInner.canViewScrollUp(view);
    }

    @Override
    public boolean canViewScrollDown(View view) {
        return null != mViewScrollHelperInner && mViewScrollHelperInner.canViewScrollDown(view);
    }

    @Override
    public boolean canViewScrollLeft(View view) {
        return null != mViewScrollHelperInner && mViewScrollHelperInner.canViewScrollLeft(view);
    }

    @Override
    public boolean canViewScrollRight(View view) {
        return null != mViewScrollHelperInner && mViewScrollHelperInner.canViewScrollRight(view);
    }

    @Override
    public void addViewScroller(View view) {
        if(null == view){
            return;
        }
        if(null == mTouchListener){
            mTouchListener=new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(null == mChildTouchChangeListener){
                        return false;
                    }
                    int action=event.getAction();
                    switch (action){
                        case MotionEvent.ACTION_DOWN:
                            mChildTouchChangeListener.onChildTouchChanged(false);
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            mChildTouchChangeListener.onChildTouchChanged(true);
                            break;
                    }
                    return false;
                }
            };
            view.setOnTouchListener(mTouchListener);
        }
        if(null == mViewScrollHelperInner){
            if(view instanceof AbsListView){
                mViewScrollHelperInner=new AbsListViewScrollHelper();
            }else{
                mViewScrollHelperInner=new NormalViewScrollHelper();
            }
            mViewScrollHelperInner.setOrientation(view,orientationIsHorizontal);
            mViewScrollHelperInner.setNegativeDragEnable(view,negativeDragEnable);
            mViewScrollHelperInner.setPositiveDragEnable(view,positiveDragEnable);
            mViewScrollHelperInner.setScrollToEdgeListener(listener);
        }
        mViewScrollHelperInner.addViewScroller(view);
    }

    @Override
    public void setOrientation(View view,boolean isHorizontal) {
        this.orientationIsHorizontal=isHorizontal;
        if(null != mViewScrollHelperInner){
            mViewScrollHelperInner.setOrientation(view,isHorizontal);
        }
    }

    @Override
    public void setPositiveDragEnable(View view,boolean enable) {
        positiveDragEnable=enable;
        if(null != mViewScrollHelperInner){
            mViewScrollHelperInner.setPositiveDragEnable(view,enable);
        }
    }

    @Override
    public void setNegativeDragEnable(View view,boolean enable) {
        negativeDragEnable=enable;
        if(null != mViewScrollHelperInner){
            mViewScrollHelperInner.setNegativeDragEnable(view,enable);
        }
    }

    @Override
    public void setScrollToEdgeListener(onScrollToEdgeListener listener) {
        this.listener=listener;
        if(null != mViewScrollHelperInner){
            mViewScrollHelperInner.setScrollToEdgeListener(listener);
        }
    }

    public void setChildTouchChangeListener(onChildTouchChangeListener childTouchChangeListener){
        this.mChildTouchChangeListener = childTouchChangeListener;
    }
}
