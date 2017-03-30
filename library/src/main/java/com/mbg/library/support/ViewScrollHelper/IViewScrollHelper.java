package com.mbg.library.support.ViewScrollHelper;

import android.view.View;

/**
 * Created by Administrator on 2017/3/25.
 */

public interface IViewScrollHelper {
    boolean canViewScrollUp(View view);
    boolean canViewScrollDown(View view);
    boolean canViewScrollLeft(View view);
    boolean canViewScrollRight(View view);
    void addViewScroller(View view);
    void setOrientation(View view,boolean isHorizontal);
    void setPositiveDragEnable(View view,boolean enable);
    void setNegativeDragEnable(View view,boolean enable);
    void setScrollToEdgeListener(onScrollToEdgeListener listener);

    public interface onScrollToEdgeListener{
        void onScrollToEdge(int edgeType);
    }

    public interface onChildTouchChangeListener{
        void onChildTouchChanged(boolean isUp);
    }
}
