package com.mbg.library.support.ViewScrollHelper;

import android.view.View;

/**
 * Created by Administrator on 2017/3/25.
 */

public abstract class BaseViewScrollHelper implements IViewScrollHelper {

    private boolean oritationIsHorizontal=false;
    private boolean positiveDragEnable=true,negativeDragEnable=true;
    protected onScrollToEdgeListener listener;

    @Override
    public void setScrollToEdgeListener(onScrollToEdgeListener listener) {
        this.listener=listener;
    }

    @Override
    public void setOrientation(View view,boolean isHorizontal) {
        if(oritationIsHorizontal == isHorizontal){
            return;
        }
        this.oritationIsHorizontal=isHorizontal;
        addViewScroller(view);
    }

    @Override
    public void setNegativeDragEnable(View view,boolean enable) {
        if(negativeDragEnable == enable){
            return;
        }
        negativeDragEnable = enable;
        addViewScroller(view);
    }

    @Override
    public void setPositiveDragEnable(View view,boolean enable) {
        if(positiveDragEnable == enable){
            return;
        }
        positiveDragEnable =enable;
        addViewScroller(view);
    }

    @Override
    public void addViewScroller(View view) {
        if(null == view){
            return;
        }
        if(oritationIsHorizontal){
            if(!positiveDragEnable && !negativeDragEnable){
                addHorizontalScrollListenerInner(view);
            }else if(!positiveDragEnable){
                addLeftSideScrollListenerInner(view);
            }else if(!negativeDragEnable){
                addRightSideScrollListenerInner(view);
            }else{
                addNoneScollListener(view);
            }
        }else{
            if(!positiveDragEnable && !negativeDragEnable){
                addVerticalScrollListenerInner(view);
            }else if(!positiveDragEnable){
                addUpSideScrollListenerInner(view);
            }else if(!negativeDragEnable){
                addDownSideScrollListenerInner(view);
            }else{
                addNoneScollListener(view);
            }
        }
    }

    protected abstract void addNoneScollListener(View view);
    protected abstract void addHorizontalScrollListenerInner(View view);//水平方向均自动滑动
    protected abstract void addLeftSideScrollListenerInner(View view);//左侧滑动
    protected abstract void addRightSideScrollListenerInner(View view);//右侧
    protected abstract void addVerticalScrollListenerInner(View view);//
    protected abstract void addUpSideScrollListenerInner(View view);
    protected abstract void addDownSideScrollListenerInner(View view);
}
