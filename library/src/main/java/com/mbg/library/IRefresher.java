package com.mbg.library;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2017/3/17.
 */

public interface IRefresher {
    View getView(Context context, ViewGroup viewGroup);
    void onDrag(float offset);
    boolean canRefresh(float offset);
    float getOverlayOffset();
    boolean onStartRefresh();//when user want to complete when onstartRefresh return true;
    void onStopRefresh();
    long onRefreshComplete();//the delayTime when hide Refresher
}
