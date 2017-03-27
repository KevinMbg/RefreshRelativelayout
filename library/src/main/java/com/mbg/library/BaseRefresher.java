package com.mbg.library;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2017/3/19.
 */

public abstract class BaseRefresher implements IRefresher {
    protected View view;
    @Override
    public View getView(Context context, ViewGroup viewGroup) {
        if(null == view){
            view=createRefresherView(context,viewGroup);
        }
        return view;
    }

    @Override
    public boolean canRefresh(float offset) {
        if(null == view){
            return false;
        }
        int width=view.getMeasuredWidth();
        int height=view.getMeasuredHeight();
        if(0 == width || 0 == height){
            return false;
        }
        return canRefresh(width,height,offset);
    }

    protected abstract View createRefresherView(Context context, ViewGroup viewGroup);
    protected abstract boolean canRefresh(int viewWidth,int viewHeight,float offset);
}
