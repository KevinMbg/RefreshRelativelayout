package com.mbg.refreshlayout;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mbg.library.DefaultNegativeRefreshers.NegativeRefresherWithNodata;
import com.mbg.library.DefaultPositiveRefreshers.PositiveRefresherWithText;
import com.mbg.library.ISingleRefreshListener;
import com.mbg.library.RefreshRelativeLayout;

public class NestedScrollViewActivity extends BaseActivity {

    RefreshRelativeLayout mRefreshLatyout;
    LinearLayout target;
    int[] colors;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neste_scroll_view);
        colors=new int[]{Color.RED,Color.YELLOW,Color.GRAY,Color.MAGENTA,Color.GREEN,Color.WHITE};
        mRefreshLatyout=getRefreshRelativeLayout();
        target=(LinearLayout)findViewById(R.id.tartget);
        mRefreshLatyout.addPositiveRefreshListener(new ISingleRefreshListener() {
            @Override
            public void onRefresh() {
                onPositiveRefresh();
            }
        });
        mRefreshLatyout.addNegativeRefreshListener(new ISingleRefreshListener() {
            @Override
            public void onRefresh() {
                onNegativeRefresh();
            }
        });
    }

    @Override
    protected String getActivityTitle() {
        return "嵌套ScrollView";
    }

    private void onPositiveRefresh(){
        target.postDelayed(new Runnable() {
            @Override
            public void run() {
                target.removeAllViews();
                addViewsToTarget();
                mRefreshLatyout.positiveRefreshComplete();
            }
        },1000);
    }

    private void onNegativeRefresh(){
        target.postDelayed(new Runnable() {
            @Override
            public void run() {
                addViewsToTarget();
                mRefreshLatyout.negativeRefreshComplete();
            }
        },1000);
    }

    private void addViewsToTarget(){
        for(int i=0;i<colors.length;i++){
            View view=new View(this);
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,500);
            view.setLayoutParams(layoutParams);
            view.setBackgroundColor(colors[i]);
            target.addView(view);
        }
    }


}
