package com.mbg.library.support.ViewScrollHelper;

import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by Administrator on 2017/3/25.
 */

public class NormalViewScrollHelper extends BaseViewScrollHelper {

    private static final String TAG="NormalViewScrollHelper";

    private ViewTreeObserver.OnScrollChangedListener mCacheListener;

    //子控件是否能上滑
    @Override
    public boolean canViewScrollUp(View view) {
        return null != view && ViewCompat.canScrollVertically(view,1);
    }

    //子控件是否能下拉
    @Override
    public boolean canViewScrollDown(View view) {
        return null != view && ViewCompat.canScrollVertically(view,-1);
    }

    //子控件是否能左滑
    @Override
    public boolean canViewScrollLeft(View view) {
        return null != view && ViewCompat.canScrollHorizontally(view,1);
    }

    //子控件是否能右滑
    @Override
    public boolean canViewScrollRight(View view) {
        return null != view && ViewCompat.canScrollHorizontally(view,-1);
    }

    @Override
    protected void addNoneScollListener(View view) {
        if(Build.VERSION.SDK_INT >= 23) {
            view.setOnScrollChangeListener(null);
        }else if(mCacheListener != null){
            view.getViewTreeObserver().removeOnScrollChangedListener(mCacheListener);
        }
    }

    @Override
    protected void addHorizontalScrollListenerInner(final View view) {
        if(Build.VERSION.SDK_INT >= 23) {
            view.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (null == listener) {
                        return;
                    }
                    if (!canViewScrollRight(v)) {
                        listener.onScrollToEdge(ViewScrollHelper.EDGE_LEFT);
                    } else if (!canViewScrollLeft(v)) {
                        listener.onScrollToEdge(ViewScrollHelper.EDGE_RIGHT);
                    }

                }
            });
        }else{
            if(null != mCacheListener){
                view.getViewTreeObserver().removeOnScrollChangedListener(mCacheListener);
            }
            mCacheListener=new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    if (null == listener) {
                        return;
                    }
                    if (!canViewScrollRight(view)) {
                        listener.onScrollToEdge(ViewScrollHelper.EDGE_LEFT);
                    } else if (!canViewScrollLeft(view)) {
                        listener.onScrollToEdge(ViewScrollHelper.EDGE_RIGHT);
                    }
                }
            };
            view.getViewTreeObserver().addOnScrollChangedListener(mCacheListener);
        }
    }

    @Override
    protected void addLeftSideScrollListenerInner(final View view) {
        if(Build.VERSION.SDK_INT >= 23) {
            view.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (null == listener) {
                        return;
                    }
                    if (!canViewScrollRight(v)) {
                        listener.onScrollToEdge(ViewScrollHelper.EDGE_LEFT);
                    }
                }
            });
        }else{
            if(null != mCacheListener){
                view.getViewTreeObserver().removeOnScrollChangedListener(mCacheListener);
            }
            mCacheListener=new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    if (null == listener) {
                        return;
                    }
                    if (!canViewScrollRight(view)) {
                        listener.onScrollToEdge(ViewScrollHelper.EDGE_LEFT);
                    }
                }
            };
            view.getViewTreeObserver().addOnScrollChangedListener(mCacheListener);
        }
    }

    @Override
    protected void addRightSideScrollListenerInner(final View view) {
        if(Build.VERSION.SDK_INT >= 23) {
            view.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (null == listener) {
                        return;
                    }
                    if (!canViewScrollLeft(v)) {
                        listener.onScrollToEdge(ViewScrollHelper.EDGE_RIGHT);
                    }
                }
            });
        }else{
            if(null != mCacheListener){
                view.getViewTreeObserver().removeOnScrollChangedListener(mCacheListener);
            }
            mCacheListener=new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    if (null == listener) {
                        return;
                    }
                    if (!canViewScrollLeft(view)) {
                        listener.onScrollToEdge(ViewScrollHelper.EDGE_RIGHT);
                    }
                }
            };
            view.getViewTreeObserver().addOnScrollChangedListener(mCacheListener);
        }
    }

    @Override
    protected void addVerticalScrollListenerInner(final View view) {
        if(Build.VERSION.SDK_INT >= 23) {
            view.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    //Log.i(TAG,"setOnScrollChangeListener->listener is null?"+(null == listener));
                    if (null == listener) {
                        return;
                    }
                    if (!canViewScrollDown(view)) {
                        listener.onScrollToEdge(ViewScrollHelper.EDGE_UP);
                    } else if (!canViewScrollUp(view)) {
                        listener.onScrollToEdge(ViewScrollHelper.EDGE_DOWN);
                    }
                }
            });
        }else{
            if(null != mCacheListener){
                view.getViewTreeObserver().removeOnScrollChangedListener(mCacheListener);
            }
            mCacheListener=new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    //Log.i(TAG,"setOnScrollChangeListener->listener is null?"+(null == listener));
                    if (null == listener) {
                        return;
                    }
                    if (!canViewScrollDown(view)) {
                        listener.onScrollToEdge(ViewScrollHelper.EDGE_UP);
                    } else if (!canViewScrollUp(view)) {
                        listener.onScrollToEdge(ViewScrollHelper.EDGE_DOWN);
                    }
                }
            };
            view.getViewTreeObserver().addOnScrollChangedListener(mCacheListener);
        }
    }

    @Override
    protected void addUpSideScrollListenerInner(final View view) {
        if(Build.VERSION.SDK_INT >= 23) {
            view.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    //Log.i(TAG,"setOnScrollChangeListener->listener is null?"+(null == listener));
                    if (null == listener) {
                        return;
                    }
                    if (!canViewScrollDown(v)) {
                        listener.onScrollToEdge(ViewScrollHelper.EDGE_UP);
                    }
                }
            });
        }else{
            if(null != mCacheListener){
                view.getViewTreeObserver().removeOnScrollChangedListener(mCacheListener);
            }
            mCacheListener=new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    //Log.i(TAG,"setOnScrollChangeListener->listener is null?"+(null == listener));
                    if (null == listener) {
                        return;
                    }
                    if (!canViewScrollDown(view)) {
                        listener.onScrollToEdge(ViewScrollHelper.EDGE_UP);
                    }
                }
            };
            view.getViewTreeObserver().addOnScrollChangedListener(mCacheListener);
        }
    }

    @Override
    protected void addDownSideScrollListenerInner(final View view) {
        if(Build.VERSION.SDK_INT >= 23) {
            view.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    //Log.i(TAG,"setOnScrollChangeListener->listener is null?"+(null == listener));
                    if (null == listener) {
                        return;
                    }
                    if (!canViewScrollUp(v)) {
                        listener.onScrollToEdge(ViewScrollHelper.EDGE_DOWN);
                    }
                }
            });
        }else{
            if(null != mCacheListener){
                view.getViewTreeObserver().removeOnScrollChangedListener(mCacheListener);
            }
            mCacheListener=new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    //Log.i(TAG,"setOnScrollChangeListener->listener is null?"+(null == listener));
                    if (null == listener) {
                        return;
                    }
                    if (!canViewScrollUp(view)) {
                        listener.onScrollToEdge(ViewScrollHelper.EDGE_DOWN);
                    }
                }
            };
            view.getViewTreeObserver().addOnScrollChangedListener(mCacheListener);
        }
    }
}
