package com.mbg.library.support.ViewScrollHelper;

import android.util.Log;
import android.view.View;
import android.widget.AbsListView;


/**
 * Created by Administrator on 2017/3/25.
 */

public class AbsListViewScrollHelper extends NormalViewScrollHelper {

    /*@Override
    public boolean canViewScrollDown(View view) {
        //检测是否能下拉
        if(null == view) {
            return false;
        }
        AbsListView absListView= (AbsListView) view;
        return absListView.getChildCount() > 0
                && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                .getTop() < absListView.getPaddingTop());
    }

    @Override
    public boolean canViewScrollUp(View view) {
        //检测是否能上滑
        if(null == view){
            return false;
        }
        AbsListView absListView= (AbsListView) view;
        int childCount=absListView.getChildCount();
        return childCount > 0 &&
                (absListView.getLastVisiblePosition() < childCount -1
                  || absListView.getHeight() < absListView.getChildAt(childCount -1).getBottom());
    }

    @Override
    public boolean canViewScrollRight(View view) {
        //检测是否能右滑
        if(null == view){
            return false;
        }
        AbsListView absListView= (AbsListView) view;
        return absListView.getChildCount() > 0
                  && ( absListView.getFirstVisiblePosition() > 0
                       || absListView.getChildAt(0).getLeft() <absListView.getPaddingLeft() );
    }

    @Override
    public boolean canViewScrollLeft(View view) {
        //检测是否能左滑
        if(null == view){
            return false;
        }
        AbsListView absListView= (AbsListView) view;
        int childCount=absListView.getChildCount();
        return childCount > 0
                && (absListView.getLastVisiblePosition() < childCount -1
                     ||(absListView.getRight()-absListView.getPaddingRight()) < absListView.getChildAt(childCount-1).getRight());
    }
*/


    @Override
    protected void addNoneScollListener(View view) {
        AbsListView absListView= (AbsListView) view;
        absListView.setOnScrollListener(null);
    }

    @Override
    protected void addHorizontalScrollListenerInner(View view) {
        AbsListView absListView= (AbsListView) view;
        absListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(null == listener){
                    return;
                }
                if(SCROLL_STATE_IDLE != scrollState){
                    return;
                }
                if(!canViewScrollRight(view)){
                    listener.onScrollToEdge(ViewScrollHelper.EDGE_LEFT);
                }else if(!canViewScrollLeft(view)){
                    listener.onScrollToEdge(ViewScrollHelper.EDGE_RIGHT);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    protected void addLeftSideScrollListenerInner(View view) {
        AbsListView absListView= (AbsListView) view;
        absListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(null == listener){
                    return;
                }
                if(SCROLL_STATE_IDLE == scrollState && !canViewScrollRight(view)){
                    listener.onScrollToEdge(ViewScrollHelper.EDGE_LEFT);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    protected void addRightSideScrollListenerInner(View view) {
        AbsListView absListView= (AbsListView) view;
        absListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(null == listener){
                    return;
                }
                if(SCROLL_STATE_IDLE == scrollState && !canViewScrollLeft(view)){
                    listener.onScrollToEdge(ViewScrollHelper.EDGE_RIGHT);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    protected void addVerticalScrollListenerInner(View view) {
        AbsListView absListView= (AbsListView) view;
        absListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(null == listener){
                    return;
                }
                if(SCROLL_STATE_IDLE != scrollState){
                    return;
                }
                if(!canViewScrollDown(view)){
                    listener.onScrollToEdge(ViewScrollHelper.EDGE_UP);
                }else if(!canViewScrollUp(view)){
                    listener.onScrollToEdge(ViewScrollHelper.EDGE_DOWN);
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    protected void addUpSideScrollListenerInner(View view) {
        AbsListView absListView= (AbsListView) view;
        absListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(null == listener){
                    return;
                }
                if(SCROLL_STATE_IDLE == scrollState && !canViewScrollDown(view)){
                    listener.onScrollToEdge(ViewScrollHelper.EDGE_UP);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    protected void addDownSideScrollListenerInner(View view) {
        AbsListView absListView= (AbsListView) view;
        absListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.i("onScrollStateChanged","scrollState:"+scrollState);
                if(null == listener){
                    return;
                }
                if(SCROLL_STATE_IDLE == scrollState && !canViewScrollUp(view)){
                    listener.onScrollToEdge(ViewScrollHelper.EDGE_DOWN);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }
}
