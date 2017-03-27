package com.mbg.refreshlayout;

import android.os.Bundle;
import android.widget.TextView;

import com.mbg.library.IRefreshListener;
import com.mbg.library.RefreshRelativeLayout;

public class NestedNormalViewActivity extends BaseActivity {

    private RefreshRelativeLayout refreshRelativeLayout;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested_normal_view);
        initView();
    }

    private int curRefNum=0;
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
        textView=(TextView)findViewById(R.id.normal_text);
        refreshRelativeLayout.startPositiveRefresh();
    }

    private void positiveRefresh(){
        textView.postDelayed(new Runnable() {
            @Override
            public void run() {
                curRefNum=1;
                textView.setText("这是第"+curRefNum+"次刷新\n");
                refreshRelativeLayout.positiveRefreshComplete();
            }
        },1000);
    }

    private void negativeRefresh(){
        textView.postDelayed(new Runnable() {
            @Override
            public void run() {
                String text=textView.getText().toString();
                textView.setText(text+"这是第"+ (++curRefNum) +"次刷新\n");
                refreshRelativeLayout.negativeRefreshComplete();
            }
        },1000);
    }

    @Override
    protected String getActivityTitle() {
        return "嵌套普通View(竖直方向刷新)";
    }
}
