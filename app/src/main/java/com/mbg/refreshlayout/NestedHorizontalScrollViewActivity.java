package com.mbg.refreshlayout;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mbg.library.IRefreshListener;
import com.mbg.library.RefreshRelativeLayout;

public class NestedHorizontalScrollViewActivity extends BaseActivity {

    private RefreshRelativeLayout refreshRelativeLayout;
    private LinearLayout target;
    private int[] colors;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested_horizontal_scroll_view);
        colors=new int[]{Color.RED,Color.YELLOW,Color.GRAY,Color.MAGENTA,Color.GREEN,Color.WHITE};
        initView();
    }

    private void initView(){
        refreshRelativeLayout=getRefreshRelativeLayout();
        refreshRelativeLayout.addRefreshListener(new IRefreshListener() {
            @Override
            public void onPositiveRefresh() {
                positiveRefresh();
            }

            @Override
            public void onNegativeRefresh() {
                negativeRefresh();
            }
        });
        target=(LinearLayout)findViewById(R.id.linearlayout);
    }

    private void positiveRefresh(){
        target.postDelayed(new Runnable() {
            @Override
            public void run() {
                target.removeAllViews();
                addViewsToTarget();
                refreshRelativeLayout.positiveRefreshComplete();
            }
        },1000);
    }

    private void negativeRefresh(){
        target.postDelayed(new Runnable() {
            @Override
            public void run() {
                addViewsToTarget();
                refreshRelativeLayout.negativeRefreshComplete();
            }
        },1000);
    }

    private void addViewsToTarget(){
        for(int i=0;i<colors.length;i++){
            View view=new View(this);
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(500, ViewGroup.LayoutParams.MATCH_PARENT);
            view.setLayoutParams(layoutParams);
            view.setBackgroundColor(colors[i]);
            target.addView(view);
        }
    }

    @Override
    protected boolean isHorizontal() {
        return true;
    }

    @Override
    protected String getActivityTitle() {
        return "水平ScrollView";
    }
}
