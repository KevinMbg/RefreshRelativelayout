package com.mbg.refreshlayout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.mbg.library.RefreshRelativeLayout;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RefreshRelativeLayout refreshRelativeLayout=getRefreshRelativeLayout();
        refreshRelativeLayout.setNegativeEnable(false);
        refreshRelativeLayout.setPositiveEnable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected String getActivityTitle() {
        return "MainActivity";
    }

    public void NestedScrollView(View view){
        skipToNextActivity(NestedScrollViewActivity.class);
    }
    public void NestedRecyclerView(View view){
        skipToNextActivity(NestedRecyclerViewActivity.class);
    }
    public void NestedListView(View view){
        skipToNextActivity(NestedListViewActivity.class);
    }

    public void NestedGridView(View view){
        skipToNextActivity(NestedGridViewActivity.class);
    }

    public void NestedNormalViewVertical(View view){
        skipToNextActivity(NestedNormalViewActivity.class);
    }
    public void NestedWebView(View view){
        skipToNextActivity(NestedWebviewActivity.class);
    }

    public void NestedHorizontalScrollView(View view){
        skipToNextActivity(NestedHorizontalScrollViewActivity.class);
    }

    public void NestedHorizontalRecyclerView(View view){
        skipToNextActivity(NestedHorizontalRecyclerActivity.class);
    }

    public void NestedDoubleDirection(View view){
        skipToNextActivity(NestedDoubleDirectionActivity.class);
    }

    public void CustomRefresher(View view){
        skipToNextActivity(CustomRefresherActivity.class);
    }

    private void skipToNextActivity(Class T){
        Intent intent=new Intent(this,T);
        startActivity(intent);
    }
}
